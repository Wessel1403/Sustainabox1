package com.example.sustainabox.ui.home;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sustainabox.CaptureAct;
import com.example.sustainabox.MainActivity;
import com.example.sustainabox.User;
import com.example.sustainabox.databinding.FragmentHomeBinding;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private int totalCredits;
    private int availableCredits;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final Button qrScannerButton = binding.openQrScannerButton;
        homeViewModel.getQRButtonText().observe(getViewLifecycleOwner(), qrScannerButton::setText);

        // This needs to be changed later to get this info from database, but I can't figure out how to do that.
        totalCredits = 5;
        availableCredits = 3;

        updateCreditDisplay();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void updateCreditDisplay() {
        TextView creditDisplay = binding.creditDisplayText;
        ProgressBar creditProgressBar = binding.creditProgressbar;

        creditDisplay.setText(availableCredits + " / " + totalCredits);

        int percentage = (int) (((float) availableCredits / (float) totalCredits) * 100);

        creditProgressBar.setProgress(percentage);
    }
}