package server.model;

import common.FileDTO;

public class File implements FileDTO {

    private String name;
    private int size;
    private String owner;
    private int permission;

    public File(String name, int size, String owner, int permission) {
        this.name = name;
        this.size = size;
        this.owner = owner;
        this.permission = permission;
    }


    public String getName() {
        return this.name;
    }

    public String getOwner() {
        return this.owner;
    }

    public int getSize() {
        return this.size;
    }

    public int getPermission() {
        return this.permission;
    }
}
