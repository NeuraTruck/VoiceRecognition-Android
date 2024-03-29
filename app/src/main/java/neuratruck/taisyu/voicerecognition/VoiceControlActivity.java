package neuratruck.taisyu.voicerecognition;

import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.UUID;
import android.speech.RecognitionListener;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import androidx.annotation.NonNull; // NonNull アノテーションのインポート


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
        textViewResult = findViewById(R.id.textViewResult);
        if (textViewResult == null) {
            Log.d("VoiceControlActivity", "textViewResult is null");
        } else {
            Log.d("VoiceControlActivity", "textViewResult is not null");
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, YOUR_REQUEST_CODE);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, YOUR_AUDIO_PERMISSION_REQUEST_CODE);
        }

        // Bluetoothアダプタとデバイスの設定
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        m5StackDevice = bluetoothAdapter.getRemoteDevice("B8:F0:09:C5:24:FE");

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
        speechRecognizer.setRecognitionListener(recognitionListener);

        // ボタンの設定
        btnControl = findViewById(R.id.btnStart);
        btnControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("VoiceControl", "Button clicked, starting voice recognition...");
                startVoiceRecognition();
            }
        });
    }

    private void startVoiceRecognition() {
        Log.d("VoiceControlActivity", "startVoiceRecognition called");

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        speechRecognizer.startListening(intent);
    }

    private void sendCommandViaBluetooth(String command) {
        Log.d("VoiceControlActivity", "Sending command via Bluetooth: " + command);

        String convertedCommand = convertVoiceCommandToNumber(command) + "\n"; // 音声コマンドを数字に変換
        try {
            if (outputStream != null) {
//                String commandWithNewLine = command + "\n"; // 改行文字を追加
//                outputStream.write(commandWithNewLine.getBytes());
                outputStream.write(convertedCommand.getBytes()); // 数字を文字列として送信
                Log.d("VoiceControlActivity", "Command sent: " + convertedCommand); // 送信成功時のログ出力
            }
        } catch (Exception e) {
            Log.e("VoiceControlActivity", "Error sending command: ", e); // 送信失敗時のログ出力
            e.printStackTrace();
        }
    }

    private String convertVoiceCommandToNumber(String voiceCommand) {
        switch (voiceCommand.toLowerCase()) {
            case "go":
                return "1";
            case "stop":
                return "2";
            case "back":
                return "3";
            case "turn right":
                return "4";
            case "turn left":
                return "5";
            default:
                return null; // 未知のコマンドの場合はnullを返す
        }
    }

    private final RecognitionListener recognitionListener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle results) {
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if (matches != null && !matches.isEmpty()) {
                String command = matches.get(0);
//                if (command.equalsIgnoreCase("Go") || command.equalsIgnoreCase("Stop") || command.equalsIgnoreCase("Left") || command.equalsIgnoreCase("Right") || command.equalsIgnoreCase("Back")) {
                    sendCommandViaBluetooth(command);
  //              }
            }
        }

        // その他のオーバーライドメソッド
        @Override
        public void onBeginningOfSpeech() {}

        @Override
        public void onRmsChanged(float rmsdB) {}

        @Override
        public void onBufferReceived(byte[] buffer) {}

        @Override
        public void onEndOfSpeech() {}

        @Override
        public void onError(int error) {
            Log.d("VoiceRecognition", "Error occurred: " + error);
            if (error == SpeechRecognizer.ERROR_NO_MATCH || error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
                // 音声認識を再開
                startVoiceRecognition();
            }        }

        @Override
        public void onResults(Bundle results) {
            ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            if (matches != null && !matches.isEmpty()) {
                // 結果の取得と表示
                final String resultText = matches.get(0); // 最も確信度が高い結果を取得
                Log.d("VoiceControlActivity", "Recognized Text: " + resultText);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (textViewResult != null) {
                            textViewResult.setText(resultText);
                        }
                    }
                });
/*
                String command = matches.get(0); // 最も信頼度の高い結果を取得
                if ("Go".equalsIgnoreCase(command) || "Stop".equalsIgnoreCase(command)) {
                    textViewResult.setText(command); // 結果をTextViewに表示
                } else {
                    textViewResult.setText(""); // 認識された単語が条件に一致しない場合は何も表示しない
                }
*/
                Log.d("VoiceControlActivity", "Calling sendCommandViaBluetooth with: " + resultText);
                sendCommandViaBluetooth(resultText);
            }
        }

        @Override
        public void onPartialResults(Bundle partialResults) {}

        @Override
        public void onEvent(int eventType, Bundle params) {}
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // パーミッションが付与された場合の処理
                } else {
                    // パーミッションが拒否された場合の処理
                    // 例えば、機能の使用を制限したり、ユーザーに説明を表示するなど
                    Toast.makeText(this, "音声録音のパーミッションが必要です。", Toast.LENGTH_SHORT).show();
                    finish(); // アプリを終了
                }
                break;
        }
    }

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private static final int YOUR_REQUEST_CODE = 1; // または他の任意の整数
    private static final int YOUR_AUDIO_PERMISSION_REQUEST_CODE = 1;

    private TextView textViewResult;

}