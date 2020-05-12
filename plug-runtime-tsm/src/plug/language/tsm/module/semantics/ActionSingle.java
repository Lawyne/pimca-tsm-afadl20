package plug.language.tsm.module.semantics;

import plug.language.tsm.ast.Behavior;

public class ActionSingle<E> extends Action<E> {
    public Behavior<E> action;
    public ActionSingle(Behavior<E> action) {
        this.action = action;
    }

    @Override
    public boolean isEnabled(E source) {
        return action.guard.test(source);
    }

    @Override
    public boolean isUrgent() {
        return action.urgent;
    }

    @Override
    public E execute(E source) {
        return action.action.apply(source);
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public String toString() {
        return (action.urgent ? "[u]" : "") + action.name;
    }
}
