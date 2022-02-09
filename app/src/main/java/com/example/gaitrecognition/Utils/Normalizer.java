package com.example.gaitrecognition.Utils;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;

public class Normalizer extends Service {
    public static int DEFAULT_CUT = 10;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public static ArrayList<float[]> normalize(ArrayList<float[]> arrayToNormalize, int seconds){

        ArrayList<float[]> normalizedArrayList = new ArrayList<float[]>();

        int maxSeconds = (int) arrayToNormalize.get(arrayToNormalize.size() - 1)[0];

        if (seconds > 0){
            for(int i = 0; i <= maxSeconds; i+= seconds){
                ArrayList<float[]> arrayToProcess = new ArrayList<float[]>();
                for(float[] searchArray : arrayToNormalize){
                    if (searchArray[0] < i && searchArray[0] > i - seconds){
                        arrayToProcess.add(searchArray);
                        //arrayToNormalize.remove(searchArray);
                    }

                }

                if(!arrayToProcess.isEmpty()){
                    float[] ortalamaXYZ = ortalamaAl(arrayToProcess);
                    float[] standartSapmaXYZ = standartSapmaAl(arrayToProcess);
                    float[] addToNormalizedArray = new float[]{ortalamaXYZ[0], ortalamaXYZ[1], ortalamaXYZ[2],
                            standartSapmaXYZ[0], standartSapmaXYZ[1], standartSapmaXYZ[2]};
                    normalizedArrayList.add(addToNormalizedArray);
                    Log.e("Processed", "oXYZ, ssapma: " + Arrays.toString(addToNormalizedArray));
                }
            }
            return normalizedArrayList;
        }else{
            Log.e("Normalize error!", "Please fix your input!");
            return null;
        }
    }

    private static float[] ortalamaAl(ArrayList<float[]> ortalamaArrayList){
        float xAverage = 0;
        float yAverage = 0;
        float zAverage = 0;
        float[] averageXYZ;

        for(float[] inside : ortalamaArrayList){
            xAverage += inside[1];
            yAverage += inside[2];
            zAverage += inside[3];
        }

        averageXYZ = new float[]{xAverage, yAverage, zAverage};
        return averageXYZ;
    }

    private static float[] standartSapmaAl(ArrayList<float[]> standartArrayList){
        ArrayList<Float> xList = new ArrayList<Float>();
        ArrayList<Float> yList = new ArrayList<Float>();
        ArrayList<Float> zList = new ArrayList<Float>();

        float sumX = 0, sumY = 0, sumZ = 0;
        float ortalamaX, ortalamaY, ortalamaZ ;
        int length = standartArrayList.size();


        for(float[] floatArr : standartArrayList){
            xList.add(floatArr[1]);
            yList.add(floatArr[2]);
            zList.add(floatArr[3]);

        }
        for(float x : xList){sumX += x;}
        for(float y : xList){sumY += y;}
        for(float z : xList){sumZ += z;}

        ortalamaX = sumX / length;
        ortalamaY = sumY / length;
        ortalamaZ = sumZ / length;

        float standartSapmax, standartSapmay, standartSapmaz;
        standartSapmax = standartSapmaKok(xList, ortalamaX, length);
        standartSapmay = standartSapmaKok(yList, ortalamaY, length);
        standartSapmaz = standartSapmaKok(zList, ortalamaZ, length);

        return new float[]{standartSapmax, standartSapmay, standartSapmaz};

    }
    private static float standartSapmaKok(ArrayList<Float> list, float ortalama, int length){
        float standartSapma = 0;
        for(float sayi : list){standartSapma += Math.pow(sayi - ortalama, 2);}

        return (float) Math.sqrt(standartSapma / length );
    }

}
