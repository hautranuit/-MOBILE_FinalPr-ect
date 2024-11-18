package com.example.finalproject;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.Style;

public class MapTiler extends AppCompatActivity {

    private MapView mapView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Debug log để kiểm tra
        Log.d("MapTitler", "onCreate() chạy thành công");
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_map_tiler);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        String mapTilerKey = getMapTilerKey();
        validateKey(mapTilerKey);
        String styleUrl = "https://api.maptiler.com/maps/streets/style.json?key=" + mapTilerKey;

        // Initialize Mapbox context
        Mapbox.getInstance(this, null);

        // Set the map view layout
        setContentView(R.layout.activity_main);

        // Initialize the MapView
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        // Load the map asynchronously
        mapView.getMapAsync(map -> {
            // Set the style
            map.setStyle(new Style.Builder().fromUri(styleUrl), style -> {
                map.getUiSettings().setAttributionMargins(15, 0, 0, 15);

                // Set the camera position
                map.setCameraPosition(new CameraPosition.Builder()
                        .target(new LatLng(47.127757, 8.579139))
                        .zoom(10.0)
                        .build());
            });
        });

    }

    private String getMapTilerKey() {
        try {
            ApplicationInfo appInfo = getPackageManager().getApplicationInfo(
                    getPackageName(),
                    PackageManager.GET_META_DATA
            );
            return appInfo.metaData.getString("com.maptiler.simplemap.mapTilerKey");
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Failed to read MapTiler key from AndroidManifest.xml", e);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mapView != null) {
            mapView.onStart();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mapView != null) {
            mapView.onStop();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mapView != null) {
            mapView.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (mapView != null) {
            mapView.onLowMemory();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mapView != null) {
            mapView.onDestroy();
        }
    }

    private void validateKey(String mapTilerKey) {
        if (mapTilerKey == null) {
            throw new IllegalArgumentException("Failed to read MapTiler key from AndroidManifest.xml");
        }
        if ("placeholder".equalsIgnoreCase(mapTilerKey)) {
            throw new IllegalArgumentException("Please enter a valid MapTiler key in your AndroidManifest.xml or build.gradle");
        }
    }

}