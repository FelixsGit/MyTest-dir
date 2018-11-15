package server.net;

import common.MessageDTO;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.StandardSocketOptions;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class HangmanServer {

    private static final int LINGER_TIME = 5000;
    private static final int port = 9999;
    private Selector selector;
    private MessageDTO gameState;

    private void initSelector() throws IOException {
        selector = Selector.open();
    }
    private void initListeningSocketChannel() throws IOException{
        ServerSocketChannel listeningSocketChannel;
        listeningSocketChannel = ServerSocketChannel.open();
        listeningSocketChannel.configureBlocking(false);
        listeningSocketChannel.bind(new InetSocketAddress(port));
        listeningSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }
    private void startHandler(SelectionKey key) throws IOException{
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverSocketChannel.accept();
        clientChannel.configureBlocking(false);
        ClientHandler handler = new ClientHandler(this, clientChannel);
        clientChannel.register(selector, SelectionKey.OP_READ, new Client(handler));
        clientChannel.setOption(StandardSocketOptions.SO_LINGER, LINGER_TIME);
    }
    private void getMsgFromClient(SelectionKey key) throws IOException{
        Client client = (Client) key.attachment();
        try {
            client.handler.receiveMsg();
            key.interestOps(SelectionKey.OP_WRITE);
        } catch (IOException clientHasClosedConnection) {
            removeClient(key);
        }
    }
    void sendGameStateToClient(MessageDTO gameState){
        this.gameState = gameState;
        selector.wakeup();
    }

    private void sendToClient(SelectionKey key) throws IOException {
        Client client = (Client) key.attachment();
        try {
            client.sendMsg(gameState);
            gameState = null;
            key.interestOps(SelectionKey.OP_READ);

        } catch (IOException clientHasClosedConnection) {
            removeClient(key);
        }
    }

    private void removeClient(SelectionKey clientKey) throws IOException {
        Client client = (Client) clientKey.attachment();
        client.handler.disconnectClient();
        clientKey.cancel();
    }

    @SuppressWarnings("InfiniteLoopStatement")
    private void serve(){
        try{
            initSelector();
            initListeningSocketChannel();
            while (true) {
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (!key.isValid()) {
                        continue;
                    }
                    if (key.isAcceptable()) {
                        startHandler(key);
                    } else if (key.isReadable()) {
                        getMsgFromClient(key);
                    } else if (key.isWritable() && gameState != null) {
                        sendToClient(key);
                    }
                }
            }
        }catch (IOException e){
            System.err.println("Sever failure");
        }
    }
    private class Client{

        private ClientHandler handler;

        Client(ClientHandler handler){
            this.handler = handler;
        }

        void sendMsg(MessageDTO gameState) throws IOException{
            handler.finalSend(gameState);
        }

    }
    public static void main(String args[]){
        HangmanServer hangmanServer = new HangmanServer();
        hangmanServer.serve();
    }

}
