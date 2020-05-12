package plug.language.tsm.examples.cta;

import java.util.List;

import plug.language.tsm.ast.Behavior;

public abstract class AbstractEntity {
	public abstract List<Behavior<Configuration>> behaviors();
}
