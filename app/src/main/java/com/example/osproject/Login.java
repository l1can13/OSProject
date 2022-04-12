package com.example.osproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
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
    private SignInButton googleSignInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginButton = findViewById(R.id.loginButton);
        fbAuthLogin = FirebaseAuth.getInstance();
        password = findViewById(R.id.password);
        email = findViewById(R.id.username);
        forgotPassword = findViewById(R.id.forgotPassword);
        login_sender = getPreferences(MODE_PRIVATE);
        googleSignInButton = findViewById(R.id.googleSignIn);

        googleSignInButton.setSize(SignInButton.SIZE_STANDARD);

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

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this,ForgotPassword.class));
                finish();
            }
        });
    }

}
