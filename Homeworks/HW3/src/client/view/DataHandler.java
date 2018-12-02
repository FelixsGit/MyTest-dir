package client.view;

import common.FileDTO;
import java.util.ArrayList;
import java.util.Scanner;

class DataHandler {

    private Scanner scan = new Scanner(System.in);
    private OutputHandler outputHandler;

    DataHandler(OutputHandler outputHandler){
        this.outputHandler = outputHandler;
    }

    int optionsOnFile(String fileName, String owner, int permission,  String myName){
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
        }
        else if(owner.equals(myName)){
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

    void printFileData(ArrayList<FileDTO> fileContainer){
            for (int i = 0; i < fileContainer.size(); i++) {
                FileDTO file = fileContainer.get(i);
                outputHandler.println("name = "+file.getName()+", size = "+file.getSize()+", owner = "+file.getOwner()+", permission = "+file.getPermission());
            }
    }
}
