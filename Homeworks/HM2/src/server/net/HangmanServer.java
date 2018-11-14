package server.net;

import server.Controller.Controller;

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
    private static final int TIMEOUT_HALF_HOUR = 1800000;
    private static final int port = 9999;
    private Selector selector;
    private ServerSocketChannel listeningSocketChannel;
    private Controller controller = new Controller();

    private void initSelector() throws IOException {
        selector = Selector.open();
    }
    private void initListeningSocketChannel() throws IOException{
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
        clientChannel.register(selector, SelectionKey.OP_WRITE, new Client(handler, controller.getUsername()));
        clientChannel.setOption(StandardSocketOptions.SO_LINGER, LINGER_TIME); //Close will probably
    }

    @SuppressWarnings("InfiniteLoopStatement")
    private void serve() throws IOException{
        try{
            initSelector();
            initListeningSocketChannel();
            while(true){
                selector.select();
                Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
                while(iterator.hasNext()){
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (!key.isValid()) {
                        continue;
                    }
                    if (key.isAcceptable()) {
                        startHandler(key);
                    }
                }
            }
        }catch (IOException e){
            //send some error
        }
    }
    private class Client{
        public Client(ClientHandler handler, String gameData){

        }
    }
    public static void main(String args[]) throws IOException{
        HangmanServer hangmanServer = new HangmanServer();
        hangmanServer.serve();
    }

}
