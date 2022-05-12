package com.project.osproject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class RecyclerViewTrash extends RecyclerView.Adapter<RecyclerViewTrash.ViewHolder> {

    Context context;
    List<String> trashList;

    boolean isEnable = false;
    boolean isSelectAll = false;
    ArrayList<String> selectList = new ArrayList<>();
    MainViewModel mainViewModel;
    Trash trash;
    String FilePath;
    FirebaseAuth fbAuth;
    private List<String> filenamesList;

    RecyclerViewTrash(Context context, List<String> trashList, FirebaseAuth fbAuth, Trash trash, String Filepath) {
        this.context = context;
        this.trashList = trashList;
        this.fbAuth = fbAuth;
        this.trash = trash;
        this.FilePath = Filepath;
        filenamesList = trash.loadList();
    }

    @NonNull
    @Override
    public RecyclerViewTrash.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_recycler_view_home, parent, false);
        mainViewModel = ViewModelProviders.of((FragmentActivity) context)
                .get(MainViewModel.class);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewTrash.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        String buf = trashList.get(position);

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
                            menuInflater.inflate(R.menu.menu_delete_trash, menu);
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
                                                    new FileCustom(s, context, fbAuth, FilePath).deleteTrashFile();
//                                                    trash.python_delete("/Trash/" + FilePath);
                                                } else {
                                                    new FileCustom(s, context, fbAuth, FilePath).DeleteDir();
//                                                    trash.python_delete_folder(FilePath, s);
                                                }
                                            }
                                        });

                                        thread.start();
                                        if (!s.endsWith("-folder")) {
                                            trashList.remove(s);
                                            trash.saveTrash();
                                        }
                                        else {
                                            trashList.remove(s);
                                        }
                                    }
                                    actionMode.finish();
                                    break;
                                case R.id.menu_select_all:
                                    if (selectList.size() == trashList.size()) {
                                        isSelectAll = false;
                                        selectList.clear();
                                    } else {
                                        isSelectAll = true;
                                        selectList.clear();
                                        selectList.addAll(trashList);
                                    }
                                    mainViewModel.setTextt(String.valueOf(selectList.size()));
                                    notifyDataSetChanged();
                                    break;
                                case R.id.menu_back:
                                    for (String s : selectList) {
                                        Thread thread = new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (!s.endsWith("-folder")) {
                                                    new FileCustom(s, context, fbAuth, FilePath).renameFile(s);
                                                    trash.python_delete("/Trash/" + FilePath);
                                                } else {
                                                    new FileCustom(s, context, fbAuth, FilePath).DeleteDir();
                                                    trash.python_delete_folder(FilePath, s);
                                                }
                                            }
                                        });

                                        thread.start();
                                        if (!s.endsWith("-folder")) {
                                            filenamesList.add(s);
                                            trashList.remove(s);
                                            trash.saveTrash();
                                            trash.saveList(filenamesList);
                                        }
                                        else {
                                            trashList.remove(s);
                                        }
                                    }
                                    actionMode.finish();
                                    break;
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
                            file.downloadAndOpenTrash();
                        }
                    });
                    thread.start();
                } else {
                    trash.PathCompare("/" + buf);
                }
            }
        });
    }

    private void ClickItem(ViewHolder holder) {
        String s = trashList.get(holder.getAdapterPosition());
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
        return trashList.size();
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
}
