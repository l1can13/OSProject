package com.example.osproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;


public class Registration extends AppCompatActivity {

    private TextView SignIn;
    private Button register;
    private TextView email;
    private TextView username;
    private TextView password;
    private TextView phone;

    private DatabaseReference dbReference;


    @Expose(serialize = false)
    private FirebaseAuth fbAuth;

    private SharedPreferences sender;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        dbReference = FirebaseDatabase.getInstance().getReference();

        sender = getPreferences(MODE_PRIVATE);

        fbAuth = FirebaseAuth.getInstance();

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phoneNumber);

        register = findViewById(R.id.loginButton);

        SignIn = findViewById(R.id.alreadyHaveAccount);
        SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Registration.this,Login.class));
                finish();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            //Не работают Тосты
            public void onClick(View view) {
                if(username.getText().toString().isEmpty()){
                    Toast.makeText(Registration.this, "Введите имя пользователя!", Toast.LENGTH_SHORT).show();
                    return;
                }if(password.getText().toString().length() < 10){
                    Toast.makeText(Registration.this, "Введите корректный пароль!\nЕго длина должна быть более 10 символов.", Toast.LENGTH_SHORT).show();
                    password.setText("");
                    return;
                }
                if(email.getText().toString().isEmpty() || !isValidEmail(email.getText().toString())){
                    Toast.makeText(Registration.this,"Некорректный email!",Toast.LENGTH_SHORT);
                    email.setText("");
                    return;
                }if(phone.getText().toString().isEmpty() || !isValidPhone(phone.getText().toString())){
                    Toast.makeText(Registration.this, "Некорректный номер телефона!", Toast.LENGTH_SHORT).show();
                    phone.setText("");
                    return;
                }
                fbAuth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                .addOnCompleteListener(Registration.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(Registration.this, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            FireBaseUser user = new FireBaseUser(username.getText().toString(),email.getText().toString(),phone.getText().toString());
                            dbReference.child("User_Info").child(fbAuth.getUid()).setValue(user);

                            SharedPreferences.Editor prefsEditor = sender.edit();

                            String json = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(fbAuth);
                            prefsEditor.putString("fbAuth", json);
                            prefsEditor.apply();

                            startActivity(new Intent(Registration.this, Home.class));
                            finish();
                        }
                    }
                });


            }
        });
    }

    private final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static boolean isValidPhone(String phone){

        return (phone.matches("^((\\+7|7|8)+([0-9]){10})$") || //russian number
                phone.matches("^((\\+?380)([0-9]{9}))$") || //ukrainian number
                phone.matches("^(07[\\d]{8,12}|447[\\d]{7,11})$") || //UK number
                phone.matches("^(\\([0-9]{3}\\) |[0-9]{3}-)[0-9]{3}-[0-9]{4}$") //USA number
        );
    }

}
