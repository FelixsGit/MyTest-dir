package client.net;

import common.Message;
import common.MessageDTO;
import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import static common.MsgType.QUIT;

public class ServerConnection extends Thread {

    private CommunicationListener listener;
    private InetSocketAddress serverAddress;
    private SocketChannel socketChannel;
    private Selector selector;
    private Message msg;

    /**
     * Adds a reference to the observer interface
     * @param listener observer interface
     */
    public void addCommunicationListener(CommunicationListener listener) {
        this.listener = listener;
    }

    /**
     * Sets a new InetSocketAddress on the entered params
     * @param host the ip of the client
     * @param port the port of the server
     */
    public void connect(String host, int port) {
        serverAddress = new InetSocketAddress(host, port);
        this.start();
    }

    /**
     * Finishes the socket connection to the server
     * @param key SelectionKey
     * @throws IOException exception
     */
    private void completeConnection(SelectionKey key) throws IOException{
        socketChannel.finishConnect();
        key.interestOps(SelectionKey.OP_READ);
        try {
            InetSocketAddress remoteAddress = (InetSocketAddress) socketChannel.getRemoteAddress();
            listener.connected(remoteAddress);
            key.interestOps(SelectionKey.OP_WRITE);
        } catch (IOException couldNotGetRemAdrUsingDefaultInstead) {
            listener.connected(serverAddress);
        }
    }

    /**
     * Sets up the socket channel and initialize the connection
     * @throws IOException exception
     */
    private void initConnection() throws IOException {
        socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(serverAddress);
    }

    /**
     * Registers the selector and sets the channel ready to connect
     * @throws IOException exception
     */
    private void initSelector() throws IOException{
        selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_CONNECT);
    }

    /**
     * Sends an object to the server and sets the channel ready to read
     * @param key SelectionKey
     * @throws IOException exception
     */
    private void sendToServer(SelectionKey key) throws IOException{
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(msg);
        objectOutputStream.reset();
        objectOutputStream.flush();
        msg = null;
        socketChannel.write(ByteBuffer.wrap(byteArrayOutputStream.toByteArray()));
        byteArrayOutputStream.reset();
        byteArrayOutputStream.flush();
        key.interestOps(SelectionKey.OP_READ);
        selector.wakeup();
    }

    /**
     * Sets the next message to be send to the server
     * @param msg Message object to be sent
     */
    public void setMessage(Message msg){
        this.msg = msg;
        if(msg.getType() == QUIT){
            listener.disconnect();
        }
    }

    /**
     * Reads messages from the server on the socketChannel and sets the channel ready to write
     * @param key SelectionKey
     * @throws IOException exception
     */
    private void receiveMsg(SelectionKey key) throws IOException{
        ByteBuffer msgFromClient = ByteBuffer.allocate(8192);
        socketChannel.read(msgFromClient);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(msgFromClient.array());
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        try{
            MessageDTO gameState = (MessageDTO)objectInputStream.readObject();
            listener.sendGameStateToView(gameState);
            key.interestOps(SelectionKey.OP_WRITE);
        }catch (ClassNotFoundException e){
            System.err.println("failed to receive the message");
        }
    }

    /**
     * Loops and waits for the selector to return so that non-blocking communication
     * with the server can be performed.
     */
    @SuppressWarnings("InfiniteLoopStatement")
    public void run() {
        try {
            initConnection();
            initSelector();
            while (true) {
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while(iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (!key.isValid()) {
                        continue;
                    }
                    if(key.isConnectable()){
                        completeConnection(key);
                    }else if (key.isWritable() && (msg != null)) {
                        sendToServer(key);
                    }else if(key.isReadable()){
                        receiveMsg(key);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("no connection to server");
        }
    }
}
