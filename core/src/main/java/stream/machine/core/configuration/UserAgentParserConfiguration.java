package stream.machine.core.configuration;

import stream.machine.core.task.TaskType;

/**
 * Created by Stephane on 27/02/2015.
 */
public class UserAgentParserConfiguration extends Configuration {
    public static String userAgentField = "userAgentField";

    public UserAgentParserConfiguration() {
        super("", TaskType.UserAgent);
        put(UserAgentParserConfiguration.userAgentField, null);
    }

    public UserAgentParserConfiguration(Configuration configuration) {
        super(configuration);
    }

    public UserAgentParserConfiguration(String name, String userAgentField) {
        super(name, TaskType.UserAgent);
        put(UserAgentParserConfiguration.userAgentField, userAgentField);
    }

    public String getUserAgentField() {
        return (String) get(UserAgentParserConfiguration.userAgentField);
    }
}