package toppar.hangman_game.service;

import android.os.Handler;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import toppar.hangman_game.common.Message;
import toppar.hangman_game.common.MessageDTO;
import static toppar.hangman_game.common.MsgType.QUIT;


public class ServerConnection {
    private Socket socket;
    private String connectionStatus;
    private static final int TIMEOUT_HALF_MINUTE = 30000;
    private ObjectOutputStream toServer = null;
    private ObjectInputStream fromServer = null;
    private Handler handler;
    private boolean connected = false;

    public String getConnectionStatus() {
        return connectionStatus;
    }
    public Socket getSocket() {
        return socket;
    }
    public void startListener(OutputHandler outputHandler){
        Listener listener = new Listener(outputHandler, this);
        listener.start();
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
            connected = true;
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
                    toServer.writeObject(msg);
                    toServer.reset();
                    toServer.flush();
                    if(msg.getType() == QUIT){
                        socket = null;
                        connected = false;
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }
    @SuppressWarnings("InfiniteLoopStatement")
    private class Listener extends Thread{
        private final OutputHandler outputHandler;
        private ServerConnection serverConnection;

        public Listener(OutputHandler outputHandler, ServerConnection serverConnection){
            this.outputHandler = outputHandler;
            this.serverConnection = serverConnection;
        }

        public void run(){
            try{
                for(;;) {
                    outputHandler.handleMessage((MessageDTO)fromServer.readObject());
                }
            }catch (Throwable connectionFailure){
                if(connected){
                    System.err.println("Server connection failed");
                }
            }
        }
    }
}