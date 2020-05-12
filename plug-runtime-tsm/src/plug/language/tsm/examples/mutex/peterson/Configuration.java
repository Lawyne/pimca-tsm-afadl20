package plug.language.tsm.examples.mutex.peterson;

public class Configuration {
    public State state[] = new State[]{State.IDLE, State.IDLE};
    public Actor turn = Actor.Alice;
    public boolean flags[] = new boolean[2];
}
