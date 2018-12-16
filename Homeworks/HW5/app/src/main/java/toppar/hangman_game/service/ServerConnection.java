package toppar.hangman_game.service;

import android.nfc.Tag;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import toppar.hangman_game.common.Message;


public class ServerConnection {
    private Socket socket;
    private String connectionStatus;
    private static final int TIMEOUT_HALF_MINUTE = 30000;
    private ObjectOutputStream toServer = null;
    private ObjectInputStream fromServer = null;
    private Handler handler;

    public String getConnectionStatus() {
        return connectionStatus;
    }
    public Socket getSocket() {
        return socket;
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
            socket = new Socket();
            socket.connect(new InetSocketAddress(enteredAddress, enteredPort), TIMEOUT_HALF_MINUTE);
            toServer = new ObjectOutputStream((socket.getOutputStream()));
            toServer.reset();
            toServer.flush();
            fromServer = new ObjectInputStream(socket.getInputStream());
            connectionStatus = "connected";
        } catch (UnknownHostException e) {
            socket = null;
            connectionStatus = "Host: " + enteredAddress + " is unknown or not available in the network.";
        } catch (IOException e) {
            socket = null;
            connectionStatus = "Couldn't get I/O for the connection to: " + enteredAddress+ ".";
        }
    }
    public void writeToServer(final Message msg) {

        new Thread(new Runnable() {
            @Override
            public void run(){
                try {
                    Log.e(msg.getBody(),"sending username");
                    System.out.println(toServer);
                    System.out.println(Message.class);
                    toServer.writeObject(msg);
                    toServer.reset();
                    toServer.flush();
                    Log.e(msg.getBody(),"done sending username");
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
}