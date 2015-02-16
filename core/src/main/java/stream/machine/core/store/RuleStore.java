package stream.machine.core.store;

import stream.machine.core.model.Rule;

import java.util.UUID;

/**
 * Created by Stephane on 07/12/2014.
 */
public interface RuleStore extends Store {
    Rule create(final Rule rule);

    Rule update(final Rule rule);

    Rule read(final UUID ruleId);
}
