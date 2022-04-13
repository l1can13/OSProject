package com.example.osproject;

import static androidx.core.app.NotificationCompat.PRIORITY_HIGH;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Home extends AppCompatActivity {

    /* Элементы из xml файлов */
    private RecyclerViewAdapter recyclerViewAdapter;
    private BottomNavigationView bottomNavigationView;
    private DrawerLayout drawerLayout;
    private ImageButton menuButton;
    private ImageButton backButton;
    private FloatingActionButton addButton;
    private NavigationView sideMenu;
    private View sideMenuHeader;
    private NotificationManager notificationManager;
    private RecyclerView recyclerView;

    /* Shared Preferences */
    private SharedPreferences fb_SharedPreference_settings;
    private List<String> filenamesList = new LinkedList<>();

    /* FireBase */
    private FirebaseAuth fbAuth;
    private DatabaseReference dbReference;

    /* Уведомления */
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

    private void saveList(List<String> list) {
        try {
            dbReference.child("User_Data").child(fbAuth.getUid()).setValue(list);
        } catch(Exception e) {
            System.out.println("ОШИБКА ПРИ СОХРАНЕНИИ СПИСКА ИМЕН ФАЙЛОВ!");
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private List<String> loadList() {
        List<String> arrayItems = new ArrayList<>();

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("User_Data/" + fbAuth.getUid());

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayItems.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    String data = postSnapshot.getValue(String.class);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);

        if (resultCode == RESULT_OK && requestCode == 0) {
            Uri uri = result.getData();
            FileCustom file = new FileCustom(uri, getApplicationContext());
            filenamesList.add(file.getName());
            recyclerViewAdapter.notifyItemInserted(filenamesList.size() - 1);
            recyclerView.scrollToPosition(filenamesList.size() - 1);

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    file.upload();
                }
            });
            thread.start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveList(filenamesList);
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
            startActivity(new Intent(this, Registration.class));
        } else {
            dbReference = FirebaseDatabase.getInstance().getReference();
            filenamesList = loadList();

            setContentView(R.layout.activity_home);
            sideMenu = findViewById(R.id.navigationView);
            addButton = findViewById(R.id.addButton);
            menuButton = findViewById(R.id.menu);
            drawerLayout = findViewById(R.id.drawerLayout);
            bottomNavigationView = findViewById(R.id.bottomMenu);
            recyclerView = findViewById(R.id.recyclerView);
            notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

            sideMenuHeader = sideMenu.getHeaderView(0);
            backButton = sideMenuHeader.findViewById(R.id.backButton);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));

            recyclerViewAdapter = new RecyclerViewAdapter(this, filenamesList);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                ActivityCompat.requestPermissions(Home.this, new String[]{ Manifest.permission.MANAGE_EXTERNAL_STORAGE }, 1);
            }
            ActivityCompat.requestPermissions(Home.this, new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE }, 1);

            recyclerView.setAdapter(recyclerViewAdapter);

            bottomNavigationView.setSelectedItemId(R.id.homeItem);

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
                            saveList(filenamesList);
                            startActivity(new Intent(getApplicationContext(), Photo.class));
                            overridePendingTransition(0, 0);
                            return true;
                        case R.id.filesItem:
                            saveList(filenamesList);
                            startActivity(new Intent(getApplicationContext(), Files.class));
                            overridePendingTransition(0, 0);
                            return true;
                        case R.id.homeItem:
                            return true;
                        case R.id.generalItem:
                            saveList(filenamesList);
                            startActivity(new Intent(getApplicationContext(), General.class));
                            overridePendingTransition(0, 0);
                            return true;
                        case R.id.accountItem:
                            saveList(filenamesList);
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
