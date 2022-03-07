package com.example.gaitrecognition.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gaitrecognition.R;
import com.example.gaitrecognition.Utils.MyAdapter;

import java.io.File;

public class TrainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train);

        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        RecyclerView recyclerView2 = findViewById(R.id.recyclerView2);

        TextView noFilesTextView = findViewById(R.id.noFilesTextView);

        String path = getIntent().getStringExtra("path");
        File root = new File(path);
        File[] filesAndFolders = root.listFiles();
        if(filesAndFolders == null || filesAndFolders.length == 0){
            noFilesTextView.setVisibility(View.VISIBLE);
            return;
        }

        File trainingFolder = new File(getExternalFilesDir("TrainOutputs").getPath());
        File[] trainingFolders = trainingFolder.listFiles();
        noFilesTextView.setVisibility((View.INVISIBLE));

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter( new MyAdapter(getApplicationContext(), filesAndFolders));

        recyclerView2.setLayoutManager(new LinearLayoutManager(this));
        recyclerView2.setAdapter( new MyAdapter(getApplicationContext(), trainingFolders));



    }
}