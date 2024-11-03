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
import android.widget.ImageButton;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;


public class Map_1 extends AppCompatActivity implements OnMapReadyCallback {

    private LatLng searchLatLng;
    private GoogleMap gMap;
    private SearchView mapSearchView;
    private LatLng manualLocation = new LatLng(10.8699971, 106.8030189); // Vị trí cố định
    private MarkerOptions currentMarkerOptions; // Để lưu marker vị trí hiện tại
    private MarkerOptions searchMarkerOptions;  // Để lưu marker vị trí tìm kiếm
    private void navigateToLocation(LatLng destination) {
        // Tọa độ cố định của điểm xuất phát
        LatLng fixedStartLocation = new LatLng(10.8699971, 106.8030189);

        // Tạo URI để mở Google Maps với điểm bắt đầu cố định và điểm đến là `destination`
        String uri = "https://www.google.com/maps/dir/?api=1" +
                "&origin=" + fixedStartLocation.latitude + "," + fixedStartLocation.longitude +
                "&destination=" + destination.latitude + "," + destination.longitude +
                "&travelmode=driving";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");

        // Kiểm tra xem có ứng dụng Google Maps trên thiết bị hay không
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            // Xử lý nếu không có Google Maps
            Toast.makeText(this, "Google Maps không được cài đặt", Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map1);

        // Initialize SearchView
        mapSearchView = findViewById(R.id.mapSearch);

        // Initialize the current location button
        ImageButton currentLocationButton = findViewById(R.id.currentLocationButton);

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

        // Set up current location button listener
        currentLocationButton.setOnClickListener(v -> moveToCurrentLocation());

        ImageButton navigateButton = findViewById(R.id.navigateButton);
        navigateButton.setOnClickListener(v -> {
            if (searchLatLng != null) {
                navigateToLocation(searchLatLng); // Điều hướng tới vị trí tìm kiếm
            } else {
                Toast.makeText(this, "Vui lòng chọn địa điểm trước khi điều hướng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;
        updateManualLocation();
    }

    private void updateManualLocation() {
        currentMarkerOptions = new MarkerOptions().position(manualLocation).title("Vị trí hiện tại");
        gMap.addMarker(currentMarkerOptions);
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(manualLocation, 15));
    }

    private void moveToCurrentLocation() {
        if (gMap != null) {
            // Chuyển camera đến vị trí hiện tại mà không xóa marker
            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(manualLocation, 15));

            // Đảm bảo rằng marker vị trí hiện tại vẫn hiển thị
            if (currentMarkerOptions != null) {
                gMap.addMarker(currentMarkerOptions);
            }
        }
    }

    private void searchLocation(String location) {
        if (location != null && !location.isEmpty()) {
            Geocoder geocoder = new Geocoder(this);
            try {
                List<Address> addressList = geocoder.getFromLocationName(location, 1);
                if (addressList != null && !addressList.isEmpty()) {
                    Address address = addressList.get(0);
                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());

                    // Cập nhật giá trị cho searchLatLng
                    searchLatLng = latLng;

                    // Xóa marker tìm kiếm trước đó (nếu có)
                    if (searchMarkerOptions != null) {
                        gMap.clear(); // Xóa toàn bộ marker, sau đó thêm lại các marker
                        gMap.addMarker(currentMarkerOptions); // Thêm lại marker của vị trí hiện tại
                    }

                    // Tạo và thêm marker cho vị trí tìm kiếm
                    searchMarkerOptions = new MarkerOptions().position(latLng).title(location);
                    gMap.addMarker(searchMarkerOptions);
                    gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                } else {
                    // Trường hợp không tìm thấy địa điểm
                    Toast.makeText(this, "Không tìm thấy địa điểm", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
