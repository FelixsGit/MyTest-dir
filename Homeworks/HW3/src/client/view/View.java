package client.view;

import common.Catalog;
import common.FileDTO;
import common.MsgContainerDTO;
import java.rmi.RemoteException;
import java.util.Scanner;

public class View implements Runnable{

    private final Scanner scan = new Scanner(System.in);
    private boolean receiveUserInput = false;
    private Catalog catalog;
    private OutputHandler outputHandler;
    private DataHandler dataHandler;
    private static int MYID ;

    public void start(Catalog catalog) throws RemoteException{
        this.catalog = catalog;
        receiveUserInput = true;
        outputHandler = new OutputHandler();
        dataHandler = new DataHandler(outputHandler);
        new Thread(this).start();
    }

    public void run(){
        MsgContainerDTO container;
        FileDTO file;
        while(receiveUserInput){
            if(MYID != 0){
                outputHandler.println("AVAILABLE COMMANDS ----> LOGOUT, UPLOAD, DOWNLOAD, VIEW");
            }else{
                outputHandler.println("AVAILABLE COMMANDS ----> REGISTER, LOGIN");
            }
            String userMsg = scan.nextLine();
            switch(userMsg){

                case "REGISTER":
                    outputHandler.println("Enter new username: ");
                    String regname = scan.nextLine();
                    outputHandler.println("Enter new password");
                    String regpass = scan.nextLine();
                    try {
                        container = catalog.createNewAccount(regname, regpass);
                        dataHandler.extractMessage(container);
                    }catch(RemoteException e){
                        e.printStackTrace();
                    }
                    break;
                /*
                case "GETUSERS":
                    try {
                        container = catalog.getAllUsers();
                        dataHandler.extractMessage(container);
                        break;
                    }catch (RemoteException e){
                        e.printStackTrace();
                    }
                  */
                case "LOGIN":
                    try{
                        outputHandler.println("Enter your username: ");
                        String username = scan.nextLine();
                        outputHandler.println("Enter your password");
                        String password = scan.nextLine();
                        container = catalog.login(username, password, outputHandler);
                        MYID = dataHandler.extractMessage(container);
                        break;
                    }catch (RemoteException e){
                        e.printStackTrace();
                    }

                case "LOGOUT":
                    try {
                        catalog.logout(MYID);
                        MYID = 0;
                        outputHandler.println("now offline");
                        break;
                    }catch (RemoteException e){
                        e.printStackTrace();
                    }

                case "UPLOAD":
                    outputHandler.println("enter file name...");
                    String filename = scan.nextLine();
                    outputHandler.println("enter size of file...");
                    int size = Integer.parseInt(scan.nextLine());
                    outputHandler.println("enter read/write permissions, 0  for only you have access, 1 for all have access");
                    int permission = Integer.parseInt(scan.nextLine());
                    try {
                        container = catalog.uploadFile(filename, size, MYID, permission);
                        dataHandler.extractMessage(container);
                        break;
                    }catch (RemoteException e){
                        e.printStackTrace();
                    }

                case "VIEW":
                    try {
                        container = catalog.getAllFiles();
                        outputHandler.println("Catalog Files");
                        dataHandler.extractMessage(container);
                        break;
                    }catch (RemoteException e){
                        e.printStackTrace();
                    }

                case "DOWNLOAD":
                    try {
                        container = catalog.getAllFiles();
                        outputHandler.println("Catalog Files");
                        dataHandler.extractMessage(container);
                    }catch(RemoteException e){
                        e.printStackTrace();
                    }
                    outputHandler.println("enter the ID of the file to download...");
                    int id = Integer.parseInt(scan.nextLine());
                    try{
                        file = catalog.downloadFileWithID(id);
                        outputHandler.printMessage("downloaded the file: "+ file.getName());
                        int command = dataHandler.optionsOnFile(file.writePermission(), file.getName(), file.getOwner(), MYID);
                        if(command == 1){
                            outputHandler.println("modifying the file....");
                            while(true){
                                outputHandler.println("enter new size of file");
                                String newSize = scan.nextLine();
                                if(isNumeric(newSize)){
                                    int updatedSize = Integer.parseInt(newSize);
                                    outputHandler.println("saving the modified file to Catalog...");
                                    catalog.updateModifiedFile(MYID, file.getOwner(),updatedSize, file.getName());
                                    break;
                                }
                            }
                        }else if(command == 2){
                            outputHandler.println("deleting file the file from Catalog....");
                            catalog.deleteFile(MYID,file.getOwner(),file.getName());
                        }else if(command == 3){
                            outputHandler.println("keeping the file, and reading it");
                        }else{
                            outputHandler.println("You only have permission to read the file");
                        }
                    }catch (RemoteException e){
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }
    public static boolean isNumeric(String str) {
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }
}
