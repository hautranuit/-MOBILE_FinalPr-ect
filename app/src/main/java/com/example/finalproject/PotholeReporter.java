package com.example.finalproject;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.finalproject.api.ApiClient;
import com.example.finalproject.api.Pothole;
import com.example.finalproject.api.restful_api;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineRequest;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.geojson.Point;
import com.mapbox.maps.MapView;
import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import com.mapbox.maps.plugin.annotation.AnnotationPluginImplKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotation;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PotholeReporter {
    private final Context context;
    private final MapView mapView;
    private PointAnnotationManager pointAnnotationManager;
    private Point point;  // Lưu trữ tọa độ của pothole
    private long lastCameraUpdateTime = 0;  // Để kiểm tra thời gian thay đổi camera

    private final DatabaseHelper dbHelper;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;

    public PotholeReporter(Context context, MapView mapView) {
        this.context = context;
        this.mapView = mapView;
        this.dbHelper = new DatabaseHelper(context);
    }

    public void reportPothole(String email, String size) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Yêu cầu quyền truy cập vị trí nếu chưa được cấp
            return;
        }

        LocationEngine locationEngine = LocationEngineProvider.getBestLocationEngine(context);
        LocationEngineCallback<LocationEngineResult> callback = new LocationEngineCallback<LocationEngineResult>() {
            @Override
            public void onSuccess(LocationEngineResult result) {
                Location location = result.getLastLocation();
                if (location != null) {
                    double longitude = location.getLongitude();
                    double latitude = location.getLatitude();

                    // Ghi log vị trí để kiểm tra
                    Log.d("PotholeReporter", "Location obtained: Latitude: " + latitude + ", Longitude: " + longitude);

                    // Thêm ổ gà với email và kích thước
                    insertPothole(longitude, latitude, email, size);

                    // Thêm marker lên bản đồ
                    addMarker(Point.fromLngLat(longitude, latitude), size);
                } else {
                    Log.e("PotholeReporter", "Location is null.");
                }
            }

            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(context, "Failed to get location: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        };

        // Gỡ bỏ cập nhật sau khi lấy vị trí
        locationEngine.getLastLocation(callback);
    }

    public void addMarker(Point point, String size) {
        mapView.getMapboxMap().getStyle(style -> {
            if (style != null) {
                AnnotationPlugin annotationPlugin = AnnotationPluginImplKt.getAnnotations(mapView);
                pointAnnotationManager = PointAnnotationManagerKt.createPointAnnotationManager(annotationPlugin, mapView);

                // Tải ảnh dựa trên kích thước
                Bitmap bitmap;
                switch (size.toLowerCase()) {
                    case "small":
                        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pothole_icon_small);
                        break;
                    case "medium":
                        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pothole_icon_medium);
                        break;
                    case "big":
                        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pothole_icon_large);
                        break;
                    default:
                        return;
                }

                // Giảm kích thước Bitmap
                Bitmap scaledBitmap = scaleBitmap(bitmap, 0.1f); // Giảm kích thước xuống 20% (thay đổi nếu cần)

                // Thêm PointAnnotationOptions
                PointAnnotationOptions options = new PointAnnotationOptions()
                        .withPoint(point)
                        .withIconImage(scaledBitmap)
                        .withIconSize(1.0f); // Kích thước Mapbox iconSize (dùng 1.0f vì bitmap đã được giảm)

                pointAnnotationManager.create(options);

            }
        });
    }


    // Phương thức để khởi tạo và lắng nghe sự thay đổi camera
    public void startListeningCameraChange() {
        mapView.getMapboxMap().addOnCameraChangeListener((cameraState) -> {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastCameraUpdateTime > 250) { // Nếu quá 250ms, coi như camera đã dừng lại
                if (cameraState != null) {
                    double zoom = mapView.getMapboxMap().getCameraState().getZoom();
                    updateIconSize(zoom);
                } else {
                    Toast.makeText(context, "Camera state is null", Toast.LENGTH_SHORT).show();
                }
            }
            lastCameraUpdateTime = currentTime; // Cập nhật thời gian
        });
    }


    private void insertPothole(double longitude, double latitude, String email, String size) {
        // Tạo một đối tượng Pothole với các tham số đã cung cấp
        Pothole pothole = new Pothole();
        pothole.setLongitude(longitude);
        pothole.setLatitude(latitude);
        pothole.setEmail(email);
        pothole.setSize(size);

        // Sử dụng java.util.Date để lấy thời gian hiện tại
        String currentTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault()).format(new Date());
        pothole.setTime(currentTime); // Gán thời gian định dạng ISO 8601

        // Lấy Retrofit instance và ApiService
        restful_api apiService = ApiClient.getRetrofitInstance().create(restful_api.class);

        // Gửi yêu cầu POST
        apiService.addPothole(pothole).enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Xử lý khi API trả về thành công
                    Toast.makeText(context, "Pothole added successfully!", Toast.LENGTH_SHORT).show();
                } else {
                    // Xử lý khi API trả về lỗi
                    Toast.makeText(context, "Failed to add pothole. Error: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                // Xử lý khi không thể kết nối với server
                Toast.makeText(context, "Failed to connect to server: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getAllPotholes(ApiCallback<List<Pothole>> callback) {
        // Tạo instance của API interface
        restful_api apiInterface = ApiClient.getRetrofitInstance().create(restful_api.class);

        // Gọi API để lấy danh sách potholes
        Call<List<Pothole>> call = apiInterface.getAllPotholes();
        call.enqueue(new Callback<List<Pothole>>() {
            @Override
            public void onResponse(Call<List<Pothole>> call, Response<List<Pothole>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Trả về danh sách potholes thông qua callback
                    callback.onSuccess(response.body());
                } else {
                    // Trả về lỗi nếu response không thành công
                    callback.onError(new Exception("Failed to fetch potholes. Response code: " + response.code()));
                }
            }

            @Override
            public void onFailure(Call<List<Pothole>> call, Throwable t) {
                // Trả về lỗi nếu gọi API thất bại
                callback.onError(new Exception("API call failed: " + t.getMessage(), t));
            }
        });
    }

    // Interface callback để xử lý kết quả bất đồng bộ
    public interface ApiCallback<T> {
        void onSuccess(T result);
        void onError(Exception e);
    }

    // Phương thức để thay đổi kích thước Bitmap sử dụng Matrix
    public static Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        return Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
    }

    // Phương thức để giảm kích thước ảnh Bitmap
    private Bitmap scaleBitmap(Bitmap bitmap, float scaleFactor) {
        int width = Math.round(bitmap.getWidth() * scaleFactor);
        int height = Math.round(bitmap.getHeight() * scaleFactor);
        return Bitmap.createScaledBitmap(bitmap, width, height, true);
    }

    private void updateIconSize(double zoom) {
        if (pointAnnotationManager != null) {
            // Tính toán kích thước biểu tượng mới dựa trên mức độ zoom
            double newIconSize = Math.pow(2, (zoom - 15) / 3); // Điều chỉnh hệ số (3 thay vì 2) để giảm kích thước tổng thể
            newIconSize = Math.max(0.05, Math.min(0.15, newIconSize)); // Giới hạn kích thước trong khoảng hợp lý

            // Cập nhật tất cả các biểu tượng trong PointAnnotationManager
            for (PointAnnotation annotation : pointAnnotationManager.getAnnotations()) {
                annotation.setIconSize(newIconSize); // Cập nhật kích thước
            }

            // Áp dụng thay đổi cho PointAnnotationManager
            pointAnnotationManager.update(pointAnnotationManager.getAnnotations());
        }
    }


}
