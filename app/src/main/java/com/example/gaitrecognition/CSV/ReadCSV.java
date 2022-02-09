package com.example.gaitrecognition.CSV;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class ReadCSV extends Service {
    public static  ArrayList<float[]> readCSV = new ArrayList<float[]>();
    public static File readCSVfile;
    public static ArrayList<String> readFileNames = new ArrayList<String>();
    public static String name;

    private float time, x, y, z;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!readCSV.isEmpty()){
            readCSV.clear();
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

                float[] newLine = createFloatBox(time, x, y, z);
                readCSV.add(newLine);

                Log.e(csvfile.getName() ,time + "," + x + "," + y + "," +  z);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "The specified file was not found", Toast.LENGTH_SHORT).show();
        }
    }

    public static float[] createFloatBox(float time, float x, float y, float z){
        return new float[]{time, x, y, z};
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}


