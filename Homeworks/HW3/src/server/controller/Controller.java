package server.controller;

import common.Catalog;
import common.MsgContainerDTO;
import server.integration.CatalogDAO;
import server.model.Account;
import server.model.File;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Controller extends UnicastRemoteObject implements Catalog {

    private final CatalogDAO catalogDAO;

    public Controller() throws RemoteException {
        super();
        this.catalogDAO = new CatalogDAO();
    }

    public synchronized MsgContainerDTO createNewAccount(String username, String password){
        return catalogDAO.createAccount(new Account(username, password));
    }

    public synchronized MsgContainerDTO getAllUsers(){
        return catalogDAO.getAllUsers();
    }

    public synchronized  MsgContainerDTO login(String username, String password){
        return catalogDAO.verifyUser(new Account(username, password));
    }

    public synchronized  MsgContainerDTO uploadFile(String name, int size, int id, int permission){
        return catalogDAO.uploadFile(new File(name, size, id, permission));
    }

    public synchronized  MsgContainerDTO getAllFiles(){
        return catalogDAO.getAllFiles();
    }

    public synchronized void logout(int id){
        catalogDAO.logoutUser(id);
    }
}
