package com.project.sanveg.smartstick;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class GestureActivity extends AppCompatActivity implements GestureOverlayView.OnGesturePerformedListener {
    GestureLibrary mLibrary;
    TextToSpeech speech;
    public SharedPreferences emergenceContacts;
    private String emgName1, emgName2, emgName3;
    private String emgCont1, emgCont2, emgCont3;
    static final Integer CALL = 0x2;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture);

        mLibrary = GestureLibraries.fromRawResource(this, R.raw.gesture_file);
        if (!mLibrary.load()) {
            finish();
        }

        GestureOverlayView gestures = (GestureOverlayView) findViewById(R.id.gestures);
        gestures.addOnGesturePerformedListener(this);

        //Speech initialization
        speech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    speech.setLanguage(Locale.UK);
                }
            }
        });

        emergenceContacts = getSharedPreferences("Emergence_Contacts", Context.MODE_PRIVATE);

        emgName1 = emergenceContacts.getString("EmgNameOne", "");
        emgCont1 = emergenceContacts.getString("EmgNumberOne", "");

        emgName2 = emergenceContacts.getString("EmgNameTwo", "");
        emgCont2 = emergenceContacts.getString("EmgNumberTwo", "");

        emgName3 = emergenceContacts.getString("EmgNameThree", "");
        emgCont3 = emergenceContacts.getString("EmgNumberThree", "");

    }

    public void onGesturePerformed(GestureOverlayView overlay, Gesture gesture) {
        ArrayList<Prediction> predictions = mLibrary.recognize(gesture);

        if (predictions.size() > 0 && predictions.get(0).score > 1.0) {
            String result = predictions.get(0).name;

            if ("emg_one".equalsIgnoreCase(result)) {
                if (emgCont1 != null) {
                    String speakTextOne = getResources().getString(R.string.calling) + emgName1;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        speech.speak(speakTextOne, TextToSpeech.QUEUE_FLUSH, null, null);
                    }

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        String speakTextNew = "Please allow call permissions";
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            speech.speak(speakTextNew, TextToSpeech.QUEUE_FLUSH, null, null);
                        }
                        askForPermission(Manifest.permission.CALL_PHONE,CALL);
                    }

                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + emgCont1));
                    startActivity(callIntent);
                } else{
                    String speakText = getResources().getString(R.string.no_contact)
                            + getResources().getString(R.string.save_cont);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        speech.speak(speakText, TextToSpeech.QUEUE_FLUSH, null, null);
                    }
                }
            } else if ("emg_two".equalsIgnoreCase(result)) {
                if (emgCont2 != null) {
                    String speakTextOne = getResources().getString(R.string.calling) + emgName2;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        speech.speak(speakTextOne, TextToSpeech.QUEUE_FLUSH, null, null);
                    }

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        String speakTextNew = "Please allow call permissions";
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            speech.speak(speakTextNew, TextToSpeech.QUEUE_FLUSH, null, null);
                        }
                        askForPermission(Manifest.permission.CALL_PHONE,CALL);
                    }

                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + emgCont2));
                    startActivity(callIntent);
                } else{
                    String speakText = getResources().getString(R.string.no_contact)
                            + getResources().getString(R.string.save_cont);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        speech.speak(speakText, TextToSpeech.QUEUE_FLUSH, null, null);
                    }
                }
            } else if ("emg_three".equalsIgnoreCase(result)) {
                if (emgCont3 != null) {
                    String speakTextOne = getResources().getString(R.string.calling) + emgName3;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        speech.speak(speakTextOne, TextToSpeech.QUEUE_FLUSH, null, null);
                    }

                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        String speakTextNew = "Please allow call permissions";
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            speech.speak(speakTextNew, TextToSpeech.QUEUE_FLUSH, null, null);
                        }
                        askForPermission(Manifest.permission.CALL_PHONE,CALL);
                    }

                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:" + emgCont3));
                    startActivity(callIntent);
                } else{
                    String speakText = getResources().getString(R.string.no_contact)
                            + getResources().getString(R.string.save_cont);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        speech.speak(speakText, TextToSpeech.QUEUE_FLUSH, null, null);
                    }
                }
            }
        }
    }

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(GestureActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(GestureActivity.this, permission)) {

                //This is called if user has denied the permission before
                //In this case I am just asking the permission again
                ActivityCompat.requestPermissions(GestureActivity.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(GestureActivity.this, new String[]{permission}, requestCode);
            }
        } else {
            Toast.makeText(this, "" + permission + " is already granted.", Toast.LENGTH_SHORT).show();
        }
    }
}


