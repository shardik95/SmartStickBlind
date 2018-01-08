package com.project.sanveg.smartstick;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Sanny on 12/29/2016.
 */

public class MainActivity extends Activity {
    public final String ACTION_USB_PERMISSION = "com.project.sanveg.smartstick.USB_PERMISSION";
    private final int REQ_CODE_SPEECH_INPUT = 100;

    Button startButton, stopButton, clearButton;
    TextView distText, navText, calibText;
    UsbManager usbManager;
    UsbDevice device;
    UsbSerialDevice serialPort;
    UsbDeviceConnection connection;
    Vibrator var;
    Context context;
    Boolean calibStat;
    TextToSpeech speech;
    ImageView swipeImage;
    MediaPlayer mPlayer;

    //Defining a Callback which triggers whenever data is read.
    UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onReceivedData(byte[] arg0) {
            String data;
            try {
                data = new String(arg0, "ASCII");
                String newData = data.trim();
                Log.i("Arduino data", data);

                //data.concat("/n");
                //tvAppend(distText,data);
                //!data.isEmpty() || data != null

                // Checks if the calibration is complete.
                if (newData.equals("D")) {
                    Log.i("Arduino data", "Calib");
                    onCalibration(calibText);
                    String speakText = getResources().getString(R.string.calib_done);
                    speech.speak(speakText, TextToSpeech.QUEUE_FLUSH, null, null);
                    calibStat = true;
                }

                //Runs only after calibration done
                if (calibStat) {
                    if (data.equals("P")) {
                        tvAppend(distText, "Pothole detected\n");
                        // Vibrate for 500 milliseconds
                        var.vibrate(500);
                        String speakText = getResources().getString(R.string.pothole);
                        speech.speak(speakText, TextToSpeech.QUEUE_FLUSH, null, null);
                    }

                    if (data.equals("O")) {
                        tvAppend(distText, "Obstacle ahead\n");
                        // Vibrate for 500 milliseconds
                        var.vibrate(500);
                        String speakText = getResources().getString(R.string.obstacle_ahead);
                        speech.speak(speakText, TextToSpeech.QUEUE_FLUSH, null, null);
                    }

                    if (data.equals("N")) {
                        tvAppend(distText, "Obstacle close ahead\n");
                        var.vibrate(1000);
                    }

                    if (data.equals("B")) {
                        tvAppend(distText, "Blocked Ahead\n");
                        String speakText = getResources().getString(R.string.block_ahead);
                        speech.speak(speakText, TextToSpeech.QUEUE_FLUSH, null, null);
                    }

                    if (data.equals("R")) {
                        tvAppend(distText, "Left clear\n");
                        String speakText = getResources().getString(R.string.obstacle_right);
                        speech.speak(speakText, TextToSpeech.QUEUE_FLUSH, null, null);
                    }

                    if (data.equals("L")) {
                        tvAppend(distText, "Right clear\n");
                        String speakText = getResources().getString(R.string.obstacle_left);
                        speech.speak(speakText, TextToSpeech.QUEUE_FLUSH, null, null);
                    }

                    if (data.equals("A")) {
                        tvAppend(distText, "Turn obstacle ahead\n");
                        var.vibrate(500);
                        String speakText = getResources().getString(R.string.side_clear);
                        speech.speak(speakText, TextToSpeech.QUEUE_FLUSH, null, null);
                    }

                    if (data.equals("P")) {
                        tvAppend(distText, "Pothole detected\n");
                        var.vibrate(500);
                        String speakText = getResources().getString(R.string.pothole);
                        speech.speak(speakText, TextToSpeech.QUEUE_FLUSH, null, null);
                    }
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    };

    //Broadcast Receiver to automatically start and stop the Serial connection.
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_USB_PERMISSION)) {
                boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                if (granted) {
                    connection = usbManager.openDevice(device);
                    serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
                    if (serialPort != null) {
                        if (serialPort.open()) { //Set Serial Connection Parameters.
                            enableUi(true);
                            serialPort.setBaudRate(9600);
                            serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                            serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                            serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                            serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                            serialPort.read(mCallback);
                            Toast.makeText(MainActivity.this, "Serial Connection Opened!", Toast.LENGTH_SHORT).show();

                        } else {
                            Log.d("SERIAL", "PORT NOT OPEN");
                            Toast.makeText(MainActivity.this, "Serial PORT NOT OPEN!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.d("SERIAL", "PORT IS NULL");
                        Toast.makeText(MainActivity.this, "Serial PORT IS NULL!", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Log.d("SERIAL", "PERM NOT GRANTED");
                }
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
                onClickStart(startButton);
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                onClickStop(stopButton);

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        usbManager = (UsbManager) getSystemService(MainActivity.this.USB_SERVICE);
        usbManager = (UsbManager) getSystemService(USB_SERVICE);
        startButton = (Button) findViewById(R.id.start_service);
        clearButton = (Button) findViewById(R.id.clear_dist);
        stopButton = (Button) findViewById(R.id.stop_service);
        distText = (TextView) findViewById(R.id.distance_text_view);
        navText = (TextView) findViewById(R.id.nav_text);
        calibText = (TextView) findViewById(R.id.calibrate_text);
        swipeImage = (ImageView) findViewById(R.id.swipe_image);
        mPlayer = MediaPlayer.create(MainActivity.this, R.raw.start_instruction);

        calibStat = false;

        //Read instructions
        readInstructions();

        //Speech initialization

        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, 1234);

        speech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    speech.setLanguage(Locale.UK);
                }
            }
        });

