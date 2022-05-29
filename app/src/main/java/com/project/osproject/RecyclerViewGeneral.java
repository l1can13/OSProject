package com.project.osproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RecyclerViewGeneral extends RecyclerView.Adapter<RecyclerViewGeneral.ViewHolder> {
    private Context context;
    private List<String> filenamesList;

    private String CurId;
    private MainViewModel mainViewModel;
    private General home;
    private String FilePath;


    @RequiresApi(api = Build.VERSION_CODES.S)

    RecyclerViewGeneral(Context context, List<String> filenamesList, General home, String Filepath, String id) {
        this.context = context;
        this.filenamesList = filenamesList;
        this.home = home;
        this.FilePath = Filepath;
        this.CurId = id;
    }

    @NonNull
    @Override
    public RecyclerViewGeneral.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_recycler_view_general, parent, false);
        mainViewModel = ViewModelProviders.of((FragmentActivity) context)
                .get(MainViewModel.class);
        return new RecyclerViewGeneral.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String buf = filenamesList.get(position);

        StringBuilder typeOfFile = new StringBuilder("");
        for (int i = buf.length() - 1; i > 0; --i) {
            if (buf.charAt(i) == '.' || buf.charAt(i) == '-')
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
            case "folder":
                holder.fileImage.setImageResource(R.drawable.folders);
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


        holder.recyclerViewItemsParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!buf.endsWith("-folder")) {
                    System.out.println("PATH TO FTP " + FilePath + "    " + buf + "   " + CurId);
                    FileCustom file = new FileCustom(buf, context, CurId, FilePath);
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            file.downloadAndOpen();
                        }
                    });
                    thread.start();
                }else{
                    String[] splitter = FilePath.split("/");
                    if(splitter.length == 1){

                        StringBuilder id_folder = new StringBuilder("");

                        for (int i = 0; buf.charAt(i) != '-'; ++i) {
                            id_folder.append(buf.charAt(i));
                        }
                        home.setID(id_folder.toString());

                    }else
                        home.PathCompare("/" + buf);
                }
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
        RelativeLayout recyclerViewItemsParent;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fileImage = itemView.findViewById(R.id.recyclerViewImage);
            filename = itemView.findViewById(R.id.recyclerViewFileName);
            recyclerViewItemsParent = itemView.findViewById(R.id.recyclerViewItemsParent);
        }
    }

    public void filterList(LinkedList<String> filteredList){
        filenamesList = filteredList;
        notifyDataSetChanged();
    }
}
