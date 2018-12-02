package common;

import java.io.Serializable;

/**
 * A DTO that is the Message Object the client are sending to the server
 */

public class Message implements Serializable {

    private final MsgType type;
    private final String body;

    public Message(MsgType type, String body){
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
