package server.controller;

import client.view.OutputHandler;
import common.Catalog;
import common.ClientOutput;
import common.FileDTO;
import common.MsgContainerDTO;
import server.integration.CatalogDAO;
import server.model.Account;
import server.model.File;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class Controller extends UnicastRemoteObject implements Catalog {

    private Map<String, ClientOutput> loggedInUsers = new HashMap<>();

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

    public synchronized  MsgContainerDTO login(String username, String password, ClientOutput clientOutput)throws RemoteException{
        loggedInUsers.put(username, clientOutput);
        clientOutput.printMessage("authenticating user...");
        /*
        Set set = loggedInUsers.entrySet();
        Iterator iterator = set.iterator();
        System.out.println("List of ONLINE users");
        while(iterator.hasNext()) {
            Map.Entry mapEntry = (Map.Entry)iterator.next();
            System.out.println("user: "+ mapEntry.getKey());
        }
        */
        return catalogDAO.verifyUser(new Account(username, password));
    }

    public synchronized  MsgContainerDTO uploadFile(String name, int size, int id, int permission){
        return catalogDAO.uploadFile(new File(name, size, id, permission));
    }

    public synchronized FileDTO downloadFileWithID(int id){
        return catalogDAO.downloadFileWithID(id);
    }

    public synchronized  MsgContainerDTO getAllFiles(){
        return catalogDAO.getAllFiles();
    }

    public synchronized void updateModifiedFile(int userID, int owner, int newSize, String fileName) throws RemoteException{
        if(owner != userID){
            notifyOwner(userID, owner, fileName, "update");
        }
        catalogDAO.updateFile(fileName, newSize);
    }

    public synchronized void logout(int id){
        loggedInUsers.remove(catalogDAO.getUserFromID(id));
    }

    public synchronized void deleteFile(int userID, int owner, String fileName) throws RemoteException{
        if(owner != userID){
            notifyOwner(userID, owner, fileName, "delete");
        }
        catalogDAO.deleteFile(fileName);
    }

    private void notifyOwner(int userID, int owner, String fileName, String action) throws RemoteException {
        String ownerName = catalogDAO.getUserFromID(owner);
        if (loggedInUsers.containsKey(ownerName) && action.equals("update")) {
            ClientOutput clientOutput = loggedInUsers.get(ownerName);
            String userWhoModified = catalogDAO.getUserFromID(userID);
            clientOutput.printMessage("Your file with name " + fileName + " has been modified by user " + userWhoModified);
        }else if(loggedInUsers.containsKey(ownerName) && action.equals("delete")){
            ClientOutput clientOutput = loggedInUsers.get(ownerName);
            String userWhoModified = catalogDAO.getUserFromID(userID);
            clientOutput.printMessage("Your file with name " + fileName + " has been deleted by user " + userWhoModified);
        }else{
            System.out.println("owner of the file is not online, no notification can be sent...");
        }
    }
}
