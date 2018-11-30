package client.view;

import common.Catalog;

import java.util.Scanner;

public class View implements Runnable{

    private final Scanner scan = new Scanner(System.in);
    private boolean receiveUserInput = false;
    private Catalog catalog;

    public void start(Catalog catalog){
        this.catalog = catalog;
        receiveUserInput = true;
        new Thread(this).start();
    }

    public void run(){
        while(receiveUserInput){
            String userMsg = scan.nextLine();
            switch(userMsg){
                case "new":
                    catalog.createNewAccount("Felix", "123");
            }
        }
    }
}
