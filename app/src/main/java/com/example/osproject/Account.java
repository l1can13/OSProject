package com.example.osproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;

import de.hdodenhof.circleimageview.CircleImageView;

public class Account extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private LinearLayout darkMode;
    private LinearLayout sendFeedback;
    private RelativeLayout version;
    private LinearLayout clearCache;
    private CircleImageView avatar;
    private Uri uri;

    private TextView logout_button;
    private FirebaseAuth fbAuth;
    private SharedPreferences accountPref;

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
            uri = result.getData();
            avatar.setImageURI(uri);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        accountPref = getPreferences(MODE_PRIVATE);

        logout_button = findViewById(R.id.logout);

        fbAuth = FirebaseAuth.getInstance();

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