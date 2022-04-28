package com.project.osproject;

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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
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

import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Home extends AppCompatActivity {

    /* Элементы из xml файлов */
    private RecyclerViewHome recyclerViewAdapter;
    private BottomNavigationView bottomNavigationView;
    private DrawerLayout drawerLayout;
    private ImageButton menuButton;
    private ImageButton backButton;
    private FloatingActionButton addButton;
    private NavigationView sideMenu;
    private View sideMenuHeader;
    private NotificationManager notificationManager;
    private RecyclerView recyclerView;
    private EditText findBar;

    /*Элементы для бокового меню*/
    private TextView left_side_username;
    private TextView left_side_email;
    private CircleImageView left_side_avatar;

    /* Shared Preferences */
    private SharedPreferences fb_SharedPreference_settings;
    private List<String> filenamesList = new LinkedList<>();

    /* FireBase */
    private FirebaseAuth fbAuth;
    private DatabaseReference dbReference;
    private Python python;
    private boolean python_flag = false;

    /* Уведомления */
    private static final int NOTIFY_ID = 101;
    private static String CHANNEL_ID = "Test channel";

    private String FilePath = "";

    public void showFileChooser() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        Intent chooser = Intent.createChooser(intent, "Select a File to Upload");
        System.out.println("FILEPATH: " + FilePath);

        try {
            startActivityForResult(chooser, 0);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Please install a File Manager.", Toast.LENGTH_SHORT).show();
        }
    }

    private void saveList(List<String> list) {

        python.getModule("main").callAttr("Save", "User_Data/" + fbAuth.getUid() + "/" + FilePath, filenamesList.toArray(new String[0]),python_flag);
        python_flag = true;
        //dbReference.child("User_Data").child(fbAuth.getUid()+FilePath).setValue(list);

    }

    @SuppressLint("NotifyDataSetChanged")
    public void PathCompare(String path){
        FilePath += path;
        //loaderList();
        filenamesList = loadList();
        recyclerView.setAdapter(new RecyclerViewHome(this, filenamesList,fbAuth, this));
    }

    @SuppressLint({"NotifyDataSetChanged", "RestrictedApi"})
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void loaderList() {

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("User_Data/" + fbAuth.getUid()+FilePath);
        System.out.println("ПУТЬ: " + dbRef.getPath());
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                filenamesList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    String data = "";
                    if(postSnapshot.getValue() instanceof ArrayList){
                        data = postSnapshot.getKey();
                    }
                    else{
                        data = postSnapshot.getValue(String.class);
                    }
                    filenamesList.add(data);
                    recyclerViewAdapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @SuppressLint({"NotifyDataSetChanged", "RestrictedApi"})
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private List<String> loadList() {
        List<String> arrayItems = new ArrayList<>();

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("User_Data/" + fbAuth.getUid()+FilePath);

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayItems.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {

                    String data = "";
                    if(postSnapshot.getValue() instanceof ArrayList || postSnapshot.getValue() instanceof HashMap){
                        data = postSnapshot.getKey();
                    }
                    else{
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

    private boolean isFileDuplicate(FileCustom file){
        for(int i = 0; i < filenamesList.size(); ++i){
            if(filenamesList.get(i).equals(file.getName())){
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);

        if (resultCode == RESULT_OK && requestCode == 0) {
            Uri uri = result.getData();
            FileCustom file = new FileCustom(uri, getApplicationContext(), fbAuth, FilePath);
            if (!isFileDuplicate(file)) {
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        file.upload();
                    }
                });
                thread.start();
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (file.getFlag()) {
                    filenamesList.add(file.getName());
                    recyclerViewAdapter.notifyItemInserted(filenamesList.size() - 1);
                    recyclerView.scrollToPosition(filenamesList.size() - 1);
                    saveList(filenamesList);
                }
            } else {
                Toast.makeText(Home.this, "Такой файл уже есть в вашем хранилище!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setAvatar(StorageReference profileRef){
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("User_Avatar").child(fbAuth.getUid());
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).into(left_side_avatar);
                        }
                    });
                }else{
                    GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(Home.this);
                    if(googleSignInAccount == null) {
                        StorageReference Ref = FirebaseStorage.getInstance().getReference()
                                .child("profile_avatars").child("default.jpg");
                        Ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Picasso.get().load(uri).into(left_side_avatar);
                            }
                        });
                    }else
                        Picasso.get().load(googleSignInAccount.getPhotoUrl()).into(left_side_avatar);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveList(filenamesList);
    }

    private void ShowOptions(){
        AlertDialog.Builder dialog = new AlertDialog.Builder(Home.this)
                .setPositiveButton("Добавить файл", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        showFileChooser();

                    }
                })
                .setNegativeButton("Создать папку", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        CreateFolder();
                        dialogInterface.cancel();
                    }
                })
                .setNeutralButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        dialog.show();

    }

    private void CreateFolder() {
        final EditText input = new EditText(this);
        AlertDialog.Builder dialog = new AlertDialog.Builder(Home.this)
                .setTitle("Введите название папки")
                .setView(input)
                .setPositiveButton("Создать папку", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (!input.getText().toString().isEmpty()) {
                            FileCustom file = new FileCustom(getApplicationContext(), fbAuth, FilePath+input.getText().toString()+"-folder/");
                            Thread thread = new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    file.CreateDir();
                                }
                            });
                            thread.start();
                        } else {
                            Toast.makeText(Home.this, "Введите непустое название!", Toast.LENGTH_SHORT).show();
                            input.setText("");
                        }
                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();;
                    }
                });
        dialog.show();
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
            if (fbUser.isEmailVerified()) {
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
                findBar = findViewById(R.id.search);
                sideMenuHeader = sideMenu.getHeaderView(0);
                backButton = sideMenuHeader.findViewById(R.id.backButton);

                left_side_avatar = sideMenuHeader.findViewById(R.id.userAvatar);
                left_side_email = sideMenuHeader.findViewById(R.id.userEmail);
                left_side_username = sideMenuHeader.findViewById(R.id.username);


                dbReference.child("User_Info").child(fbAuth.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        FireBaseUser user = snapshot.getValue(FireBaseUser.class);
                        left_side_username.setText(user.getUsername());
                        left_side_email.setText(user.getEmail());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                if(!Python.isStarted())
                    Python.start(new AndroidPlatform(this));


                setAvatar(FirebaseStorage.getInstance().getReference()
                        .child("profile_avatars").child(fbAuth.getUid() + ".jpg"));

                recyclerView.setLayoutManager(new LinearLayoutManager(this));

                recyclerViewAdapter = new RecyclerViewHome(this, filenamesList,fbAuth, this);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    ActivityCompat.requestPermissions(Home.this, new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE}, 1);
                }
                ActivityCompat.requestPermissions(Home.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

                recyclerView.setAdapter(recyclerViewAdapter);

                bottomNavigationView.setSelectedItemId(R.id.homeItem);

                python = Python.getInstance();

                addButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //System.out.println(py.getModule("main").callAttr("Connect"));
                        //System.out.println("ХУЙНЯ: " + obj.toString());
                        ShowOptions();
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
                            case R.id.savedUsers:
                                saveList(filenamesList);
                                startActivity(new Intent(getApplicationContext(), SavedUsers.class));
                                overridePendingTransition(0, 0);
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
                            case R.id.homeItem:
                                saveList(filenamesList);
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
            else
            {
                //Высылаем подтверждение по почте
                verifyEmail();
            }
        }
    }

    private void verifyEmail() {
        //Диалоговое окно
        String UserEmail = fbAuth.getCurrentUser().getEmail();
        AlertDialog.Builder dialog = new AlertDialog.Builder(Home.this)
                .setTitle("Вы не авторизованы!")
                .setMessage("Пожалуйста, подтвердите свой адрес электоронной почты " + UserEmail +"!")
                .setPositiveButton("Отправить код еще раз", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        fbAuth.getCurrentUser().sendEmailVerification()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(Home.this, "Письмо с подтверждением отправлено на почту " + UserEmail, Toast.LENGTH_SHORT).show();
                                    }
                                });
                        startActivity(new Intent(Home.this, Home.class));
                    }
                }).setNegativeButton("Выйти из аккаунта", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        fbAuth.signOut();

                        SharedPreferences.Editor prefsEditor = fb_SharedPreference_settings.edit();
                        prefsEditor.remove("fbAuth");
                        prefsEditor.apply();
                        startActivity(new Intent(Home.this, Home.class));
                    }
                }).setNeutralButton("Войти", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(new Intent(Home.this, Login.class));
                    }
                });
        dialog.show();
    }
}

