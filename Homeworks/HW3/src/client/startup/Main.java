package client.startup;

import client.view.View;
import common.Catalog;
import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class Main {

    public static void main(String[] args) {
        try {
            Catalog catalog = (Catalog) Naming.lookup("myRMI");
            new View().start(catalog);
        } catch (NotBoundException | MalformedURLException | RemoteException ex) {
            System.err.println("Error: Could not start client");
            ex.printStackTrace();
        }
    }
}
