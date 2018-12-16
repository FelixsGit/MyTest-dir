package toppar.hangman_game.common;

import java.io.Serializable;

public class Message implements Serializable {

    private final MsgType type;
    private final String body;

    public Message(MsgType type,String body){
        this.type = type;
        this.body = body;
    }

    public String getBody(){
        return body;
    }

    public MsgType getType(){
        return type;
    }
}
