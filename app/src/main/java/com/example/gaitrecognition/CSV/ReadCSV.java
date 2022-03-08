package com.example.gaitrecognition.CSV;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.gaitrecognition.NaiveBayesCheck;
import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class ReadCSV extends Service {
    public static  ArrayList<float[]> readCSV = new ArrayList<float[]>();
    public static File readCSVfile;
    public static File readTrainingCSVfile;

    public static ArrayList<String> readFileNames = new ArrayList<String>();

    public static ArrayList<ArrayList>  trainingCSVdata = new ArrayList<ArrayList>();

    public static String name;

    private float time, x, y, z, xA, yA, zA, xS, yS, zS;

    public static boolean isTrainingModeOn = false;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!readCSV.isEmpty()){
            readCSV.clear();
        }
        if(!trainingCSVdata.isEmpty()){
            trainingCSVdata.clear();
        }if(isTrainingModeOn){
            if(ReadCSV.readTrainingCSVfile!= null){
               tryReadingTrainingCSV(readTrainingCSVfile);

               for (ArrayList line : trainingCSVdata){
                   float[] floatBoxInsıde = (float[]) line.get(0);

                   xA = floatBoxInsıde[0];
                   yA = floatBoxInsıde[1];
                   zA = floatBoxInsıde[2];
                   xS = floatBoxInsıde[3];
                   yS = floatBoxInsıde[4];
                   zS = floatBoxInsıde[5];




                   String NAME = (String )line.get(1); //TODO xyz AS parçalarını ekle!

                   NaiveBayesCheck.nbTrainer(xA, yA, zA, xS, yS,zS, NAME);
               }

            }
            isTrainingModeOn = false;
            return START_NOT_STICKY;
        }
        if(ReadCSV.readCSVfile!= null){
            this.name = readCSVfile.getName().substring(0, readCSVfile.getName().lastIndexOf('.'));
            ReadCSV.readFileNames.add(this.name);
            tryReadingCSV(readCSVfile);
        }

        return START_NOT_STICKY;
    }

    public void tryReadingCSV(File csvfile){
        try {

            CSVReader reader = new CSVReader(new FileReader(csvfile.getAbsolutePath()));
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                // nextLine[] is an array of values from the line
                time = Float.parseFloat(nextLine[0]);
                x = Float.parseFloat(nextLine[1]);
                y = Float.parseFloat(nextLine[2]);
                z = Float.parseFloat(nextLine[3]);

                this.name = nextLine[4];

                float[] newLine = createFloatBox(time, x, y, z);
                readCSV.add(newLine);

                Log.e(csvfile.getName() ,time + "," + x + "," + y + "," +  z);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "The specified file was not found", Toast.LENGTH_SHORT).show();
        }
    }

    public void tryReadingTrainingCSV(File trainingCSVfile){
        try {

            CSVReader reader = new CSVReader(new FileReader(trainingCSVfile.getAbsolutePath()));
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                // nextLine[] is an array of values from the line

                xA = Float.parseFloat(nextLine[0]);
                yA = Float.parseFloat(nextLine[1]);
                zA = Float.parseFloat(nextLine[2]);

                xS = Float.parseFloat(nextLine[3]);
                yS = Float.parseFloat(nextLine[4]);
                zS = Float.parseFloat(nextLine[5]);


                this.name = nextLine[6];

                ArrayList newTrainingLine = createArrayListBox(xA, yA, zA, xS, yS, zS,  this.name);
                trainingCSVdata.add(newTrainingLine);

                //
                // TODO LOG MESSAGE
                // Log.e(trainingCSVfile.getName() ,time + "," + x + "," + y + "," +  z);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "The specified file was not found", Toast.LENGTH_SHORT).show();
        }
    }

    public static float[] createFloatBox(float time, float x, float y, float z){
        return new float[]{time, x, y, z};
    }

    public static ArrayList createArrayListBox(float xA, float yA, float zA, float xS, float yS, float zS, String NAME){
        ArrayList arrToReturn = new ArrayList();

        arrToReturn.add(new float[]{xA,yA,zA, xS, yS, zS});
        arrToReturn.add(NAME);

        return arrToReturn;

    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}


