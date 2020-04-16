package net;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.Socket;

public abstract class AbsConcurrentServer extends AbstractServer {
    private static final Logger LOGGER = LogManager.getLogger(AbsConcurrentServer.class.getName());

    public AbsConcurrentServer(int port) {
        super(port);
    }

    protected void processRequest(Socket client) {
        LOGGER.info("processing request");
        Thread tw = createWorker(client);
        LOGGER.info("starting processing request thread");
        tw.start();
    }

    protected abstract Thread createWorker(Socket client);


}