        enableUi(false);
        context = getApplicationContext();

        //Vibrator
        var = (Vibrator) this.context.getSystemService(Context.VIBRATOR_SERVICE);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(broadcastReceiver, filter);

        final GestureDetector.SimpleOnGestureListener listener = new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                promptSpeechInput();
                return true;
            }
        };

        final GestureDetector detector = new GestureDetector(context, listener);

        detector.setOnDoubleTapListener(listener);
        detector.setIsLongpressEnabled(true);

        getWindow().getDecorView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                return detector.onTouchEvent(event);
            }
        });

        swipeImage.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this) {
            public void onSwipeTop() {
                launchGesture();
            }

            public void onSwipeRight() {
                launchGesture();
            }

            public void onSwipeLeft() {
                readInstructions();
            }

            public void onSwipeBottom() {
                launchGesture();
            }

        });



    }

    public void readInstructions(){
        if(mPlayer.isPlaying()){
            mPlayer.pause();
        }
        mPlayer.start();
    }

    public void launchGesture(){
        if(mPlayer.isLooping()){
            mPlayer.pause();
        }
        mPlayer.pause();
        Thread logoTimer = new Thread() {
            public void run() {
                try {
                    sleep(500);
                    String speakText = getResources().getString(R.string.gesture);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        speech.speak(speakText, TextToSpeech.QUEUE_FLUSH, null, null);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(MainActivity.this, GestureActivity.class);
                startActivity(intent);
            }
        };
        logoTimer.start();
    }

    public void enableUi(boolean flag) {
        if (!flag) {
            startButton.setVisibility(View.VISIBLE);
            stopButton.setVisibility(View.GONE);
        } else {
            startButton.setVisibility(View.GONE);
            stopButton.setVisibility(View.VISIBLE);
        }
        distText.setEnabled(flag);
    }

    public void onClickInstructions(View view){ onStartInstructions();}

    public void onStartInstructions(){
        readInstructions();
    }

    public void onClickStart(View view) {
        onStartRead();
    }

    public void onStartRead(){
        String speakText = getResources().getString(R.string.sensor_start);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            speech.speak(speakText, TextToSpeech.QUEUE_FLUSH, null, null);
        }

        HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
        if (!usbDevices.isEmpty()) {
            boolean keep = true;
            for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                device = entry.getValue();
                int deviceVID = device.getVendorId();
                if (deviceVID == 0x2341)//Arduino Vendor ID
                {
                    PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
                    usbManager.requestPermission(device, pi);
                    keep = false;
                } else {
                    connection = null;
                    device = null;
                }

                if (!keep)
                    break;
            }
        }
    }

    public void onClickStop(View view) {
        enableUi(false);
        serialPort.close();
        String speakText = getResources().getString(R.string.sensor_stop);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            speech.speak(speakText, TextToSpeech.QUEUE_FLUSH, null, null);
        }

    }

    public void onClickClear(View view) {
        distText.setText("");
        navText.setText("");
    }

    public void onClickSetting(View view) {
        Intent settingIntent = new Intent(this, Settings.class);
        startActivity(settingIntent);
    }

    private void tvAppend(TextView tv, CharSequence text) {
        final TextView ftv = tv;
        final CharSequence ftext = text;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ftv.setText("");
                ftv.append(ftext);
            }
        });
    }

    private void onCalibration(TextView tv) {
        final TextView ftv = tv;
        final CharSequence calibDone = getResources().getString(R.string.calib_done);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ftv.setText("");
                ftv.append(calibDone);
            }
        });
    }


    // Function to activate speech input
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Receiving speech input
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    // txtSpeechInput.setText(result.get(0));
                    String s = result.get(0);
                    String a[] = s.split(" ");
                    String destination = " ";
                    for (String anA : a) {
                        destination = destination + anA + "+";
                    }
                    destination = destination.substring(0, destination.length() - 1);
                    navText.setText(destination);
                    destination = "google.navigation:q=" + destination + "&mode=walking";
                    try {
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(destination));
                        startActivity(intent);
                    } catch (ActivityNotFoundException noMaps) {
                        String speakText = getResources().getString(R.string.no_map);
                        Toast.makeText(this, speakText, Toast.LENGTH_LONG).show();
                        speech.speak(speakText, TextToSpeech.QUEUE_FLUSH, null, null);
                    }
                }
                break;
            }
        }
    }
}
