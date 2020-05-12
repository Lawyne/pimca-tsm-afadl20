package plug.language.tsm.examples.nflags;

import plug.language.tsm.ast.Behavior;
import plug.language.tsm.ast.BehaviorSoup;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("Duplicates")
public class NFlags {

    List<Behavior<Configuration>> behaviors(int i) {
        List<Behavior<Configuration>> behaviors = new ArrayList<>();

        Behavior<Configuration> t2f =
                new Behavior<>(
                        i + "_t2f",
                        (c) -> c.flags[i],
                        (c) -> {
                            c.flags[i] = false;
                            return c;
                        });
        Behavior<Configuration> f2t =
                new Behavior<>(
                        i + "_f2t",
                        (c) -> !c.flags[i],
                        (c) -> {
                            c.flags[i] = true;
                            return c;
                        });
        return Arrays.asList(t2f, f2t);
    }
    public BehaviorSoup<Configuration> model(int nb_bits) {

        BehaviorSoup<Configuration> soup = new BehaviorSoup<>(new Configuration(nb_bits));
        for (int i = 0; i<nb_bits; i++) {
            soup.behaviors.addAll(behaviors(i));
        }
        return soup;
    }

}




