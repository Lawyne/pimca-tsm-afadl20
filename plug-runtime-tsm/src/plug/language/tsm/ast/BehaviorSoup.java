package plug.language.tsm.ast;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

public class BehaviorSoup<Env> {
    public Env state;
    public List<Behavior<Env>> behaviors = new ArrayList<>();

    public BehaviorSoup(Env initialState) {
        this.state = initialState;
    }

    public void add(Behavior<Env> behavior) {
        behaviors.add(behavior);
    }

    public void add(String name, Predicate<Env> guard, Function<Env, Env> action) {
        behaviors.add(new Behavior<>(name, guard, action));
    }
}
