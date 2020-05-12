package plug.language.tsm.examples.nflags;

public class Configuration {
    public boolean flags[];

    public Configuration() {
        flags = new boolean[0];
    }
    public Configuration(int n) {
        flags = new boolean[n];
    }
}
