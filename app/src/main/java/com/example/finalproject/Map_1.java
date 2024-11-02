package com.example.finalproject;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.WindowInsets;
import android.view.WindowInsetsController;
import android.widget.SearchView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentContainerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class Map_1 extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap gMap;
    private SearchView mapSearchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map1);

        // Initialize SearchView
        mapSearchView = findViewById(R.id.mapSearch);

        // Set up edge-to-edge UI adjustments
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBarsInsets.left, systemBarsInsets.top, systemBarsInsets.right, systemBarsInsets.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        // Initialize map fragment
        FragmentContainerView mapFragmentContainer = findViewById(R.id.id_map);
        if (mapFragmentContainer != null) {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.id_map);
            if (mapFragment != null) {
                mapFragment.getMapAsync(this);
            }
        }

        // Set up SearchView listener
        mapSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchLocation(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;
        updateManualLocation();
    }

    private void updateManualLocation() {
        LatLng manualLocation = new LatLng(10.8699971, 106.8030189);
        gMap.addMarker(new MarkerOptions().position(manualLocation).title("Vị trí hiện tại"));
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(manualLocation, 15));
    }

    private void searchLocation(String location) {
        if (location != null && !location.isEmpty()) {
            Geocoder geocoder = new Geocoder(this);
            try {
                List<Address> addressList = geocoder.getFromLocationName(location, 1);
                if (addressList != null && !addressList.isEmpty()) {
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                    // Add a marker and move the camera to the searched location
                    gMap.clear();  // Clear previous markers
                    gMap.addMarker(new MarkerOptions().position(latLng).title(location));
                    gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
