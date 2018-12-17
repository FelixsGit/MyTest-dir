package toppar.hangman_game.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import toppar.hangman_game.R;
import toppar.hangman_game.common.Message;
import toppar.hangman_game.common.MessageDTO;
import toppar.hangman_game.service.OutputHandler;
import static toppar.hangman_game.common.MsgType.GUESS;
import static toppar.hangman_game.common.MsgType.QUIT;
import static toppar.hangman_game.common.MsgType.RESTART;
import static toppar.hangman_game.common.MsgType.USERNAME;


public class GameActivity extends Activity {

    private String gameState;
    /**
     * This method is called when the user press the play button
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        if(MainActivity.serverConnection != null && MainActivity.serverConnection.getSocket().isConnected()){
            MainActivity.serverConnection.startListener(new ConsoleOutput());
            ParseScanner parseScanner = new ParseScanner();
            parseScanner.start();
            sendUsername(username);
        }
        else{
            MainActivity.serverConnection = null;
            Intent intentNew = new Intent(GameActivity.this, MainActivity.class);
            startActivity(intentNew);
        }
    }
    private class ParseScanner extends Thread{
        Button guessButton = findViewById(R.id.guessButton);
        Button quitButton = findViewById(R.id.quitButton);
        Button restartButton = findViewById(R.id.restartButton);
        public void run(){
            try {
                while (true) {
                    guessButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(gameState.equals("Running") || gameState.equals("Starting")){
                                String guess = ((EditText)findViewById(R.id.guessEditText)).getText().toString();
                                if(guess.length() > 0){
                                    sendGuess(guess);
                                }
                            }
                        }
                    });
                    quitButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            sendQuit();
                            MainActivity.serverConnection = null;
                            Intent intentNew = new Intent(GameActivity.this, MainActivity.class);
                            startActivity(intentNew);
                        }
                    });
                    restartButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ((TextView) findViewById(R.id.responseTextViewSTATE)).setText("");
                            sendRestart();
                        }
                    });
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private class ConsoleOutput implements OutputHandler {
        @Override
        public void handleMessage(MessageDTO msg) {
            /**
             * In this method the user receives messages send from server
             *
             */
            if (msg.getGameStatus().equals("Running") || msg.getGameStatus().equals("Starting")) {
                gameState = "Running";
                ((TextView) findViewById(R.id.responseTextViewScore)).setText("Your score: " + msg.getScore());
                ((TextView) findViewById(R.id.responseTextViewAttemptsLeft)).setText("Attempts left: " + msg.getTriesLeft());
                ((TextView) findViewById(R.id.responseTextViewAttemptsUI)).setText(msg.getProgressUI());
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < msg.getGuessesMade().length; i++) {
                    if (msg.getGuessesMade()[i] != null) {
                        sb.append(msg.getGuessesMade()[i] + ",");
                    }
                }
                ((TextView) findViewById(R.id.responseTextViewAttemptsGuessesMade)).setText("Guesses made: " + sb.toString());
            }else if(msg.getGameStatus().equals("won")){
                gameState = msg.getGameStatus();
                ((TextView) findViewById(R.id.responseTextViewScore)).setText("Your score: " + msg.getScore());
                ((TextView) findViewById(R.id.responseTextViewAttemptsUI)).setText(msg.getCorrectUI());
                ((TextView) findViewById(R.id.responseTextViewAttemptsLeft)).setText("Attempts left: " + msg.getTriesLeft());
                ((TextView) findViewById(R.id.responseTextViewSTATE)).setText("YOU WON");
            }else if(msg.getGameStatus().equals("lost")){
                gameState = msg.getGameStatus();
                ((TextView) findViewById(R.id.responseTextViewSTATE)).setText("YOU LOST");
                ((TextView) findViewById(R.id.responseTextViewScore)).setText("Your score: " + msg.getScore());
                ((TextView) findViewById(R.id.responseTextViewAttemptsLeft)).setText("Attempts left: " + msg.getTriesLeft());
                ((TextView) findViewById(R.id.responseTextViewAttemptsUI)).setText(msg.getCorrectUI());
            }
        }
    }

    private void sendUsername(String username){
        sendMessage(new Message(USERNAME, username));
    }
    private void sendGuess(String guess){
        sendMessage(new Message(GUESS, guess));
    }
    private void sendQuit(){
        sendMessage(new Message(QUIT, null));
    }
    private void sendRestart(){
        sendMessage(new Message(RESTART, null));
    }
    private void sendMessage(Message msg){
        MainActivity.serverConnection.writeToServer(msg);
    }
}
