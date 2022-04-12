package com.example.osproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import android.view.View;

import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Account extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private LinearLayout darkMode;
    private LinearLayout sendFeedback;
    private RelativeLayout version;
    private LinearLayout clearCache;
    private CircleImageView avatar;
    private Uri uri;
    private StorageReference storageReference;

    private TextView logout_button;
    private FirebaseAuth fbAuth;
    private SharedPreferences accountPref;
    private DatabaseReference dbRef;

    private TextView username;
    private TextView userEmail;


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
        if (resultCode == RESULT_OK) {
            uploadImageToFirebase(result.getData());
        }
    }

    private void uploadImageToFirebase(Uri ImageUri) {
        StorageReference fileRef = storageReference.child("profile_avatars").child(fbAuth.getUid() + ".jpg");

        fileRef.putFile(ImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                avatar.setImageURI(ImageUri);
                Toast.makeText(Account.this, "Фото профиля загружено успешно!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Account.this, "Что-то пошло не так.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        accountPref = getPreferences(MODE_PRIVATE);

        logout_button = findViewById(R.id.logout);

        fbAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        dbRef = FirebaseDatabase.getInstance().getReference("User_Info/" + fbAuth.getUid());

        username = findViewById(R.id.username);

        userEmail = findViewById(R.id.userEmail);

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FireBaseUser user = snapshot.getValue(FireBaseUser.class);
                System.out.println(user);
                username.setText(user.getUsername());
                userEmail.setText(user.getEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //Toast.makeText(Account.this, "Такого пользователя нет!", Toast.LENGTH_SHORT).show();
            }
        });


        StorageReference profileRef = storageReference.child("profile_avatars").child(fbAuth.getUid() + ".jpg");

        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(avatar);
            }
        });
        bottomNavigationView = findViewById(R.id.bottomMenu);
        darkMode = findViewById(R.id.darkMode);
        sendFeedback = findViewById(R.id.feedback);
        version = findViewById(R.id.version);
        clearCache = findViewById(R.id.clearCache);


        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fbAuth.signOut();

                SharedPreferences.Editor prefsEditor = accountPref.edit();
                prefsEditor.remove("fbAuth");
                prefsEditor.apply();

                startActivity(new Intent(Account.this, Home.class));
                finish();
            }
        });

        avatar = findViewById(R.id.userAvatar);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFileChooser();
            }
        });

        sendFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), FeedbackDialog.class));
            }
        });

        bottomNavigationView.setSelectedItemId(R.id.accountItem);
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
                        startActivity(new Intent(getApplicationContext(), Home.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.generalItem:
                        startActivity(new Intent(getApplicationContext(), General.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.accountItem:
                        return true;
                }
                return false;
            }
        });
    }
}