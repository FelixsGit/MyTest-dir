package toppar.hangman_game.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import toppar.hangman_game.R;
import toppar.hangman_game.common.Message;
import static toppar.hangman_game.common.MsgType.USERNAME;


public class GameActivity extends Activity {
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
            sendUsername(username);
        }
        else{
            MainActivity.serverConnection = null;
            Intent intentNew = new Intent(GameActivity.this, MainActivity.class);
            startActivity(intentNew);
        }
    }
    private void sendUsername(String username){
        sendMessage(new Message(USERNAME, username));
    }
    private void sendMessage(Message msg){
        MainActivity.serverConnection.writeToServer(msg);
    }
}
