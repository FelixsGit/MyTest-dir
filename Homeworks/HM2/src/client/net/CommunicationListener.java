package client.net;

import common.MessageDTO;

import java.net.InetSocketAddress;

/**
 * Interface implemented in the observer pattern for communication with view 'layer'
 */
public interface CommunicationListener {

    void connected(InetSocketAddress serverAddress);

    void sendGameStateToView(MessageDTO gameState);

    void disconnect();

}
