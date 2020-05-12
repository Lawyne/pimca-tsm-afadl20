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
import java.util.Collections;
import java.util.stream.Collectors;

public class APIHandlerV1 extends AbstractAPIHandler {
    ByteArrayLanguageModule module;

    public APIHandlerV1(ByteArrayLanguageModule module, Socket socket) {
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

        //TODO: the future API should support <Payload, Target>*, now we have only Target* Payload
        Collection<byte[]> targets = result.stream().map(f -> f.target).collect(Collectors.toList());
        Marshaller.writeBuffers(targets, outputStream);

        byte[] payload = result.stream().findFirst().get().payload;
        Marshaller.writeBuffer(payload, outputStream);
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
        //TODO: the future API should be more generic here
        byte[] source = Unmarshaller.readBuffer(inputStream);

        TreeItem treeItem = module.getTreeProjector().projectConfiguration(source);

        //TODO: a future API should send only on tree item
        //TreeItemSerializer.writeTreeItem(treeItem, outputStream);
        TreeItemSerializer.writeTreeItems(Collections.singletonList(treeItem), outputStream);
    }

    @Override
    public void handleFireableProjection() throws IOException {
        //TODO: the future API should be more generic here
        byte[] fireable = Unmarshaller.readBuffer(inputStream);

        TreeItem treeItem = module.getTreeProjector().projectFireable(fireable);

        //TODO: a future API should send only on tree item
        //TreeItemSerializer.writeTreeItem(treeItem, outputStream);
        Marshaller.writeString(treeItem.name, outputStream);
    }

    @Override
    public void handlePayloadProjection() throws IOException {
        //TODO: the future API should expose this function
        byte[] fireable = Unmarshaller.readBuffer(inputStream);

        TreeItem treeItem = module.getTreeProjector().projectPayload(fireable);
        TreeItemSerializer.writeTreeItem(treeItem, outputStream);
    }
}
