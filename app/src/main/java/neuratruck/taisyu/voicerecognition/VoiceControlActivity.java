package neuratruck.taisyu.voicerecognition;

import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.View;
import android.widget.Button;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;

public class VoiceControlActivity extends Activity {
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice m5StackDevice;
    private BluetoothSocket btSocket;
    private OutputStream outputStream;
    private SpeechRecognizer speechRecognizer;
    private Button btnControl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_control);

        // Bluetoothアダプタとデバイスの設定
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        m5StackDevice = bluetoothAdapter.getRemoteDevice("M5Stack_MAC_Address");

        // Bluetoothソケットの設定
        try {
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
            btSocket = m5StackDevice.createRfcommSocketToServiceRecord(uuid);
            btSocket.connect();
            outputStream = btSocket.getOutputStream();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 音声認識の設定
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        // ボタンの設定
        btnControl = findViewById(R.id.btnControl);
        btnControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceRecognition();
            }
        });
    }

    private void startVoiceRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizer.startListening(intent);
    }

    private void sendCommandViaBluetooth(String command) {
        try {
            if (outputStream != null) {
                outputStream.write(command.getBytes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final RecognitionListener recognitionListener = new RecognitionListener() {
        @Override
        public void onResults(Bundle results) {
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if (matches != null) {
                String command = matches.get(0);
                if (command.equalsIgnoreCase("Go") || command.equalsIgnoreCase("Stop") || command.equalsIgnoreCase("Left") || command.equalsIgnoreCase("Right") || command.equalsIgnoreCase("Back")) {
                    sendCommandViaBluetooth(command);
                }
            }
        }

        // その他のオーバーライドメソッド
    };
}
