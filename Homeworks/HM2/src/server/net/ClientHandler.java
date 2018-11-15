package server.net;

import common.Message;
import common.MessageDTO;
import server.model.HangmanGame;
import server.model.ServerLogs;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.ForkJoinPool;

public class ClientHandler implements Runnable{

    private HangmanServer hangmanServer;
    private SocketChannel clientChannel;
    private HangmanGame hangmanGame = new HangmanGame();
    private LinkedList<Message> incomingMessageList = new LinkedList<>();
    private ServerLogs logs = new ServerLogs();

    public ClientHandler(HangmanServer hangmanServer, SocketChannel clientChannel){
        this.clientChannel = clientChannel;
        this.hangmanServer = hangmanServer;
    }

    public void run(){
        while (!incomingMessageList.isEmpty()) {
            try {
                Message msg = incomingMessageList.getFirst();
                String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
                switch (msg.getType()) {
                    case USERNAME:
                        incomingMessageList.removeFirst();
                        hangmanGame.changeUsername(msg.getBody());
                        hangmanServer.sendGameStateToClient(hangmanGame.newGame());
                        break;
                    case GUESS:
                        incomingMessageList.removeFirst();
                        hangmanServer.sendGameStateToClient(hangmanGame.makeGuess(msg.getBody()));
                        break;
                    case RESTART:
                        incomingMessageList.removeFirst();
                        hangmanServer.sendGameStateToClient(hangmanGame.newGame());
                        break;
                    case QUIT:
                        logs.appendEntry(timeStamp+": user @"+hangmanGame.getUsername()+" disconnected.\n");
                        incomingMessageList.removeFirst();
                        disconnectClient();
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
            incomingMessageList.addLast(msg);
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