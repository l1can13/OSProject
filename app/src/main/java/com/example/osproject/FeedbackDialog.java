package com.example.osproject;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class FeedbackDialog extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_dialog);

        EditText email = findViewById(R.id.email);
        EditText describe = findViewById(R.id.describe);
        Button ok = findViewById(R.id.okButton);
        Button cancel = findViewById(R.id.cancelButton);
        ImageButton backButton = findViewById(R.id.backButton);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!email.getText().toString().isEmpty() && isValidEmail(email.getText().toString())) {
                    Thread thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                MailSender.sendEmail(email.getText().toString(), describe.getText().toString());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    thread.start();
                    Toast.makeText(getApplicationContext(), "Сообщение отправлено, спасибо за отзыв!", Toast.LENGTH_SHORT).show();
                    thread.interrupt();
                    finish();
                }
                else{
                    Toast.makeText(FeedbackDialog.this, "Неправильный адресс почты!", Toast.LENGTH_SHORT).show();
                    email.setText("");
                }

            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

}
