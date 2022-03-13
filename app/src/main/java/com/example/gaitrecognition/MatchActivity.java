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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Random;

import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.bayes.NaiveBayesUpdateable;
import weka.core.Instances;
import weka.core.converters.CSVLoader;

public class MatchActivity extends AppCompatActivity {


    TextView walkingTextView;
    TextView yuruyunuzTextView;
    TextView detaylarTextView;
    ImageView humanImageView;

    Button walkingButton;
    ProgressBar progressBar;
    double progressBarStatus = 0;
    int countdownMiliseconds = 30000;

    String detailedDesctiption = "";

    public static Instances trainingDataGlobal;


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
                //Save as csv and load into the CSVLoader

                String fileName = "defaultModel" + ".csv";
                String filePath = "Models";
                if (!NaiveBayesCheck.trainingCSVstringForWeka.equals("")){
                    File fileToWrite = new File(getExternalFilesDir(filePath), fileName);
                    FileOutputStream fos = null;
                    try{fos = new FileOutputStream(fileToWrite);
                    }catch(Exception e){
                        Log.e("err MatchActivity 76", "err!");
                    }
                    try {
                        fos.write(NaiveBayesCheck.trainingCSVstringForWeka.getBytes(StandardCharsets.UTF_8));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }


                CSVLoader csvLoader = new CSVLoader();
                try {
                    csvLoader.setSource(new File(getExternalFilesDir("Models"), "defaultModel.csv"));

                } catch (IOException e) {
                    e.printStackTrace();
                }
                Instances data = null;
                try {
                    data = csvLoader.getDataSet();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                /*
                if(data.classIndex() == -1){
                    data.setClassIndex( data.numAttributes() -1);
                }*/

                int trainingDataSize = (int) Math.round(data.numInstances() * 0.66);
                int testDataSize = (int) data.numInstances() - trainingDataSize;

                data.randomize(new Random(0));
                Instances trainingInstances = new Instances(data,0,trainingDataSize);
                Instances testInstances = new Instances(data, trainingDataSize, testDataSize);
                trainingInstances.setClassIndex(trainingInstances.numAttributes() -1);
                testInstances.setClassIndex(trainingInstances.numAttributes() -1);

                trainingDataGlobal = new Instances(trainingInstances);

                NaiveBayes nb = MainActivity.wekaNaiveBayes;

                try {
                    nb.buildClassifier(trainingInstances);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {

                    Evaluation evaluation = new Evaluation(trainingInstances);
                    evaluation.evaluateModel(nb, testInstances);
                    detailedDesctiption =  evaluation.toSummaryString();
                } catch (Exception e) {
                    e.printStackTrace();
                }


                startWalking(view);

                //Evaluate!

                //Save as csv and load into the CSVLoader




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
                ArrayList<float[]> results = Normalizer.normalize(ValueRecorder.valueArrays, 10); //TODO DYNAMIC NUMBER FROM Trainer.class

                for(float[] inResults : results) {
                    String bayesResult = NaiveBayesCheck.nbChecker(inResults[0], inResults[1], inResults[2], inResults[3], inResults[4], inResults[5]);
                    String detailedBayesResult = NaiveBayesCheck.detailedNBchecker(inResults[0], inResults[1], inResults[2], inResults[3], inResults[4], inResults[5]);
                    Log.e("BayesCheckResult: ", bayesResult);
                    walkingTextView.setText("Bulunan kişi: " + bayesResult);

                    walkingButton.setVisibility(View.VISIBLE);

                    progressBar.setVisibility(View.INVISIBLE);
                    yuruyunuzTextView.setVisibility(View.INVISIBLE);
                    detaylarTextView.setVisibility(View.VISIBLE);

                    humanImageView.setVisibility(View.VISIBLE);

                    //detaylarTextView.setText("Detaylar: \n" + detailedBayesResult);
                    detaylarTextView.setText(detailedDesctiption);
                    walkingButton.setText("Yürü");
                }

                    String filePath = "Models";
                    String fileNameForEvaluate = "defaultEvaluate" + ".csv";
                    if (!NaiveBayesCheck.checkingCSVstringForWeka.equals("")){
                        File fileToWrite2 = new File(getExternalFilesDir(filePath), fileNameForEvaluate);
                        FileOutputStream fos2 = null;
                        try{fos2 = new FileOutputStream(fileToWrite2);
                        }catch(Exception e){
                            Log.e("err MatchActivity 1566", "err!");
                        }
                        try {
                            fos2.write(NaiveBayesCheck.checkingCSVstringForWeka.getBytes(StandardCharsets.UTF_8));


                        } catch (IOException e) {
                            Log.e("err MatchActivity 1567", "err!");
                            e.printStackTrace();
                        }finally {
                            try {
                                fos2.close();
                            } catch (IOException e) {
                                Log.e("err MatchActivity 1568", "err!");

                                e.printStackTrace();
                            }
                        }
                    }

                    CSVLoader csvLoader = new CSVLoader();
                    CSVLoader csvLoaderTrain = new CSVLoader();

                    try {
                        csvLoader.setSource(new File(getExternalFilesDir("Models"), "defaultEvaluate.csv"));
                        csvLoaderTrain.setSource(new File(getExternalFilesDir("Models"), "defaultModel.csv"));

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        Instances unlabeledData = csvLoader.getDataSet();
                        Instances labeledTrainData = csvLoaderTrain.getDataSet();
                        labeledTrainData.setClassIndex(labeledTrainData.numAttributes() -1);
                        unlabeledData.setClassIndex(unlabeledData.numAttributes() - 1);
                        Instances labeled = new Instances(unlabeledData);
                        labeled.setClassIndex(labeled.numAttributes() -1);

                        labeled.addAll(labeledTrainData);

                        for(int i = 0; i < unlabeledData.numInstances(); i++){
                            NaiveBayesUpdateable nb = MainActivity.wekaNaiveBayes;
                            double clslabel = nb.classifyInstance(unlabeledData.instance(i));

                            labeled.instance(i).setClassValue(clslabel);
                            Log.e("Solution", clslabel + " -> " + trainingDataGlobal.classAttribute().value((int) clslabel));
                            walkingTextView.setText("Bulunan kişi: " + trainingDataGlobal.classAttribute().value((int) clslabel));

                        }

                    } catch (IOException e) {
                        Log.e("err MatchActivity 1568", "err!");

                        e.printStackTrace();
                    } catch (Exception e) {
                        Log.e("err MatchActivity 1568", "err!");

                        e.printStackTrace();
                    }



            }
        }.start();
    }
}