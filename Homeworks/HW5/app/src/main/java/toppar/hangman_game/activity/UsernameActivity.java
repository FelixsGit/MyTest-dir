package toppar.hangman_game.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import toppar.hangman_game.R;


public class UsernameActivity extends Activity {

    /**
     * This method is called when the user connects to the server
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_username);
        Button playButton = findViewById(R.id.playButton);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlayButtonClicked(v);
            }
        });
    }
    private void onPlayButtonClicked(View view) {
        String username = ((EditText)findViewById(R.id.usernameEditText)).getText().toString();
        Intent intent = new Intent(getApplicationContext(), GameActivity.class);
        intent.putExtra("username", username);
        startActivity(intent);
    }
}
