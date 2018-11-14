package server.net;

import common.Message;
import common.MessageDTO;
import server.model.HangmanGame;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.concurrent.ForkJoinPool;

public class ClientHandler implements Runnable{

    private HangmanServer hangmanServer;
    private SocketChannel clientChannel;
    private HangmanGame hangmanGame = new HangmanGame();
    private ArrayList<Message> incomingMessageList = new ArrayList<Message>();

    public ClientHandler(HangmanServer hangmanServer, SocketChannel clientChannel){
        this.clientChannel = clientChannel;
        this.hangmanServer = hangmanServer;
    }

    public void run(){
        while (!incomingMessageList.isEmpty()) {
            try {
                Message msg = incomingMessageList.get(0);
                switch (msg.getType()) {
                    case USERNAME:
                        incomingMessageList.remove(0);
                        hangmanGame.changeUsername(msg.getBody());
                        hangmanServer.sendGameStateToClient(hangmanGame.newGame());
                        break;
                    case GUESS:
                        incomingMessageList.remove(0);
                        hangmanServer.sendGameStateToClient(hangmanGame.makeGuess(msg.getBody()));
                        break;
                    case RESTART:
                        incomingMessageList.remove(0);
                        hangmanServer.sendGameStateToClient(hangmanGame.newGame());
                        break;
                    default:
                        disconnectClient();
                        break;
                }
            }catch (IOException e){
                disconnectClient();
            }
        }
    }
    void receiveMsg() throws IOException{
        ByteBuffer msgFromClient = ByteBuffer.allocate(8192);
        clientChannel.read(msgFromClient);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(msgFromClient.array());
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        try{
            Message msg = (Message)objectInputStream.readObject();
            incomingMessageList.add(msg);
            ForkJoinPool.commonPool().execute(this);
        }catch (ClassNotFoundException e){
            //some error
        }
    }

    void finalSend(MessageDTO gameState) throws IOException{
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(gameState);
        objectOutputStream.reset();
        objectOutputStream.flush();
        clientChannel.write(ByteBuffer.wrap(byteArrayOutputStream.toByteArray()));
        byteArrayOutputStream.reset();
        byteArrayOutputStream.flush();
    }



    public void disconnectClient(){
        try {
            clientChannel.close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

}