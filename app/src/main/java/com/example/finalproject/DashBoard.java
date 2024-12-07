package com.example.finalproject;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class DashBoard extends AppCompatActivity {
    private PieChart pieChart;
    private BarChart barChart;
    private LineChart lineChart;
    private ApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);

        // Đảm bảo ID "main" tồn tại trong activity_dash_board.xml
        View mainView = findViewById(R.id.main);
        ViewCompat.setOnApplyWindowInsetsListener(mainView, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://finalmap.click/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        // Khởi tạo các biểu đồ
        pieChart = findViewById(R.id.pie_chart);
        barChart = findViewById(R.id.column_chart);
        lineChart = findViewById(R.id.line_chart);

        // Thiết lập các biểu đồ
        setupPieChart();
        setupBarChart();
        setupLineChart();
        findViewById(R.id.load_data_button).setOnClickListener(v -> {
            setupPieChart();
            setupBarChart();
            setupLineChart();
        });

        // Button Refresh Data
        findViewById(R.id.refresh_data_button).setOnClickListener(v -> refreshData());
    }
    private void refreshData() {
        Toast.makeText(this, "Refreshing data...", Toast.LENGTH_SHORT).show();
        setupPieChart();
        setupBarChart();
        setupLineChart();
    }

    private void setupPieChart() {
        Call<Long> smallCall = apiService.countPotholesBySize("small");
        Call<Long> mediumCall = apiService.countPotholesBySize("medium");
        Call<Long> bigCall = apiService.countPotholesBySize("big");

        smallCall.enqueue(new Callback<Long>() {
            @Override
            public void onResponse(Call<Long> call, Response<Long> response) {
                long smallCount = response.body() != null ? response.body() : 0;

                mediumCall.enqueue(new Callback<Long>() {
                    @Override
                    public void onResponse(Call<Long> call, Response<Long> response) {
                        long mediumCount = response.body() != null ? response.body() : 0;

                        bigCall.enqueue(new Callback<Long>() {
                            @Override
                            public void onResponse(Call<Long> call, Response<Long> response) {
                                long bigCount = response.body() != null ? response.body() : 0;
                                updatePieChart((int) smallCount, (int) mediumCount, (int) bigCount);
                            }

                            @Override
                            public void onFailure(Call<Long> call, Throwable t) {
                                pieChart.setNoDataText("Failed to load data.");
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call<Long> call, Throwable t) {
                        pieChart.setNoDataText("Failed to load data.");
                    }
                });
            }

            @Override
            public void onFailure(Call<Long> call, Throwable t) {
                pieChart.setNoDataText("Failed to load data.");
            }
        });
    }

    private void updatePieChart(int smallCount, int mediumCount, int bigCount) {
        if (smallCount == 0 && mediumCount == 0 && bigCount == 0) {
            pieChart.setNoDataText("No data available");
            return;
        }

        List<PieEntry> entries = new ArrayList<>();

        if (smallCount > 0) entries.add(new PieEntry(smallCount, "Small"));
        if (mediumCount > 0) entries.add(new PieEntry(mediumCount, "Medium"));
        if (bigCount > 0) entries.add(new PieEntry(bigCount, "Big"));

        PieDataSet dataSet = new PieDataSet(entries, "Pothole Sizes");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        PieData data = new PieData(dataSet);

        pieChart.setData(data);
        pieChart.invalidate();
    }

    private void setupBarChart() {
        String startTime = "2024-12-06"; // Thời gian bắt đầu
        String endTime = "2024-12-07";   // Thời gian kết thúc

        Call<List<Pothole>> call = apiService.getPotholesByTime(startTime, endTime);
        call.enqueue(new Callback<List<Pothole>>() {
            @Override
            public void onResponse(Call<List<Pothole>> call, Response<List<Pothole>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateBarChart(response.body());
                } else {
                    barChart.setNoDataText("No data available");
                }
            }

            @Override
            public void onFailure(Call<List<Pothole>> call, Throwable t) {
                barChart.setNoDataText("Failed to load data: " + t.getMessage());
            }
        });
    }

    private void updateBarChart(List<Pothole> potholes) {
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < potholes.size(); i++) {
            Pothole pothole = potholes.get(i);
            entries.add(new BarEntry(i, pothole.getCount()));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Potholes by Day");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        BarData data = new BarData(dataSet);

        barChart.setData(data);
        barChart.invalidate(); // Refresh biểu đồ
    }

    private void setupLineChart() {
        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            float value = i * 5f; // Example line chart data
            entries.add(new Entry(i, value));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Data Points");

        LineData data = new LineData(dataSet);

        lineChart.setData(data);
        lineChart.invalidate(); // Refresh biểu đồ
    }

    interface ApiService {
        @GET("/map/potholes/count-by-size")
        Call<Long> countPotholesBySize(@Query("size") String size);

        @GET("/map/potholes/time")
        Call<List<Pothole>> getPotholesByTime(@Query("startTime") String startTime, @Query("endTime") String endTime);
    }

    public static class Pothole {
        private String date;
        private int count;

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }
    }
}
