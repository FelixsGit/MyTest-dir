package client.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayDeque;
import java.util.Queue;

public class ServerConnection extends Thread {

    private CommunicationListener listener;
    private InetSocketAddress serverAddress;
    private SocketChannel socketChannel;
    private Selector selector;
    private final Queue<ByteBuffer> messagesToSend = new ArrayDeque<>();
    private boolean connected = false;

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
        } catch (IOException couldNotGetRemAddrUsingDefaultInstead) {
            listener.connected(serverAddress);
        }
    }
    private void initConnection() throws IOException {
        socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(serverAddress);
        connected = true;
    }
    private void initSelector() throws IOException{
        selector = Selector.open();
        socketChannel.register(selector, SelectionKey.OP_CONNECT);
    }
    public void run() {
        try {
            initConnection();
            initSelector();
            while (connected || !messagesToSend.isEmpty()) {
                selector.select();
                for (SelectionKey key : selector.selectedKeys()) {
                    selector.selectedKeys().remove(key);
                    if (!key.isValid()) {
                        continue;
                    }
                    if (key.isConnectable()) {
                        completeConnection(key);
                        System.out.println("connection completed");
                    }
                    /*else if (key.isReadable()) {
                        recvFromServer(key);
                    } else if (key.isWritable()) {
                        sendToServer(key);
                    }
                    */
                }
            }
        } catch (Exception e) {
            System.err.println("lost connection");
        }
      //try to disconnect and by sending disconnect to server, catch it if it don't work
    }
}
