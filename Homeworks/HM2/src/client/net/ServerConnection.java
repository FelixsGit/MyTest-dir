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

    public void addCommunicationListener(CommunicationListener listener) {
        this.listener = listener;
    }
    public void connect(String host, int port) {
        serverAddress = new InetSocketAddress(host, port);
        this.start();
    }
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
    private void initConnection() throws IOException {
        socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(serverAddress);
    }
    private void initSelector() throws IOException{
        selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_CONNECT);
    }
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
    public void setMessage(Message msg){
        this.msg = msg;
        if(msg.getType() == QUIT){
            listener.disconnect();
        }
    }

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
            //some error
        }
    }
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
      //try to disconnect and by sending disconnect to server, catch it if it don't work
    }
}
