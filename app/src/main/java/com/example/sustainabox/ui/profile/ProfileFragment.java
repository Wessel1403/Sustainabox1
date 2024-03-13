package com.example.sustainabox.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sustainabox.R;
import com.example.sustainabox.changeEmail;
import com.example.sustainabox.changePassword;
import com.example.sustainabox.databinding.FragmentHomeBinding;
import com.example.sustainabox.databinding.FragmentProfileBinding;
import com.example.sustainabox.ui.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    Button buttonChangeEmail, buttonChangePassword, buttonLogOut;
    TextView name, email;
    DatabaseReference mDatabase;
    String firstName, lastName;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");

        name = binding.textViewName;
        email = binding.textViewEmail;
        buttonChangeEmail = binding.buttonChangeEmail;
        buttonChangePassword = binding.buttonChangePassword;
        buttonLogOut = binding.buttonLogout;

        updateUserFirstName(mDatabase, name);
        updateEmail(mDatabase, email);

        // Get the name from your data source (e.g., SharedPreferences, database, etc.)

        buttonLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        buttonChangeEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), changeEmail.class);
                startActivity(intent);
            }
        });

        buttonChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), changePassword.class);
                startActivity(intent);
            }
        });

        return root;
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}