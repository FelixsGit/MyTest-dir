package common;

import server.model.MsgContainer;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * specifies the servers remote methods
 */
public interface Catalog extends Remote {

    MsgContainerDTO createNewAccount(String name, String password) throws RemoteException;
    MsgContainerDTO getAllUsers() throws RemoteException;
    MsgContainerDTO getAllFiles() throws RemoteException;
    MsgContainerDTO login(String username, String password)throws RemoteException;
    void logout(int id)throws RemoteException;
    MsgContainerDTO uploadFile(String name,  int size, int id, int permission)throws RemoteException;
}