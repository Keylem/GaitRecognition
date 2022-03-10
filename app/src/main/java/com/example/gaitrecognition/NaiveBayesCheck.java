package com.example.gaitrecognition;

import android.util.Log;

import java.util.ArrayList;

import weka.classifiers.bayes.NaiveBayes;

public class NaiveBayesCheck {

    public NaiveBayesCheck(){
    }

    public static void nbTrainer(float xA, float yA, float zA, float xS, float yS, float zS, String NAME){

        MainActivity.bayes.learn(NAME, featuresBoxCreator(xA, yA, zA, xS, yS, zS));
        Log.e("BayesStatus", "" + MainActivity.bayes.getCategoriesTotal());
    }

    public static String nbChecker(float xA, float yA, float zA, float xS, float yS, float zS){
        return MainActivity.bayes.classify(featuresBoxCreator(xA, yA, zA, xS, yS, zS)).getCategory();
    }

    public static String detailedNBchecker(float xA, float yA, float zA, float xS, float yS, float zS){
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

    {

    }
}
