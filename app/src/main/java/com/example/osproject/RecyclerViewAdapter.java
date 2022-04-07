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

import java.io.FileNotFoundException;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    Context context;
    List<String> filenamesList;

    RecyclerViewAdapter(Context context, List<String> filenamesList) {
        this.context = context;
        this.filenamesList = filenamesList;
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

        holder.fileImage.setImageResource(R.drawable.file);
        holder.filename.setText(buf);

        holder.recyclerViewItemsParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileCustom file = new FileCustom(buf, context);
                file.downloadFile();
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