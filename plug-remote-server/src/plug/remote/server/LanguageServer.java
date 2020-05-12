package plug.remote.server;

import plug.language.tsm.examples.cta.*;
import plug.language.tsm.ast.BehaviorSoup;
import plug.language.tsm.module.SoupAtomEvaluator;
import plug.language.tsm.module.SoupMarshaller;
import plug.language.tsm.module.semantics.SoupTransitionRelation;
import plug.language.tsm.module.SoupTreeProjector;
import plug.remote.server.v0.APIHandlerV1;
import plug.runtime.core.LanguageModule;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class LanguageServer {
    int port;
    LanguageModule module;
    ServerSocket serverSocket;

    public LanguageServer(int port, LanguageModule module) {
        this.port = port;
        this.module = module;
    }

    public static void main(String args[]) {
        //BehaviorSoup soup = ExampleModels.counters(2, 2);
        //BehaviorSoup soup = ExampleModels.counter( 2);
        //BehaviorSoup soup = ExampleModels.aliceBobPeterson();
        //BehaviorSoup soup = ExampleModels.nbits(10);
    	BehaviorSoup soup = new ExMain().model();
        LanguageModule soupModule = new LanguageModule(
                new SoupTransitionRelation(soup),
                new SoupAtomEvaluator(),
                new SoupTreeProjector(),
                new SoupMarshaller(soup)
        );

        LanguageServer server = new LanguageServer(1234, soupModule);
        server.start();
    }

    void start() {
        try {
            serverSocket = new ServerSocket(port);

            while (true) {
                Socket socket = serverSocket.accept();
                Thread thread = new Thread(new APIHandlerV1(module, socket));
                thread.start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (serverSocket!= null && !serverSocket.isClosed()) {
                try {
                    serverSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
