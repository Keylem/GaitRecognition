package com.example.gaitrecognition.Utils;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.gaitrecognition.CSV.CreateCSV;
import com.example.gaitrecognition.CSV.ReadCSV;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import de.daslaboratorium.machinelearning.classifier.Classification;
import de.daslaboratorium.machinelearning.classifier.Classifier;
import de.daslaboratorium.machinelearning.classifier.bayes.BayesClassifier;

public class Trainer extends Service {

    public static ArrayList<Trainer> namesNumbersAndAndFiles = new ArrayList<>();

    public static int DEFAULT_CUT_SECONDS = 1;
    public static Classifier classifier = new Classifier() {
        @Override
        public Classification classify(Collection features) {
            return null;
        }
    };

    String name;
    int length;
    ArrayList<float[]> arrayInputManipulated;
    public static Context context;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public Trainer(){

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int value = 600;
        int divider = 15;
        Trainer testTrainer = new Trainer(Normalizer.normalize(ReadCSV.readCSV, divider), ReadCSV.name, (int) value / divider);
        extractNamesNumbersAndFiles();
        ReadCSV.readCSV.clear();
        return START_NOT_STICKY;
    }

    public Trainer(ArrayList<float[]> rawArrayListInput, String name, int length){
        takeInputAndManipulate(rawArrayListInput , name, length);
    }

    public void takeInputAndManipulate(ArrayList<float[]> arrayInput, String name, int length){

        if (inputCheck(arrayInput, name, length)){
            ArrayList<float[]> arrayInputManipulated = new ArrayList<float[]>();
            for(int i = 0; i < length ; i++) {
                arrayInputManipulated.add(arrayInput.get(i));
            }
            Trainer trainObject = new Trainer();
            trainObject.setSettings(name, length, arrayInputManipulated);
            namesNumbersAndAndFiles.add(trainObject);
        }
    }
    private boolean inputCheck(ArrayList<float[]> arrayInput, String name, int length){

        if(arrayInput.size() + 1 > length && !name.contains(" ")){
            return true;
        }
        return true;
    }

    public void setSettings(String name, int length, ArrayList<float[]> arrayInputManipulated){
        this.arrayInputManipulated = arrayInputManipulated;
        this.length = length;
        this.name = name;
    }

    public  void extractNamesNumbersAndFiles(){
        String finalString = "";
        String fileName =";";
        if(!Trainer.namesNumbersAndAndFiles.isEmpty()){
            fileName += System.currentTimeMillis();
            for(Trainer trainer : Trainer.namesNumbersAndAndFiles){
                fileName += trainer.name;
                finalString += CreateCSV.arrayToString(trainer.arrayInputManipulated, trainer.name);
            }
            fileName +=".csv";

            String finalString1 = finalString;
            String finalFileName = fileName;
            new Thread(
                    new Runnable() {
                        @Override
                        public void run() {

                            threadWork(finalString1, finalFileName);
                        }
                    }
            ).run();
        }
    }

    private void threadWork(String finalString, String fileName){
        if(!CreateCSV.isExternalStorageAvailableForRW()){
            Toast.makeText(this, "FileSystem is not readable/writable!", Toast.LENGTH_SHORT).show();
        }else{

            if(!finalString.equals("")) {
                Log.e("Wew", "WEEWWWW");

                File fileToWrite = new File(getExternalFilesDir("TrainOutputs"),fileName);
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream((fileToWrite));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                try {

                    fos.write(finalString.getBytes(StandardCharsets.UTF_8));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    try {
                        fos.close();
                        Log.e("SavedSD", "Information saved to your SD card");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        namesNumbersAndAndFiles.clear();
        trainNaiveBayes(null);
    }

    private void trainNaiveBayes(ArrayList<Trainer> features  ){
        Classifier<Object[], String > classifier = new BayesClassifier<Object[], String>();

        for(Trainer trainerIn : features){
            List<Object[]> trainFeatures = new ArrayList<>();
            trainFeatures.add(trainerIn.arrayInputManipulated.toArray());
            classifier.learn(trainerIn.name, trainFeatures);
        }
        this.classifier = classifier;
    }
}
