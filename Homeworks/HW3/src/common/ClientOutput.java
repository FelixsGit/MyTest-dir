package common;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientOutput extends Remote{

    void printMessage(String message) throws RemoteException;

}