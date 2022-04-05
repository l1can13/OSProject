package com.example.osproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

public class Files extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private NavigationView sideMenu;
    private DrawerLayout drawerLayout;
    private ImageButton menuButton;
    private ImageButton backButton;
    private View sideMenuHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_files);

        bottomNavigationView = findViewById(R.id.bottomMenu);
        menuButton = findViewById(R.id.menu);
        drawerLayout = findViewById(R.id.drawerLayout);
        sideMenu = findViewById(R.id.navigationView);

        bottomNavigationView.setSelectedItemId(R.id.filesItem);
        sideMenuHeader = sideMenu.getHeaderView(0);
        backButton = sideMenuHeader.findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.close();
            }
        });

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.photoItem:
                        startActivity(new Intent(getApplicationContext(), Photo.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.filesItem:
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

        sideMenu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.settings:
                        Toast.makeText(getApplicationContext(), "settings", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.notifications:
                        Toast.makeText(getApplicationContext(), "notification", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.trash:
                        Toast.makeText(getApplicationContext(), "trash", Toast.LENGTH_SHORT).show();
                        return true;
                }
                return false;
            }
        });
    }
}
