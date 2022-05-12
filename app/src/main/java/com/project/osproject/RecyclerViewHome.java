package com.project.osproject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;


import java.lang.reflect.Array;
import java.util.ArrayList;

import com.google.firebase.auth.FirebaseAuth;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class RecyclerViewHome extends RecyclerView.Adapter<RecyclerViewHome.ViewHolder> {


    Context context;
    List<String> filenamesList;




    boolean isEnable = false;
    boolean isSelectAll = false;
    ArrayList<String> selectList = new ArrayList<>();
    MainViewModel mainViewModel;
    Home home;
    private String FilePath;

    FirebaseAuth fbAuth;

    @RequiresApi(api = Build.VERSION_CODES.S)
    RecyclerViewHome(Context context, List<String> filenamesList, FirebaseAuth fbAuth, Home home, String Filepath) {
        this.context = context;
        this.filenamesList = filenamesList;
        this.fbAuth = fbAuth;
        this.home = home;
        this.FilePath = Filepath;
    }

    @NonNull
    @Override
    public RecyclerViewHome.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_recycler_view_home, parent, false);
        mainViewModel = ViewModelProviders.of((FragmentActivity) context)
                .get(MainViewModel.class);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHome.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
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
        holder.recyclerViewItemsParent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                if (!isEnable) {
                    ActionMode.Callback callback = new ActionMode.Callback() {

                        @Override
                        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                            MenuInflater menuInflater = actionMode.getMenuInflater();
                            menuInflater.inflate(R.menu.menu_delete, menu);
                            return true;
                        }

                        @Override
                        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                            isEnable = true;
                            ClickItem(holder);
                            mainViewModel.getTextt().observe((LifecycleOwner) context, new Observer<String>() {
                                @Override
                                public void onChanged(String s) {
                                    actionMode.setTitle(String.format("%s Selected", s));
                                }
                            });
                            return true;
                        }

                        @Override
                        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                            int id = menuItem.getItemId();
                            switch (id) {
                                case R.id.menu_delete:
                                    for (String s : selectList) {
                                        Thread thread = new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (!s.endsWith("-folder")) {
                                                    new FileCustom(s, context, fbAuth, FilePath).deleteFile();
                                                    home.python_delete(FilePath);

                                                } else {
                                                    new FileCustom(s, context, fbAuth, FilePath).DeleteDir();
                                                    home.python_delete_folder(FilePath, s);

                                                }

                                            }
                                        });

                                        thread.start();
                                        if (!s.endsWith("-folder")) {
                                            filenamesList.remove(s);
                                            home.saveList();
                                        }
                                        else {
                                            filenamesList.remove(s);
                                        }
                                    }
                                    actionMode.finish();
                                    break;
                                case R.id.menu_select_all:
                                    if (selectList.size() == filenamesList.size()) {
                                        isSelectAll = false;
                                        selectList.clear();
                                    } else {
                                        isSelectAll = true;
                                        selectList.clear();
                                        selectList.addAll(filenamesList);
                                    }
                                    mainViewModel.setTextt(String.valueOf(selectList.size()));
                                    notifyDataSetChanged();
                                    break;
                                case R.id.menu_rename:
                                    final EditText input = new EditText(context);
                                    AlertDialog.Builder dialog = new AlertDialog.Builder(context)
                                            .setTitle("Введите нового файла")
                                            .setView(input)
                                            .setPositiveButton("Применить", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    if(selectList.size() == 1){
                                                        Thread thread = new Thread(new Runnable() {
                                                            @Override
                                                            public void run() {
                                                                StringBuilder typeOfFile = new StringBuilder("");
                                                                for (String s : selectList) {
                                                                    for (int i = s.length() - 1; i > 0; --i) {
                                                                        if (s.charAt(i) == '.' || s.charAt(i) == '-')
                                                                            break;
                                                                        typeOfFile.append(s.charAt(i));
                                                                    }
                                                                }
                                                                if(!filenamesList.get(position).endsWith("-folder")) {
                                                                    new FileCustom(filenamesList.get(position), context, fbAuth, FilePath).renameFile(input.getText().toString() + "." + typeOfFile.reverse());
                                                                    filenamesList.set(position, input.getText().toString() + "." + typeOfFile);
                                                                    home.saveList();
                                                                }
                                                                else{
                                                                    new FileCustom(filenamesList.get(position), context, fbAuth, FilePath).renameFile(input.getText().toString() + "-" + typeOfFile.reverse());
                                                                    home.python_rename_folder(FilePath, filenamesList.get(position), input.getText().toString() + "-" + typeOfFile);
                                                                    filenamesList.set(position, input.getText().toString() + "-" + typeOfFile);

                                                                }

                                                                new FileCustom(filenamesList.get(position), context, fbAuth).renameFile(input.getText().toString() + "." + typeOfFile.reverse());
                                                                filenamesList.set(position, input.getText().toString() + "." + typeOfFile);
                                                                home.saveList();
                                                            }
                                                        });
                                                        thread.start();
                                                    }
                                                    else{
                                                        AlertDialog.Builder dialog = new AlertDialog.Builder(context)
                                                                .setTitle("Выберите только один файл!")
                                                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                                        dialogInterface.cancel();
                                                                    }
                                                                });
                                                        dialog.show();
                                                    }
                                                }
                                            })
                                            .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.cancel();;
                                                }
                                            });
                                    dialog.show();
                            }
                            return true;
                        }

                        @Override
                        public void onDestroyActionMode(ActionMode actionMode) {
                            isEnable = false;
                            isSelectAll = false;
                            selectList.clear();
                            notifyDataSetChanged();

                        }
                    };
                    ((AppCompatActivity) view.getContext()).startActionMode(callback);
                } else {
                    ClickItem(holder);
                }
                return true;
            }
        });

        if (isSelectAll) {
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.recyclerViewItemsParent.setBackgroundColor(Color.LTGRAY);
        } else {
            holder.checkBox.setVisibility(View.GONE);
            holder.recyclerViewItemsParent.setBackgroundColor(Color.TRANSPARENT);
        }

        holder.recyclerViewItemsParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!buf.endsWith("-folder")) {
                    System.out.println("BUF: " + buf);
                    FileCustom file = new FileCustom(buf, context, fbAuth, FilePath);
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            file.downloadAndOpen();
                        }
                    });
                    thread.start();
                }else{
                    home.PathCompare("/" + buf);
                }
            }
        });
    }

    private void ClickItem(ViewHolder holder) {
        String s = filenamesList.get(holder.getAdapterPosition());
        if (holder.checkBox.getVisibility() == View.GONE) {
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.recyclerViewItemsParent.setBackgroundColor(Color.LTGRAY);

            selectList.add(s);

        } else {
            holder.checkBox.setVisibility(View.GONE);
            holder.recyclerViewItemsParent.setBackgroundColor(Color.TRANSPARENT);

            selectList.remove(s);
        }
        mainViewModel.setTextt(String.valueOf(selectList.size()));

    }

    @Override
    public int getItemCount() {
        return filenamesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView fileImage;
        TextView filename;
        RelativeLayout recyclerViewItemsParent;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.check_box);
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
