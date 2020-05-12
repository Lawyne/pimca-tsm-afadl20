package plug.language.tsm.module;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import plug.language.tsm.ast.Behavior;
import plug.language.tsm.ast.BehaviorSoup;
import plug.language.tsm.module.semantics.Action;
import plug.runtime.core.defaults.DefaultMarshaller;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

@SuppressWarnings("Duplicates")
public class SoupMarshaller<E> extends DefaultMarshaller<E, Action<E>, Void> {
    Kryo kryo = new Kryo();
    BehaviorSoup<E> soup;
    Map<Action<E>, Integer> behavior2int = new IdentityHashMap<>();
    Map<Integer, Action<E>> int2behavior = new HashMap<>();

    public SoupMarshaller(BehaviorSoup<E> soup) {
        this.soup = soup;
//        kryo.register(soup.state.getClass());
//        kryo.register(Behavior.class);
//        kryo.register(Void.class);
        kryo.setRegistrationRequired(false);
        kryo.setReferences(true);
    }

    @Override
    public byte[] serializeConfiguration(E configuration) {
        return bytes(configuration);
    }

    @Override
    public byte[] serializeFireable(Action<E> fireable) {
        Integer id = behavior2int.get(fireable);
        if (id == null) {
            id = behavior2int.size();
            behavior2int.put(fireable, id);
            int2behavior.put(id, fireable);
        }
        return bytes(id);
    }

    @Override
    public byte[] serializePayload(Void payload) {
        return bytes(payload);
    }

    byte[] bytes(Object o) {
        if (o == null) return new byte[0];
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Output os = new Output(baos);
        kryo.writeObject(os, o);
        os.flush();
        byte[] buffer = baos.toByteArray();
        os.close();
        return buffer;
    }

    @SuppressWarnings("unchecked")
    @Override
    public E deserializeConfiguration(byte[] buffer) {
        return (E)object(buffer, soup.state.getClass());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Action<E> deserializeFireable(byte[] buffer) {
        int id = object(buffer, Integer.class);
        Action<E> fireable = int2behavior.get(id);
        if (fireable == null) {
            throw new RuntimeException("[SoupMarshaller] No behavior registered for ID="+id);
        }
        return fireable;
    }

    @Override
    public Void deserializePayload(byte[] buffer) {
        return object(buffer, Void.class);
    }

    <T> T object(byte[] buffer, Class<T> clazz) {
        if (buffer.length == 0) return null;
        Input is = new Input(buffer);
        T object = kryo.readObject(is, clazz);
        is.close();
        return object;
    }
}
