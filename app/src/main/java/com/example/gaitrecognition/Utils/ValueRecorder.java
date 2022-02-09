package com.example.gaitrecognition.Utils;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.gaitrecognition.CSV.CreateCSV;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;

public class ValueRecorder extends Service implements SensorEventListener {
    public static boolean isThreadDone = false;

    public static ArrayList<float[]> valueArrays = new ArrayList<float[]>();
    public static boolean recordSwitch = false;
    private SensorManager sensorManager;
    private Sensor recorderSensor;
    private float x, y, z;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        this.sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        recorderSensor = sensorManager.getDefaultSensor((Sensor.TYPE_LINEAR_ACCELERATION));
        sensorManager.registerListener(ValueRecorder.this, recorderSensor, sensorManager.SENSOR_DELAY_UI);

        final NumberFormat numFormat = NumberFormat.getNumberInstance();
        numFormat.setMaximumFractionDigits(5);
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        double startTime = System.currentTimeMillis();
                        float savedX = x;
                        float savedY = y;
                        float savedZ = z;
                        while(recordSwitch){
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            if (savedX != x && savedY != y && savedZ != z) {
                                double currentTime = System.currentTimeMillis();
                                float elapsedTime = (float) (currentTime - startTime) / 1000;
                                createFloatBox(elapsedTime, x, y, z);
                                Log.e("Service", "X: " + x + "\n" + "Y: " + y + "\n" + "Z: " + z + "\n" + "ElapsedTime: " + elapsedTime);
                            }
                        }
                        //peekAtArrayList(); // To Take A peek at the ArrayList
                        CreateCSV.arraysToWrite = (ArrayList<float[]>) ValueRecorder.valueArrays.clone();
                        Intent csvCreatorIntent = new Intent(ValueRecorder.this, CreateCSV.class);
                        startService(csvCreatorIntent);
                        ValueRecorder.valueArrays.clear();
                    }
                }
        ).start();


        return START_NOT_STICKY;
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {


        x = sensorEvent.values[0];
        y = sensorEvent.values[1];
        z = sensorEvent.values[2];

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    private void  createFloatBox(float time, float x, float y, float z){
        float[] arr = new float[]{time, x,y,z};
        this.valueArrays.add(arr);
    }

    private void peekAtArrayList(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            System.out.println( Arrays.toString((valueArrays.toArray())));
        }
    }
}
