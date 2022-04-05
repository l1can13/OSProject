package com.example.osproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class Photo extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        bottomNavigationView = findViewById(R.id.bottomMenu);
        bottomNavigationView.setSelectedItemId(R.id.photoItem);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.photoItem:
                        return true;
                    case R.id.filesItem:
                        startActivity(new Intent(getApplicationContext(), Files.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.homeItem:
                        startActivity(new Intent(getApplicationContext(), Home.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.generalItem:
                        startActivity(new Intent(getApplicationContext(), General.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.accountItem:
                        startActivity(new Intent(getApplicationContext(), Account.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });
    }
}
