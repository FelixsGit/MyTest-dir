package client.view;
import client.controller.Controller;
import client.net.OutputHandler;
import common.Message;
import common.MessageDTO;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static common.MsgType.*;

public class View {

    private Controller controller = new Controller();
    private boolean started = false;
    private boolean serverError = false;
;
    public void establishConnection() throws IOException{
        controller.establishConnection(new ConsoleOutput());
        ParseScanner parseScanner = new ParseScanner();
        parseScanner.start();
        System.out.println("Please enter your username by typing: username 'your username'.");
    }

    private class ConsoleOutput implements OutputHandler{
        @Override
        public void handleMessage(MessageDTO msg){

            if(msg == null){
                serverError = true;
            }
            else if(msg.getGameStatus().equals("Starting")){
                System.out.println("Welcome "+msg.getUsername()+"! This is a Hangman game.\nTo make a guess type: guess 'your guess'" +
                        "\nTo quit the game type: quit\nTo restart the game type: restart\nGood luck!");
                System.out.println(msg.getProgressUI());
                started = true;
            }else if(msg.getGameStatus().equals("Running")){
                System.out.println();
                System.out.println(msg.getProgressUI());
                System.out.print("Your guesses: ");
                for(int i = 0; i < msg.getGuessesMade().length; i++){
                    if(msg.getGuessesMade()[i] != null){
                        System.out.print(msg.getGuessesMade()[i]+",");
                    }
                }
                System.out.println();
                System.out.println("You got "+msg.getTriesLeft()+ " tries left" );
            }else if(msg.getGameStatus().equals("won")){
                System.out.println();
                System.out.println("You won!");
                System.out.println(msg.getCorrectUI());
                System.out.println("Your score are now: "+msg.getScore());
                System.out.println("To play again type: restart\nTo disconnect type: quit");
            }else if(msg.getGameStatus().equals("lost")) {
                System.out.println();
                System.out.println("You lost!");
                System.out.println("The correct word was: " + msg.getCorrectUI());
                System.out.println("Your score are now: " + msg.getScore());
                System.out.println("To play again type: restart\nTo disconnect type: quit");
            }

        }
    }
    private class ParseScanner extends Thread{
        private BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
        public void run(){
            try {
                String input;
                while (true) {
                    if ((input = stdIn.readLine()) != null) {
                        String[] parts = input.split(" ");
                        String part1 = parts[0];
                        if(part1.equals("username")) {
                            String part2 = parts[1];
                            controller.sendUpdate(new Message(USERNAME,part2));
                        }else if(part1.equals("guess") && started){
                            String part2 = parts[1];
                            controller.sendUpdate(new Message(GUESS,part2));
                        }else if(part1.equals("quit") && started && serverError){
                            break;
                        }else if(part1.equals("quit") && started && !serverError){
                            serverError = true;
                            controller.disconnect();
                            break;
                        }
                        else if(part1.equals("restart") && started) {
                            controller.sendUpdate(new Message(RESTART, null));
                        }else{
                            System.err.println("Wrong input format");
                        }
                    }
                }
            }catch (IOException e){
                System.err.println("Input failure");
            }
        }
    }
}
