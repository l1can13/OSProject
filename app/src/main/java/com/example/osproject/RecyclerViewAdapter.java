package com.example.osproject;

import android.Manifest;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.io.FileNotFoundException;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    Context context;
    List<String> filenamesList;
    List<String> trashList;
    FirebaseAuth fbAuth;

    RecyclerViewAdapter(Context context, List<String> filenamesList, FirebaseAuth fbAuth) {
        this.context = context;
        this.filenamesList = filenamesList;
        this.fbAuth = fbAuth;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_recycler_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
        String buf = filenamesList.get(position);

        StringBuilder typeOfFile = new StringBuilder("");
        for(int i = buf.length() - 1; i > 0; --i) {
            if(buf.charAt(i) == '.')
                break;
            typeOfFile.append(buf.charAt(i));
        }
        switch (typeOfFile.reverse().toString()) {
            case "doc":
                holder.fileImage.setImageResource(R.drawable.doc);
                break;
            case "docx":
                holder.fileImage.setImageResource(R.drawable.docx);
                break;
            case "gif":
                holder.fileImage.setImageResource(R.drawable.gif);
                break;
            case "jpg":
                holder.fileImage.setImageResource(R.drawable.jpg);
                break;
            case "mp3":
                holder.fileImage.setImageResource(R.drawable.mp3);
                break;
            case "mp4":
                holder.fileImage.setImageResource(R.drawable.mp4);
                break;
            case "pdf":
                holder.fileImage.setImageResource(R.drawable.pdf);
                break;
            case "ppt":
                holder.fileImage.setImageResource(R.drawable.ppt);
                break;
            case "pptx":
                holder.fileImage.setImageResource(R.drawable.pptx);
                break;
            case "txt":
                holder.fileImage.setImageResource(R.drawable.txt);
                break;
            case "xlsx":
                holder.fileImage.setImageResource(R.drawable.xlsx);
                break;
            default:
                holder.fileImage.setImageResource(R.drawable.file);
                break;
        }

        holder.filename.setText(buf);

        holder.recyclerViewItemsParent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                FileCustom file = new FileCustom(buf, context, fbAuth);
//                Thread thread = new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        file.moveToTrash(file);
//                    }
//                });
//                thread.start();
                new Home().addToThrashList(file);
//                filenamesList.remove(buf);
                return true;
            }
        });

        holder.recyclerViewItemsParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileCustom file = new FileCustom(buf, context, fbAuth);
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        file.downloadAndOpen();
                    }
                });
                thread.start();
            }
        });
    }

    @Override
    public int getItemCount() {
        return filenamesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView fileImage;
        TextView filename;
        LinearLayout recyclerViewItemsParent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            fileImage = itemView.findViewById(R.id.recyclerViewImage);
            filename = itemView.findViewById(R.id.recyclerViewFileName);
            recyclerViewItemsParent = itemView.findViewById(R.id.recyclerViewItemsParent);
        }
    }
}
