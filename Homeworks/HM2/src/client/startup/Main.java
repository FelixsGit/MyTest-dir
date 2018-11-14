package client.startup;
import client.view.View;

public class Main {

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
