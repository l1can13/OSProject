package com.example.osproject;

import android.content.Context;
import android.graphics.Color;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
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

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewSavedUsers extends RecyclerView.Adapter<RecyclerViewSavedUsers.ViewHolder> {

    Context context;
    List<String> savedUsersList;

    boolean isEnable = false;
    boolean isSelectAll = false;
    ArrayList<String> selectList = new ArrayList<>();
    MainViewModel mainViewModel;

    FirebaseAuth fbAuth;

    RecyclerViewSavedUsers(Context context, List<String> savedUsersList, FirebaseAuth fbAuth) {
        this.context = context;
        this.savedUsersList = savedUsersList;
        this.fbAuth = fbAuth;
    }

    @NonNull
    @Override
    public RecyclerViewSavedUsers.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_recycler_view_saved_users, parent, false);
        mainViewModel = ViewModelProviders.of((FragmentActivity) context)
                .get(MainViewModel.class);
        return new RecyclerViewSavedUsers.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewSavedUsers.ViewHolder holder, int position) {
        String buf = savedUsersList.get(position);

        // Тут нужно установить аватар пользователя и textview'шки (имя пользователя и почту)
        // holder.userAvatar.setImageResource(...);
        // holder.username.setText(buf);
        // holder.userEmail.setText(...);

        // holder.recyclerViewItemsParent.setClickListener... - устаналиваем логику на нажатие (если потребуется)

        // Далее - множественный выбор
        // Здесь устаналивается логика на длительное нажатие (скорее всего тут ничего корректировать не придется)
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
                                        savedUsersList.remove(s);
                                    }
                                    actionMode.finish();
                                    break;
                                case R.id.menu_select_all:
                                    if (selectList.size() == savedUsersList.size()) {
                                        isSelectAll = false;
                                        selectList.clear();
                                    } else {
                                        isSelectAll = true;
                                        selectList.clear();
                                        selectList.addAll(savedUsersList);
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
    }

    private void ClickItem(RecyclerViewSavedUsers.ViewHolder holder) {
        String s = savedUsersList.get(holder.getAdapterPosition());
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
        return savedUsersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView userAvatar;
        TextView username, userEmail;
        RelativeLayout recyclerViewItemsParent;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.check_box);
            userAvatar = itemView.findViewById(R.id.recyclerViewImage);
            username = itemView.findViewById(R.id.username);
            userEmail = itemView.findViewById(R.id.userEmail);
            recyclerViewItemsParent = itemView.findViewById(R.id.recyclerViewItemsParent);
        }
    }
}
