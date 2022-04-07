package com.example.osproject;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class ForgotPassword extends AppCompatActivity {

    private ImageButton backButton;
    private EditText emailEnter;
    private Button ok, cancel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        backButton = findViewById(R.id.backButton);
        emailEnter = findViewById(R.id.email);
        ok = findViewById(R.id.okButton);
        cancel = findViewById(R.id.cancelButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Что-то полезное
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

}
