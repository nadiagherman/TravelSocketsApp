package service;


import net.Request;
import net.Response;
import net.ResponseType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public abstract class BaseServiceProxy {
    private static final Logger LOGGER = LogManager.getLogger(BaseServiceProxy.class.getName());

    protected Socket connection;
    private String host;
    private int port;
    private ObjectOutputStream output;
    private ObjectInputStream input;
    private boolean finished;
    private BlockingQueue<Response> responses = new ArrayBlockingQueue<Response>(10);

    public BaseServiceProxy(String host, int port) {
        LOGGER.traceEntry("setting server host = " + host + " port = " + port);
        this.host = host;
        this.port = port;
    }

    protected void ensureConnected() {
        LOGGER.traceEntry("ensuring connection to server");
        try {
            if (connection != null) {
                return;
            }
            LOGGER.info("creating connection");
            connection = new Socket(host, port);
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            finished = false;
            startReader();
        } catch (IOException e) {
            LOGGER.warn("connection failed");
            e.printStackTrace();
            throw new ServiceException(e);
        }
    }


    protected void closeConnection() {
        finished = true;
        if (connection == null) {
            return;
        }
        Socket tempConnection = connection;
        connection = null;
        LOGGER.info("closing connection");
        try {
            input.close();
            output.close();
            tempConnection.close();
            LOGGER.info("connection closed");
        } catch (IOException e) {
            LOGGER.warn("closing connection failed");
            e.printStackTrace();
        }
    }

    protected void sendRequest(Request request) {
        LOGGER.info("sending request to server" + request);
        try {
            output.writeObject(request);
            output.flush();
        } catch (IOException e) {
            LOGGER.warn("sending request failed");
            throw new ServiceException("Error sending object " + e);
        }
        LOGGER.traceExit("request sent");
    }

    protected Response readResponse() {
        LOGGER.traceEntry("reading response from queue");
        Response response = null;
        try {
            response = responses.take();
        } catch (InterruptedException e) {
            LOGGER.warn("reading response failed");
            e.printStackTrace();
        }
        LOGGER.info("response read " + response);
        return response;
    }

    private void startReader() {
        LOGGER.traceEntry("starting reader thread");
        Thread tw = new Thread(new ReaderThread());
        tw.start();
    }

    private class ReaderThread implements Runnable {

        public void run() {
            LOGGER.traceEntry("running reader thread");
            while (!finished) {
                try {
                    Response response = (Response) input.readObject();
                    LOGGER.info("response received from server " + response);
                    if (response.type() == ResponseType.TICKET_ADDED) {
                        handleTicketAdded(response);
                        continue;
                    }
                    try {
                        responses.put(response);
                    } catch (InterruptedException e) {
                        LOGGER.warn("add response to queue failed");
                        e.printStackTrace();
                    }
                } catch (IOException | ClassNotFoundException e) {
                    closeConnection();
                    break;
                } catch (Exception ex) {
                    if (!finished) {
                        ex.printStackTrace();
                        closeConnection();
                        break;
                    }
                }
            }
        }
    }


    protected abstract void handleTicketAdded(Response response);
}
