package server.net;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class HangmanServer {

    private static final int LINGER_TIME = 5000;
    private static final int TIMEOUT_HALF_HOUR = 1800000;
    private int portNo = 9999;

    public static void main(String[] args) throws Exception {
        HangmanServer server = new HangmanServer();
        server.serve();
    }
    @SuppressWarnings("InfiniteLoopStatement")
    private void serve() {
        try {
            ServerSocket listeningSocket = new ServerSocket(portNo);
            while (true) {
                Socket clientSocket = listeningSocket.accept();
                startHandler(clientSocket);
            }
        } catch (IOException e) {
            System.err.println("Server failure.");
        }
    }
    private void startHandler(Socket clientSocket) throws IOException{
        System.out.println("A new user with address: "+clientSocket.getInetAddress()+ " connected to the hangman server");
        clientSocket.setSoLinger(true, LINGER_TIME);
        clientSocket.setSoTimeout(TIMEOUT_HALF_HOUR);
        ClientHandler handler = new ClientHandler(clientSocket);
        handler.setPriority(Thread.MAX_PRIORITY);
        handler.start();
    }

}
