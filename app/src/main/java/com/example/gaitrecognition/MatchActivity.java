package com.example.gaitrecognition;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gaitrecognition.Utils.Normalizer;
import com.example.gaitrecognition.Utils.ValueRecorder;

import java.util.ArrayList;

public class MatchActivity extends AppCompatActivity {


    TextView walkingTextView;
    Button walkingButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        walkingTextView = findViewById(R.id.walkTextView);
        walkingButton = findViewById(R.id.walkButton);

        walkingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startWalking(view);
            }
        });
    }

    public void startWalking(View view){
        walkingButton.setText("Walking...");
        ValueRecorder.recordSwitch= true;
        Intent valueRecorderIntent = new Intent(getApplication(), ValueRecorder.class);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                valueRecorderIntent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                valueRecorderIntent.setData(Uri.parse("package:" + packageName));
                startService(valueRecorderIntent);
            }

        }
        new CountDownTimer(30000, 1000){
            @Override
            public void onTick(long millisUntilFinished) {
                walkingTextView.setText(String.valueOf(millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                ValueRecorder.recordSwitch = false;
                ArrayList<float[]> results = Normalizer.normalize(ValueRecorder.valueArrays, 15); //TODO DYNAMIC NUMBER FROM Trainer.class

                for(float[] inResults : results){
                    String bayesResult = NaiveBayesCheck.nbChecker(inResults[0], inResults[1], inResults[2], inResults[3], inResults[4], inResults[5]);
                    Log.e("BayesCheckResult: ", bayesResult);
                    walkingTextView.setText(bayesResult);
                    walkingButton.setText("WALK");
                }
            }
        }.start();
    }
}