package toppar.hangman_game.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import toppar.hangman_game.R;
import toppar.hangman_game.service.ServerConnection;

public class MainActivity extends Activity {

    public static ServerConnection serverConnection;

    /**
     * This method is called when the app is started
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button connectButton = findViewById(R.id.connectButton);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onConnectButtonClicked(v);
            }
        });
    }

    /**
     * This method is called when the 'connect'  button is clicked
     * @param view
     */
    private void onConnectButtonClicked(View view){
        String serverAddress = ((EditText)findViewById(R.id.addressEditText)).getText().toString();
        String serverPort = ((EditText)findViewById(R.id.portEditText)).getText().toString();
        new SocketConnection().execute(serverAddress, serverPort);
    }

    /**
     * This class is responsible for  trying to connect with the server  on the
     * entered address and  port
     */
    private class SocketConnection extends AsyncTask<String, Void, ServerConnection> {

        @Override
        protected ServerConnection doInBackground(String... configs) {
            String serverAddress = configs[0];
            String serverPort = configs[1];
            serverConnection = new ServerConnection();
            serverConnection.connect(serverAddress, serverPort);
            return serverConnection;
        }

        @Override
        protected void onPostExecute(ServerConnection serverConnection) {
            if(serverConnection != null && serverConnection.getSocket() != null && serverConnection.getSocket().isConnected()){
                Intent intent = new Intent(getApplicationContext(), UsernameActivity.class);
                startActivity(intent);
            }
            else {
                if(serverConnection != null && serverConnection.getConnectionStatus() != null && !serverConnection.getConnectionStatus().isEmpty()){
                    ((TextView) findViewById(R.id.responseTextView)).setText("Connection Error: "+serverConnection.getConnectionStatus());
                }
            }
        }
    }
}