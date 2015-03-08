package stream.machine.worker.service;

import com.codahale.metrics.annotation.Timed;
import stream.machine.core.communication.MessageConsumer;
import stream.machine.core.configuration.Configuration;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.manager.ManageableBase;
import stream.machine.core.model.Event;
import stream.machine.core.store.EventStore;
import stream.machine.core.stream.StreamManager;
import stream.machine.core.task.Task;
import stream.machine.core.task.TaskFactory;
import stream.machine.core.task.TaskType;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Path("/event")
@Produces(MediaType.APPLICATION_JSON)
public class EventService extends ManageableBase implements MessageConsumer {
    private Map<String, AtomicReference<Task>> tasks;
    private final int timeoutInSeconds;
    private final TaskFactory taskFactory;
    private final EventStore eventStore;

    public EventService(StreamManager streamManager, int timeoutInSeconds) {
        super("EventService");
        this.timeoutInSeconds = timeoutInSeconds;
        this.tasks = new ConcurrentHashMap<String, AtomicReference<Task>>();
        if (streamManager != null) {
            this.eventStore = streamManager.getStoreManager().getEventStore();
            this.taskFactory = streamManager.getTaskFactory();
            register(streamManager);
            try {
                for (TaskType type : TaskType.values()) {
                    Map<String, Task> typedTasks = streamManager.getTaskFactory().buildAll(type);
                    if (typedTasks != null && typedTasks.size() > 0) {
                        for (Map.Entry<String, Task> typedTask : typedTasks.entrySet()) {
                            this.tasks.put(typedTask.getKey(), new AtomicReference<Task>(typedTask.getValue()));
                        }
                    }
                }
            } catch (ApplicationException error) {
                logger.error(error.getMessage());
            }

        } else {
            this.eventStore = null;
            this.tasks = null;
            this.taskFactory = null;
        }
    }

    @POST
    @Timed
    @Path("/process/{task}")
    public Event process(@PathParam("task") String taskName, Event event) throws ApplicationException {
        return process(event, taskName);
    }

    @POST
    @Timed
    @Path("/processMultiple/{task}")
    public List<Event> processMultiple(@PathParam("task") String taskName, List<Event> events) throws ApplicationException {
        return processMultiple(events, taskName);
    }

    @Override
    public void start() throws ApplicationException {
        if (tasks != null) {
            for (AtomicReference<Task> task : this.tasks.values()) {
                task.get().start();
            }
        }
    }

    @Override
    public void stop() throws ApplicationException {
        if (tasks != null) {
            for (AtomicReference<Task> task : this.tasks.values()) {
                task.get().start();
            }
        }
    }

    @Override
    public <T> void onMessage(String topicName, T data) {
        if (data != null) {
            if (data instanceof Configuration) {
                Configuration configuration = (Configuration) data;
                if (topicName == ServiceTopics.TaskConfigurationCreated) {
                    createTask(configuration);
                } else if (topicName == ServiceTopics.TaskConfigurationUpdated) {
                    updateTask(configuration);
                } else if (topicName == ServiceTopics.TaskConfigurationUpdated) {
                    deleteTask(configuration);
                }
            }
        }
    }

    private void deleteTask(Configuration configuration) {
        if (this.tasks != null) {
            if (this.tasks.containsKey(configuration.getName())) {
                this.tasks.remove(configuration.getName());
            }
        }
    }

    private void updateTask(Configuration configuration) {
        if (this.tasks != null && taskFactory != null) {
            if (this.tasks.containsKey(configuration.getName())) {

                AtomicReference<Task> taskReference = this.tasks.get(configuration.getName());
                if (taskReference != null) {
                    Task task = taskFactory.build(configuration);
                    taskReference.set(task);
                }
            }
        }
    }

    private void createTask(Configuration configuration) {
        if (this.tasks != null && taskFactory != null) {
            if (!this.tasks.containsKey(configuration.getName())) {
                AtomicReference<Task> taskReference = new AtomicReference<Task>(taskFactory.build(configuration));
                this.tasks.put(configuration.getName(), taskReference);
            }
        }
    }

    private void register(StreamManager streamManager) {
        streamManager.register(ServiceTopics.TaskConfigurationCreated, this);
        streamManager.register(ServiceTopics.TaskConfigurationUpdated, this);
        streamManager.register(ServiceTopics.TaskConfigurationDeleted, this);
    }

    private Event process(Event event, String taskName) throws ApplicationException {
        if (tasks != null) {
            try {
                if (tasks.containsKey(taskName)) {
                    Task task = tasks.get(taskName).get();
                    if (task != null) {
                        return task.process(event).get(timeoutInSeconds, TimeUnit.SECONDS);
                    }
                }
            } catch (Exception error) {
                logger.error(String.format("Failed to  process event with task %s", taskName), error);
                throw new ApplicationException(String.format("Failed to  process event with task %s", taskName), error);
            }
        }
        return event;
    }

    private List<Event> processMultiple(List<Event> events, String taskName) throws ApplicationException {
        if (tasks != null) {
            try {
                if (tasks.containsKey(taskName)) {
                    Task task = tasks.get(taskName).get();
                    if (task != null) {
                        return task.processMultiple(events).get(timeoutInSeconds, TimeUnit.SECONDS);
                    }
                }
            } catch (Exception error) {
                logger.error(String.format("Failed to  process multiple event with task %s", taskName), error);
                throw new ApplicationException(String.format("Failed to  process multiple event with task %s", taskName), error);
            }
        }
        return events;
    }


}

