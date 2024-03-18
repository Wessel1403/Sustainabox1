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

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private int totalCredits;
    private int availableCredits;
    private DatabaseReference mDatabase;
    private String userId;
    private TextView containerCountText;
    private int numberContainers;
    private int avaiableContainers;
// ...


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final Button qrScannerButton = binding.openQrScannerButton;
        homeViewModel.getQRButtonText().observe(getViewLifecycleOwner(), qrScannerButton::setText);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference("Users");

        // This needs to be changed later to get this info from database, but I can't figure out how to do that.
        totalCredits = 5;
        numberContainers = 0;
        getUserCredits(mDatabase);
        getUserContainers(mDatabase);

        // Initialize containerCountText
        //containerCountText = root.findViewById(R.id.containerCountText);

        Button btnScan = root.findViewById(R.id.open_qr_scanner_button);
        btnScan.setOnClickListener(v -> {
            scanCode();
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void updateContainerCount() {
        TextView containerCountText = binding.containerCountText;
        containerCountText.setText("Boxes: " + numberContainers);
    }

    private void updateCreditDisplay() {
        TextView creditDisplay = binding.creditDisplayText;
        ProgressBar creditProgressBar = binding.creditProgressbar;

        creditDisplay.setText(availableCredits + " / " + totalCredits);

        int percentage = (int) (((float) availableCredits / (float) totalCredits) * 100);

        creditProgressBar.setProgress(percentage);
    }

    //Sets available credits to current credits of user
    void getUserCredits(DatabaseReference mDatabase) {
        mDatabase.child(userId).child("credits").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    String credits = String.valueOf(task.getResult().getValue());
                    availableCredits = Integer.parseInt(credits);
                    updateCreditDisplay();
                }
            }
        });
    }
    //Sets available containers to current containers of user
    void getUserContainers(DatabaseReference mDatabase) {
        mDatabase.child(userId).child("containerCount").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    Log.d("firebase", String.valueOf(task.getResult().getValue()));
                    String containers = String.valueOf(task.getResult().getValue());
                    avaiableContainers = Integer.parseInt(containers);
                    updateContainerCount();
                }
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
            int creditsToAdd = 1; // You may adjust this value based on your requirements
            updateUserCredits(creditsToAdd, containerId);
            updateUserContainers(creditsToAdd, containerId);
        }
    });
    private void updateUserContainers(int creditsToAdd, String containerId) {
        // Update the user's containers in the database
        mDatabase.child(userId).child("containerCount").setValue(avaiableContainers + creditsToAdd)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Credits updated successfully
                            avaiableContainers += creditsToAdd;

                            // Associate the container with the user
                            associateContainerWithUser(containerId);

                            // Update UI
                            updateContainerCount();
                            Toast.makeText(getContext(), "Credits updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            // Failed to update credits
                            Toast.makeText(getContext(), "Failed to update credits", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void updateUserCredits(int creditsToAdd, String containerId) {
        // Update the user's credits in the database
        mDatabase.child(userId).child("credits").setValue(availableCredits + creditsToAdd)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Credits updated successfully
                            availableCredits += creditsToAdd;

                            // Update UI
                            updateCreditDisplay();
                            Toast.makeText(getContext(), "Credits updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            // Failed to update credits
                            Toast.makeText(getContext(), "Failed to update credits", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void associateContainerWithUser(String containerId) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");
        DatabaseReference containersRef = FirebaseDatabase.getInstance().getReference("AssociatedContainers");

        // Associate the container with the user under a specific node
        DatabaseReference userContainersRef = usersRef.child(userId).child("associatedContainers");
        // Add the containerId under the associatedContainers node
        userContainersRef.child(containerId).setValue(true);

        // Add the user to the container's associated user list
        containersRef.child(containerId).setValue(userId);
    }
}
