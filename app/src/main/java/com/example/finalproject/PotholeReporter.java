package com.example.finalproject;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

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
import com.mapbox.maps.CameraState;

public class PotholeReporter {
    private final Context context;
    private final MapView mapView;
    private PointAnnotationManager pointAnnotationManager;
    private Point point;  // Lưu trữ tọa độ của pothole
    private long lastCameraUpdateTime = 0;  // Để kiểm tra thời gian thay đổi camera

    public PotholeReporter(Context context, MapView mapView) {
        this.context = context;
        this.mapView = mapView;
    }

    public void reportPothole() {
        // Kiểm tra quyền
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Nếu không có quyền, yêu cầu quyền từ Activity hoặc Fragment
            throw new SecurityException("Location permission not granted. Ensure permissions are requested.");
        }

        LocationEngine locationEngine = LocationEngineProvider.getBestLocationEngine(context);
        locationEngine.getLastLocation(new LocationEngineCallback<LocationEngineResult>() {
            @Override
            public void onSuccess(LocationEngineResult result) {
                Location location = result.getLastLocation();
                if (location != null) {
                    point = Point.fromLngLat(location.getLongitude(), location.getLatitude());
                    addMarker(point);
                }
            }

            @Override
            public void onFailure(@NonNull Exception exception) {
                // Xử lý lỗi
            }
        });
    }

    private void addMarker(Point point) {
        mapView.getMapboxMap().getStyle(style -> {
            if (style != null) {
                // Lấy AnnotationPlugin từ MapView
                AnnotationPlugin annotationPlugin = AnnotationPluginImplKt.getAnnotations(mapView);

                // Tạo PointAnnotationManager từ AnnotationPlugin
                pointAnnotationManager = PointAnnotationManagerKt.createPointAnnotationManager(annotationPlugin, mapView);

                // Tạo biểu tượng từ tài nguyên drawable
                Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pothole_icon);

                // Thay đổi kích thước của ảnh (giảm kích thước ảnh ban đầu)
                Bitmap scaledBitmap = scaleBitmap(bitmap, 0.01f);  // Giảm kích thước ảnh xuống 10% (thay đổi tỷ lệ theo yêu cầu)

                // Cấu hình PointAnnotationOptions với kích thước ban đầu đã giảm
                PointAnnotationOptions options = new PointAnnotationOptions()
                        .withPoint(point)
                        .withIconImage(scaledBitmap)
                        .withIconSize(0.1); // Kích thước biểu tượng ban đầu nhỏ hơn

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
                    }
                    lastCameraUpdateTime = currentTime; // Cập nhật thời gian
                });
            }
        });
    }

    private Bitmap scaleBitmap(Bitmap originalBitmap, float scaleFactor) {
        int width = originalBitmap.getWidth();
        int height = originalBitmap.getHeight();
        int newWidth = (int) (width * scaleFactor);
        int newHeight = (int) (height * scaleFactor);
        return Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, false);
    }

    private void updateIconSize(double zoom) {
        if (pointAnnotationManager != null && point != null) {
            // Giới hạn kích thước icon (tối thiểu và tối đa)
            double minSize = 0.05; // Kích thước tối thiểu của biểu tượng (giảm xuống so với trước)
            double maxSize = 0.5; // Kích thước tối đa của biểu tượng

            // Tính toán kích thước mới dựa trên mức zoom
            double newSize = Math.max(minSize, Math.min(maxSize, (zoom / 22.0) * 2.0)); // Điều chỉnh theo zoom, với giới hạn

            // Tạo biểu tượng từ tài nguyên drawable
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.pothole_icon);

            // Thay đổi kích thước của ảnh khi zoom
            Bitmap scaledBitmap = scaleBitmap(bitmap, (float) newSize); // Chuyển đổi newSize từ double sang float

            // Cấu hình PointAnnotationOptions với kích thước mới
            PointAnnotationOptions options = new PointAnnotationOptions()
                    .withPoint(point)
                    .withIconImage(scaledBitmap)
                    .withIconSize((float) newSize);  // Chuyển đổi newSize từ double sang float

            // Xóa tất cả các annotation hiện tại và thêm lại marker vào bản đồ với kích thước mới
            pointAnnotationManager.deleteAll();
            pointAnnotationManager.create(options);
        }
    }

}
