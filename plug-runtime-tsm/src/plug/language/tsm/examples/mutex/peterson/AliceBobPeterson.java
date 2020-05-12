package plug.language.tsm.examples.mutex.peterson;

import plug.language.tsm.ast.Behavior;
import plug.language.tsm.ast.BehaviorSoup;
import plug.language.tsm.ast.Channel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("Duplicates")
public class AliceBobPeterson {
        List<Behavior<Configuration>> behaviors(Actor actor) {
            List<Behavior<Configuration>> behaviors = new ArrayList<>();

            Behavior<Configuration> i2w =
                    new Behavior<>(
                            actor.name() + "_wantsIn",
                            (c) -> c.state[actor.ordinal()] == State.IDLE,
                            (c) -> {
                                int id = actor.ordinal();
                                c.state[id] = State.WAITING;
                                c.flags[id] = true;
                                c.turn = Actor.values()[1-id];
                                return c;
                            }, true);
            Behavior<Configuration> w2c =
                    new Behavior<>(
                            actor.name() + "_getsIn",
                            (c) ->     c.state[actor.ordinal()] == State.WAITING
                                    && (c.turn == actor
                                    || !c.flags[1-actor.ordinal()]),
                            (c) -> {
                                c.state[actor.ordinal()] = State.CRITICAL;
                                return c;
                            });
            Behavior<Configuration> c2i =
                    new Behavior<>(
                            actor.name() + "_getsOut",
                            (c) -> c.state[actor.ordinal()] == State.CRITICAL,
                            (c) -> {
                                int id = actor.ordinal();
                                c.state[id] = State.IDLE;
                                c.flags[id] = false;
                                return c;
                            });
            return Arrays.asList(i2w, w2c, c2i);
        }
        public BehaviorSoup<Configuration> model() {
            BehaviorSoup<Configuration> soup = new BehaviorSoup<>(new Configuration());

            List<Behavior<Configuration>> alices = behaviors(Actor.Alice);
            List<Behavior<Configuration>> bobs = behaviors(Actor.Bob);

            alices.get(0).channel = Optional.of(Channel.out("chan"));
            bobs.get(0).channel = Optional.of(Channel.in("chan"));

            soup.behaviors.addAll(alices);
            soup.behaviors.addAll(bobs);
            return soup;
        }

}
