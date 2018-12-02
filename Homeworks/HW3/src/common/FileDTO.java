package common;

import java.io.Serializable;

public interface FileDTO extends Serializable {

    String getOwner();

    int getSize();

    int getPermission();

    String getName();
}