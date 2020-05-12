package plug.language.tsm.examples.nbits;

import plug.language.tsm.ast.Behavior;
import plug.language.tsm.ast.BehaviorSoup;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@SuppressWarnings("Duplicates")
public class NBits {

    List<Behavior<Configuration>> behaviors(int i) {
        List<Behavior<Configuration>> behaviors = new ArrayList<>();


        //final int f[] = new int[] {170, 377, 399, 394, 554, 1004, 208, 350, 755, 96, 305, 72, 699, 516, 675, 716, 321, 491, 777, 601};

        final int f[] = null;

        Behavior<Configuration> t2f =
                new Behavior<>(
                        i + "_t2f",
                        (c) -> ((c.bits >> i) & 1) > 0 && ( (f != null) ? (((f[(int)c.bits%f.length] >> i) & 1) > 0) : true),
                        (c) -> {
                            c.bits &= ~ (1 << i);
                            return c;
                        });
        Behavior<Configuration> f2t =
                new Behavior<>(
                        i + "_f2t",
                        (c) -> ((c.bits >> i) & 1) == 0 && ( (f != null) ? (((f[(int)c.bits%f.length] >> i) & 1) > 0) : true),
                        (c) -> {
                            c.bits |= 1 << i;
                            return c;
                        });
        return Arrays.asList(t2f, f2t);
    }
    public BehaviorSoup<Configuration> model(int nbits) {

        BehaviorSoup<Configuration> soup = new BehaviorSoup<>(new Configuration());
        for (int i = 0; i<nbits; i++) {
            soup.behaviors.addAll(behaviors(i));
        }
        return soup;
    }

    public static void main(String[] args) {
        Random r = new Random();

        for (int i =0 ;i < 20; i++) {
            System.out.print(r.nextInt(1024) + ", ");
        }
    }
}




