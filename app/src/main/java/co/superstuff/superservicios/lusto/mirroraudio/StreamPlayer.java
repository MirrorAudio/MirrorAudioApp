package co.superstuff.superservicios.lusto.mirroraudio;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

/**
 * Esta clase se encarga de abrir una conexión socket con una dirección de host,
 * luego se pondrá a escuchar continuamente para ir reproduciendo (si se le permite)
 * lo que va recibiendo.
 */
public class StreamPlayer implements Runnable {
    private Thread thread;
    private Socket socket;
    private InputStream is;
    private AudioTrack audioTrack;
    private String address;
    private int bufsize;
    private boolean playing;

    public StreamPlayer() {
        // Iniciamos cosas básicas
        this.playing = false;
        this.bufsize = AudioTrack.getMinBufferSize(
                16000,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT);
        this.audioTrack = new AudioTrack(
                AudioManager.STREAM_MUSIC,
                16000,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT,
                this.bufsize,
                AudioTrack.MODE_STREAM);
        // Iniciamos el hilo secundario
        this.thread = new Thread(this);
        this.thread.start();
    }

    public boolean isConnected() {
        if (this.socket == null)
            return false;
        return this.socket.isConnected();
    }

    public boolean connect(String address) {
        this.address = address;
        if (this.address == null || this.address.length() == 0) {
            Log.e("StreamPlayer", "No se ha proporcionado una dirección");
            return false;
        }
        // Miramos si se desconecta bien
        if (this.isConnected()) {
            boolean success = this.disconnect();
            if (!success) {
                Log.e("StreamPlayer", "No se puede desconectar el socket");
                return false;
            }
        }
        return true;
    }

    public boolean disconnect() {
        if (this.socket == null)
            return true;
        try {
            this.socket.close();
            if (this.is != null)
                this.is.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean play() {
        try {
            this.audioTrack.play();
            this.playing = true;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean stop() {
        try {
            this.audioTrack.stop();
            this.playing = false;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public boolean isPlaying() {
        return this.playing;
    }

    @Override
    public void run() {
        while (true) {
            if (this.socket == null && this.address != null && this.address.length() != 0) {
                // Volvemos a crear el objeto
                try {
                    this.socket = new Socket(this.address, 28118);
                    this.is = this.socket.getInputStream();
                } catch (IOException e) {
                    Log.e("StreamPlayer", "No se puede conectar a la dirección: " + this.address);
                    e.printStackTrace();
                }
            } else if (this.socket != null) {
                byte[] buffer = new byte[this.bufsize];
                while (this.isConnected()) {
                    try {
                        this.is.read(buffer, 0, this.bufsize);
                        this.audioTrack.write(buffer, 0, buffer.length);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
