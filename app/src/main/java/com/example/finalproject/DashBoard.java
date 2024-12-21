package com.example.finalproject;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.graphics.Color;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Locale;


public class DashBoard extends AppCompatActivity {
    private PieChart pieChart;
    private PieChart secondPieChart;
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

        Intent intentDashboard = getIntent();

        // Lấy giá trị email từ Intent
        String email = intentDashboard.getStringExtra("USER_EMAIL");

        ImageView iconBack = findViewById(R.id.iconBack);

        // Đặt sự kiện click
        iconBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Quay về màn hình trước đó
                finish();
            }
        });
        // Khởi tạo Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://finalmap.click/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);

        // Khởi tạo các biểu đồ
        pieChart = findViewById(R.id.pie_chart);
        secondPieChart = findViewById(R.id.second_pie_chart);
        barChart = findViewById(R.id.column_chart);
        lineChart = findViewById(R.id.line_chart);

        // Thiết lập các biểu đồ
        setupPieChart();
        setupSecondPieChart();
        setupBarChart();
        setupLineChart();


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
    private void setupSecondPieChart() {
        // Gọi API với email cụ thể
        Call<Map<String, Long>> call = apiService.countPotholesBySizeAndEmail("22520412@gm.uit.edu.vn");

        call.enqueue(new Callback<Map<String, Long>>() {
            @Override
            public void onResponse(Call<Map<String, Long>> call, Response<Map<String, Long>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Cập nhật biểu đồ với dữ liệu trả về
                    updateSecondPieChart(response.body());
                } else {
                    secondPieChart.setNoDataText("No data available.");
                }
            }

            @Override
            public void onFailure(Call<Map<String, Long>> call, Throwable t) {
                secondPieChart.setNoDataText("Failed to load data.");
                t.printStackTrace();
            }
        });
    }
    private void updateSecondPieChart(Map<String, Long> data) {
        // Đảm bảo rằng dữ liệu không rỗng
        if (data == null || data.isEmpty()) {
            secondPieChart.setNoDataText("No data available");
            return;
        }

        List<PieEntry> entries = new ArrayList<>();

        // Lấy giá trị cho các size "small", "medium", "big"
        int smallCount = data.getOrDefault("small", 0L).intValue();
        int mediumCount = data.getOrDefault("medium", 0L).intValue();
        int bigCount = data.getOrDefault("big", 0L).intValue();

        if (smallCount > 0) entries.add(new PieEntry(smallCount, "Small"));
        if (mediumCount > 0) entries.add(new PieEntry(mediumCount, "Medium"));
        if (bigCount > 0) entries.add(new PieEntry(bigCount, "Big"));

        PieDataSet dataSet = new PieDataSet(entries, "Pothole Sizes by Email");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        PieData pieData = new PieData(dataSet);

        Description description = new Description();
        description.setText("Potholes by Email");
        secondPieChart.setDescription(description);

        secondPieChart.setData(pieData);
        secondPieChart.invalidate(); // Làm mới biểu đồ
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
        Description description = new Description();
        description.setText("Count by Size");
        pieChart.setDescription(description);
        pieChart.setData(data);
        pieChart.invalidate();
    }

    private void setupBarChart() {
        Call<Map<String, Long>> call = apiService.countPotholesByDay();

        call.enqueue(new Callback<Map<String, Long>>() {
            @Override
            public void onResponse(Call<Map<String, Long>> call, Response<Map<String, Long>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateBarChart(response.body());
                } else {
                    barChart.setNoDataText("No data available");
                }
            }

            @Override
            public void onFailure(Call<Map<String, Long>> call, Throwable t) {
                barChart.setNoDataText("Failed to load data: " + t.getMessage());
            }
        });
    }

    private void updateBarChart(Map<String, Long> data) {
        // Sắp xếp dữ liệu theo ngày
        Map<String, Long> sortedData = new TreeMap<>((date1, date2) -> {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                return dateFormat.parse(date1).compareTo(dateFormat.parse(date2));
            } catch (ParseException e) {
                e.printStackTrace();
                return 0; // Nếu lỗi, giữ nguyên thứ tự
            }
        });
        sortedData.putAll(data);

        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        int index = 0; // Chỉ số index trên trục X
        for (String date : sortedData.keySet()) {
            int count = sortedData.get(date) != null ? sortedData.get(date).intValue() : 0; // Dữ liệu chỉ lấy số nguyên
            entries.add(new BarEntry(index, count));

            // Chuyển đổi định dạng ngày tháng năm -> ngày tháng
            labels.add(formatToDayMonth(date));
            index++;
        }

        BarDataSet dataSet = new BarDataSet(entries, "Potholes by Day");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);

        // Cập nhật trục X
        XAxis xAxis = barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels)); // Sử dụng ngày tháng làm nhãn trục X
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f); // Đảm bảo mỗi nhãn là một cột
        xAxis.setLabelCount(labels.size());

        // Cập nhật trục Y để hiển thị các số nguyên
        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setGranularity(1f); // Chỉ hiển thị các số nguyên
        leftAxis.setDrawLabels(true);
        leftAxis.setAxisMinimum(0); // Đảm bảo không hiển thị số âm

        barChart.getAxisRight().setEnabled(false); // Tắt trục Y bên phải
        Description description = new Description();
        description.setText("Count by Day");
        barChart.setDescription(description);
        barChart.invalidate(); // Refresh biểu đồ
    }

    private String formatToDayMonth(String date) {
        try {
            // Định dạng ngày tháng năm gốc
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            // Định dạng ngày tháng mới
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());
            return outputFormat.format(inputFormat.parse(date));
        } catch (ParseException e) {
            e.printStackTrace();
            return date; // Trả về giá trị gốc nếu xảy ra lỗi
        }
    }

    private void setupLineChart() {
        // Email cố định để gọi API
        String email = "22520412@gm.uit.edu.vn";

        // Gọi API để lấy danh sách ổ gà
        apiService.getPotholesReportedByUser(email).enqueue(new Callback<List<Map<String, Object>>>() {
            @Override
            public void onResponse(Call<List<Map<String, Object>>> call, Response<List<Map<String, Object>>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Gọi phương thức để cập nhật biểu đồ
                    updateLineChart(response.body(), email);
                } else {
                    // Nếu không có dữ liệu, hiển thị thông báo
                    lineChart.setNoDataText("No data available for the provided email: " + email);
                    lineChart.invalidate();
                }
            }

            @Override
            public void onFailure(Call<List<Map<String, Object>>> call, Throwable t) {
                // Nếu có lỗi khi gọi API
                lineChart.setNoDataText("Failed to load data: " + t.getMessage());
                lineChart.invalidate();
            }
        });

        // Thiết lập LineChart
        lineChart.getDescription().setEnabled(false);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setTouchEnabled(true);

        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(5, true);

        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setGranularity(1f);
        lineChart.getAxisRight().setEnabled(false);
    }

    private void updateLineChart(List<Map<String, Object>> data, String email) {
        // Tạo Map để nhóm dữ liệu theo ngày với key là email và value là số lượng ổ gà
        Map<String, Integer> potholesPerDay = new HashMap<>();
        List<String> dates = new ArrayList<>();  // Danh sách lưu trữ ngày để sử dụng trên trục X
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM"); // Định dạng ngày tháng (dd/MM)

        // Lặp qua danh sách dữ liệu trả về từ API
        for (Map<String, Object> entry : data) {
            // Lấy giá trị từ timeReported và chia tách thành ngày
            String timestamp = entry.get("timeReported").toString();
            String date = timestamp.split("T")[0];  // Lấy ngày (yyyy-MM-dd)

            // Tính số lượng ổ gà trong mỗi ngày
            potholesPerDay.put(date, potholesPerDay.getOrDefault(date, 0) + 1);
        }

        // Kiểm tra nếu không có dữ liệu
        if (potholesPerDay.isEmpty()) {
            lineChart.setNoDataText("No data available for the provided email.");
            lineChart.invalidate();
            return;
        }

        // Tạo các entry cho biểu đồ (dữ liệu ngày và số lượng ổ gà)
        List<Entry> entries = new ArrayList<>();
        int i = 0;  // Đếm các ngày để gán chỉ số cho từng Entry
        for (Map.Entry<String, Integer> entry : potholesPerDay.entrySet()) {
            // Định dạng lại ngày từ "yyyy-MM-dd" sang "dd/MM"
            try {
                String formattedDate = dateFormat.format(new SimpleDateFormat("yyyy-MM-dd").parse(entry.getKey()));
                dates.add(formattedDate);  // Thêm ngày đã được định dạng vào danh sách dates
            } catch (ParseException e) {
                e.printStackTrace();
            }
            entries.add(new Entry(i++, entry.getValue()));
        }

        // Tạo dataset cho LineChart
        LineDataSet dataSet = new LineDataSet(entries, "Potholes per Day for account ");
        dataSet.setColor(Color.BLUE);  // Màu sắc cho đường biểu đồ
        dataSet.setValueTextColor(Color.BLACK);  // Màu sắc cho giá trị biểu đồ

        // Tạo LineData và gán vào biểu đồ
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        // Thiết lập trục Y bắt đầu từ 0
        YAxis yAxis = lineChart.getAxisLeft();
        yAxis.setAxisMinimum(0f);  // Đảm bảo trục Y bắt đầu từ 0
        lineChart.getAxisRight().setEnabled(false);  // Tắt trục Y bên phải

        // Thiết lập trục X và định dạng ngày
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);  // Đảm bảo có một điểm trên trục X cho mỗi ngày
        xAxis.setLabelCount(dates.size(), true); // Đảm bảo có đủ số lượng nhãn
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dates)); // Dùng danh sách ngày đã định dạng

        // Cập nhật biểu đồ
        lineChart.invalidate();
    }



    interface ApiService {
        @GET("/map/potholes/count-by-size")
        Call<Long> countPotholesBySize(@Query("size") String size);

        @GET("/map/potholes/count-by-eachday")
        Call<Map<String, Long>> countPotholesByDay();

        @GET("/map/potholes/count-by-size-and-email")
        Call<Map<String, Long>> countPotholesBySizeAndEmail(@Query("email") String email);

        @GET("/map/potholes/user-reports")
        Call<List<Map<String, Object>>> getPotholesReportedByUser(@Query("email") String email);

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
