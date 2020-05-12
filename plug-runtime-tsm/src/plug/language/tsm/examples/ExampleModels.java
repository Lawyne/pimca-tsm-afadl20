package plug.language.tsm.examples;

import plug.language.tsm.examples.mutex.peterson.AliceBobPeterson;
import plug.language.tsm.examples.mutex.peterson.Configuration;
import plug.language.tsm.examples.nbits.NBits;
import plug.language.tsm.examples.nflags.NFlags;
import plug.language.tsm.ast.BehaviorSoup;

@SuppressWarnings("Duplicates")
public class ExampleModels {
    public static BehaviorSoup<Integer> counter(int max) {
        BehaviorSoup<Integer> soup = new BehaviorSoup<>(0);

        soup.add("reset", i -> i == max, i -> 0);
        soup.add("inc", i -> i < max, i -> i+1);

        return soup;
    }

    public static BehaviorSoup<int[]> counters(int size, int max) {
        BehaviorSoup<int[]> soup = new BehaviorSoup<>(new int[size]);

        for (int i = 0; i < size; i++) {
            final int idx = i;
            soup.add("reset["+idx+"]",
                    e -> e[idx] == max,
                    e -> { e[idx] = 0; return e; });
            soup.add("inc["+idx+"]",
                    e -> e[idx] < max,
                    e -> { e[idx]++; return e; });
        }
        return soup;
    }

    public static BehaviorSoup<Configuration> aliceBobPeterson() {
        return new AliceBobPeterson().model();
    }

    public static BehaviorSoup<plug.language.tsm.examples.nflags.Configuration> nFlags(int n) {
        return new NFlags().model(n);
    }

    public static BehaviorSoup<plug.language.tsm.examples.nbits.Configuration> nbits(int n) {
        return new NBits().model(n);
    }
}

