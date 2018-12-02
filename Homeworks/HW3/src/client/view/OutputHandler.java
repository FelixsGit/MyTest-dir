package client.view;

import common.ClientOutput;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class OutputHandler extends UnicastRemoteObject implements ClientOutput {


    OutputHandler()throws RemoteException{
        super();
    }

    void println(String output) {
        System.out.println(output);
    }

    void print(String output) {
        System.out.print(output);
    }

    synchronized public void printMessage(String message)throws RemoteException{
        System.out.println("Server: "+ message);
    }
}
