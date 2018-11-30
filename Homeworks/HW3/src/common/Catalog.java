package common;

import java.rmi.Remote;

public interface Catalog extends Remote {

    public static final String CATALOG_NAME_IN_REGISTRY = "catalog";

    void createNewUser(String name, String password);
}
