package com.example.gaitrecognition;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.gaitrecognition.Activities.RecordActivity;
import com.example.gaitrecognition.Activities.TrainActivity;

import de.daslaboratorium.machinelearning.classifier.bayes.BayesClassifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.bayes.NaiveBayesUpdateable;
import weka.core.Instances;

public class MainActivity extends AppCompatActivity {

    public static String nameTransporter = "initial";

    public static BayesClassifier <Float, String> bayes = new BayesClassifier<Float, String>();

    public static NaiveBayesUpdateable wekaNaiveBayes = new NaiveBayesUpdateable();



    {bayes.setMemoryCapacity(Integer.MAX_VALUE);}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkPermission();
    }

    public void onRecordClick(View view){
        Intent recordIntent = new Intent(this, RecordActivity.class);
        NaiveBayesTest naiveBayesTest = new NaiveBayesTest();
        startActivity(recordIntent);


    }

    public void onTrainClick(View view){
        Intent trainIntent = new Intent(this, TrainActivity.class);
        String path = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            path = getExternalFilesDir("RecordedSaves").getPath(); //TODO to be rearanged!
        }
        trainIntent.putExtra("path", path); //TODO To be implemented on every method!
        startActivity(trainIntent);
    }

    public void onMatchClick(View view){
        Intent matchIntent = new Intent(this, MatchActivity.class);
        startActivity(matchIntent);
    }


    private void checkPermission(){
        if(!isPermissionGranted()){
            requestPermission();
        }
    }


    private boolean isPermissionGranted(){
        int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(result == PackageManager.PERMISSION_GRANTED){
            return true;
        }else{
            return false;
        }
    }

    private void requestPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            Toast.makeText(MainActivity.this, "Storage permission required, please allow from settings", Toast.LENGTH_SHORT).show();
        }else{
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.DISABLE_KEYGUARD}, 111);

        }


    }

}