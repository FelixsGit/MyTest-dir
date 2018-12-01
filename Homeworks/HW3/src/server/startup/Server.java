package server.startup;

import server.controller.Controller;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Server {

    public static void main(String[] args) {
        try {
            Server server = new Server();
            server.startRMI();
            System.out.println("Category server started.\n");
        } catch (RemoteException | MalformedURLException e) {
            System.err.println("Failed to start server.");
            e.printStackTrace();
        }
    }

    private void startRMI() throws RemoteException, MalformedURLException {
        try {
            LocateRegistry.getRegistry().list();
        } catch (RemoteException noRegistryRunning) {
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);
        }
        Controller controller = new Controller();
        Naming.rebind("myRMI", controller);
    }
}