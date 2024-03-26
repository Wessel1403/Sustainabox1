package com.example.sustainabox;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.sustainabox.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class MainActivityShopOwner extends AppCompatActivity {

    private DatabaseReference mDatabase =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://sustainabox-a4b7e-default-rtdb.europe-west1.firebasedatabase.app/");
    private Button buttonScan, buttonProfile;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_homeshopowner);

        buttonScan = findViewById(R.id.open_qr_scanner);
        buttonProfile = findViewById(R.id.profileButton);

        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanCode();
            }
        });

        buttonProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivityShopOwner.this, ShopOwnerProfileActivity.class);
                startActivity(intent);
            }
        });
    }

    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Press the volume up button for flash");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);

        qrLauncher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> qrLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            String containerId = result.getContents(); // Assuming the container ID is in the QR code content

            getUserFromContainer(containerId);
        }
    });

    private void getUserFromContainer(String containerID) {
        Task<DataSnapshot> dataSnapshotTask = mDatabase.child("AssociatedContainers").child(containerID).get();

        dataSnapshotTask.addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                setUserID(task.getResult().getValue(String.class));

                if (userID != null) {
                    unlinkContainer(containerID);
                    updateUserCredits(userID, 1);
                } else {
                    Log.d("ContainerScan", "Container: " + containerID + " is not linked.");
                }
            }
        });
    }

    private void updateUserCredits(String userID, int creditsToAdd) {
        Task<DataSnapshot> dataSnapshotTask = mDatabase.child("Users").child(userID).child("credits").get();

        dataSnapshotTask.addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                int currentCredits = task.getResult().getValue(Integer.class);

                // Update the user's credits in the database
                mDatabase.child("Users").child(userID).child("credits").setValue(currentCredits + creditsToAdd);
            }
        });
    }

    private void unlinkContainer(String containerID) {
        mDatabase.child("Users").child(userID).child("associatedContainers").child(containerID).removeValue();
        mDatabase.child("AssociatedContainers").child(containerID).removeValue();
    }

    private void setUserID(String userID) { this.userID = userID; }
}