package toppar.hangman_game.server.net;

import toppar.hangman_game.common.Message;
import toppar.hangman_game.common.MessageDTO;
import toppar.hangman_game.server.model.HangmanGame;
import toppar.hangman_game.server.model.ServerLogs;

import java.io.*;
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
        toClient = new ObjectOutputStream(clientSocket.getOutputStream());
        fromClient = new ObjectInputStream(clientSocket.getInputStream());
    }
    public void sendMessage(MessageDTO msg){
        try{
            toClient.writeObject(msg);
            toClient.reset();
            toClient.flush();
        }catch (IOException e){
            System.err.println("Could not write msg to toppar.hangman_game.client");
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