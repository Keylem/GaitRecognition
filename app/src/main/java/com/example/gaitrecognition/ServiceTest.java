package com.example.gaitrecognition;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

public class ServiceTest extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
/*        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        while(true){
                            Log.e("Service", "Service is running...");
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }


        ).start(); */
        Log.e("Service", "Service is running...");
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
