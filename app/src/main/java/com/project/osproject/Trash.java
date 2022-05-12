package com.project.osproject;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Trash extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerViewTrash recyclerViewAdapter;
    private List<String> trashList = new LinkedList<>();
    private Home home = new Home();
    private FirebaseAuth fbAuth;
    private SharedPreferences fb_SharedPreference_settings;
    private Python python;

    public void saveTrash() {
        python.getModule("main").callAttr("Save", "User_Data/" + fbAuth.getUid() + "/Trash/", trashList.toArray(new String[0]));
    }

    public void saveList(List<String> filenamesList) {
        python.getModule("main").callAttr("Save", "User_Data/" + fbAuth.getUid() + "/Current/", filenamesList.toArray(new String[0]));
    }

    @SuppressLint({"NotifyDataSetChanged", "RestrictedApi"})
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public List<String> loadTrash() {
        List<String> arrayItems = new ArrayList<>();

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("User_Data/" + fbAuth.getUid() + "/Trash/");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayItems.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                    String data = "";
                    if (postSnapshot.getValue() instanceof ArrayList || postSnapshot.getValue() instanceof HashMap) {
                        data = postSnapshot.getKey();
                    } else {
                        data = postSnapshot.getValue(String.class);
                    }
                    arrayItems.add(data);
                    recyclerViewAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return arrayItems;
    }

    @SuppressLint({"NotifyDataSetChanged", "RestrictedApi"})
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public List<String> loadList() {
        List<String> arrayItems = new ArrayList<>();

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("User_Data/" + fbAuth.getUid() + "/Current/");

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayItems.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    String data = "";
                    if (postSnapshot.getValue() instanceof ArrayList || postSnapshot.getValue() instanceof HashMap) {
                        data = postSnapshot.getKey();
                    } else {
                        data = postSnapshot.getValue(String.class);
                    }
                    arrayItems.add(data);
                    recyclerViewAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return arrayItems;
    }

    public void PathCompare(String path) {
        try {
            trashList = new ArrayList<String>(Arrays.asList(python.getModule("main")
                    .callAttr("loader", "User_Data/" + fbAuth.getUid() + "/Trash/")
                    .toJava(String[].class)));
            recyclerView.setAdapter(new RecyclerViewTrash(this, trashList, fbAuth, this, ""));
        } catch (NullPointerException e) {
            Toast.makeText(this, "Попробуйте еще раз!", Toast.LENGTH_SHORT).show();
        }
    }

    public void python_delete_folder(String path, String name) {
        python.getModule("main").callAttr("delete_folder", "User_Data/" + fbAuth.getUid() + path, name);
    }

    public void python_delete(String path) {
        python.getModule("main").callAttr("delete", "User_Data/" + fbAuth.getUid() + path);
    }

    public void python_rename_folder(String path, String old_name, String new_name) {
        python.getModule("main").callAttr("folder_rename", "User_Data/" + fbAuth.getUid() + path, old_name, new_name);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trash);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        fb_SharedPreference_settings = getPreferences(MODE_PRIVATE);
        fbAuth = FirebaseAuth.getInstance();
        if (fb_SharedPreference_settings.contains("fbAuth")) {
            Gson gson = new Gson();
            String json = fb_SharedPreference_settings.getString("fbAuth", "");
            fbAuth = gson.fromJson(json, FirebaseAuth.class);
        }

        if (!Python.isStarted())
            Python.start(new AndroidPlatform(this));
        python = Python.getInstance();

        trashList = loadTrash();

        recyclerViewAdapter = new RecyclerViewTrash(this, trashList, fbAuth, this, "");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE}, 1);
        }
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        recyclerView.setAdapter(recyclerViewAdapter);
    }
}
