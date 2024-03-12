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
    private Button btnScan;
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

        totalCredits = 5;
        getUserCredits(mDatabase);

        Button btnScan = root.findViewById(R.id.open_qr_scanner_button);
        btnScan.setOnClickListener(v -> {
            scanCode();
        });

        return root;
    }
    ActivityResultLauncher<ScanOptions> qrLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            int creditsToAdd = Integer.parseInt(result.getContents());
            updateUserCredits(creditsToAdd);

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Result");
            builder.setMessage(result.getContents());
            builder.setPositiveButton("OK", (dialogInterface, i) -> dialogInterface.dismiss()).show();
        }
    });

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void updateUserCredits(int creditsToAdd) {
        // Update the user's credits in the database
        mDatabase.child(userId).child("credits").setValue(availableCredits + creditsToAdd)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Credits updated successfully
                            availableCredits += creditsToAdd;
                            updateCreditDisplay();
                            Toast.makeText(getContext(), "Credits updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            // Failed to update credits
                            Toast.makeText(getContext(), "Failed to update credits", Toast.LENGTH_SHORT).show();
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
    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Press the volume up button for flash");
        options.setBeepEnabled(true);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        qrLauncher.launch(options);
    }
}
