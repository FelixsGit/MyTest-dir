package client.startup;
import client.view.View;

public class Main {

    /**
     * Starts the client application
     * @param args nothing for now, can add ip and port for specific server
     */
    public static void main(String args[]){
        View view = new View();
        try{
            view.play();
        }
        catch (Exception e) {
            System.out.println("connection Failed");
        }
    }
}
