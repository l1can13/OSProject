package com.example.osproject;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {



    Context context;
    List<String> filenamesList;
    boolean isEnable=false;
    boolean isSelectAll=false;
    ArrayList<String> selectList=new ArrayList<>();
    MainViewModel mainViewModel;

    RecyclerViewAdapter(Context context, List<String> filenamesList) {
        this.context = context;
        this.filenamesList = filenamesList;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_recycler_view, parent, false);
        mainViewModel= ViewModelProviders.of((FragmentActivity) context)
                .get(MainViewModel.class);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.ViewHolder holder, int position) {
        String buf = filenamesList.get(position);

        holder.fileImage.setImageResource(R.drawable.file);
        holder.filename.setText(buf);
        holder.checkBox.setImageResource(R.drawable.checkbox);
        holder.recyclerViewItemsParent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(!isEnable) {

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
                                    actionMode.setTitle(String.format("%s Selected",s));
                                }
                            });

                            return true;
                        }

                        @Override
                        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                            int id = menuItem.getItemId();
                            switch (id){
                                case R.id.menu_delete:

                                    for(String s : selectList){
                                        Thread thread = new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                new FileCustom(s, context).deleteFile();
                                            }
                                        });

                                        thread.start();

                                        filenamesList.remove(s);

                                    }
                                    actionMode.finish();
                                    break;
                                case R.id.menu_select_all:
                                    if(selectList.size() == filenamesList.size()){
                                        isSelectAll = false;
                                        selectList.clear();
                                    }
                                    else{
                                        isSelectAll = true;
                                        selectList.clear();
                                        selectList.addAll(filenamesList);
                                    }
                                    mainViewModel.setTextt(String.valueOf(selectList.size()));
                                    notifyDataSetChanged();
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
                }
                else
                {
                    ClickItem(holder);
                }
                return  true;

            }
        });

        if(isSelectAll){
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.recyclerViewItemsParent.setBackgroundColor(Color.LTGRAY);
        }
        else{
            holder.checkBox.setVisibility(View.GONE);
            holder.recyclerViewItemsParent.setBackgroundColor(Color.TRANSPARENT);
        }

        holder.recyclerViewItemsParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FileCustom file = new FileCustom(buf, context);
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

    private void ClickItem(ViewHolder holder) {
        String s = filenamesList.get(holder.getAdapterPosition());
        if(holder.checkBox.getVisibility() == View.GONE){
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.recyclerViewItemsParent.setBackgroundColor(Color.LTGRAY);

            selectList.add(s);

        }
        else{
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
        LinearLayout recyclerViewItemsParent;
        ImageView checkBox;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.check_box);
            fileImage = itemView.findViewById(R.id.recyclerViewImage);
            filename = itemView.findViewById(R.id.recyclerViewFileName);
            recyclerViewItemsParent = itemView.findViewById(R.id.recyclerViewItemsParent);
        }
    }
}
