package server.controller;

import client.view.OutputHandler;
import common.*;
import server.integration.CatalogDAO;
import server.model.Account;
import server.model.AccountException;
import server.model.File;
import server.model.FileException;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Controller extends UnicastRemoteObject implements Catalog {

    private Map<String, ClientOutput> loggedInUsers = new HashMap<>();

    private final CatalogDAO catalogDAO;

    public Controller() throws RemoteException {
        super();
        this.catalogDAO = new CatalogDAO();
    }

    public synchronized void createNewAccount(String username, String password) throws AccountException{
        try {
            catalogDAO.createAccount(new Account(username, password));
        }catch (AccountException e){
            throw new AccountException("username already taken");
        }
    }
    public synchronized  AccountDTO login(String username, String password, ClientOutput clientOutput)throws AccountException, RemoteException{
        clientOutput.printMessage("authenticating user...");
        try {
            AccountDTO account = catalogDAO.verifyUser(new Account(username, password));
            if(account != null){
                loggedInUsers.put(username,clientOutput);
                return account;
            }
        }catch (AccountException e){
            throw new AccountException("wrong username or password");
        }
        return null;
    }
    public synchronized void logout(String username){
        loggedInUsers.remove(username);
    }

    public synchronized void uploadFile(String name, int size, String owner, int permission) throws FileException {
        try {
            catalogDAO.uploadFile(new File(name, size, owner, permission));
        }catch(FileException e){
            throw new FileException("file with that name already exists");
        }
    }
    public synchronized ArrayList<FileDTO> getAllFiles() throws FileException{
        try {
            return catalogDAO.getAllFiles();
        }catch (FileException e){
            throw new FileException("failed to retrieve files");
        }
    }
    public synchronized FileDTO downloadFileWithName(String fileName) throws FileException{
        try {
            FileDTO file = catalogDAO.getFileWithName(fileName);
            if(file != null){
                return file;
            }
        }catch (FileException e){
            throw new FileException("no file with that name found");
        }
        return null;
    }

    public synchronized void updateModifiedFile(String fileName, String owner, int newSize, String editorName) throws FileException, RemoteException{
        if(!owner.equals(editorName)){
            notifyOwner(fileName, owner, editorName, "update");
        }
        try {
            catalogDAO.updateFile(fileName, newSize);
        }catch (FileException e){
            throw new FileException("failed to delete file");
        }
    }

    public synchronized void deleteFile(String fileName, String owner, String editorName) throws FileException, RemoteException{
        if(!owner.equals(editorName)){
            notifyOwner(fileName, owner, editorName, "delete");
        }
        try {
            catalogDAO.deleteFile(fileName);
        }catch (FileException e){
            throw new FileException("failed to delete file");
        }
    }

    private void notifyOwner(String fileName, String owner, String editorName, String action) throws RemoteException{
        if (loggedInUsers.containsKey(owner) && action.equals("update")) {
            ClientOutput clientOutput = loggedInUsers.get(owner);
            clientOutput.printMessage("Your file with name " + fileName + " has been modified by user " + editorName);
        }else if(loggedInUsers.containsKey(owner) && action.equals("delete")){
            ClientOutput clientOutput = loggedInUsers.get(owner);
            clientOutput.printMessage("Your file with name " + fileName + " has been deleted by user " + editorName);
        }else{
            System.out.println("owner of the file is not online, no notification can be sent...");
        }
    }
}
