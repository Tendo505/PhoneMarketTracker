package com.example.phonemarkettracker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** Display-only chart dashboard powered by MPAndroidChart. */
public class ChartActivity extends Activity {

    private static final String[] PHONE_LABELS = {
            "iPhone 15", "Galaxy S24", "Redmi 13", "OPPO Reno"
    };

    private static final float BAR_WIDTH = 0.58f;
    private static final int CHART_ANIMATION_DURATION = 700;

    // create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        BarChart phoneSalesChart = findViewById(R.id.phoneSalesChart);
        displayPhoneSalesChart(phoneSalesChart);
        setUpNavigation();
    }

    private void setUpNavigation() {
        findViewById(R.id.navProducts).setOnClickListener(view -> openProductMenu());
        findViewById(R.id.navCart).setOnClickListener(view -> openCartScreen());
    }

    // create chart data
    private List<BarEntry> createPhoneSalesEntries() {
        List<BarEntry> phoneSalesEntries = new ArrayList<>();
        phoneSalesEntries.add(new BarEntry(0f, 8f));
        phoneSalesEntries.add(new BarEntry(1f, 6f));
        phoneSalesEntries.add(new BarEntry(2f, 4f));
        phoneSalesEntries.add(new BarEntry(3f, 3f));
        return phoneSalesEntries;
    }

    // create chart data
    private BarData createPhoneSalesData() {
        BarDataSet phoneSalesDataSet = new BarDataSet(
                createPhoneSalesEntries(), "Phones sold");

        stylePhoneSalesDataSet(phoneSalesDataSet);

        BarData phoneSalesData = new BarData(phoneSalesDataSet);
        phoneSalesData.setBarWidth(BAR_WIDTH);
        return phoneSalesData;
    }

    private void stylePhoneSalesDataSet(BarDataSet phoneSalesDataSet) {
        phoneSalesDataSet.setColors(
                Color.parseColor("#3F51B5"),
                Color.parseColor("#00A8B8"),
                Color.parseColor("#7C4DFF"),
                Color.parseColor("#FF9800"));
        phoneSalesDataSet.setValueTextColor(Color.parseColor("#172033"));
        phoneSalesDataSet.setValueTextSize(11f);
    }

    // display output
    private void displayPhoneSalesChart(BarChart phoneSalesChart) {
        phoneSalesChart.setData(createPhoneSalesData());
        stylePhoneSalesChart(phoneSalesChart);
        styleHorizontalAxis(phoneSalesChart.getXAxis());
        styleVerticalAxis(phoneSalesChart.getAxisLeft());
        phoneSalesChart.getAxisRight().setEnabled(false);
        phoneSalesChart.animateY(CHART_ANIMATION_DURATION);
        phoneSalesChart.invalidate();
    }

    private void stylePhoneSalesChart(BarChart phoneSalesChart) {
        phoneSalesChart.getDescription().setEnabled(false);
        phoneSalesChart.getLegend().setEnabled(false);
        phoneSalesChart.setDrawGridBackground(false);
        phoneSalesChart.setDrawBarShadow(false);
        phoneSalesChart.setDrawValueAboveBar(true);
        phoneSalesChart.setPinchZoom(false);
        phoneSalesChart.setScaleEnabled(false);
        phoneSalesChart.setExtraBottomOffset(8f);
    }

    private void styleHorizontalAxis(XAxis horizontalAxis) {
        horizontalAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        horizontalAxis.setValueFormatter(
                new IndexAxisValueFormatter(Arrays.asList(PHONE_LABELS)));
        horizontalAxis.setGranularity(1f);
        horizontalAxis.setLabelCount(PHONE_LABELS.length);
        horizontalAxis.setTextColor(Color.parseColor("#67738B"));
        horizontalAxis.setTextSize(10f);
        horizontalAxis.setDrawGridLines(false);
        horizontalAxis.setDrawAxisLine(false);
    }

    private void styleVerticalAxis(YAxis verticalAxis) {
        verticalAxis.setAxisMinimum(0f);
        verticalAxis.setGranularity(1f);
        verticalAxis.setTextColor(Color.parseColor("#67738B"));
        verticalAxis.setDrawAxisLine(false);
        verticalAxis.setGridColor(Color.parseColor("#E6EBF2"));
    }

    private void openProductMenu() {
        Intent productMenuIntent = new Intent(this, ProductActivity.class);
        startActivity(productMenuIntent);
    }

    private void openCartScreen() {
        Intent cartIntent = new Intent(this, CartActivity.class);
        startActivity(cartIntent);
    }
}
