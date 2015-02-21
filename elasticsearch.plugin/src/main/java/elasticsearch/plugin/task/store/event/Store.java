package elasticsearch.plugin.task.store.event;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import elasticsearch.plugin.ElasticsearchPlugin;
import elasticsearch.plugin.task.store.StoreBase;
import elasticsearch.plugin.task.store.StoreManager;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.action.support.IndicesOptions;
import org.elasticsearch.client.Client;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.joda.time.DateTime;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.model.Event;
import stream.machine.core.store.EventStore;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Created by Stephane on 15/02/2015.
 */
public class Store extends StoreBase implements EventStore {
    private final boolean useDateInIndex;
    private final boolean useEventTypeInIndex;
    private final String indexPattern;
    private final ObjectMapper mapper;


    public Store(StoreManager storeManager) {
        super("Elasticsearch.EventStore",storeManager);
        this.useDateInIndex = false;
        this.useEventTypeInIndex = false;
        this.indexPattern = "logstash-";
        this.mapper = new ObjectMapper();

    }

    public Store() {
        super("Elasticsearch.EventStore", ElasticsearchPlugin.getStoreManager());
        this.useDateInIndex = false;
        this.useEventTypeInIndex = false;
        this.indexPattern = "logstash-";
        this.mapper = new ObjectMapper();

    }

    public Store(StoreManager storeManager, boolean useDateInIndex, boolean useTypeIndex, String indexPattern) {
        super("Elasticsearch.EventStore", storeManager);
        this.useDateInIndex = useDateInIndex;
        this.useEventTypeInIndex = useTypeIndex;
        this.indexPattern = indexPattern;
        this.mapper = new ObjectMapper();
    }

    @Override
    public void start() throws ApplicationException {
        logger.info("Elasticsearch.EventStore has started");
    }

    @Override
    public void stop() throws ApplicationException {
        logger.info("Elasticsearch.EventStore has stopped");
    }

    @Override
    public List<Event> save(List<Event> events) {
        Client client = storeManager.getClient();
        if (client == null) return events;
        BulkRequestBuilder bulkRequest = client.prepareBulk();
        for (Event event : events) {
            String eventType = event.getType();
            DateTime eventTimeStamp = event.getTimestamp();
            try {
                String eventAsString = mapper.writeValueAsString(event);
                bulkRequest.add(client.prepareIndex(buildIndex(eventType, eventTimeStamp), eventType, event.getKey().toString()).setSource(eventAsString));
            } catch (ApplicationException error) {
                logger.error(String.format("Request building failed for event %s", event.getKey().toString()), error);
                event.put(Event.storeError, error.getMessage());
            } catch (JsonProcessingException error) {
                logger.error(String.format("Json conversion failed for event %s", event.getKey().toString()), error);
                event.put(Event.storeError, error.getMessage());
            }
        }

        BulkResponse bulkResponse = bulkRequest.execute().actionGet();
        if (bulkResponse.hasFailures()) {
            for (BulkItemResponse response : bulkResponse.getItems()) {
                final String eventId = response.getId();

                Predicate<Event> findEvent = new Predicate<Event>() {
                    UUID id = UUID.fromString(eventId);

                    public boolean apply(Event event) {
                        return event.getKey().equals(id);
                    }
                };
                Collection<Event> filteredEvents = Collections2.filter(events, findEvent);
                if (filteredEvents != null && filteredEvents.size() == 1) {
                    Event filteredEvent = (Event) filteredEvents.toArray()[0];
                    if (response.getFailure() != null) {
                        filteredEvent.put(Event.storeError, response.getFailure().getMessage());
                    } else {
                        filteredEvent.put("version", response.getVersion());
                    }
                }
            }
        }
        return events;
    }

    @Override
    public Event save(Event event) {
        Client client = storeManager.getClient();
        if (client == null) return event;
        IndexRequestBuilder indexRequest;

        String eventType = event.getType();
        DateTime eventTimeStamp = event.getTimestamp();
        try {
            String eventAsString = mapper.writeValueAsString(event);
            indexRequest = client.prepareIndex(buildIndex(eventType, eventTimeStamp), eventType, event.getKey().toString())
                    .setSource(eventAsString);
        } catch (ApplicationException error) {
            logger.error(String.format("Request building failed for event %s", event.getKey().toString()), error);
            event.put(Event.storeError, error.getMessage());
            return event;
        } catch (JsonProcessingException error) {
            logger.error(String.format("Json conversion failed for event %s", event.getKey().toString()), error);
            event.put(Event.storeError, error.getMessage());
            return event;
        }

        IndexResponse indexResponse = indexRequest.execute().actionGet();
        event.put("version", indexResponse.getVersion());
        return event;
    }

    @Override
    public List<Event> fetch(String eventType, DateTime startTime, DateTime stopTime) {

        Client client = storeManager.getClient();
        if (client == null) return null;
        List<Event> events = new ArrayList<Event>();

        try {
            boolean searchComplete = false;
            QueryBuilder typeQuery = QueryBuilders.matchQuery(Event.type, eventType);
            QueryBuilder dateQuery = QueryBuilders
                    .rangeQuery(Event.timestamp)
                    .from(startTime)
                    .to(stopTime)
                    .includeLower(true)
                    .includeUpper(true);
            QueryBuilder query = QueryBuilders.boolQuery()
                    .must(typeQuery)
                    .must(dateQuery);

            SearchResponse searchResponse = client.prepareSearch()
                    .setIndices(buildIndices(eventType, startTime, stopTime))
                    .setIndicesOptions(IndicesOptions.fromOptions(true,false,false,false))
                    .setSearchType(SearchType.SCAN)
                    .setScroll(new TimeValue(60000))
                    .setQuery(query)
                    .setSize(100).execute().actionGet();
            while (!searchComplete) {
                for (SearchHit hit : searchResponse.getHits().getHits()) {
                    try {
                        events.add(mapper.readValue(hit.getSourceAsString(), Event.class));
                    } catch (IOException error) {
                        logger.error("Failed to serialize event", error);
                    }
                }
                searchResponse = client.prepareSearchScroll(searchResponse.getScrollId())
                        .setScroll(new TimeValue(600000))
                        .execute()
                        .actionGet();
                //Break condition: No hits are returned
                if (searchResponse.getHits().getHits().length == 0) {
                    searchComplete = true;
                }
            }
        } catch (ApplicationException error) {
            logger.error("Failed to build index list", error);
        }

        return events;
    }

    @Override
    public List<Event> update(List<Event> events) {
        return null;
    }

    @Override
    public Event update(Event event) {
        return null;
    }


    private String buildIndex(String eventType, DateTime date) throws ApplicationException {
        String index = indexPattern;
        if (useEventTypeInIndex) {
            index = String.format("%s.%s", index, eventType);
        }
        if (useDateInIndex) {
            index = String.format("%s.%s", index, date.toString("yyyy.MM.dd"));
        }
        return index.toLowerCase();
    }

    public String[] buildIndices(String eventType, DateTime start, DateTime stop) throws ApplicationException {
        List<String> indices = new ArrayList<String>();
        if (!useDateInIndex) {
            String index = indexPattern;
            if (useEventTypeInIndex) {
                index = String.format("%s.%s", index, eventType);
            }
            indices.add(index.toLowerCase());
        } else {
            DateTime date = start;
            do {
                String index = buildIndex(eventType, date);
                //TODO : test with non existing index
                if (!indices.contains(index)) {
                    indices.add(index);
                }
                date = date.plusDays(1);
            }
            while (date.isBefore(stop) || date.equals(stop));
        }
        return indices.toArray(new String[indices.size()]);
    }


}
