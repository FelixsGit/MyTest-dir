package server.controller;

import common.Catalog;
import server.integration.CatalogDAO;
import server.model.Account;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Controller extends UnicastRemoteObject implements Catalog{

    private final CatalogDAO catalogDAO;


    public Controller() throws RemoteException {
        super();
        this.catalogDAO = new CatalogDAO();
    }
    @Override
    public synchronized void createNewAccount(String username, String password){
        catalogDAO.createAccount(new Account(username, password));
    }
}
