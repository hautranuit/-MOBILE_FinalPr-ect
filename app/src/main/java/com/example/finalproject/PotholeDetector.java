package com.example.finalproject;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class PotholeDetector {
    private final SensorManager sensorManager;
    private final Sensor accelerometer;
    private final PotholeCallback callback;
    private static final int SMOOTHING_WINDOW_SIZE = 5; // Số lượng mẫu để làm mượt dữ liệu
    private float[] smoothingBuffer = new float[SMOOTHING_WINDOW_SIZE];
    private int bufferIndex = 0;
    private long lastDetectionTime = 0;
    private static final long DETECTION_INTERVAL = 1500; // Thời gian tối thiểu giữa các lần phát hiện (ms)

    public PotholeDetector(Context context, PotholeCallback callback) {
        this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        this.accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.callback = callback;
    }

    public void startDetection() {
        sensorManager.registerListener(sensorEventListener, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void stopDetection() {
        sensorManager.unregisterListener(sensorEventListener);
    }

    private final SensorEventListener sensorEventListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            double magnitude = Math.sqrt(x * x + y * y + z * z);

            // Làm mượt dữ liệu
            smoothingBuffer[bufferIndex] = (float) magnitude;
            bufferIndex = (bufferIndex + 1) % SMOOTHING_WINDOW_SIZE;

            // Tìm giá trị lớn nhất trong cửa sổ làm mượt
            double smoothedMagnitude = 0;
            for (float value : smoothingBuffer) {
                smoothedMagnitude += value;
            }
            smoothedMagnitude /= SMOOTHING_WINDOW_SIZE;

            String potholeSize = null;

            // Phân loại kích thước ổ gà
            if (smoothedMagnitude > 15 && smoothedMagnitude <= 20) {
                potholeSize = "small";
            } else if (smoothedMagnitude > 20 && smoothedMagnitude <= 25) {
                potholeSize = "medium";
            } else if (smoothedMagnitude > 25) {
                potholeSize = "big";
            }

            // Thêm điều kiện kiểm tra khoảng thời gian giữa các lần phát hiện
            long currentTime = System.currentTimeMillis();
            if (potholeSize != null && currentTime - lastDetectionTime > DETECTION_INTERVAL) {
                lastDetectionTime = currentTime;
                callback.onPotholeDetected(potholeSize);
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };

    // Giao diện callback cho sự kiện phát hiện ổ gà
    public interface PotholeCallback {
        void onPotholeDetected(String size); // Trả về kích thước ổ gà qua callback
    }
}
