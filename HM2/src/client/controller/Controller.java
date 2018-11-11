package client.controller;
import client.net.OutputHandler;
import client.net.ServerConnection;
import common.Message;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.CompletableFuture;

public class Controller {

    private ServerConnection serverConnection = new ServerConnection();


    public void establishConnection(OutputHandler outputHandler) {
        CompletableFuture.runAsync(() -> {
            try {
                serverConnection.connect(outputHandler);
            } catch (IOException ioe) {
                throw new UncheckedIOException(ioe);
            }
        });
    }

    public void disconnect() throws IOException {
        try {
            serverConnection.disconnect();
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    public void sendUpdate(Message msg) {
        CompletableFuture.runAsync(() -> {
            try {
                serverConnection.sendMsg(msg);
            } catch (IOException ioe) {
                throw new UncheckedIOException(ioe);
            }
        });
    }
}
