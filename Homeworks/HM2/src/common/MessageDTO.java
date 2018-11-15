package common;

import java.io.Serializable;

/**
 * A DTO that is the game state object that the server are sending to the client
 */

public class MessageDTO implements Serializable{

    private String progressUI;
    private int triesLeft;
    private String[] guessesMade;
    private String gameStatus;
    private String correctUI;
    private String username;
    private int score;

    public MessageDTO(String progressUI,int triesLeft, String[] guessesMade,
                      String gameStatus, String correctUI, String username, int score){
        this.progressUI = progressUI;
        this.triesLeft = triesLeft;
        this.guessesMade = guessesMade;
        this.gameStatus = gameStatus;
        this.correctUI = correctUI;
        this.username = username;
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getProgressUI() {
        return progressUI;
    }

    public void setProgressUI(String progressUI) {
        this.progressUI = progressUI;
    }

    public int getTriesLeft() {
        return triesLeft;
    }

    public void setTriesLeft(int triesLeft) {
        this.triesLeft = triesLeft;
    }

    public String[] getGuessesMade() {
        return guessesMade;
    }

    public void setGuessesMade(String[] guessesMade) {
        this.guessesMade = guessesMade;
    }

    public String getGameStatus() {
        return gameStatus;
    }

    public void setGameStatus(String gameStatus) {
        this.gameStatus = gameStatus;
    }

    public String getCorrectUI() {
        return correctUI;
    }

    public void setCorrectUI(String correctUI) {
        this.correctUI = correctUI;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
