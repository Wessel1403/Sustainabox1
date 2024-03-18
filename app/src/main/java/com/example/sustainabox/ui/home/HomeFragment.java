package com.example.sustainabox.ui.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sustainabox.CaptureAct;
import com.example.sustainabox.MainActivity;
import com.example.sustainabox.User;
import com.example.sustainabox.databinding.FragmentHomeBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.example.sustainabox.R;
import com.example.sustainabox.User;
import com.example.sustainabox.ui.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private DatabaseReference mDatabase =  FirebaseDatabase.getInstance().getReferenceFromUrl("https://sustainabox-a4b7e-default-rtdb.europe-west1.firebasedatabase.app/");
    private ValueEventListener dataListener;

    // UI
    private TextView containerCountText;

    // Variables
    private String userId;
    private int totalCredits = 0;
    private int availableCredits = 0;
    private int ownedContainerCount = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Get userID.
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Initialize UI
        containerCountText = root.findViewById(R.id.containerCountText);

        final Button qrScannerButton = binding.openQrScannerButton;
        homeViewModel.getQRButtonText().observe(getViewLifecycleOwner(), qrScannerButton::setText);

        Button btnScan = root.findViewById(R.id.open_qr_scanner_button);
        btnScan.setOnClickListener(v -> {
            scanCode();
        });

        // Setup data listener
        setDataListener();

        return root;
    }

    @Override
    public void onDestroyView() {
        mDatabase.removeEventListener(dataListener);
        super.onDestroyView();
        binding = null;
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

            updateUserCredits(-1);
            associateContainerWithUser(containerId);
        }
    });

    private void updateContainerCountDisplay() {
        TextView containerCountText = binding.containerCountText;
        containerCountText.setText("Boxes: " + ownedContainerCount);
    }

    private void updateCreditDisplay() {
        TextView creditDisplay = binding.creditDisplayText;
        ProgressBar creditProgressBar = binding.creditProgressbar;

        creditDisplay.setText(availableCredits + " / " + totalCredits);

        int percentage = (int) (((float) availableCredits / (float) totalCredits) * 100);

        creditProgressBar.setProgress(percentage);
    }

    /**
     * Sets a listener for the data in the database.
     * Updates availableCredits, totalCredits and owned containers whenever there is a change in the database.
     */
    private void setDataListener() {
        dataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataSnapshot ds = dataSnapshot.child("Users").child(userId);

                // Update variables
                availableCredits = ds.child("credits").getValue(Integer.class);
                ownedContainerCount = (int) ds.child("associatedContainers").getChildrenCount();
                totalCredits = availableCredits + ownedContainerCount;

                // Update UI
                updateContainerCountDisplay();
                updateCreditDisplay();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("Data update error", "loadPost:onCancelled", databaseError.toException());
            }
        };
        mDatabase.addValueEventListener(dataListener);
    }

    private void updateUserCredits(int creditsToAdd) {
        // Update the user's credits in the database
        mDatabase.child("Users").child(userId).child("credits").setValue(availableCredits + creditsToAdd)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getContext(), "Credits updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            // Failed to update credits
                            Toast.makeText(getContext(), "Failed to update credits", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void associateContainerWithUser(String containerId) {
        DatabaseReference containersRef = mDatabase.child("AssociatedContainers");
        DatabaseReference userContainersRef = mDatabase.child("Users").child(userId).child("associatedContainers");

        // Add the containerId under the associatedContainers node
        userContainersRef.child(containerId).setValue(true);

        // Add the user to the container's associated user list
        containersRef.child(containerId).setValue(userId);
    }
}
