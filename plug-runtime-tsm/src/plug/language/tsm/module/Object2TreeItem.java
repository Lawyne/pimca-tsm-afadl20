package plug.language.tsm.module;

import plug.runtime.core.TreeItem;
import plug.utils.ObjectGraphExploration;
import plug.utils.Pair;

import java.lang.reflect.Field;
import java.util.ArrayDeque;
import java.util.Arrays;

@SuppressWarnings("Duplicates")
public class Object2TreeItem {

    Visitor visitor = new Visitor();
    ObjectGraphExploration explorer = new ObjectGraphExploration(visitor);

    public static TreeItem getTreeItem(Object o) {
        Object2TreeItem o2t = new Object2TreeItem();
        o2t.explorer.explore(o, o.getClass());
        return o2t.visitor.stack.pop().children.stream().findAny().get();
    }

    class Visitor implements ObjectGraphExploration.IVisitor {
        ArrayDeque<TreeItem> stack = new ArrayDeque<>();

        public Visitor() {
            this.stack.push(new TreeItem("root"));
        }
        @Override
        public boolean startObject(Object object, Class<?> clazz) {
            if (clazz.isEnum()) {
                TreeItem node = new TreeItem(object.toString());
                stack.push(node);
                return true;
            }
            if (object instanceof String) {
                TreeItem node = new TreeItem(object.toString());
                stack.push(node);
                return false;
            }
            TreeItem node = new TreeItem(clazz.getCanonicalName());
            stack.push(node);
            return true;
        }

        @Override
        public boolean endObject(Object object, Class<?> clazz) {
            TreeItem item = stack.pop();
            TreeItem parent = stack.peek();
            parent.children.add(item);
            return false;
        }

        @Override
        public boolean startPrimitive(Object object, Class<?> clazz) {
            stack.push(new TreeItem(object.toString()));
            return true;
        }

        @Override
        public boolean endPrimitive(Object object, Class<?> clazz) {
            TreeItem item = stack.pop();
            TreeItem parent = stack.peek();
            parent.children.add(item);
            return false;
        }

        @Override
        public boolean startArray(Object object, Class<?> clazz) {
            if (object instanceof boolean[]) {
                TreeItem node = new TreeItem(Arrays.toString((boolean[]) object));
                stack.push(node);
                return false;
            } else if (object instanceof Object[]) {
                TreeItem node = new TreeItem(Arrays.toString((Object[]) object));
                stack.push(node);
                return true;
            }
            TreeItem node = new TreeItem(object.getClass().getSimpleName());
            stack.push(node);
            return true;
        }

        @Override
        public boolean endArray(Object object, Class<?> clazz) {
            TreeItem item = stack.pop();
            TreeItem parent = stack.peek();
            parent.children.add(item);
            return false;
        }

        @Override
        public boolean startField(Field field, Object object, Class<?> clazz) {
            try {
                Object o = field.get(object);
                TreeItem treeItem;
                if (clazz.isEnum() || clazz.isPrimitive() || o instanceof String) {
                    treeItem = new TreeItem(field.getName() + " = " + o.toString());
                    stack.push(treeItem);
                    return false;
                }
                treeItem = new TreeItem(field.getName() + " =  [" + clazz.getSimpleName() +"]");
                stack.push(treeItem);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        public boolean endField(Field field, Object object, Class<?> clazz) {
            TreeItem item = stack.pop();
            TreeItem parent = stack.peek();
            parent.children.add(item);
            return false;
        }
    }

    public static void main(String args[]) {
        Object2TreeItem o2t = new Object2TreeItem();
        byte[][] bytes = new byte[3][2];
        Pair<Integer, Integer> pair = new Pair<>(2, 3);
        o2t.explorer.explore(bytes, bytes.getClass());
        System.out.println(o2t.visitor.stack.peek().children);
    }
}

