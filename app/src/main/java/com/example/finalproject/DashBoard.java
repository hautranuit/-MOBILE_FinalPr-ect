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
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        int index = 0; // Chỉ số index trên trục X
        for (String date : data.keySet()) {
            int count = data.get(date) != null ? data.get(date).intValue() : 0; // Dữ liệu chỉ lấy số nguyên
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
        // Lấy email từ Intent
        String email = getIntent().getStringExtra("USER_EMAIL");

        // Kiểm tra nếu email không có giá trị, xử lý lỗi
        if (email == null || email.isEmpty()) {
            Toast.makeText(this, "Email is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        // Gọi API để lấy dữ liệu số lượng ổ gà phân loại theo kích thước và email
        apiService.countPotholesBySizeAndEmail(email).enqueue(new Callback<Map<String, Long>>() {
            @Override
            public void onResponse(Call<Map<String, Long>> call, Response<Map<String, Long>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Nhận dữ liệu từ API
                    Map<String, Long> potholeData = response.body();
                    updateLineChart(potholeData); // Cập nhật biểu đồ LineChart với dữ liệu thực
                } else {
                    lineChart.setNoDataText("No data available for the provided email.");
                    lineChart.invalidate();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Long>> call, Throwable t) {
                lineChart.setNoDataText("Failed to load data: " + t.getMessage());
                lineChart.invalidate();
            }
        });
    }

    private void updateLineChart(Map<String, Long> potholeData) {
        if (potholeData == null || potholeData.isEmpty()) {
            lineChart.setNoDataText("No data available");
            lineChart.invalidate();
            return;
        }

        // Dữ liệu thực cho từng loại ổ gà
        List<Entry> smallEntries = new ArrayList<>();
        List<Entry> mediumEntries = new ArrayList<>();
        List<Entry> bigEntries = new ArrayList<>();

        // Thêm dữ liệu vào danh sách dựa trên kích thước ổ gà
        int index = 0; // Sử dụng index làm trục X giả định
        if (potholeData.containsKey("small")) {
            smallEntries.add(new Entry(index++, potholeData.get("small")));
        }
        if (potholeData.containsKey("medium")) {
            mediumEntries.add(new Entry(index++, potholeData.get("medium")));
        }
        if (potholeData.containsKey("big")) {
            bigEntries.add(new Entry(index++, potholeData.get("big")));
        }

        // Tạo từng đường dữ liệu
        LineDataSet smallDataSet = new LineDataSet(smallEntries, "Small Potholes");
        smallDataSet.setColor(ColorTemplate.COLORFUL_COLORS[0]);
        smallDataSet.setCircleColor(ColorTemplate.COLORFUL_COLORS[0]);

        LineDataSet mediumDataSet = new LineDataSet(mediumEntries, "Medium Potholes");
        mediumDataSet.setColor(ColorTemplate.COLORFUL_COLORS[1]);
        mediumDataSet.setCircleColor(ColorTemplate.COLORFUL_COLORS[1]);

        LineDataSet bigDataSet = new LineDataSet(bigEntries, "Big Potholes");
        bigDataSet.setColor(ColorTemplate.COLORFUL_COLORS[2]);
        bigDataSet.setCircleColor(ColorTemplate.COLORFUL_COLORS[2]);

        // Thêm tất cả các tập dữ liệu vào LineData
        LineData lineData = new LineData(smallDataSet, mediumDataSet, bigDataSet);
        lineChart.setData(lineData);

        // Cấu hình trục X
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f); // Khoảng cách giữa các nhãn trên trục X
        xAxis.setLabelCount(index); // Số lượng nhãn hiển thị

        // Cấu hình trục Y
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setGranularity(1f);
        leftAxis.setAxisMinimum(0); // Đảm bảo trục Y không hiển thị số âm

        lineChart.getAxisRight().setEnabled(false); // Tắt trục Y bên phải

        lineChart.invalidate(); // Làm mới biểu đồ
    }


    interface ApiService {
        @GET("/map/potholes/count-by-size")
        Call<Long> countPotholesBySize(@Query("size") String size);

        @GET("/map/potholes/count-by-eachday")
        Call<Map<String, Long>> countPotholesByDay();

        @GET("/map/potholes/count-by-size-and-email")
        Call<Map<String, Long>> countPotholesBySizeAndEmail(@Query("email") String email);

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
