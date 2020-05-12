package plug.language.tsm.examples.cta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import plug.language.tsm.ast.Behavior;
import plug.language.tsm.ast.Channel;

public class Pump extends AbstractEntity{

	@Override
	public List<Behavior<Configuration>> behaviors() {
		List<Behavior<Configuration>> behaviors = new ArrayList<>();

        Behavior<Configuration> o2o =
                new Behavior<>(
                         "o2o",
                        (c) -> c.pIsOpen,
                        (c) -> c, 
                        Channel.out("ManualPump")
                        ,false);
        Behavior<Configuration> o2c =
                new Behavior<>(
                        "o2c",
                        (c) ->    c.pIsOpen,
                        (c) -> {
                            c.pIsOpen = false;
                            return c;
                        },
                        Channel.in("PumpSignal")
                        );
        Behavior<Configuration> c2o =
                new Behavior<>(
                         "c2o",
                        (c) -> !c.pIsOpen,
                        (c) -> {
                            c.pIsOpen = true;                            
                            return c;
                        },
                        Channel.in("PumpSignal")
                        );
        return Arrays.asList(o2o, o2c, c2o);
	}
	
}
