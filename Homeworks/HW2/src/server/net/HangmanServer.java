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

/**
 * Class establishes connection with the different
 */

public class HangmanServer {

    private static final int LINGER_TIME = 5000;
    private static final int port = 9999;
    private Selector selector;
    private MessageDTO gameState;

    /**
     * opens the selector
     * @throws IOException
     */
    private void initSelector() throws IOException {
        selector = Selector.open();
    }

    /**
     * Initializes the listening socket channel and setting the channel ready to accept connection.
     * @throws IOException exception
     */
    private void initListeningSocketChannel() throws IOException{
        ServerSocketChannel listeningSocketChannel;
        listeningSocketChannel = ServerSocketChannel.open();
        listeningSocketChannel.configureBlocking(false);
        listeningSocketChannel.bind(new InetSocketAddress(port));
        listeningSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
    }

    /**
     * Binds the socket channel to a specific key so that the server knows
     * which client to receive what message. Sets the channel ready to read input from the user.
     * @param key SelectionKey
     * @throws IOException exception
     */
    private void startHandler(SelectionKey key) throws IOException{
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        SocketChannel clientChannel = serverSocketChannel.accept();
        clientChannel.configureBlocking(false);
        ClientHandler handler = new ClientHandler(this, clientChannel);
        clientChannel.register(selector, SelectionKey.OP_READ, new Client(handler));
        clientChannel.setOption(StandardSocketOptions.SO_LINGER, LINGER_TIME);
    }

    /**
     * When a message is sent from the client on the assigned param, a call will be sent to the
     * ClientHandler object so that the message can be handled.
     * @param key SelectionKey
     * @throws IOException exception
     */
    private void getMsgFromClient(SelectionKey key) throws IOException{
        Client client = (Client) key.attachment();
        try {
            client.handler.receiveMsg();
            key.interestOps(SelectionKey.OP_WRITE);
        } catch (IOException clientHasClosedConnection) {
            removeClient(key);
        }
    }

    /**
     * Sets a reference to the param object to be sent to the client and wakes up with selector
     * @param gameState MessageDTO (game state object)
     */
    void sendGameStateToClient(MessageDTO gameState){
        this.gameState = gameState;
        selector.wakeup();
    }

    /**
     * When a message is ready to be sent to the client with the matching param this method will
     * call a function in the client class to handle the sending.
     * @param key MessageDTO (game state object)
     * @throws IOException exception
     */
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

    /**
     * Removes the key assigned to a specific client and calls the ClientHandler object
     * to close the channel.
     * @param clientKey SelectionKey
     * @throws IOException exception
     */
    private void removeClient(SelectionKey clientKey) throws IOException {
        Client client = (Client) clientKey.attachment();
        client.handler.disconnectClient();
        clientKey.cancel();
    }

    /**
     * Loops and waits for the selector to return so that non-blocking communication
     * with the client can be performed.
     */
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

    /**
     * Starts the server application
     * @param args nothing right now.
     */
    public static void main(String args[]){
        HangmanServer hangmanServer = new HangmanServer();
        hangmanServer.serve();
    }

}
