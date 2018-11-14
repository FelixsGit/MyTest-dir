package client.net;

import common.Message;
import common.MessageDTO;
import common.MessageException;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

import static common.MsgType.QUIT;

public class ServerConnection {
    private static final int TIMEOUT_HALF_HOUR = 1800000;
    private static final int TIMEOUT_HALF_MINUTE = 30000;
    private Socket socket;
    private ObjectOutputStream toServer;
    private ObjectInputStream fromServer;
    private boolean connected;
    private String host = "localhost";
    private int port = 9999;

    @SuppressWarnings("InfiniteLoopStatement")
    public void connect(OutputHandler outputHandler) throws IOException {
        socket = new Socket();
        socket.connect(new InetSocketAddress(host, port), TIMEOUT_HALF_MINUTE);
        socket.setSoTimeout(TIMEOUT_HALF_HOUR);
        connected = true;
        toServer = new ObjectOutputStream(socket.getOutputStream());
        fromServer = new ObjectInputStream(socket.getInputStream());
        Listener listener = new Listener(outputHandler, this);
        listener.start();
    }

    public void sendMsg(Message msg)throws IOException{
        toServer.writeObject(msg);
        toServer.flush();
        toServer.reset();

    }
    public void disconnect() throws IOException {
        sendMsg(new Message(QUIT,null));
        try {
            socket.close();
            socket = null;
            connected = false;
        }catch (NullPointerException e){
            System.err.println("Thanks for playing");
        }

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
                    outputHandler.handleMessage(null);
                }

            }
        }

    }
}
