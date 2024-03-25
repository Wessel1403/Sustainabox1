package com.example.sustainabox.ui.map;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.sustainabox.R;
import com.example.sustainabox.databinding.FragmentMapBinding;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends Fragment implements OnMapReadyCallback {
    private FragmentMapBinding binding;
    private GoogleMap mMap;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        MapViewModel mapViewModel =
                new ViewModelProvider(this).get(MapViewModel.class);

        binding = FragmentMapBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.google_map);

        mapFragment.getMapAsync(this);

        return root;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng eindhoven = new LatLng(51.4381, 5.4752);

        LatLng store1 = new LatLng(51.4420, 5.4700);
        LatLng store2 = new LatLng(51.4500, 5.4900);
        LatLng store3 = new LatLng(51.4300, 5.4732); //good
        LatLng store4 = new LatLng(51.4398, 5.4800);
        LatLng store5 = new LatLng(51.4350, 5.4775);

        mMap.addMarker(new MarkerOptions().position(store1).title("Jumbo, open: 08:00-20:00"));
        mMap.addMarker(new MarkerOptions().position(store2).title("TU/e, open: 07:00 - 23:00"));
        mMap.addMarker(new MarkerOptions().position(store3).title("Park, open: 06:00-00:00"));
        mMap.addMarker(new MarkerOptions().position(store4).title("Centre, open: 06:00-23:00"));
        mMap.addMarker(new MarkerOptions().position(store5).title("Museum, open: 14:00-17:00"));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(eindhoven));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}