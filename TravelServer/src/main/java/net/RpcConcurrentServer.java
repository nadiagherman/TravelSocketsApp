package net;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import service.TravelObserver;
import service.TravelServices;

import java.net.Socket;

public class RpcConcurrentServer extends AbsConcurrentServer {
    private static final Logger LOGGER = LogManager.getLogger(RpcConcurrentServer.class.getName());
    private TravelServices travelServices;

    public RpcConcurrentServer(int port, TravelServices travelServices) {
        super(port);
        this.travelServices= travelServices;
    }

    @Override
    protected Thread createWorker(Socket client) {
        LOGGER.info("creating TravelClientRpcWorker for client " + client);
        TravelClientRpcWorker worker=new TravelClientRpcWorker(travelServices, client);
        Thread tw=new Thread(worker);
        LOGGER.traceExit();
        return tw;
    }
}
