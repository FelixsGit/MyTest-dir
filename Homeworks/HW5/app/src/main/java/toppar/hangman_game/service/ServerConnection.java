package toppar.hangman_game.service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

public class ServerConnection {
    private Socket clientSocket = null;
    private String connectionStatus;
    private static final int TIMEOUT_HALF_HOUR = 1800000;
    private static final int TIMEOUT_HALF_MINUTE = 30000;
    private ObjectOutputStream toServer;
    private ObjectInputStream fromServer;

    public String getConnectionStatus() {
        return connectionStatus;
    }
    public Socket getClientSocket() {
        return clientSocket;
    }
    public void connect(String enteredAddress, String enteredPortString) {
        try {
            int enteredPort;
            try {
                enteredPort = Integer.parseInt(enteredPortString);
            } catch (NumberFormatException ex) {
                connectionStatus = "Unable to connect!\nPort is not numeric value!";
                return;
            }
            clientSocket = new Socket();
            clientSocket.connect(new InetSocketAddress(enteredAddress, enteredPort), TIMEOUT_HALF_MINUTE);
            clientSocket.setSoTimeout(TIMEOUT_HALF_HOUR);
            toServer = new ObjectOutputStream(clientSocket.getOutputStream());
            fromServer = new ObjectInputStream(clientSocket.getInputStream());

            connectionStatus = "connected";
        } catch (UnknownHostException e) {
            clientSocket = null;
            connectionStatus = "Host: " + enteredAddress + " is unknown or not available in the network.";
        } catch (IOException e) {
            clientSocket = null;
            connectionStatus = "Couldn't get I/O for the connection to: " + enteredAddress+ ".";
        }
    }
}