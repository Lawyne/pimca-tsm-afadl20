package plug.remote.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

public abstract class AbstractAPIHandler implements Runnable {
    Socket socket; //the client socket
    public BufferedInputStream inputStream;
    public BufferedOutputStream outputStream;

    public AbstractAPIHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            inputStream = new BufferedInputStream(socket.getInputStream());
            outputStream = new BufferedOutputStream(socket.getOutputStream());

            boolean alive = handleRequest();
            while (alive) {
                alive = handleRequest();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (socket != null && !socket.isClosed()) {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    boolean handleRequest() throws Exception {
        int status = inputStream.read();
        if (status == 1) {
            byte request = (byte) inputStream.read();
            switch (request) {
                case 1: // Initial Configurations
                    handleInitialConfigurations();
                    break;
                case 2: // Fireable transitions
                    handleFireableTransitions();
                    break;
                case 3: // Fire transition
                    handleFireOneTransition();
                    break;

                //atomic propositions
                case 4: // Register atomic propositions
                    handleRegisterAtomicPropositions();
                    break;
                case 5: // Atomic propositions valuations
                    handleAtomicPropositionValuation();
                    break;
                case 6: // Extended atomic propositions valuations
                    handleExtendedAtomicPropositionValuation();
                    break;

                //projections
                case 10: // Configuration items
                    handleConfigurationProjection();
                    break;
                case 11: // Fireable transition description
                    handleFireableProjection();
                    break;
                case 12:
                    handlePayloadProjection();
                    break;
            }
            return true;
        }
        return false;
    }

    abstract public void handleInitialConfigurations() throws IOException;

    abstract public void handleFireableTransitions() throws IOException;

    abstract public void handleFireOneTransition() throws IOException;

    abstract public void handleRegisterAtomicPropositions() throws Exception;

    abstract public void handleAtomicPropositionValuation() throws IOException;

    abstract public void handleExtendedAtomicPropositionValuation() throws IOException;

    abstract public void handleConfigurationProjection() throws IOException;

    abstract public void handleFireableProjection() throws IOException;

    abstract public void handlePayloadProjection() throws IOException;
}
