package plug.remote.server;

import plug.runtime.core.TreeItem;
import plug.utils.marshaling.Marshaller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class TreeItemSerializer {
    public static void writeTreeItem(TreeItem item, OutputStream stream) throws IOException {
        Marshaller.writeString(item.type, stream);
        Marshaller.writeString(item.name, stream);
        Marshaller.writeString(item.icon, stream);

        writeTreeItems(item.children, stream);
    }

    public static void writeTreeItems(List<TreeItem> items, OutputStream stream) throws IOException {
        if (items == null) {
            Marshaller.writeInt(0, stream);
            return;
        }
        Marshaller.writeInt(items.size(), stream);
        for (TreeItem item : items) {
            writeTreeItem(item, stream);
        }
    }
}
