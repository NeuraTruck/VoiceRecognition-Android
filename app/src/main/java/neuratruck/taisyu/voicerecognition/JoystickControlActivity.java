/*
package neuratruck.taisyu.voicerecognition;

import io.github.controlwear.virtual.joystick.android.JoystickView;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.OutputStream;
import java.util.UUID;

import java.io.IOException;
import android.util.Log;

public class JoystickControlActivity extends Activity{
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice m5StackDevice;
    private BluetoothSocket btSocket;
    private OutputStream outputStream;

    private JoystickView joystickLeft, joystickRight;
    //private TextView textViewLeft, textViewRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_joystick_control);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, YOUR_REQUEST_CODE);
        }

        // Bluetoothアダプタとデバイスの設定
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        m5StackDevice = bluetoothAdapter.getRemoteDevice("B8:F0:09:C5:24:FE");

        // Bluetoothソケットの設定
        try {
            UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
            btSocket = m5StackDevice.createRfcommSocketToServiceRecord(uuid);
            btSocket.connect();
            outputStream = btSocket.getOutputStream();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("JoystickControlActivity", "Bluetooth接続に失敗しました: " + e.getMessage());
        }

// 左スティックのStrengthを表示するTextView
        //TextView strengthTextViewLeft = findViewById(R.id.strengthTextViewLeft);

// 左スティックのJoystickView
        JoystickView joystickLeft = findViewById(R.id.joystickLeft);

        joystickLeft.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                if (strength == 0) {
                    // ジョイスティックが中立位置に戻ったときの処理
                    leftJoystickValue = 0;
                    sendValueViaBluetooth(0, rightJoystickValue);
                } else {

                    // 角度をラジアンに変換
                    double radians = Math.toRadians(angle);

                    // Y軸の成分を計算
                    double yComponent = strength * Math.sin(radians);

                    // Y軸の成分を 0 から 255 の範囲に変換
                    leftJoystickValue = (int) (((yComponent) / 200) * 255);

                    // Strengthが変更されたときにTextViewに新しい値を設定
                    //strengthTextViewLeft.setText("Strength: " + strength);
                    sendValueViaBluetooth(leftJoystickValue, rightJoystickValue);
                }
            }
        });
// 右スティックのStrengthを表示するTextView
        //TextView strengthTextViewRight = findViewById(R.id.strengthTextViewRight);

// 右スティックのJoystickView
        JoystickView joystickRight = findViewById(R.id.joystickRight);
        joystickRight.setOnMoveListener(new JoystickView.OnMoveListener() {
            @Override
            public void onMove(int angle, int strength) {
                if (strength == 0) {
                    // ジョイスティックが中立位置に戻ったときの処理
                    rightJoystickValue = 0;
                    sendValueViaBluetooth(leftJoystickValue, 0);
                } else {

                    // 角度をラジアンに変換
                    double radians = Math.toRadians(angle);

                    // Y軸の成分を計算
                    double yComponent = strength * Math.sin(radians);

                    // Y軸の成分を 0 から 255 の範囲に変換
                    rightJoystickValue = (int) (((yComponent) / 200) * 255);

                    // Strengthが変更されたときにTextViewに新しい値を設定
                    //strengthTextViewRight.setText("Strength: " + strength);
                    sendValueViaBluetooth(leftJoystickValue, rightJoystickValue);
                }
            }
        });
        // Initialize Bluetooth connection (similar to your existing code)
        setupBluetoothConnection();

    }

    private void setupBluetoothConnection() {
        // Your existing Bluetooth setup code
    }

    private int leftJoystickValue = 0;
    private int rightJoystickValue = 0;

    private int strengthToValue(int strength) {
        // 0〜100のStrengthを-128〜128の範囲に変換
        return (int) ((strength / 100.0) * 255) - 128;
    }

    private int calculateValue(int angle, int strength) {
        // Calculate the value based on angle and strength
        // This is a simple example. You'll need to adjust the logic based on your specific needs
        int calculatedValue = (int)(255 * (strength / 100.0));
        if (angle > 180) {
            calculatedValue *= -1;
        }
        return calculatedValue;
    }

    private void sendValueViaBluetooth(int leftValue, int rightValue) {
        try {
            if (outputStream != null) {
                byte[] data = new byte[2];
                data[0] = (byte) (leftValue & 0xFF);
                data[1] = (byte) (rightValue & 0xFF);
                outputStream.write(data);
                Log.d("JoystickControlActivity", "Sending data: Left=" + leftValue + ", Right=" + rightValue);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("JoystickControlActivity", "データ送信に失敗しました: " + e.getMessage());
        }
    }

    private static final int YOUR_REQUEST_CODE = 1; // または他の任意の整数
}

 */