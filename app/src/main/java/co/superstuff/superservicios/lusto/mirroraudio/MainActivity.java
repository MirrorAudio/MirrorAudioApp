package co.superstuff.superservicios.lusto.mirroraudio;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private Button buttonConnect;
    private Button buttonPlay;
    private StreamPlayer streamPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.editText = (EditText) this.findViewById(R.id.editText);
        this.buttonConnect = (Button) this.findViewById(R.id.buttonConnect);
        this.buttonPlay = (Button) this.findViewById(R.id.buttonPlay);

        this.streamPlayer = new StreamPlayer();
    }

    public void onPlayEvent(View view) {
        if (this.streamPlayer.isPlaying()) {
            this.stop();
        } else {
            this.play();
        }
    }

    public void onConnectEvent(View view) {
        if (this.streamPlayer.isConnected()) {
            this.buttonConnect.setText(this.getString(R.string.textButtonOn)); // al revés
            if (this.streamPlayer.isPlaying())
                this.stop();
            this.disconnect();
            this.editText.setEnabled(true);
            this.buttonPlay.setEnabled(false);
        } else {
            this.buttonConnect.setText(this.getString(R.string.textButtonOff)); // al revés
            this.editText.setEnabled(false);
            this.buttonPlay.setEnabled(true);
            this.connect();
        }
    }

    private void play() {
        Toast.makeText(this, "Reproduciendo...", Toast.LENGTH_SHORT).show();
        this.streamPlayer.play();
        this.buttonPlay.setText(this.getString(R.string.textButtonStop));
    }

    private void stop() {
        Toast.makeText(this, "Parando...", Toast.LENGTH_SHORT).show();
        this.streamPlayer.stop();
        this.buttonPlay.setText(this.getString(R.string.textButtonPlay));
    }

    private void connect() {
        Toast.makeText(this, "Conectando...", Toast.LENGTH_SHORT).show();
        String address = this.editText.getText().toString();
        Log.d("Connect", address);
        if (address.length() == 0) {
            Toast.makeText(this, "No has escrito una dirección", Toast.LENGTH_SHORT).show();
            return;
        } else {
            this.streamPlayer.connect(address);
        }
    }

    private void disconnect() {
        Toast.makeText(this, "Desconectando...", Toast.LENGTH_SHORT).show();
        if (this.streamPlayer.disconnect()) {
            Toast.makeText(this, "Desconectado", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "No se puede desconectar", Toast.LENGTH_SHORT).show();
            Log.e("Socket", "No puedo cerrar el socket...");
        }
    }
}
