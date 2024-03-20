package com.example.sustainabox;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.sustainabox.databinding.ActivityMainBinding;
import com.example.sustainabox.databinding.FragmentProfileBinding;
import com.example.sustainabox.ui.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ShopOwnerProfileActivity extends AppCompatActivity {

    private FragmentProfileBinding binding;
    Button buttonChangeEmail, buttonChangePassword, buttonLogOut;
    TextView name, email;
    DatabaseReference mDatabase;
    String firstName, lastName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);

        mDatabase = FirebaseDatabase.getInstance().getReference("Users");

        name = findViewById(R.id.textViewName);
        email = findViewById(R.id.textViewEmail);
        buttonChangeEmail = findViewById(R.id.buttonChangeEmail);
        buttonChangePassword = findViewById(R.id.buttonChangePassword);
        buttonLogOut = findViewById(R.id.buttonLogout);

        updateUserFirstName(mDatabase, name);
        updateEmail(mDatabase, email);


        buttonLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ShopOwnerProfileActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        buttonChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShopOwnerProfileActivity.this, changeEmail.class);
                startActivity(intent);
            }
        });

        buttonChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShopOwnerProfileActivity.this, changePassword.class);
                startActivity(intent);
            }
        });
    }

    void updateEmail(DatabaseReference mDatabase, TextView email) {
        mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("email").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    email.setText(String.valueOf(task.getResult().getValue()));
                }
            }
        });
    }

    void updateUserFirstName(DatabaseReference mDatabase, TextView name) {
        mDatabase.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("firstName").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    name.setText(String.valueOf(task.getResult().getValue()));
                }
            }
        });
    }
}