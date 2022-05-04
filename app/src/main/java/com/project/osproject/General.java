package com.project.osproject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import static androidx.core.app.NotificationCompat.PRIORITY_HIGH;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class General extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
    private NavigationView sideMenu;
    private DrawerLayout drawerLayout;
    private ImageButton menuButton;
    private ImageButton backButton;
    private View sideMenuHeader;

    private FirebaseAuth fbAuth;
    private DatabaseReference dbReference;


    private NotificationManager notificationManager;

    private TextView left_side_username;
    private TextView left_side_email;
    private CircleImageView left_side_avatar;

    private static final int NOTIFY_ID = 101;
    private static String CHANNEL_ID = "Test channel";

    private void setAvatar(StorageReference profileRef){
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("User_Avatar").child(fbAuth.getUid());
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Picasso.get().load(uri).into(left_side_avatar);
                        }
                    });
                }else{
                    GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(General.this);
                    if(googleSignInAccount == null) {
                        StorageReference Ref = FirebaseStorage.getInstance().getReference()
                                .child("profile_avatars").child("default.jpg");
                        Ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Picasso.get().load(uri).into(left_side_avatar);
                            }
                        });
                    }else
                        Picasso.get().load(googleSignInAccount.getPhotoUrl()).into(left_side_avatar);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general);

        bottomNavigationView = findViewById(R.id.bottomMenu);
        menuButton = findViewById(R.id.menu);
        drawerLayout = findViewById(R.id.drawerLayout);
        sideMenu = findViewById(R.id.navigationView);
        notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        bottomNavigationView.setSelectedItemId(R.id.generalItem);
        sideMenuHeader = sideMenu.getHeaderView(0);
        backButton = sideMenuHeader.findViewById(R.id.backButton);

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.close();
            }
        });

        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        left_side_avatar = sideMenuHeader.findViewById(R.id.userAvatar);
        left_side_email = sideMenuHeader.findViewById(R.id.userEmail);
        left_side_username = sideMenuHeader.findViewById(R.id.username);
        fbAuth = FirebaseAuth.getInstance();
        dbReference = FirebaseDatabase.getInstance().getReference();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.homeItem:
                        startActivity(new Intent(getApplicationContext(), Home.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.generalItem:
                        return true;
                    case R.id.accountItem:
                        startActivity(new Intent(getApplicationContext(), Account.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });

        dbReference.child("User_Info").child(fbAuth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                FireBaseUser user = snapshot.getValue(FireBaseUser.class);
                left_side_username.setText(user.getUsername());
                left_side_email.setText(user.getEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        setAvatar(FirebaseStorage.getInstance().getReference()
                .child("profile_avatars").child(fbAuth.getUid() + ".jpg"));

        sideMenu.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.savedUsers:
                        startActivity(new Intent(getApplicationContext(), SavedUsers.class));
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.notifications:
                        Dialog dialog;

                        final String[] items = {" Изменение общих файлов", " Обновления приложения", " Приглашение в команду"};
                        final ArrayList itemsSelected = new ArrayList();

                        AlertDialog.Builder builder = new AlertDialog.Builder(General.this);
                        builder.setTitle("Уведомления : ");
                        builder.setMultiChoiceItems(items, null,
                                new DialogInterface.OnMultiChoiceClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int selectedItemId, boolean isSelected) {
                                        if (isSelected) {

                                            itemsSelected.add(selectedItemId);
                                        } else if (itemsSelected.contains(selectedItemId)) {

                                            itemsSelected.remove(Integer.valueOf(selectedItemId));
                                        }
                                    }
                                })
                                .setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                })
                                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                    }
                                });

                        dialog = builder.create();
                        dialog.show();

                        Intent intent = new Intent(getApplicationContext(), General.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        NotificationCompat.Builder notificationBuilder =
                                new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                                        .setSmallIcon(R.drawable.ic_launcher_foreground)
                                        .setAutoCancel(false)
                                        .setWhen(System.currentTimeMillis())
                                        .setContentText("Test text")
                                        .setContentTitle("Test title")
                                        .setPriority(PRIORITY_HIGH);

                        createChannelIfNeeded(notificationManager);
                        notificationManager.notify(NOTIFY_ID, notificationBuilder.build());
                        return true;
                    case R.id.trash:
                        Toast.makeText(getApplicationContext(), "trash", Toast.LENGTH_SHORT).show();
                        return true;
                }
                return false;
            }

            public void createChannelIfNeeded(NotificationManager manager) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, CHANNEL_ID, NotificationManager.IMPORTANCE_DEFAULT);
                    manager.createNotificationChannel(notificationChannel);
                }
            }
        });
    }
}
