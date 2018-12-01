package client.view;

import common.Catalog;
import common.MsgContainerDTO;
import java.rmi.RemoteException;
import java.util.Scanner;

public class View implements Runnable{

    private final Scanner scan = new Scanner(System.in);
    private boolean receiveUserInput = false;
    private Catalog catalog;
    private PrintToConsol printToConsol = new PrintToConsol();
    private static int MYID ;

    public void start(Catalog catalog){
        this.catalog = catalog;
        receiveUserInput = true;
        new Thread(this).start();
    }

    public void run(){
        MsgContainerDTO container;
        while(receiveUserInput){
            if(MYID != 0){
                System.out.println("AVAILABLE COMMANDS ----> GETUSERS, LOGOUT, UPLOAD, VIEW");
            }else{
                System.out.println("AVAILABLE COMMANDS ----> NEWUSER, GETUSERS, LOGIN");
            }
            String userMsg = scan.nextLine();
            switch(userMsg){
                
                case "NEWUSER":
                    System.out.println("Enter new username: ");
                    String regname = scan.nextLine();
                    System.out.println("Enter new password");
                    String regpass = scan.nextLine();
                    try {
                        container = catalog.createNewAccount(regname, regpass);
                        printToConsol.extractMessage(container);
                    }catch(RemoteException e){
                        e.printStackTrace();
                    }
                    break;

                case "GETUSERS":
                    try {
                        container = catalog.getAllUsers();
                        printToConsol.extractMessage(container);
                        break;
                    }catch (RemoteException e){
                        e.printStackTrace();
                    }

                case "LOGIN":
                    try{
                        System.out.println("Enter your username: ");
                        String username = scan.nextLine();
                        System.out.println("Enter your password");
                        String password = scan.nextLine();
                        container = catalog.login(username, password);
                        MYID = printToConsol.extractMessage(container);
                        break;
                    }catch (RemoteException e){
                        e.printStackTrace();
                    }

                case "LOGOUT":
                    try {
                        catalog.logout(MYID);
                        MYID = 0;
                        System.out.println("now offline");
                        break;
                    }catch (RemoteException e){
                        e.printStackTrace();
                    }

                case "UPLOAD":
                    System.out.println("enter file name...");
                    String filename = scan.nextLine();
                    System.out.println("enter size of file...");
                    int size = Integer.parseInt(scan.nextLine());
                    System.out.println("enter read/write permissions, 0  for only you have access, 1 for all have access");
                    int permission = Integer.parseInt(scan.nextLine());
                    try {
                        container = catalog.uploadFile(filename, size, MYID, permission);
                        printToConsol.extractMessage(container);
                        break;
                    }catch (RemoteException e){
                        e.printStackTrace();
                    }

                case "VIEW":
                    try {
                        container = catalog.getAllFiles();
                        System.out.println("Catalog Files");
                        printToConsol.extractMessage(container);
                        break;
                    }catch (RemoteException e){
                        e.printStackTrace();
                    }
            }
        }
    }
}
