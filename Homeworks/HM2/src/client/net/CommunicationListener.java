package client.net;

import java.net.InetSocketAddress;

public interface CommunicationListener {

    void connected(InetSocketAddress serverAddress);

}
