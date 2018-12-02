package client.view;

import common.AccountDTO;
import common.Catalog;
import common.FileDTO;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Scanner;

public class View implements Runnable{

    private final Scanner scan = new Scanner(System.in);
    private boolean receiveUserInput = false;
    private Catalog catalog;
    private OutputHandler outputHandler;
    private DataHandler dataHandler;
    private AccountDTO account;

    public void start(Catalog catalog) throws RemoteException{
        this.catalog = catalog;
        receiveUserInput = true;
        outputHandler = new OutputHandler();
        dataHandler = new DataHandler(outputHandler);
        new Thread(this).start();
    }

    public void run(){
        while(receiveUserInput){
            if(account != null){
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
                        catalog.createNewAccount(regname, regpass);
                    }catch(Exception e){
                        outputHandler.println("username already taken!");
                    }
                    break;
                case "LOGIN":
                    try{
                        outputHandler.println("Enter your username: ");
                        String username = scan.nextLine();
                        outputHandler.println("Enter your password");
                        String password = scan.nextLine();
                        account = catalog.login(username, password, outputHandler);
                        outputHandler.println("login successful");
                    }catch (Exception e){
                        outputHandler.println("wrong username or password");
                    }
                    break;
                case "LOGOUT":
                    try {
                        catalog.logout(account.getUsername());
                        account = null;
                        outputHandler.println("now offline");
                    }catch (RemoteException e){
                        e.printStackTrace();
                    }
                    break;
                case "UPLOAD":
                    try {
                        outputHandler.println("enter file name...");
                        String filename = scan.nextLine();
                        outputHandler.println("enter size of file...");
                        int size = Integer.parseInt(scan.nextLine());
                        outputHandler.println("enter read/write permissions, 0  for only you have access, 1 for all have access");
                        int permission = Integer.parseInt(scan.nextLine());
                        catalog.uploadFile(filename,size,account.getUsername(),permission);
                        outputHandler.println("file uploaded to catalog");
                    }catch (Exception e){
                        outputHandler.println("file with that name already exists");
                    }
                    break;
                case "VIEW":
                    try {
                        ArrayList<FileDTO> fileContainer;
                        fileContainer = catalog.getAllFiles();
                        outputHandler.println("Catalog Files");
                        dataHandler.printFileData(fileContainer);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    break;
                case "DOWNLOAD":
                    try {
                        ArrayList<FileDTO> fileContainer;
                        fileContainer = catalog.getAllFiles();
                        outputHandler.println("Catalog Files");
                        dataHandler.printFileData(fileContainer);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    outputHandler.println("enter the name of the file to download...");
                    String fileName = scan.nextLine();
                    try{
                        FileDTO file = catalog.downloadFileWithName(fileName);
                        outputHandler.printMessage("downloaded the file: "+ file.getName());
                        int command = dataHandler.optionsOnFile(file.getName(), file.getOwner(), file.getPermission(), account.getUsername());
                        if(command == 1){
                            outputHandler.println("modifying the file....");
                            while(true){
                                outputHandler.println("enter new size of file");
                                String newSize = scan.nextLine();
                                if(isNumeric(newSize)){
                                    int updatedSize = Integer.parseInt(newSize);
                                    outputHandler.println("saving the modified file to Catalog...");
                                    catalog.updateModifiedFile(file.getName(), file.getOwner(), updatedSize, account.getUsername());
                                    break;
                                }
                            }
                        }
                        else if(command == 2){
                            outputHandler.println("deleting file the file from Catalog....");
                            catalog.deleteFile(file.getName(), file.getOwner(), account.getUsername());
                        }else if(command == 3){
                            outputHandler.println("keeping the file, and reading it");
                        }else{
                            outputHandler.println("You only have permission to read the file");
                        }
                    }catch (Exception e){
                        outputHandler.println("no file with that name found");
                    }
                    break;

            }
        }
    }
    private static boolean isNumeric(String str) {
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c))
                return false;
        }
        return true;
    }
}
