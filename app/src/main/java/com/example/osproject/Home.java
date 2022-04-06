package com.example.osproject;

import static androidx.core.app.NotificationCompat.PRIORITY_HIGH;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Home extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private DrawerLayout drawerLayout;
    private ImageButton menuButton;
    private ImageButton backButton;
    private ImageButton addButton;
    private NavigationView sideMenu;
    private View sideMenuHeader;
    private ToggleButton TglBtnLast;
    private ToggleButton TglBtnFav;
    private TextView TestText;
    private NotificationManager notificationManager;
    private SharedPreferences fb_SharedPreference_settings;
    private List<FileCustom> fileList = new LinkedList<>();

    private FirebaseAuth fbAuth;

    private static final int NOTIFY_ID = 101;
    private static String CHANNEL_ID = "Test channel";

    public void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        Intent chooser = Intent.createChooser(intent, "Select a File to Upload");

        try {
            startActivityForResult(chooser, 0);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);

        if (resultCode == RESULT_OK && requestCode == 0) {
            Uri uri = result.getData();
            FileCustom file = new FileCustom(uri, getApplicationContext());
            fileList.add(file);

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        file.upload();
                    } catch (IOException e) {
                        System.out.println("ОШИБКА В ACTIVITY RESULT!");
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
            Toast.makeText(this, "Файл успешно загружен на сервер!", Toast.LENGTH_SHORT).show();
            System.out.println("ВСЕ ПОЛУЧИЛОСЬ!");
            thread.interrupt();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityCompat.requestPermissions(Home.this, new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE }, 1);
        fb_SharedPreference_settings = getPreferences(MODE_PRIVATE);
        fbAuth = FirebaseAuth.getInstance();
        if (fb_SharedPreference_settings.contains("fbAuth")) {
            Gson gson = new Gson();
            String json = fb_SharedPreference_settings.getString("fbAuth", "");
            fbAuth = gson.fromJson(json, FirebaseAuth.class);
        }

        FirebaseUser fbUser = fbAuth.getCurrentUser();

        if (fbUser == null) {
            startActivity(new Intent(getApplicationContext(), Registration.class));
        } else {
            setContentView(R.layout.activity_home);
            sideMenu = findViewById(R.id.navigationView);
            menuButton = findViewById(R.id.menu);
            drawerLayout = findViewById(R.id.drawerLayout);
            bottomNavigationView = findViewById(R.id.bottomMenu);
            addButton = findViewById(R.id.addButton);
            TglBtnLast = (ToggleButton) findViewById(R.id.buttonLast);
            TglBtnFav = (ToggleButton) findViewById(R.id.buttonFav);
            TestText = (TextView) findViewById(R.id.textViewtest);
            notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);


            sideMenuHeader = sideMenu.getHeaderView(0);
            backButton = sideMenuHeader.findViewById(R.id.backButton);

            bottomNavigationView.setSelectedItemId(R.id.homeItem);

            TglBtnLast.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TestText.setTextSize(20);
                    TglBtnFav.setTextColor(getResources().getColor(R.color.darkGreenTrans));
                    TglBtnLast.setTextColor(getResources().getColor(R.color.darkGreen));
                }
            });
            TglBtnFav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    TestText.setTextSize(40);
                    TglBtnLast.setTextColor(getResources().getColor(R.color.darkGreenTrans));
                    TglBtnFav.setTextColor(getResources().getColor(R.color.darkGreen));
                }
            });

            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showFileChooser();
                }
            });

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

            sideMenu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.settings:
                            Toast.makeText(getApplicationContext(), "settings", Toast.LENGTH_SHORT).show();
                            return true;
                        case R.id.notifications:
                            Dialog dialog;

                            final String[] items = {" Изменение общих файлов", " Обновления приложения", " Приглашение в команду"};
                            final ArrayList itemsSelected = new ArrayList();

                            AlertDialog.Builder builder = new AlertDialog.Builder(Home.this);
                            builder.setTitle("Уведомления : ");
                            builder.setMultiChoiceItems(items, null,
                                    new DialogInterface.OnMultiChoiceClickListener() {


                                        @Override
                                        public void onClick(DialogInterface dialog, int selectedItemId,
                                                            boolean isSelected) {
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

                            Intent intent = new Intent(getApplicationContext(), Home.class);
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

            bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.photoItem:
                            startActivity(new Intent(getApplicationContext(), Photo.class));
                            overridePendingTransition(0, 0);
                            return true;
                        case R.id.filesItem:
                            startActivity(new Intent(getApplicationContext(), Files.class));
                            overridePendingTransition(0, 0);
                            return true;
                        case R.id.homeItem:
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
}
