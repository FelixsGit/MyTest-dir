package client.net;

import common.MessageDTO;

public interface OutputHandler {

    void handleMessage(MessageDTO msg);
}
