package client.view;

import common.MsgContainerDTO;

import java.util.LinkedList;
import java.util.Scanner;

class DataHandler {

    private Scanner scan = new Scanner(System.in);
    private OutputHandler outputHandler;

    DataHandler(OutputHandler outputHandler){
        this.outputHandler = outputHandler;
    }

    int optionsOnFile(int permission, String fileName, int authorID, int myID){
        if(permission == 1){
            while(true) {
                outputHandler.println("MODIFY, DELETE or KEEP file: " + fileName);
                String commandOnFile = scan.nextLine();
                if (commandOnFile.equals("MODIFY")) {
                    return 1;
                }else if(commandOnFile.equals("DELETE")){
                    return 2;
                }else if(commandOnFile.equals("KEEP")){
                    return 3;
                }
            }
        }else if(authorID == myID){
            while(true) {
                outputHandler.println("MODIFY or DELETE or KEEP file: " + fileName);
                String commandOnFile = scan.nextLine();
                if (commandOnFile.equals("MODIFY")) {
                    return 1;
                }else if(commandOnFile.equals("DELETE")){
                    return 2;
                }else if(commandOnFile.equals("KEEP")){
                    return 3;
                }
            }
        }
        return 0;
    }

    int extractMessage(MsgContainerDTO msgContainer){
        if(msgContainer.getStatus().equals("TAKEN")){
            outputHandler.println("username already taken");
        }else if(msgContainer.getStatus().equals("OK")) {
            outputHandler.println("action complete");
            LinkedList<String> container = msgContainer.getMsg();
            for (int i = 0; i < container.size(); i++) {
                outputHandler.println(container.get(i));
            }
            container.clear();
        }else if(msgContainer.getStatus().equals("VALID")){
            outputHandler.println("You are now logged in");
            LinkedList<String> container = msgContainer.getMsg();
            for (int i = 0; i < container.size(); i++) {
                return Integer.parseInt(container.get(i));
            }
            container.clear();
        }else if(msgContainer.getStatus().equals("NOTVALID")){
            outputHandler.println("Wrong username or password");
        }else if(msgContainer.getStatus().equals("ERROR")){
            outputHandler.println("failed to create file");
        }else if(msgContainer.getStatus().equals("NAMEERROR")){
            outputHandler.println("File name already exisits");
        }
        return 0;
    }
}
