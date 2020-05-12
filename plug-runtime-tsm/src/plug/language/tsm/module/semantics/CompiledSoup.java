package plug.language.tsm.module.semantics;

import plug.language.tsm.ast.Behavior;
import plug.language.tsm.ast.BehaviorSoup;
import plug.language.tsm.ast.Channel;
import plug.language.tsm.ast.Direction;
import plug.utils.CartesianProduct;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CompiledSoup<Env> {
    public Env state;

    public List<Action<Env>> actions = new ArrayList<>();

    public static CompiledSoup<Object> compile(BehaviorSoup<Object> soup) {
        CompiledSoup<Object> compiledSoup = new CompiledSoup<>();
        compiledSoup.state = soup.state;

        Map<Integer, List<Behavior<Object>>> chan2InputSchedule = new HashMap<>();
        Map<Integer, List<Behavior<Object>>> chan2OutputSchedule = new HashMap<>();

        for (Behavior<Object> beh : soup.behaviors) {
            Channel chan = beh.channel.orElse(null);
            if (chan == null) {
                compiledSoup.actions.add(new ActionSingle<>(beh));
            } else if (chan.direction == Direction.IN) {
                List<Behavior<Object>> inschedule = chan2InputSchedule.computeIfAbsent(chan.id, k -> new ArrayList<>());
                inschedule.add(beh);
            } else if (chan.direction == Direction.OUT) {
                List<Behavior<Object>> outschedule = chan2OutputSchedule.computeIfAbsent(chan.id, k -> new ArrayList<>());
                outschedule.add(beh);
            }
        }

        for (Map.Entry<Integer, List<Behavior<Object>>> entry  : chan2InputSchedule.entrySet()) {
            List<Behavior<Object>> inschedule = entry.getValue();
            List<Behavior<Object>> outschedule = chan2OutputSchedule.get(entry.getKey());

            if (inschedule == null || outschedule == null || inschedule.isEmpty() || outschedule.isEmpty()) {
                //one of the two is empty so no contribution to the soup
                continue;
            }
            //inputs and output present
            List<List<Behavior<Object>>> in = new ArrayList<>();
            in.add(inschedule);
            in.add(outschedule);
            CartesianProduct<Behavior<Object>> cp = new CartesianProduct<>();
            cp.cartesianProduct(
                    in,
                    (result) -> compiledSoup.actions.add(new ActionSync<>(result[0], result[1])),
                    Behavior[]::new);
        }

        return compiledSoup;
    }
}
