package client.net;

import common.MessageDTO;

import java.net.InetSocketAddress;

public interface CommunicationListener {

    void connected(InetSocketAddress serverAddress);

    void sendGameStateToView(MessageDTO gameState);

    void disconnect();

}
