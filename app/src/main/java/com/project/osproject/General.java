package com.project.osproject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import static androidx.core.app.NotificationCompat.PRIORITY_HIGH;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class General extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private NavigationView sideMenu;
    private DrawerLayout drawerLayout;
    private ImageButton menuButton;
    private ImageButton backButton;
    private View sideMenuHeader;
    private NotificationManager notificationManager;

    private static final int NOTIFY_ID = 101;
    private static String CHANNEL_ID = "Test channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general);

        bottomNavigationView = findViewById(R.id.bottomMenu);
        menuButton = findViewById(R.id.menu);
        drawerLayout = findViewById(R.id.drawerLayout);
        sideMenu = findViewById(R.id.navigationView);
        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        bottomNavigationView.setSelectedItemId(R.id.generalItem);
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
                    case R.id.homeItem:
                        startActivity(new Intent(getApplicationContext(), Home.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.generalItem:
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
                    case R.id.savedUsers:
                        startActivity(new Intent(getApplicationContext(), SavedUsers.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.notifications:
                        Dialog dialog;

                        final String[] items = {" Изменение общих файлов", " Обновления приложения", " Приглашение в команду"};
                        final ArrayList itemsSelected = new ArrayList();

                        AlertDialog.Builder builder = new AlertDialog.Builder(General.this);
                        builder.setTitle("Уведомления : ");
                        builder.setMultiChoiceItems(items, null,
                                new DialogInterface.OnMultiChoiceClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int selectedItemId, boolean isSelected) {
                                        if (isSelected) {

                                            itemsSelected.add(selectedItemId);
                                        } else if (itemsSelected.contains(selectedItemId)) {

                                            itemsSelected.remove(Integer.valueOf(selectedItemId));
                                        }
                                    }
                                })
                                .setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                })
                                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                });

                        dialog = builder.create();
                        dialog.show();

                        Intent intent = new Intent(getApplicationContext(), General.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        NotificationCompat.Builder notificationBuilder =
                                new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                                        .setAutoCancel(false)
                                        .setWhen(System.currentTimeMillis())
                                        .setContentText("Test text")
                                        .setContentTitle("Test title")
                                        .setPriority(PRIORITY_HIGH);

                        createChannelIfNeeded(notificationManager);
                        notificationManager.notify(NOTIFY_ID, notificationBuilder.build());
                        return true;
                    case R.id.trash:
                        Toast.makeText(getApplicationContext(), "trash", Toast.LENGTH_SHORT).show();
                        return true;
                }
                return false;
            }

            public void createChannelIfNeeded(NotificationManager manager) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);
                    manager.createNotificationChannel(notificationChannel);
                }
            }
        });
    }
}
