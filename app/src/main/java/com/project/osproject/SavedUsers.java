package com.project.osproject;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;

import java.util.LinkedList;
import java.util.List;

public class SavedUsers extends AppCompatActivity {

    private FirebaseAuth fbAuth;
    private RecyclerView recyclerView;
    private RecyclerViewSavedUsers recyclerViewAdapter;
    private FloatingActionButton addButton;
    private List<String> savedUsers = new LinkedList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_users);

        fbAuth = FirebaseAuth.getInstance();
        addButton = findViewById(R.id.addButton);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerViewAdapter = new RecyclerViewSavedUsers(this, savedUsers, fbAuth);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            ActivityCompat.requestPermissions(SavedUsers.this, new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE}, 1);
        }
        ActivityCompat.requestPermissions(SavedUsers.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        recyclerView.setAdapter(recyclerViewAdapter);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // устанавливаем логику на кнопку "+"
            }
        });
    }
}
