package common;

import java.rmi.Remote;

public interface Catalog extends Remote {

    String CATALOG_NAME_IN_REGISTRY = "catalog";

    void createNewAccount(String name, String password);

}
