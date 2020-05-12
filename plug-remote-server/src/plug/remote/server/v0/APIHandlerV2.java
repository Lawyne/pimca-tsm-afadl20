package plug.remote.server.v0;

import plug.remote.server.AbstractAPIHandler;
import plug.remote.server.TreeItemSerializer;
import plug.runtime.core.Fanout;
import plug.runtime.core.LanguageModule;
import plug.runtime.core.TreeItem;
import plug.utils.marshaling.Marshaller;
import plug.utils.marshaling.Unmarshaller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Collection;

public class APIHandlerV2 extends AbstractAPIHandler {
    LanguageModule module;

    public APIHandlerV2(LanguageModule module, Socket socket) {
        super(socket);
        this.module = module;
    }

    @Override
    public void handleInitialConfigurations() throws IOException {
        @SuppressWarnings("unchecked")
        Collection<Object> configurations = module.getTransitionRelation().initialConfigurations();
        sendConfigurations(configurations);
    }

    @Override
    public void handleFireableTransitions() throws IOException {
        Object source = receiveConfiguration();
        @SuppressWarnings("unchecked")
        Collection<Object> fireables = module.getTransitionRelation().fireableTransitionsFrom(source);
        sendFireables(fireables);
    }

    @Override
    public void handleFireOneTransition() throws IOException {
        Object source = receiveConfiguration();
        Object fireable = receiveFireable();
        @SuppressWarnings("unchecked")
        Collection<Fanout> result = module.getTransitionRelation().fireOneTransition(source, fireable);

        Marshaller.writeInt(result.size(), outputStream);
        for (Fanout fanout : result) {
            sendPayload(fanout.payload);
            sendConfiguration(fanout.target);
        }
    }

    @Override
    public void handleRegisterAtomicPropositions() throws Exception {
        String[] propositions = Unmarshaller.readStringArray(inputStream);
        int[] indices = module.getAtomEvaluator().registerAtomicPropositions(propositions);
        Marshaller.writeIntArray(indices, outputStream);
    }

    @Override
    public void handleAtomicPropositionValuation() throws IOException {
        Object source = receiveConfiguration();
        @SuppressWarnings("unchecked")
        boolean[] result = module.getAtomEvaluator().getAtomicPropositionValuations(source);
        Marshaller.writeBooleanArray(result, outputStream);
    }

    @Override
    public void handleExtendedAtomicPropositionValuation() throws IOException {
        Object source = receiveConfiguration();
        Object fireable = receiveFireable();
        Object payload = receivePayload();
        Object target = receiveConfiguration();

        @SuppressWarnings("unchecked")
        boolean[] result = module.getAtomEvaluator().getAtomicPropositionValuations(source, fireable, payload, target);
        Marshaller.writeBooleanArray(result, outputStream);
    }

    @Override
    public void handleConfigurationProjection() throws IOException {
        Object source = receiveConfiguration();

        @SuppressWarnings("unchecked")
        TreeItem treeItem = module.getTreeProjector().projectConfiguration(source);

        TreeItemSerializer.writeTreeItem(treeItem, outputStream);
    }

    @Override
    public void handleFireableProjection() throws IOException {
        Object fireable = receiveFireable();

        @SuppressWarnings("unchecked")
        TreeItem treeItem = module.getTreeProjector().projectFireable(fireable);

        TreeItemSerializer.writeTreeItem(treeItem, outputStream);
    }

    @Override
    public void handlePayloadProjection() throws IOException {
        Object fireable = receivePayload();

        @SuppressWarnings("unchecked")
        TreeItem treeItem = module.getTreeProjector().projectPayload(fireable);
        TreeItemSerializer.writeTreeItem(treeItem, outputStream);
    }

    Object receiveConfiguration() throws IOException {
        int size = Unmarshaller.readInt(inputStream);
        return module.getMarshaller().deserializeConfiguration(Unmarshaller.readData(size, inputStream));
    }

    void sendConfiguration(Object configuration) throws IOException {
        @SuppressWarnings("unchecked")
        byte[] buffer = module.getMarshaller().serializeConfiguration(configuration);
        Marshaller.writeInt(buffer.length, outputStream);
        Marshaller.write(buffer, outputStream);
    }

    void sendConfigurations(Collection<Object> configurations) throws IOException {
        Marshaller.writeInt(configurations.size(), outputStream);
        for (Object o : configurations) {
            sendConfiguration(o);
        }
    }

    Object receiveFireable() throws IOException {
        int size = Unmarshaller.readInt(inputStream);
        return module.getMarshaller().deserializeFireable(Unmarshaller.readData(size, inputStream));
    }

    void sendFireable(Object fireable) throws IOException {
        @SuppressWarnings("unchecked")
        byte[] buffer = module.getMarshaller().serializeFireable(fireable);
        Marshaller.writeInt(buffer.length, outputStream);
        Marshaller.write(buffer, outputStream);
    }

    void sendFireables(Collection<Object> fireables) throws IOException {
        Marshaller.writeInt(fireables.size(), outputStream);
        for (Object o : fireables) {
            sendFireable(o);
        }
    }

    Object receivePayload() throws IOException {
        int size = Unmarshaller.readInt(inputStream);
        return module.getMarshaller().deserializePayload(Unmarshaller.readData(size, inputStream));
    }

    void sendPayload(Object payload) throws IOException {
        @SuppressWarnings("unchecked")
        byte[] buffer = module.getMarshaller().serializePayload(payload);
        Marshaller.writeInt(buffer.length, outputStream);
        Marshaller.write(buffer, outputStream);
    }

}