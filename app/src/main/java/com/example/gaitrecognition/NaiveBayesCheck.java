package com.example.gaitrecognition;

import android.util.Log;

import java.io.File;
import java.util.ArrayList;

import weka.classifiers.bayes.NaiveBayes;
import weka.core.converters.CSVLoader;

public class NaiveBayesCheck {

    public static ArrayList trainingArrayListForWeka = new ArrayList();
    public static String trainingCSVstringForWeka = "";
    public static String checkingCSVstringForWeka = "";

    public NaiveBayesCheck(){
    }

    public static void nbTrainer(float xA, float yA, float zA, float xS, float yS, float zS, String NAME){

        MainActivity.bayes.learn(NAME, featuresBoxCreator(xA, yA, zA, xS, yS, zS));
        Log.e("BayesStatus", "" + MainActivity.bayes.getCategoriesTotal());

        //For the bad use on WEKA, please forgive me
        trainingCSVstringForWeka +="" + xA + "," + yA + "," + zA + "," + xS + "," + yS + "," + zS + ","+ NAME + "\n";
    }

    public static String nbChecker(float xA, float yA, float zA, float xS, float yS, float zS){

        checkingCSVstringForWeka +="" + xA + "," + yA + "," + zA + "," + xS + "," + yS + "," + zS  + "\n";

        return MainActivity.bayes.classify(featuresBoxCreator(xA, yA, zA, xS, yS, zS)).getCategory();
    }

    public static String detailedNBchecker(float xA, float yA, float zA, float xS, float yS, float zS){
       // wekaTrainer();
        return "" + MainActivity.bayes.classifyDetailed(featuresBoxCreator(xA, yA, zA, xS, yS, zS));
    }

    public static ArrayList<Float> featuresBoxCreator(float xA, float yA, float zA, float xS, float yS, float zS){
        ArrayList<Float> features = new ArrayList<Float>();

        features.add(xA);
        features.add(yA);
        features.add(zA);
        features.add(xS);
        features.add(yS);
        features.add(zS);

        return features;
    }

    public static void wekaTrainer(){
        CSVLoader csvLoader= new CSVLoader();
        //File file = new File
        //csvLoader.setSource();
    }




}
