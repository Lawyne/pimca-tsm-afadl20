package plug.language.tsm.ast;

public class Channel {
    public String name;
    public int id;
    public Direction direction;

    public Channel(String name, Direction direction) {
        this.name = name.intern();
        this.id = ((Object)name).hashCode();
        this.direction = direction;
    }

    public static Channel in(String name) {
        return new Channel(name, Direction.IN);
    }

    public static Channel out(String name) {
        return new Channel(name, Direction.OUT);
    }

    @Override
    public String toString() {
        return "(" + name + (direction == Direction.OUT ? "!" : "?") + ")";
    }
}
