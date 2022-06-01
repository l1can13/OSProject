package com.project.osproject;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import static androidx.core.app.NotificationCompat.PRIORITY_HIGH;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
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
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
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
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class General extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private NavigationView sideMenu;
    private DrawerLayout drawerLayout;
    private ImageButton menuButton;
    private ImageButton backButton;
    private View sideMenuHeader;

    private EditText findBar;

    private RecyclerViewGeneral recyclerViewAdapter;
    private RecyclerView recyclerView;

    private FirebaseAuth fbAuth;
    private DatabaseReference dbReference;
    private Python python;

    private String FilePath = "";
    private String cur_id = "";

    private List<String> shared_list;

    private NotificationManager notificationManager;

    private TextView left_side_username;
    private TextView left_side_email;
    private CircleImageView left_side_avatar;

    private static final int NOTIFY_ID = 101;
    private static String CHANNEL_ID = "Test channel";

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
                    GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(General.this);
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

    private void FilePathConverter() {
        String[] splitter = FilePath.split("/");
        FilePath = "";
        for (int i = 3; i < splitter.length; ++i)
            FilePath += "/" + splitter[i];
    }

    public void python_getBack() {
        FilePath = python.getModule("main").callAttr("back", "User_Data/" + fbAuth.getUid() + "/Shared/" + FilePath).toJava(String.class);
        shared_list = new ArrayList<String>(Arrays.asList(python.getModule("main")
                .callAttr("loader", FilePath)
                .toJava(String[].class)));
        FilePathConverter();
        recyclerView.setAdapter(new RecyclerViewGeneral(this, shared_list, this,  FilePath, cur_id));
    }

    @Override
    public void onBackPressed() {
        if (!FilePath.isEmpty())
            python_getBack();
        else {
            //saveList();
            super.onBackPressed();
        }
    }


    public boolean isNewShared(){
        return python.getModule("UserLoader").callAttr("get_shared_status", fbAuth.getUid()).toJava(Boolean.class);
    }

    private void load_shared_list(){
        shared_list = new ArrayList<String>(Arrays.asList(python.getModule("main")
                .callAttr("loader", "User_Data/" + fbAuth.getUid() + "/Shared")
                .toJava(String[].class)));
    }


    public void setID(String id){
        cur_id = id;
        try {
            shared_list = new ArrayList<String>(Arrays.asList(python.getModule("main")
                    .callAttr("loader", "User_Data/" + fbAuth.getUid() + "/Shared/" + cur_id + "-folder")
                    .toJava(String[].class)));
            recyclerView.setAdapter(new RecyclerViewGeneral(this, shared_list, this,  "/", cur_id));
        } catch (NullPointerException e) {
            Toast.makeText(this, "Попробуйте еще раз!", Toast.LENGTH_SHORT).show();
        }

    }

    public void PathCompare(String path){
        FilePath += path;
        try {
            shared_list = new ArrayList<String>(Arrays.asList(python.getModule("main")
                    .callAttr("loader", "User_Data/" + fbAuth.getUid() + "/Shared/" + cur_id + "-folder/" + FilePath)
                    .toJava(String[].class)));
            recyclerView.setAdapter(new RecyclerViewGeneral(this, shared_list, this,  FilePath, cur_id));
        } catch (NullPointerException e) {
            Toast.makeText(this, "Попробуйте еще раз!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general);

        recyclerView = findViewById(R.id.recyclerView);

        bottomNavigationView = findViewById(R.id.bottomMenu);
        menuButton = findViewById(R.id.menu);
        drawerLayout = findViewById(R.id.drawerLayout);
        sideMenu = findViewById(R.id.navigationView);
        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        findBar = findViewById(R.id.search);

        bottomNavigationView.setSelectedItemId(R.id.generalItem);
        sideMenuHeader = sideMenu.getHeaderView(0);
        backButton = sideMenuHeader.findViewById(R.id.backButton);

        if(!Python.isStarted()){
            Python.start(new AndroidPlatform(this));
        }

        python = Python.getInstance();

        fbAuth = FirebaseAuth.getInstance();

        if(isNewShared()){
            ShowWindow();
        }

        load_shared_list();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerViewAdapter = new RecyclerViewGeneral(this, shared_list, this, FilePath, cur_id);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            ActivityCompat.requestPermissions(General.this, new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE}, 1);
        }
        ActivityCompat.requestPermissions(General.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        recyclerView.setAdapter(recyclerViewAdapter);


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

        findBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {


            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });

        left_side_avatar = sideMenuHeader.findViewById(R.id.userAvatar);
        left_side_email = sideMenuHeader.findViewById(R.id.userEmail);
        left_side_username = sideMenuHeader.findViewById(R.id.username);
        fbAuth = FirebaseAuth.getInstance();
        dbReference = FirebaseDatabase.getInstance().getReference();

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

        setAvatar(FirebaseStorage.getInstance().getReference()
                .child("profile_avatars").child(fbAuth.getUid() + ".jpg"));

        sideMenu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.savedUsers:
                        startActivity(new Intent(getApplicationContext(), SavedUsers.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.trash:
                        startActivity(new Intent(getApplicationContext(), Trash.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });
    }

    private void filter(String text){
        LinkedList<String> filenameListRem = new LinkedList<>();


        for(String item : shared_list){
            if(item.toLowerCase().contains(text.toLowerCase())){
                filenameListRem.add(item);
            }
        }

        recyclerViewAdapter.filterList(filenameListRem);
    }

    private void ShowWindow() {

        AlertDialog.Builder dialog = new AlertDialog.Builder(General.this)
                .setTitle("С Вами поделились файлами!")
                .setNeutralButton("Ок", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        setAllAccept();
                        dialogInterface.cancel();
                    }
                });
        dialog.show();

    }

    private void setAllAccept() {
        python.getModule("UserLoader").callAttr("setAllAccept", fbAuth.getUid());
    }
}
