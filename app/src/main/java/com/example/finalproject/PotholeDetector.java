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

            if (magnitude > 15) { // Ngưỡng phát hiện ổ gà
                callback.onPotholeDetected();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {}
    };

    // Giao diện callback cho sự kiện phát hiện ổ gà
    public interface PotholeCallback {
        void onPotholeDetected();
    }
}
