package com.project.osproject;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
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

import com.chaquo.python.Python;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RecyclerViewSavedUsers extends RecyclerView.Adapter<RecyclerViewSavedUsers.ViewHolder> {

    Context context;
    //List<String> savedUsersList;
    StorageReference profileRef;
    boolean isEnable = false;
    boolean isSelectAll = false;
    ArrayList<String> selectList = new ArrayList<>();

    private Python python;

    private FireBaseUser[] FBUsers_array;

    MainViewModel mainViewModel;

    FirebaseAuth fbAuth;

    RecyclerViewSavedUsers(Context context, FireBaseUser[] FBUsers_array, FirebaseAuth fbAuth, StorageReference ref, Python python) {
        this.context = context;
        this.FBUsers_array = FBUsers_array;
        this.fbAuth = fbAuth;
        this.profileRef = ref;
        this.python = python;
    }


    private void setAvatar(@NonNull RecyclerViewSavedUsers.ViewHolder avatar, String id){
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("User_Avatar").child(fbAuth.getUid());
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    profileRef.child(id + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).into(avatar.userAvatar);
                        }
                    });
                } else {
                    StorageReference Ref = FirebaseStorage.getInstance().getReference()
                            .child("profile_avatars").child("default.jpg");
                    Ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).into(avatar.userAvatar);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void remove(String name){
        int i;
        for(i = 0; i < FBUsers_array.length; ++i){
            if(FBUsers_array[i].getUsername().equals(name))
                break;
        }

        FireBaseUser[] arrDestination = new FireBaseUser[FBUsers_array.length - 1];

        int remainingElements = FBUsers_array.length - ( i + 1 );

        System.arraycopy(FBUsers_array, 0, arrDestination, 0, i);
        System.arraycopy(FBUsers_array, i + 1, arrDestination, i, remainingElements);

        FBUsers_array = arrDestination;

        python.getModule("UserLoader").callAttr("delete_by_name", name, fbAuth.getUid());

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
         if(FBUsers_array.length > 0) {
             FireBaseUser buf = FBUsers_array[position];
             setAvatar(holder, buf.getId());
             holder.username.setText(buf.getUsername());
             holder.userEmail.setText(buf.getEmail());
         }

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
                                        remove(s);
                                    }
                                    actionMode.finish();
                                    break;
                                case R.id.menu_select_all:
                                    if (selectList.size() == FBUsers_array.length) {
                                        isSelectAll = false;
                                        selectList.clear();
                                    } else {
                                        isSelectAll = true;
                                        selectList.clear();
                                        for(int i = 0; i < FBUsers_array.length; ++i){
                                            selectList.add(i, FBUsers_array[i].getUsername());
                                        }
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
        String s = FBUsers_array[holder.getAdapterPosition()].getUsername();
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
        return FBUsers_array.length;
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
