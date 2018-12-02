package client.view;

import client.net.CommunicationListener;
import client.net.ServerConnection;
import common.Message;
import common.MessageDTO;
import java.net.InetSocketAddress;
import java.util.Scanner;
import static common.MsgType.*;

public class View {

    private ServerConnection server;
    private static final int port = 9999;
    private static final String host = "localhost";
    private boolean receivingUserInput = false;
    private Scanner scan = new Scanner(System.in);
    private boolean started = false;
    private boolean connected = false;
    private boolean serverError = false;
    private boolean continueState = false;

    /**
     * This method connects to the server and starts the ParseScanner thread
     */
    public void play(){
        if(receivingUserInput){
            return;
        }
        receivingUserInput = true;
        server = new ServerConnection();
        ParseScanner parseScanner = new ParseScanner();
        parseScanner.start();
    }

    /**
     * This thread listens for input from the user and sends it to the server
     */
    private class ParseScanner extends Thread{

        public void run(){
            server.addCommunicationListener(new ConsoleOutput());
            server.connect(host, port);
            while(receivingUserInput){
                String input;
                try {
                    input = scan.nextLine();
                    String[] parts = input.split(" ");
                    String part1 = parts[0];
                    if(part1.equals("username") && !continueState) {
                        String part2 = parts[1];
                        server.setMessage(new Message(USERNAME,part2));
                    }else if(part1.equals("guess") && started && !continueState){
                        String part2 = parts[1];
                        server.setMessage(new Message(GUESS,part2));
                    }else if(part1.equals("restart") && started){
                        continueState = false;
                        server.setMessage(new Message(RESTART,null));
                    }else if(part1.equals("quit") && started){
                        continueState = false;
                        server.setMessage(new Message(QUIT,null));
                    }else{
                        System.err.println("wrong input format");
                    }
                }catch (Exception e){
                    System.err.println("something wrong with input");
                }
            }
        }
    }

    /**
     * This class receives messages from the server using the observer-pattern with the
     * implemented interface
     */
    private class ConsoleOutput implements CommunicationListener {

        /**
         * Tells the user the connection with the server has been established
         * @param serverAddress InetSocketAddress to the server
         */
        @Override
        public void connected(InetSocketAddress serverAddress) {
            connected = true;
            System.out.println("connected to "+ serverAddress);
            System.out.println("Please enter your username on format: username 'x' ");
        }

        /**
         * When user types quit, this method will be called and it
         * sets boolean values so that the program will terminate.
         */
        public void disconnect(){
            receivingUserInput = false;
            connected = false;
        }

        /**
         * Displays the game state to the user with the param and updates some
         * states that controls which input the user is allowed to make.
         * @param gameState MessageDTO (the state of the game)
         */
        public void sendGameStateToView(MessageDTO gameState){
            if(gameState == null){
                serverError = true;
            }
            else if(gameState.getGameStatus().equals("Starting")){
                System.out.println("Welcome "+gameState.getUsername()+"! This is a Hangman game.\nTo make a guess type: guess 'your guess'" +
                        "\nTo quit the game type: quit\nTo restart the game type: restart\nGood luck!");
                System.out.println(gameState.getProgressUI());
                started = true;
            }else if(gameState.getGameStatus().equals("Running")){
                System.out.println();
                System.out.println(gameState.getProgressUI());
                System.out.print("Your guesses: ");
                for(int i = 0; i < gameState.getGuessesMade().length; i++){
                    if(gameState.getGuessesMade()[i] != null){
                        System.out.print(gameState.getGuessesMade()[i]+",");
                    }
                }
                System.out.println();
                System.out.println("You got "+gameState.getTriesLeft()+ " tries left" );
            }else if(gameState.getGameStatus().equals("won")){
                continueState = true;
                System.out.println();
                System.out.println("You won!");
                System.out.println(gameState.getCorrectUI());
                System.out.println("Your score are now: "+gameState.getScore());
                System.out.println("To play again type: restart\nTo disconnect type: quit");
            }else if(gameState.getGameStatus().equals("lost")) {
                continueState = true;
                System.out.println();
                System.out.println("You lost!");
                System.out.println("The correct word was: " + gameState.getCorrectUI());
                System.out.println("Your score are now: " + gameState.getScore());
                System.out.println("To play again type: restart\nTo disconnect type: quit");
            }
        }
    }

}
