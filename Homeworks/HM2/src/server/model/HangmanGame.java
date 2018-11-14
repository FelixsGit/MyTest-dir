package server.model;

import common.MessageDTO;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;


public class HangmanGame {

    private String word;
    File f = new File("src/server/resources/words.txt");
    private char[] charWord;
    private String[] uniqueGuesses;
    private boolean letterGuess;
    private boolean wordGuess;
    private StringBuilder wrongUI;
    private StringBuilder correctUI;
    private int counter;
    private int[] correctInt;
    private int numberOfTriesLeft;
    private int numberOfUniqueGuesses;
    private String gameStatus;
    private String username;
    private MessageDTO messageDTO;
    private ServerLogs logs = new ServerLogs();
    private int score;

    public MessageDTO newGame() throws FileNotFoundException {
        int numberOfWords = 0;
        Random r = new Random();
        int low = 0;
        int high = 51528;
        int result = r.nextInt(high - low) + low;
        Scanner scan2 = new Scanner(f);
        scan2.useDelimiter("[^A-Za-z]");
        while (scan2.hasNextLine()) {
            numberOfWords++;
            String chosenWord = scan2.next();
            //Choosing a word, it must have a length longer than 3
            if (numberOfWords >= result && chosenWord.length() > 3) {
                word = chosenWord.toLowerCase();
                String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
                logs.appendEntry(timeStamp+": user @"+username+" started a game with the word '"+word+"'.\n");
                break;
            }

        }
        correctInt = new int[10];
        wrongUI = new StringBuilder();
        correctUI = new StringBuilder();
        charWord = word.toCharArray();
        numberOfTriesLeft = word.length();
        uniqueGuesses = new String[word.length() * 4];
        numberOfUniqueGuesses = 0;
        counter = 0;
        constructWrongUI();
        constructCorrectUI();
        gameStatus = "Starting";
        messageDTO = new MessageDTO(wrongUI.toString(),numberOfTriesLeft,uniqueGuesses,
                gameStatus,correctUI.toString(),username, score);
        return this.messageDTO;
    }
    public void changeUsername(String username){
        this.username = username;
    }
    public String getUsername()throws NullPointerException{
        return messageDTO.getUsername();
    }
    public MessageDTO makeGuess(String guess){
        this.messageDTO.setGameStatus("Running");
        if(guess.length() > 1){
            wordGuess = true;
            letterGuess = false;
        }else{
            letterGuess = true;
            wordGuess = false;
        }
        return updateGameState(guess);

    }
    private MessageDTO updateGameState(String guess) {
        if (guess.equals(word)) {
            messageDTO.setScore(messageDTO.getScore() + 1);
            messageDTO.setGameStatus("won");
        }else if (wordGuess) {
            if (guessAlreadyMade(guess)) {

            } else {
                numberOfTriesLeft--;
                if (numberOfTriesLeft == 0) {
                    addUniqueGuess(guess);
                    messageDTO.setGameStatus("lost");
                    messageDTO.setScore(messageDTO.getScore() - 1);
                } else {
                    addUniqueGuess(guess);
                }
            }
        }else{
            if(guessAlreadyMade(guess)) {
            }else if(checkForMatch(guess)) {
                correctLetter(guess);
                for (int i = 0; i < counter; i++) {
                    wrongUI.deleteCharAt(correctInt[i]);
                    wrongUI.insert(correctInt[i], guess);
                }
                counter = 0;
                if (wrongUI.toString().equals(correctUI.toString())) {
                    messageDTO.setGameStatus("won");
                    messageDTO.setScore(messageDTO.getScore() + 1);
                    addUniqueGuess(guess);
                } else {
                    addUniqueGuess(guess);
                }
            }else{
                numberOfTriesLeft--;
                if (numberOfTriesLeft == 0) {
                    addUniqueGuess(guess);
                    messageDTO.setGameStatus("lost");
                    messageDTO.setScore(messageDTO.getScore() - 1);
                }
                addUniqueGuess(guess);
            }

        }
        score = messageDTO.getScore();
        messageDTO.setTriesLeft(numberOfTriesLeft);
        messageDTO.setGuessesMade(uniqueGuesses);
        messageDTO.setProgressUI(wrongUI.toString());
        return messageDTO;
    }

    private boolean guessAlreadyMade(String guess){
        if(uniqueGuesses.length == 0){
            return false;
        }
        for(int i = 0; i < uniqueGuesses.length; i++){
            if(guess.equals(uniqueGuesses[i])){
                return true;
            }
        }
        return false;
    }
    private boolean checkForMatch(String guess){
        if(word.contains(guess)){
            return true;
        }else{
            return false;
        }
    }
    private int[] correctLetter(String guess){
        boolean letterFound = false;
        counter = 0;
        for(int i = 0; i < (word.length()*2)-1; i++){
            if(correctUI.toString().charAt(i) == guess.charAt(0)){
                correctInt[counter] = i;
                counter ++;
                letterFound = true;
            }
        }
        if(letterFound){
            return correctInt;
        }
        return null;
    }
    private void addUniqueGuess(String guess){
        uniqueGuesses[numberOfUniqueGuesses] = guess;
        numberOfUniqueGuesses++;
    }
    private void constructWrongUI(){
        for(int i = 0; i < (word.length()*2)-1; i++){
            if(i % 2 == 0) {
                wrongUI.append("_");
            }
            else
                wrongUI.append(",");
        }
    }
    private void constructCorrectUI(){
        for(int i = 0; i < (word.length()*2) -1; i++){
            if(i % 2 == 0) {
                correctUI.append(charWord[i/2]);
            }
            else
                correctUI.append(",");
        }
    }
}

