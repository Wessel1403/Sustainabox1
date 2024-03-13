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
import androidx.annotation.Nullable;
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
    private int totalCredits = 0;
    private int totalContainers = 0;
    private int availableCredits = 0;
    private DatabaseReference mDatabase;
    private String userId;
    private Button btnScan;
    private ValueEventListener dataListener;
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
        mDatabase = FirebaseDatabase.getInstance().getReferenceFromUrl("https://sustainabox-a4b7e-default-rtdb.europe-west1.firebasedatabase.app/");

        setDataListener();

        Button btnScan = root.findViewById(R.id.open_qr_scanner_button);
        btnScan.setOnClickListener(v -> {
            scanCode();
        });

        return root;
    }

    ActivityResultLauncher<ScanOptions> qrLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            String containerId = result.getContents(); // Assuming the container ID is in the QR code content
            int creditsToAdd = 1; // You may adjust this value based on your requirements
            updateUserCredits(creditsToAdd, containerId);

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Result");
            builder.setMessage(result.getContents());
            builder.setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.dismiss()).show();
        }
    });

    @Override
    public void onDestroyView() {
        mDatabase.removeEventListener(dataListener);
        super.onDestroyView();
        binding = null;
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

                            // Associate the container with the user
                            associateContainerWithUser(containerId);

                            // Update UI
                            updateCreditDisplay();
                            updateContainerCount(); // Add this line to update the container count display
                            Toast.makeText(getContext(), "Credits updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            // Failed to update credits
                            Toast.makeText(getContext(), "Failed to update credits", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void updateContainerCount() {
        totalContainers++; // Assuming each scan adds one container
        TextView containerCountDisplay = binding.containerCountText;
        containerCountDisplay.setText("Boxes: " + totalContainers);

        // Fetch and update the number of containers from the database
        getUserContainersCount(mDatabase);
    }

    // Fetch and update the number of containers from the database
    void getUserContainersCount(DatabaseReference mDatabase) {
        mDatabase.child(userId).child("associatedContainers").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
                    // Count the number of containers in the associatedContainers node
                    int containersCount = 0;
                    for (DataSnapshot containerSnapshot : task.getResult().getChildren()) {
                        containersCount++;
                    }

                    // Update the UI with the number of containers
                    updateContainersCountUI(containersCount);
                }
            }
        });
    }
    // Update the UI with the number of containers
    private void updateContainersCountUI(int containersCount) {
        TextView containersCountDisplay = binding.containerCountText; // Assuming this is where you display the count
        containersCountDisplay.setText("Boxes: " + containersCount);
    }
    private void associateContainerWithUser(String containerId) {
        // Assuming you have a "Users" node in your database
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("Users");

        // Associate the container with the user under a specific node
        DatabaseReference userContainersRef = usersRef.child(userId).child("associatedContainers");

        // Add the containerId under the associatedContainers node
        userContainersRef.child(containerId).setValue(true);

        // Update the containerCount in the database
        usersRef.child(userId).child("containerCount").setValue(totalContainers + 1)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Container count updated successfully
                            totalContainers++;

                            // Update UI
                            updateContainerCount();
                        } else {
                            // Failed to update container count
                            Toast.makeText(getContext(), "Failed to update container count", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void updateCreditDisplay() {
        TextView creditDisplay = binding.creditDisplayText;
        ProgressBar creditProgressBar = binding.creditProgressbar;

        creditDisplay.setText(availableCredits + " / " + totalCredits);

        int percentage = (int) (((float) availableCredits / (float) totalCredits) * 100);

        creditProgressBar.setProgress(percentage);
    }

    private void setDataListener() {
        dataListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Done in a very scuffed manner, it just wouldn't work otherwise for some reason.
                availableCredits = Integer.parseInt(String.valueOf(dataSnapshot.child("Users").child(userId).child("credits").getValue()));
                totalContainers = Integer.parseInt(String.valueOf(dataSnapshot.child("Users").child(userId).child("containerCount").getValue()));
                totalCredits = availableCredits + totalContainers;

                // Update the UI
                updateCreditDisplay();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Thread failed, log a message
                Log.w("Credits update", "something went wrong here", databaseError.toException());
            }
        };
        mDatabase.addValueEventListener(dataListener);
    }

    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Press the volume up button for flash");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);

        qrLauncher.launch(options);
    }
}
