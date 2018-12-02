package common;
import client.view.OutputHandler;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * specifies the servers remote methods
 */
public interface Catalog extends Remote {

    FileDTO downloadFileWithID(int id) throws RemoteException;

    MsgContainerDTO createNewAccount(String name, String password) throws RemoteException;

    MsgContainerDTO getAllUsers() throws RemoteException;

    MsgContainerDTO getAllFiles() throws RemoteException;

    MsgContainerDTO login(String username, String password, ClientOutput clientOutput)throws RemoteException;

    void logout(int id)throws RemoteException;

    MsgContainerDTO uploadFile(String name,  int size, int id, int permission)throws RemoteException;

    void deleteFile(int userID, int owner, String fileName) throws RemoteException;

    void updateModifiedFile(int userID, int owner, int newSize, String fileName)throws RemoteException;

}


