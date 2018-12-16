package toppar.hangman_game.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import toppar.hangman_game.R;


public class GameActivity extends AppCompatActivity {

    /**
     * This method is called when the user press the play button
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        Intent i = getIntent();
        String username = i.getStringExtra("username");
        ((TextView) findViewById(R.id.responseTextViewUsername)).setText("Entered username: "+username);
    }
}
