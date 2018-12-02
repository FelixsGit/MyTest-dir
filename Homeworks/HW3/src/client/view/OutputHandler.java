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

    synchronized public void printMessage(String message){
        System.out.println("Server: "+ message);
    }
}
