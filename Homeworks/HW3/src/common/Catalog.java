package common;
import client.view.OutputHandler;
import server.model.AccountException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * specifies the servers remote methods
 */
public interface Catalog extends Remote {


    void createNewAccount(String name, String password) throws Exception;

    AccountDTO login(String username, String password, ClientOutput clientOutput)throws Exception;

    ArrayList<FileDTO> getAllFiles() throws Exception;

    void logout(String username)throws RemoteException;

    void uploadFile(String name,  int size, String owner, int permission)throws Exception;

    FileDTO downloadFileWithName(String fileName) throws Exception;

    void updateModifiedFile(String fileName, String owner, int newSize, String editorName)throws Exception;

    void deleteFile(String fileName, String owner, String editorName) throws Exception;

}


