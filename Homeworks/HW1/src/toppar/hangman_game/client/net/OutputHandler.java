package toppar.hangman_game.client.net;

import toppar.hangman_game.common.MessageDTO;

public interface OutputHandler {

    void handleMessage(MessageDTO msg);
}
