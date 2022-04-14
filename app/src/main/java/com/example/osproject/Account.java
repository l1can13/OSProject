package com.example.osproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;

import android.view.View;

import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.StandardOpenOption;

import de.hdodenhof.circleimageview.CircleImageView;

public class Account extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private LinearLayout darkMode;
    private LinearLayout sendFeedback;
    private RelativeLayout version;
    private LinearLayout clearCache;
    private CircleImageView avatar;
    private GoogleSignInClient googleSignInClient;
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

    private void setAvatar(StorageReference profileRef){

        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("User_Avatar").child(fbAuth.getUid());
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(Account.this);
                            if(googleSignInAccount != null)
                                Picasso.get().load(googleSignInAccount.getPhotoUrl()).into(avatar);
                            else
                                Picasso.get().load(uri).into(avatar);
                        }
                    });
                }else{
                    StorageReference Ref = FirebaseStorage.getInstance().getReference()
                            .child("profile_avatars").child("default.jpg");
                    Ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).into(avatar);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void uploadImageToFirebase(Uri ImageUri) {
        StorageReference fileRef = storageReference.child("profile_avatars").child(fbAuth.getUid() + ".jpg");
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("User_Avatar");
        dbRef.child(fbAuth.getUid()).setValue(fbAuth.getUid()+".jpg");

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

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(Account.this, gso);
        fbAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        dbRef = FirebaseDatabase.getInstance().getReference("User_Info/" + fbAuth.getUid());

        username = findViewById(R.id.username);

        userEmail = findViewById(R.id.userEmail);

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FireBaseUser user = snapshot.getValue(FireBaseUser.class);
                username.setText(user.getUsername());
                userEmail.setText(user.getEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        avatar = findViewById(R.id.userAvatar);

        StorageReference profileRef = storageReference.child("profile_avatars").child(fbAuth.getUid() + ".jpg");

        setAvatar(profileRef);

        bottomNavigationView = findViewById(R.id.bottomMenu);
        darkMode = findViewById(R.id.darkMode);
        sendFeedback = findViewById(R.id.feedback);
        version = findViewById(R.id.version);
        clearCache = findViewById(R.id.clearCache);

        logout_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fbAuth.signOut();
                googleSignInClient.signOut();

                SharedPreferences.Editor prefsEditor = accountPref.edit();
                prefsEditor.remove("fbAuth");
                prefsEditor.apply();

                startActivity(new Intent(Account.this, Home.class));
                finish();
            }
        });


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