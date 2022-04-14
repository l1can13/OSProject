package com.example.osproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;


public class Registration extends AppCompatActivity {

    private TextView SignIn;
    private Button register;
    private TextView email;
    private TextView username;
    private TextView password;
    private TextView phone;

    private StorageReference storageReference;

    private SignInButton googleSignInButton;
    private GoogleSignInClient googleSignInClient;

    @Expose(serialize = false)
    private FirebaseAuth fbAuth;

    private DatabaseReference dbReference;

    private SharedPreferences sender;

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
        fbAuth.signInWithCredential(authCredential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        FirebaseUser FBUser = fbAuth.getCurrentUser();
                        FireBaseUser user = new FireBaseUser(FBUser.getDisplayName(), FBUser.getEmail(), FBUser.getPhoneNumber());
                        dbReference.child("User_Info").child(fbAuth.getUid()).setValue(user);


                        SharedPreferences.Editor prefsEditor = sender.edit();

                        String json = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create().toJson(fbAuth);
                        prefsEditor.putString("fbAuth", json);
                        prefsEditor.apply();

                        startActivity(new Intent(Registration.this, Home.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Registration.this, "Что-то пошло не так. Попробуйте снова.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        dbReference = FirebaseDatabase.getInstance().getReference();

        sender = getPreferences(MODE_PRIVATE);

        fbAuth = FirebaseAuth.getInstance();

        storageReference = FirebaseStorage.getInstance().getReference();

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phoneNumber);
        register = findViewById(R.id.loginButton);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(Registration.this, gso);

        googleSignInButton = findViewById(R.id.googleSignIn);
        googleSignInButton.setSize(SignInButton.SIZE_STANDARD);

        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = googleSignInClient.getSignInIntent();

                startActivityForResult(intent,1);
            }
        });

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
                    Toast.makeText(Registration.this,"Некорректный email!",Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(Registration.this, "Регистрация не завершилась!" + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        } else {

                            //шлём подвтерждение по почте
                            fbAuth.getCurrentUser().sendEmailVerification()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(Registration.this, "Пиьсмо с подтверждением отправлено на почту " + email.getText().toString(), Toast.LENGTH_SHORT).show();
                                }
                            });

                            FireBaseUser user = new FireBaseUser(username.getText().toString(),email.getText().toString(),phone.getText().toString());
                            dbReference.child("User_Info").child(fbAuth.getUid()).setValue(user);

                            StorageReference fileRef = storageReference.child("profile_avatars").child("default.jpg");
                            fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    StorageReference profileRef = storageReference.child("profile_avatars").child(fbAuth.getUid() + ".jpg");
                                    profileRef.putFile(uri);
                                }
                            });
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
