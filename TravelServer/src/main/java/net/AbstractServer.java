package net;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public abstract class AbstractServer {
    private int port;
    private ServerSocket server = null;
    private static final Logger LOGGER = LogManager.getLogger(AbstractServer.class.getName());


    public AbstractServer(int port) {
        LOGGER.traceEntry("setting server port " + port);
        this.port = port;
    }

    public void start() {
        try {
            LOGGER.info("starting server");
            server = new ServerSocket(port);
            while (true) {
                LOGGER.info("Waiting for clients");
                Socket client = server.accept();
                LOGGER.info("Client connected");
                processRequest(client);
            }
        } catch (IOException e) {
            LOGGER.warn("starting server failed");
            throw new ServerException(e);
        } finally {
            stop();
        }
    }

    protected abstract void processRequest(Socket client);

    public void stop() throws ServerException {
        try {
            LOGGER.info("stoping server");
            server.close();
        } catch (IOException e) {
            LOGGER.warn("closing server failed " + e);
            throw new ServerException("Closing server error " + e);
        }
        LOGGER.traceExit();
    }
}
