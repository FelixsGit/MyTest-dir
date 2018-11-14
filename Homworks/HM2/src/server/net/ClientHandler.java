package server.net;

import common.Message;
import common.MessageDTO;
import server.model.HangmanGame;
import server.model.ServerLogs;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ClientHandler extends Thread{

    private ObjectOutputStream toClient;
    private ObjectInputStream fromClient;
    private final Socket clientSocket;
    private boolean connected;
    private ServerLogs logs = new ServerLogs();

    public ClientHandler(Socket clientSocket)throws IOException {
        this.clientSocket = clientSocket;
        connected = true;
        fromClient = new ObjectInputStream(clientSocket.getInputStream());
        toClient = new ObjectOutputStream(clientSocket.getOutputStream());
    }
    public void sendMessage(MessageDTO msg)throws IOException{
        try{
            toClient.writeObject(msg);
            toClient.reset();
            toClient.flush();

        }catch (IOException e){
            System.err.println("Could not write msg to client");
        }
    }
    public void run(){
        HangmanGame hangmanGame = new HangmanGame();
        while(connected){
            try{
                Message msg = (Message)fromClient.readObject();
                String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
                switch(msg.getType()){
                    case USERNAME:
                        hangmanGame.changeUsername(msg.getBody());
                        sendMessage(hangmanGame.newGame());
                        break;
                    case GUESS:
                        sendMessage(hangmanGame.makeGuess(msg.getBody()));
                        break;
                    case QUIT:
                        logs.appendEntry(timeStamp+": user @"+hangmanGame.getUsername()+" disconnected.\n");
                        disconnectClient();
                        break;
                    case RESTART:
                        sendMessage(hangmanGame.newGame());
                        break;
                    default:
                        disconnectClient();
                        break;
                }
            }
            catch (IOException | ClassNotFoundException e){
                disconnectClient();
            }
        }
    }
    private void disconnectClient(){
        try{
            clientSocket.close();
        }catch (IOException e){
            e.printStackTrace();
        }
        connected = false;
    }

}