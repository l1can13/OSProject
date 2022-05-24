package com.project.osproject;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SavedUsers extends AppCompatActivity {

    private FirebaseAuth fbAuth;
    private RecyclerView recyclerView;
    private RecyclerViewSavedUsers recyclerViewAdapter;
    private FloatingActionButton addButton;
    private ImageButton backButton;

    private List<String> savedUsers;

    StorageReference profileRef;

    private FireBaseUser[] FBUsers_array;

    private DatabaseReference dbReference;

    private Python python;


    public void setAdapter(){
        recyclerViewAdapter = new RecyclerViewSavedUsers(this, FBUsers_array, fbAuth, profileRef, python);
        recyclerView.setAdapter(recyclerViewAdapter);
    }

    public void load_FBUser_array(){
        if(savedUsers.size() != 0) {
            FBUsers_array = new FireBaseUser[savedUsers.size() / 4];
            for(int i = 0; i < FBUsers_array.length; ++i){
                FBUsers_array[i] = new FireBaseUser(savedUsers.get(i * 4 + 3), savedUsers.get(i * 4), savedUsers.get(i * 4 + 2), savedUsers.get(i * 4 + 1));
            }
        }
        else{
            FBUsers_array = new FireBaseUser[0];
        }


    }

    public void load_shared_users_list(){
        savedUsers = new ArrayList<String>(Arrays.asList(python.getModule("UserLoader").callAttr("load_shared_list", fbAuth.getCurrentUser().getUid()).toJava(String[].class)));

        load_FBUser_array();
    }

    boolean IsCorrectId(String pattern){
        return python.getModule("UserLoader").callAttr("IsCorrectId", pattern).toJava(Boolean.class);
    }

    boolean isAlreadyShared(String pattern){
        return python.getModule("UserLoader").callAttr("isAlreadyShared", pattern, fbAuth.getCurrentUser().getUid()).toJava(Boolean.class);
    }

    boolean isYou(String pattern){
        return python.getModule("UserLoader").callAttr("isYou", pattern, fbAuth.getCurrentUser().getUid()).toJava(Boolean.class);
    }

    public void update_shared_users_list(){
        final EditText input = new EditText(this);

        AlertDialog.Builder dialog = new AlertDialog.Builder(SavedUsers.this)
                .setTitle("Введите почту, id или телефон пользователя")
                .setView(input)
                .setPositiveButton("Поделиться", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String pattern;
                        pattern = input.getText().toString();
                        if(!pattern.isEmpty()){
                            if(isValidEmail(pattern) || IsCorrectId(pattern) || isValidPhone(pattern)){
                                if(!isAlreadyShared(pattern)) {
                                    if (!isYou(pattern)) {
                                        savedUsers = new ArrayList<String>(Arrays.asList(python.getModule("UserLoader").callAttr("GetUserInfo_by_id_or_email_or_phone", pattern, fbAuth.getCurrentUser().getUid()).toJava(String[].class)));
                                        load_FBUser_array();

                                        setAdapter();

                                        Toast.makeText(SavedUsers.this, "Вы успешно поделились файлами!", Toast.LENGTH_SHORT).show();
                                    }
                                    else
                                        Toast.makeText(SavedUsers.this, "Нельзя делиться с самим собой!", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    Toast.makeText(SavedUsers.this, "С этим человеком Вы уже делились файлами", Toast.LENGTH_SHORT).show();
                                }
                            }
                            else{
                                Toast.makeText(SavedUsers.this, "Вы ввели не почту, не id и не телефон", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else
                        {
                            Toast.makeText(SavedUsers.this, "Пустая строка недопустима!", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        dialog.show();


    }

    private final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public boolean isNumberExists(String phone){
        return python.getModule("UserLoader").callAttr("PhoneAlreadyRegistred", phone).toJava(Boolean.class);
    }

    public boolean isValidPhone(String phone) {

        return ((phone.matches("^((\\+7|7|8)+([0-9]){10})$") || //russian number
                phone.matches("^((\\+?380)([0-9]{9}))$") || //ukrainian number
                phone.matches("^(07[\\d]{8,12}|447[\\d]{7,11})$") || //UK number
                phone.matches("^(\\([0-9]{3}\\) |[0-9]{3}-)[0-9]{3}-[0-9]{4}$") //USA number
                && !isNumberExists(phone))
        );
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_users);

        fbAuth = FirebaseAuth.getInstance();
        addButton = findViewById(R.id.addButton);
        backButton = findViewById(R.id.backButton);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbReference = FirebaseDatabase.getInstance().getReference();

        if(!Python.isStarted()){
            Python.start(new AndroidPlatform(this));
        }

        python = Python.getInstance();

        profileRef = FirebaseStorage.getInstance().getReference().child("profile_avatars");


        load_shared_users_list();

        recyclerViewAdapter = new RecyclerViewSavedUsers(this, FBUsers_array, fbAuth, profileRef, python);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            ActivityCompat.requestPermissions(SavedUsers.this, new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE}, 1);
        }
        ActivityCompat.requestPermissions(SavedUsers.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        recyclerView.setAdapter(recyclerViewAdapter);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update_shared_users_list();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SavedUsers.this, Home.class));
                finish();
            }
        });
    }
}
