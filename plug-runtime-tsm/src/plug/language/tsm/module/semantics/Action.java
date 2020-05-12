package plug.language.tsm.module.semantics;

public abstract class Action<E> {
    public abstract boolean isEnabled(E source);
    public abstract boolean isUrgent();
    public abstract E execute(E source);

    public abstract boolean isSingle();
}
