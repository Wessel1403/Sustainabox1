package com.example.sustainabox;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sustainabox.MainActivity;
import com.example.sustainabox.R;
import com.example.sustainabox.SplashActivity;
import com.example.sustainabox.ui.register.RegisterFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class changePassword extends AppCompatActivity {

    private EditText editTextPassword;
    private Button buttonChangePassword;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changepassword);
        editTextPassword = findViewById(R.id.emailInput);
        buttonChangePassword = findViewById(R.id.loginButton);
        mAuth = FirebaseAuth.getInstance();

        buttonChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = mAuth.getCurrentUser();
                String newPassword = editTextPassword.getText().toString();

                user.updatePassword(newPassword)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("update password", "User password updated.");
                                    Toast.makeText(changePassword.this, "Password changed", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            }
                        });
            }
        });

    }
}

