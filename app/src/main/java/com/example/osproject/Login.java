package com.example.osproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

public class Login extends AppCompatActivity {
    private Button loginButton;
    @Expose(serialize = false)
    private FirebaseAuth fbAuthLogin;
    private SharedPreferences login_sender;
    private TextView email;
    private TextView password;
    private TextView forgotPassword;
    private TextView backOnRegistration;

    private DatabaseReference dbReference;

    private SignInButton googleSignInButton;
    private GoogleSignInClient googleSignInClient;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            Task<GoogleSignInAccount> googleSignInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                GoogleSignInAccount googleSignInAccount = googleSignInAccountTask.getResult(ApiException.class);
                fbAuthWithGoogle(googleSignInAccount);
            }catch (Exception e){

            }
        }
    }

    private void fbAuthWithGoogle(GoogleSignInAccount googleSignInAccount) {
        AuthCredential authCredential = GoogleAuthProvider.getCredential(googleSignInAccount.getIdToken(), null);
        fbAuthLogin.signInWithCredential(authCredential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        FirebaseUser FBUser = fbAuthLogin.getCurrentUser();
                        FireBaseUser user = new FireBaseUser(FBUser.getDisplayName(), FBUser.getEmail(), FBUser.getPhoneNumber());
                        dbReference.child("User_Info").child(fbAuthLogin.getUid()).setValue(user);


                        SharedPreferences.Editor prefsEditor = login_sender.edit();

                        String json = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(fbAuthLogin);
                        prefsEditor.putString("fbAuth", json);
                        prefsEditor.apply();

                        startActivity(new Intent(Login.this, Home.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Login.this, "Что-то пошло не так. Попробуйте снова.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = findViewById(R.id.loginButton);
        fbAuthLogin = FirebaseAuth.getInstance();
        password = findViewById(R.id.password);
        email = findViewById(R.id.email);
        forgotPassword = findViewById(R.id.forgotPassword);
        login_sender = getPreferences(MODE_PRIVATE);
        backOnRegistration = findViewById(R.id.backOnRegistration);

        dbReference = FirebaseDatabase.getInstance().getReference();

        googleSignInButton = findViewById(R.id.googleSignIn);
        googleSignInButton.setSize(SignInButton.SIZE_STANDARD);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(Login.this, gso);

        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = googleSignInClient.getSignInIntent();

                startActivityForResult(intent,1);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(email.getText().toString().isEmpty()){
                    Toast.makeText(getApplicationContext(), "Введите email!", Toast.LENGTH_SHORT).show();
                    email.setText("");
                    return;
                }
                if(password.getText().toString().length() < 10){
                    Toast.makeText(Login.this, "Введите корректный пароль!", Toast.LENGTH_SHORT).show();
                    password.setText("");
                    return;
                }
                fbAuthLogin.signInWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(!task.isSuccessful()){
                            password.setText("");
                            email.setText("");
                            Toast.makeText(Login.this, "Некорректные данные!\nПопробуйте еще!", Toast.LENGTH_SHORT).show();
                        }else{
                            SharedPreferences.Editor prefsEditor = login_sender.edit();

                            String json = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(fbAuthLogin);
                            prefsEditor.putString("fbAuth", json);
                            prefsEditor.apply();
                            startActivity(new Intent(Login.this, Home.class));
                            finish();
                        }
                    }
                });
            }
        });

        backOnRegistration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this,Registration.class));
                finish();
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this,ForgotPassword.class));
                finish();
            }
        });
    }

}
