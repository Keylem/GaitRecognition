package com.example.gaitrecognition.Activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.gaitrecognition.R;
import com.example.gaitrecognition.Utils.ValueRecorder;

public class RecordActivity extends AppCompatActivity {

    TextView textViewX;
    TextView textViewY;
    TextView textViewZ;
    TextView textViewTime;

    Button startStopButton;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.record_activity);

        textViewX = (TextView) findViewById(R.id.textViewX);
        textViewY = (TextView) findViewById(R.id.textViewY);
        textViewZ = (TextView) findViewById(R.id.textViewZ);
        textViewTime = (TextView) findViewById(R.id.textViewTime);

        startStopButton = (Button) findViewById(R.id.recordButton);

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void startStopRecording(View view){
        if(ValueRecorder.recordSwitch == false){
            startStopButton.setText("Recording...");
            ValueRecorder.recordSwitch = true;
            Intent valueRecorderIntent = new Intent(getApplication(), ValueRecorder.class);
           // startService(valueRecorderIntent);

            //
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                String packageName = getPackageName();
                PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
                if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                    valueRecorderIntent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    valueRecorderIntent.setData(Uri.parse("package:" + packageName));
                    startService(valueRecorderIntent);
                }
            }
            //

        }else{
            startStopButton.setText("Record!");
            dataPreview();
            ValueRecorder.recordSwitch = false;

        }

    }

    public void dataPreview(){
        if (!ValueRecorder.valueArrays.isEmpty()){
            float[] getBackData =  ValueRecorder.valueArrays.get(ValueRecorder.valueArrays.size() -1 );
            textViewX.setText("X: " + getBackData[1]);
            textViewY.setText("Y: " + getBackData[2]);
            textViewZ.setText("Z: " + getBackData[3]);

            textViewTime.setText("Time Elapsed: " + getBackData[0]);

        }
    }
}
