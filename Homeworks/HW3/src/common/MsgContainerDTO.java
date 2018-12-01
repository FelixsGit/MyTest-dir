package common;

import java.io.Serializable;
import java.util.LinkedList;

public interface MsgContainerDTO extends Serializable {

    LinkedList<String> getMsg();

    String getStatus();

}
