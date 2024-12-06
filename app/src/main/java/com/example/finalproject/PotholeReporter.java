package com.example.finalproject;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.finalproject.api.ApiClient;
import com.example.finalproject.api.Pothole;
import com.example.finalproject.api.restful_api;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.geojson.Point;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import com.mapbox.maps.plugin.annotation.AnnotationPluginImplKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;
import com.mapbox.maps.MapboxMap;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
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

    public PotholeReporter(Context context, MapView mapView) {
        this.context = context;
        this.mapView = mapView;
        this.dbHelper = new DatabaseHelper(context);
    }

    public void reportPothole(String email, String size) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            throw new SecurityException("Location permission not granted.");
        }

        LocationEngine locationEngine = LocationEngineProvider.getBestLocationEngine(context);
        locationEngine.getLastLocation(new LocationEngineCallback<LocationEngineResult>() {
            @Override
            public void onSuccess(LocationEngineResult result) {
                Location location = result.getLastLocation();
                if (location != null) {
                    double longitude = location.getLongitude();
                    double latitude = location.getLatitude();

                    // Insert the pothole with email and size
                    insertPothole(longitude, latitude, email, size);

                    // Add marker on the map
                    addMarker(Point.fromLngLat(longitude, latitude));
                }
            }

            @Override
            public void onFailure(@NonNull Exception exception) {
                Toast.makeText(context, "Failed to get location: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void addMarker(Point point) {
        mapView.getMapboxMap().getStyle(style -> {
            if (style != null) {
                // Lấy AnnotationPlugin từ MapView
                AnnotationPlugin annotationPlugin = AnnotationPluginImplKt.getAnnotations(mapView);

                // Tạo PointAnnotationManager từ AnnotationPlugin
                pointAnnotationManager = PointAnnotationManagerKt.createPointAnnotationManager(annotationPlugin, mapView);

                // Tạo biểu tượng từ tài nguyên drawable
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pothole_icon);

                // Thay đổi kích thước của ảnh (giảm kích thước ảnh ban đầu)
                Bitmap scaledBitmap = scaleBitmap(bitmap, 0.1f);  // Giảm kích thước ảnh xuống 10%

                // Cấu hình PointAnnotationOptions với kích thước ban đầu đã giảm
                PointAnnotationOptions options = new PointAnnotationOptions()
                        .withPoint(point)
                        .withIconImage(scaledBitmap)
                        .withIconSize(0.5f); // Kích thước biểu tượng ban đầu nhỏ hơn

                // Thêm marker vào bản đồ
                pointAnnotationManager.create(options);

                // Lắng nghe sự kiện thay đổi camera
                mapView.getMapboxMap().addOnCameraChangeListener((cameraState) -> {
                    // Kiểm tra thời gian cập nhật camera để xác định khi camera đã dừng lại
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastCameraUpdateTime > 500) { // Nếu quá 500ms, coi như camera đã dừng lại
                        if (cameraState != null) {
                            // Lấy camera state từ MapboxMap để truy cập zoom
                            double zoom = mapView.getMapboxMap().getCameraState().getZoom(); // Truy cập zoom qua getCameraState()
                            updateIconSize(zoom);
                        }
                        else {
                            Toast.makeText(context, "Camera state is null", Toast.LENGTH_SHORT).show();
                        }
                    }
                    lastCameraUpdateTime = currentTime; // Cập nhật thời gian
                });
            }
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

    // Thay đổi phương thức scaleBitmap để sử dụng Matrix
    private Bitmap scaleBitmap(Bitmap originalBitmap, float scaleFactor) {
        int width = originalBitmap.getWidth();
        int height = originalBitmap.getHeight();
        int newWidth = (int) (width * scaleFactor);
        int newHeight = (int) (height * scaleFactor);
        return getResizedBitmap(originalBitmap, newHeight, newWidth);
    }

    private void updateIconSize(double zoom) {
        if (pointAnnotationManager != null && point != null) {
            double minSize = 0.05;
            double maxSize = 1;
            double newSize = Math.max(minSize, Math.min(maxSize, (zoom / 22.0) * 2.0));

            // Tạo biểu tượng từ tài nguyên drawable
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pothole_icon);

            // Thay đổi kích thước của ảnh khi zoom
            Bitmap scaledBitmap = scaleBitmap(bitmap, (float) newSize);

            // Cấu hình PointAnnotationOptions với kích thước mới
            PointAnnotationOptions options = new PointAnnotationOptions()
                    .withPoint(point)
                    .withIconImage(scaledBitmap)
                    .withIconSize((float) newSize);

            // Xóa tất cả các annotation hiện tại và thêm lại marker vào bản đồ với kích thước mới
            pointAnnotationManager.deleteAll();
            pointAnnotationManager.create(options);
        }
    }
}
