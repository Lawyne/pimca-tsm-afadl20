package plug.language.tsm.module;

import plug.language.tsm.module.semantics.Action;
import plug.runtime.core.TreeItem;
import plug.runtime.core.defaults.DefaultTreeProjector;

public class SoupTreeProjector extends DefaultTreeProjector<Object, Action<Object>, Void> {
    @Override
    public TreeItem projectConfiguration(Object configuration) {
        return Object2TreeItem.getTreeItem(configuration);
    }

    @Override
    public TreeItem projectFireable(Action<Object> fireable) {
        return new TreeItem(fireable.toString());
    }

    @Override
    public TreeItem projectPayload(Void payload) {
        return TreeItem.empty;
    }
}
