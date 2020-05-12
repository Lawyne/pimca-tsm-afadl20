package plug.language.tsm.module.semantics;

import plug.language.tsm.ast.Behavior;
import plug.language.tsm.ast.Channel;

public class ActionSync<E> extends Action<E> {
    int channelID;
    public Behavior<E> input;
    public Behavior<E> output;

    public ActionSync(Behavior<E> input, Behavior<E> output) {
        this.input = input;
        this.output = output;
        Channel in = input.channel.orElse(null);
        Channel out = output.channel.orElse(null);

        if (in == null || out == null || in.id != out.id) {
            throw new RuntimeException("Channel ID does not match");
        }
        this.channelID = in.id;
    }

    @Override
    public boolean isEnabled(E source) {
        return input.guard.test(source) && output.guard.test(source);
    }

    @Override
    public boolean isUrgent() {
        return input.urgent || output.urgent;
    }

    @Override
    public E execute(E source) {
        E rho = input.action.apply(source);
        return output.action.apply(rho);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public String toString() {
        return (input.urgent ? "[u] " : "") + input.name +
                " !" + input.channel.get().name + "? " +
                (output.urgent ? "[u] " : "") + output.name
                ;
    }
}
