package plug.remote.server.v1;

import plug.core.operators.ByteArrayLanguageModule;
import plug.remote.server.AbstractAPIHandler;
import plug.remote.server.TreeItemSerializer;
import plug.runtime.core.Fanout;
import plug.runtime.core.TreeItem;
import plug.utils.marshaling.Marshaller;
import plug.utils.marshaling.Unmarshaller;

import java.io.IOException;
import java.net.Socket;
import java.util.Collection;

public class APIHandlerV2 extends AbstractAPIHandler {
    ByteArrayLanguageModule module;

    public APIHandlerV2(ByteArrayLanguageModule module, Socket socket) {
        super(socket);
        this.module = module;
    }

    @Override
    public void handleInitialConfigurations() throws IOException {
        Collection<byte[]> configurations = module.getTransitionRelation().initialConfigurations();
        Marshaller.writeBuffers(configurations, outputStream);
    }

    @Override
    public void handleFireableTransitions() throws IOException {
        byte[] source = Unmarshaller.readBuffer(inputStream);

        Collection<byte[]> fireables = module.getTransitionRelation().fireableTransitionsFrom(source);
        Marshaller.writeBuffers(fireables, outputStream);
    }

    @Override
    public void handleFireOneTransition() throws IOException {
        byte[] source = Unmarshaller.readBuffer(inputStream);
        byte[] fireable = Unmarshaller.readBuffer(inputStream);

        Collection<Fanout<byte[], byte[]>> result = module.getTransitionRelation().fireOneTransition(source, fireable);

        Marshaller.writeInt(result.size(), outputStream);
        for (Fanout<byte[], byte[]> fanout : result) {
            Marshaller.writeBuffer(fanout.payload, outputStream);
            Marshaller.writeBuffer(fanout.target, outputStream);
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
        byte[] source = Unmarshaller.readBuffer(inputStream);

        boolean[] result = module.getAtomEvaluator().getAtomicPropositionValuations(source);
        Marshaller.writeBooleanArray(result, outputStream);
    }

    @Override
    public void handleExtendedAtomicPropositionValuation() throws IOException {
        byte[] source = Unmarshaller.readBuffer(inputStream);
        byte[] fireable = Unmarshaller.readBuffer(inputStream);
        byte[] payload = Unmarshaller.readBuffer(inputStream);
        byte[] target = Unmarshaller.readBuffer(inputStream);

        boolean[] result = module.getAtomEvaluator().getAtomicPropositionValuations(source, fireable, payload, target);
        Marshaller.writeBooleanArray(result, outputStream);
    }

    @Override
    public void handleConfigurationProjection() throws IOException {
        byte[] source = Unmarshaller.readBuffer(inputStream);

        TreeItem treeItem = module.getTreeProjector().projectConfiguration(source);

        TreeItemSerializer.writeTreeItem(treeItem, outputStream);
    }

    @Override
    public void handleFireableProjection() throws IOException {
        byte[] fireable = Unmarshaller.readBuffer(inputStream);

        TreeItem treeItem = module.getTreeProjector().projectFireable(fireable);

        TreeItemSerializer.writeTreeItem(treeItem, outputStream);
    }

    @Override
    public void handlePayloadProjection() throws IOException {
        byte[] fireable = Unmarshaller.readBuffer(inputStream);

        TreeItem treeItem = module.getTreeProjector().projectPayload(fireable);
        TreeItemSerializer.writeTreeItem(treeItem, outputStream);
    }


}