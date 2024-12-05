package com.example.finalproject;

import android.os.Bundle;
import android.view.View;
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
import java.util.Random;

public class DashBoard extends AppCompatActivity {

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
        int smallCount = getIntent().getIntExtra("smallCount", 0);
        int mediumCount = getIntent().getIntExtra("mediumCount", 0);
        int bigCount = getIntent().getIntExtra("bigCount", 0);


        // Thiết lập biểu đồ
        setupBarChart();
        setupPieChart(smallCount, mediumCount, bigCount);
        setupLineChart();
    }

    private void setupBarChart() {
        BarChart barChart = findViewById(R.id.column_chart);
        List<BarEntry> entries = new ArrayList<>();
        Random random = new Random();

        // Tạo dữ liệu ngẫu nhiên cho BarChart
        for (int i = 0; i < 10; i++) {
            float value = random.nextFloat() * 100;
            entries.add(new BarEntry(i, value));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Label");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        BarData data = new BarData(dataSet);

        barChart.setData(data);
        barChart.invalidate(); // Refresh biểu đồ
    }

    private void setupPieChart(int smallCount, int mediumCount, int bigCount) {
        PieChart pieChart = findViewById(R.id.pie_chart);
        List<PieEntry> entries = new ArrayList<>();

        entries.add(new PieEntry(smallCount, "Small"));
        entries.add(new PieEntry(mediumCount, "Medium"));
        entries.add(new PieEntry(bigCount, "Big"));

        PieDataSet dataSet = new PieDataSet(entries, "Pothole Sizes");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        PieData data = new PieData(dataSet);

        pieChart.setData(data);
        pieChart.invalidate();
    }
    private void setupLineChart() {
        LineChart lineChart = findViewById(R.id.line_chart);
        List<Entry> entries = new ArrayList<>();
        Random random = new Random();

        // Tạo dữ liệu ngẫu nhiên cho LineChart
        for (int i = 0; i < 20; i++) {
            float value = random.nextFloat() * 100;
            entries.add(new Entry(i, value));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Data Points");
        dataSet.setColor(getResources().getColor(R.color.white)); // Màu của đường kẻ
        dataSet.setValueTextColor(getResources().getColor(R.color.black)); // Màu của giá trị
        LineData data = new LineData(dataSet);

        lineChart.setData(data);
        lineChart.invalidate(); // Refresh biểu đồ
    }
}
