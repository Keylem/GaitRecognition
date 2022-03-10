package com.example.gaitrecognition;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gaitrecognition.Utils.Normalizer;
import com.example.gaitrecognition.Utils.ValueRecorder;

import java.util.ArrayList;

public class MatchActivity extends AppCompatActivity {


    TextView walkingTextView;
    TextView yuruyunuzTextView;
    TextView detaylarTextView;
    ImageView humanImageView;

    Button walkingButton;
    ProgressBar progressBar;
    double progressBarStatus = 0;
    int countdownMiliseconds = 30000;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        walkingTextView = findViewById(R.id.walkTextView);
        walkingButton = findViewById(R.id.walkButton);
        progressBar = findViewById(R.id.progressBar);
        yuruyunuzTextView = findViewById(R.id.yuruyunuzTextView);
        detaylarTextView = findViewById(R.id.detaylarTextView);
        humanImageView = findViewById(R.id.humanImageView);

        progressBar.setVisibility(View.INVISIBLE);
        yuruyunuzTextView.setVisibility(View.INVISIBLE);
        detaylarTextView.setVisibility(View.INVISIBLE);

        humanImageView.setVisibility(View.INVISIBLE);

        detaylarTextView.setMovementMethod(new ScrollingMovementMethod());

        walkingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startWalking(view);
            }
        });
    }

    public void startWalking(View view){
        //walkingButton.setText("Walking...");
        walkingButton.setVisibility(View.INVISIBLE);
        detaylarTextView.setVisibility(View.INVISIBLE);
        humanImageView.setVisibility(View.INVISIBLE);

        progressBar.setVisibility(View.VISIBLE);
        yuruyunuzTextView.setVisibility(View.VISIBLE);

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
        new CountDownTimer(countdownMiliseconds, 1000){
            @Override
            public void onTick(long millisUntilFinished) {
                walkingTextView.setText(String.valueOf(millisUntilFinished / 1000));
                progressBar.setProgress(  (int)  ((countdownMiliseconds - millisUntilFinished) * 100 / countdownMiliseconds));
            }

            @Override
            public void onFinish() {
                ValueRecorder.recordSwitch = false;
                ArrayList<float[]> results = Normalizer.normalize(ValueRecorder.valueArrays, 15); //TODO DYNAMIC NUMBER FROM Trainer.class

                for(float[] inResults : results){
                    String bayesResult = NaiveBayesCheck.nbChecker(inResults[0], inResults[1], inResults[2], inResults[3], inResults[4], inResults[5]);
                    String detailedBayesResult = NaiveBayesCheck.detailedNBchecker(inResults[0], inResults[1], inResults[2], inResults[3], inResults[4], inResults[5]);
                    Log.e("BayesCheckResult: ", bayesResult);
                    walkingTextView.setText("Bulunan kişi: " + bayesResult);

                    walkingButton.setVisibility(View.VISIBLE);

                    progressBar.setVisibility(View.INVISIBLE);
                    yuruyunuzTextView.setVisibility(View.INVISIBLE);
                    detaylarTextView.setVisibility(View.VISIBLE);

                    humanImageView.setVisibility(View.VISIBLE);

                    detaylarTextView.setText("Detaylar: \n" + detailedBayesResult);
                    walkingButton.setText("Yürü");
                }
            }
        }.start();
    }
}