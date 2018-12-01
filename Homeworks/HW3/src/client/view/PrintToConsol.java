package client.view;

import common.MsgContainerDTO;

import java.util.LinkedList;
import java.util.Scanner;

public class PrintToConsol{

    private Scanner scan = new Scanner(System.in);

    public int optionsOnFile(int permission, String fileName, int authorID, int myID){
        if(permission == 1){
            while(true) {
                System.out.println("MODIFY, DELETE or KEEP file: " + fileName);
                String commandOnFile = scan.nextLine();
                if (commandOnFile.equals("MODIFY")) {
                    return 1;
                }else if(commandOnFile.equals("DELETE")){
                    return 2;
                }else if(commandOnFile.equals("KEEP")){
                    return 0;
                }
            }
        }else if(authorID == myID){
            while(true) {
                System.out.println("MODIFY or DELETE or KEEP file: " + fileName);
                String commandOnFile = scan.nextLine();
                if (commandOnFile.equals("MODIFY")) {
                    return 1;
                }else if(commandOnFile.equals("DELETE")){
                    return 2;
                }else if(commandOnFile.equals("KEEP")){
                    return 0;
                }
            }
        }
        return 0;
    }

    public int extractMessage(MsgContainerDTO msgContainer){
        if(msgContainer.getStatus().equals("TAKEN")){
            System.out.println("username already taken");
        }else if(msgContainer.getStatus().equals("OK")) {
            System.out.println("action complete");
            LinkedList<String> container = msgContainer.getMsg();
            for (int i = 0; i < container.size(); i++) {
                System.out.println(container.get(i));
            }
            container.clear();
        }else if(msgContainer.getStatus().equals("VALID")){
            System.out.println("You are now logged in");
            LinkedList<String> container = msgContainer.getMsg();
            for (int i = 0; i < container.size(); i++) {
                return Integer.parseInt(container.get(i));
            }
            container.clear();
        }else if(msgContainer.getStatus().equals("NOTVALID")){
            System.out.println("Wrong username or password");
        }else if(msgContainer.getStatus().equals("ERROR")){
            System.out.println("failed to create file");
        }else if(msgContainer.getStatus().equals("NAMEERROR")){
            System.out.println("File name already exisits");
        }
        return 0;
    }
}
