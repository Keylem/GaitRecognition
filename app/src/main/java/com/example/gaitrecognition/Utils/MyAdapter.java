package com.example.gaitrecognition.Utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.
gaitrecognition.Activities.TrainActivity;
import com.example.gaitrecognition.CSV.ReadCSV;
import com.example.gaitrecognition.R;

import java.io.File;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder>{

    Context context;
    File[] filesAndFolders;


    public MyAdapter(Context context, File[] filesAndFolders){
        this.context = context;
        this.filesAndFolders = filesAndFolders;
    }

    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        File selectedFile = filesAndFolders[position];
        holder.textView.setText(selectedFile.getName());

        if(selectedFile.isDirectory()){
            holder.imageView.setImageResource(R.drawable.ic_baseline_folder_24);
        }else{
            holder.imageView.setImageResource(R.drawable.ic_baseline_insert_drive_file_24);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(selectedFile.isDirectory()){

                    Intent intent = new Intent(context, TrainActivity.class);
                    String path = selectedFile.getAbsolutePath();
                    intent.putExtra("path", path);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);

                }else{
                    try{
                        //open the file
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);

                        String type = "image/*";

                        intent.setDataAndType(Uri.parse(selectedFile.getAbsolutePath()), type);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }catch(Exception e){

                        Toast.makeText(context.getApplicationContext(), "Cannot open the file", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                PopupMenu popupMenu = new PopupMenu(context, view);
                popupMenu.getMenu().add("DELETE");
                popupMenu.getMenu().add("SELECT");
                popupMenu.getMenu().add("TRAINOUTPUT");
                popupMenu.getMenu().add("LEARN");
                popupMenu.show();

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        if(menuItem.getTitle().equals("DELETE")){
                            //delete
                            boolean deleted = selectedFile.delete();
                            if(deleted){
                                Toast.makeText(context.getApplicationContext(), "DELETED" + selectedFile.getName().toString(),
                                        Toast.LENGTH_SHORT).show();
                                view.setVisibility(View.GONE);
                            }
                        }
                        if(menuItem.getTitle().equals("SELECT")){
                            //select
                            Toast.makeText(context.getApplicationContext(), "SELECTED" + selectedFile.getName().toString(),
                                    Toast.LENGTH_SHORT).show();

                            ReadCSV.readCSVfile = selectedFile;
                            Intent intent = new Intent(context, ReadCSV.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startService(intent);

                        }
                        if(menuItem.getTitle().equals("TRAINOUTPUT")){

                            //STOLEN PART
                            Toast.makeText(context.getApplicationContext(), "SELECTED" + selectedFile.getName().toString(),
                                    Toast.LENGTH_SHORT).show();

                            ReadCSV.readCSVfile = selectedFile;
                            Intent intentStolen = new Intent(context, ReadCSV.class);
                            intentStolen.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startService(intentStolen);

                            //STOLEN PART

                            //Trainer testTrainer = new Trainer(Normalizer.normalize(ReadCSV.readCSV, 1), "KEREMTEST", 20);
                            Intent intent = new Intent(context, Trainer.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startService(intent);

                        }
                        if(menuItem.getTitle().equals("LEARN")) {
                            ReadCSV.readTrainingCSVfile = selectedFile;

                            Intent intent = new Intent(context, ReadCSV.class);
                            ReadCSV.isTrainingModeOn = true;
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startService(intent);
                        }



                        return true;
                    }
                });

                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return filesAndFolders.length;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView textView;
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.fileNameTextView);
            imageView = itemView.findViewById(R.id.iconView);
        }
    }
}
