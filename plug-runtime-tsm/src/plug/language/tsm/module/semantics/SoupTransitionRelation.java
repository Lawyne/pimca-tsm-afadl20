package plug.language.tsm.module.semantics;

import plug.language.tsm.ast.BehaviorSoup;
import plug.runtime.core.Fanout;
import plug.runtime.core.defaults.DefaultTransitionRelation;

import java.util.*;
import java.util.stream.Stream;

public class SoupTransitionRelation extends DefaultTransitionRelation<Object, Action<Object>, Void> {
    BehaviorSoup<Object> soup;
    CompiledSoup<Object> compiledSoup;
    public SoupTransitionRelation(BehaviorSoup<Object> soup) {
        this.soup = soup;
        this.compiledSoup = CompiledSoup.compile(soup);
    }

    @Override
    public Set<Object> initialConfigurations() {
        return Collections.singleton(compiledSoup.state);
    }

    @Override
    public Collection<Action<Object>> fireableTransitionsFrom(Object source) {
        Stream<Action<Object>> enabled = compiledSoup.actions.stream()
                .filter(action -> action.isEnabled(source));
        List<Action<Object>> urgent = new ArrayList<>();
        List<Action<Object>> normal = new ArrayList<>();
        enabled.forEach(each ->  {if (each.isUrgent()) urgent.add(each); else normal.add(each); });

        if (urgent.isEmpty()) {
            return normal;
        }
        return urgent;
    }

    @Override
    public Collection<Fanout<Void, Object>> fireOneTransition(Object source, Action<Object> transition) {
        Object target = transition.execute(source);
        return Collections.singleton(new Fanout<>(null, target));
    }
}
