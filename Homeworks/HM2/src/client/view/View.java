package client.view;

import client.net.CommunicationListener;
import client.net.ServerConnection;
import java.net.InetSocketAddress;

public class View {

    ServerConnection server;
    private static final int port = 9999;
    private static final String host = "localhost";

    /**
     * This method connects to the server and starts the ParseScanner thread
     */
    public void establishConnection(){
        server = new ServerConnection();
        ParseScanner parseScanner = new ParseScanner();
        parseScanner.start();
    }

    /**
     * This thread listens for input from the user and sends it to the server
     */
    private class ParseScanner extends Thread{

        public void run(){
            //on startup
            server.addCommunicationListener(new ConsoleOutput());
            server.connect(host,port);
        }

    }

    /**
     * This class receives messages from the server
     */
    private class ConsoleOutput implements CommunicationListener {

        @Override
        public void connected(InetSocketAddress serverAddress) {
            System.out.println("connected to "+ serverAddress);
        }
    }

}
