package common;

import java.io.Serializable;

public interface FileDTO extends Serializable {

    int getOwner();

    int getSize();

    int writePermission();

    String getName();

}