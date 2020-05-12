package plug.language.tsm.ast;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

public class Behavior<Env> {
    public String name;
    public Optional<Channel> channel;
    public boolean urgent;
    public Predicate<Env> guard;
    public Function<Env, Env> action;

    public Behavior(String name, Predicate<Env> guard, Function<Env, Env> action) {
        this(name, guard, action, Optional.empty(), false);
    }

    public Behavior(String name, Predicate<Env> guard, Function<Env, Env> action, boolean isUrgent) {
        this(name, guard, action, Optional.empty(), isUrgent);
    }

    public Behavior(String name, Predicate<Env> guard, Function<Env, Env> action, Channel channel) {
        this(name, guard, action, Optional.of(channel), false);
    }
    
    public Behavior(String name, Predicate<Env> guard, Function<Env, Env> action, Channel channel, boolean isUrgent) {
        this(name, guard, action, Optional.of(channel), isUrgent);
    }
    
    
    public Behavior(String name, Predicate<Env> guard, Function<Env, Env> action, Optional<Channel> channel, boolean isUrgent) {
        this.name = name;
        this.guard = guard;
        this.action = action;
        this.urgent = isUrgent;
        this.channel = channel;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Behavior) {
            Behavior other = (Behavior)obj;
            return Objects.equals(name, other.name)
                    && Objects.equals(guard, other.guard)
                    && Objects.equals(action, other.action);
        }
        return false;
    }
}
