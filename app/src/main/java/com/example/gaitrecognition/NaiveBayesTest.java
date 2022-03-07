package com.example.gaitrecognition;

import android.os.Build;
import android.util.Log;



import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import de.daslaboratorium.machinelearning.classifier.Classifier;
import de.daslaboratorium.machinelearning.classifier.bayes.BayesClassifier;

public class NaiveBayesTest {
    public NaiveBayesTest(){
        BayesClassifier<Float, String > bayes = new BayesClassifier< Float ,String>();

        String categoryHi = "Hi";
        String categoryHi2 = "Hi2";

        //float[] floatarr = {56f, 57f, 58f};

        ArrayList<Float> floatarr = new ArrayList<>();
        floatarr.add(56f);
        floatarr.add(57f);
        floatarr.add(58f);

        ArrayList<Float> floatarr1 = new ArrayList();
        floatarr1.add(1f);
        floatarr1.add(2f);
        floatarr1.add(3f);

        //float[] floatarr1 = {1f, 2f, 3f};
       // float[] floatarr2 = {2f, 5f, 3f};
        float[] floatarr3 = {2f, 7f, 8f};
        float[] floatarr4 = {55f, 53f, 50f};

        ArrayList<Float> floatarr2 = new ArrayList();
        floatarr2.add(2f);
        floatarr2.add(7f);
        floatarr2.add(8f);


        //     bayes.learn(categoryHi, Arrays.asList(floatarr));
        bayes.learn(categoryHi, floatarr);

        //bayes.incrementFeature(floatarr4, categoryHi);
       // bayes.incrementCategory( "Hi");
        bayes.learn(categoryHi2, floatarr1);
       // bayes.incrementFeature(floatarr2.get(0) , categoryHi2 );
        //bayes.incrementFeature(floatarr2.get(1) , categoryHi2 );
        //bayes.incrementFeature(floatarr2.get(2) , categoryHi2 );
        bayes.learn(categoryHi ,floatarr2);

        //bayes.learn(categoryHi2, Arrays.asList(floatarr3));

        float[] sus_list  = {56f, 57f, 59f};

        ArrayList<Float> susList = new ArrayList();
        susList.add(1f);
        susList.add(2f);
        susList.add(58f);
       // Log.e("asd", bayes.classify(Arrays.asList(sus_list)).getCategory());
        Log.e("asd2", ""+ bayes.getCategoriesTotal());
        Log.e("asd3",  "" + bayes.classifyDetailed(susList));



    }
}
