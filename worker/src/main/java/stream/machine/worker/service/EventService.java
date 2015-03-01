package stream.machine.worker.service;

import akka.util.Timeout;
import com.codahale.metrics.annotation.Timed;
import scala.concurrent.Await;
import scala.concurrent.duration.Duration;
import stream.machine.core.configuration.Configuration;
import stream.machine.core.exception.ApplicationException;
import stream.machine.core.manager.ManageableBase;
import stream.machine.core.model.Event;
import stream.machine.core.monitor.ConfigurationMessage;
import stream.machine.core.monitor.Message;
import stream.machine.core.monitor.MonitorConsumer;
import stream.machine.core.stream.StreamManager;
import stream.machine.core.task.Task;
import stream.machine.core.task.TaskFactory;
import stream.machine.core.task.TaskType;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

@Path("/event")
@Produces(MediaType.APPLICATION_JSON)
public class EventService extends ManageableBase implements MonitorConsumer {
    private Map<String, AtomicReference<Task>> tasks;
    private final int timeoutInSeconds;
    private final TaskFactory taskFactory;

    public EventService(StreamManager streamManager, int timeoutInSeconds) {
        super("EventService");
        this.timeoutInSeconds = timeoutInSeconds;
        this.tasks = new ConcurrentHashMap<String, AtomicReference<Task>>();
        if (streamManager != null) {
            this.taskFactory = streamManager.getTaskFactory();
            register(streamManager);
            try {
                for (TaskType type : TaskType.values()) {
                    Map<String, Task> typedTasks = streamManager.getTasks(type);
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
            this.tasks = null;
            this.taskFactory = null;
        }
    }

    @POST
    @Timed
    @Path("/process/{task}")
    public Event transform(@PathParam("task") String taskName, Event event) throws ApplicationException {
        return process(event, taskName);
    }

    @POST
    @Timed
    @Path("/processMultiple/{task}")
    public List<Event> transformMultiple(@PathParam("task") String taskName, List<Event> events) throws ApplicationException {
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
    public void onMessage(Message message) {
        if (message != null) {
            if (message instanceof ConfigurationMessage) {
                ConfigurationMessage configurationMessage = (ConfigurationMessage) message;
                if (configurationMessage.getConfiguration() != null) {
                    if (configurationMessage.getSubject() == ServiceTopics.TaskConfigurationCreated) {
                        createTask(configurationMessage.getConfiguration());
                    } else if (configurationMessage.getSubject() == ServiceTopics.TaskConfigurationUpdated) {
                        updateTask(configurationMessage.getConfiguration());
                    } else if (configurationMessage.getSubject() == ServiceTopics.TaskConfigurationUpdated) {
                        deleteTask(configurationMessage.getConfiguration());
                    }
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
            Timeout timeout = new Timeout(Duration.create(timeoutInSeconds, "seconds"));
            try {
                if (tasks.containsKey(taskName)) {
                    Task task = tasks.get(taskName).get();
                    if (task != null) {
                        return Await.result(task.process(event), timeout.duration());
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
            Timeout timeout = new Timeout(Duration.create(timeoutInSeconds, "seconds"));
            try {
                if (tasks.containsKey(taskName)) {
                    Task task = tasks.get(taskName).get();
                    if (task != null) {
                        return Await.result(task.processMultiple(events), timeout.duration());
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

