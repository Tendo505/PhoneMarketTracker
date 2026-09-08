package com.example.phonemarkettracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Displays today's persisted sales, profit or loss, and most-sold phones. */
public class ChartActivity extends Activity {

    private static final float BAR_WIDTH = 0.58f;
    private static final int CHART_ANIMATION_DURATION = 700;

    private DatabasePMT databasePMT;
    private BarChart phoneSalesChart;
    private TextView totalSoldText;
    private TextView profitLossText;
    private TextView topPhoneText;
    private TextView topQuantityText;

    // create
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        databasePMT = new DatabasePMT(this);
        connectViews();
        setUpActions();
    }

    // display output
    @Override
    protected void onResume() {
        super.onResume();
        displayDailySales();
    }

    // read
    private void connectViews() {
        phoneSalesChart = findViewById(R.id.phoneSalesChart);
        totalSoldText = findViewById(R.id.textTotalSold);
        profitLossText = findViewById(R.id.textDailyProfitLoss);
        topPhoneText = findViewById(R.id.textTopPhone);
        topQuantityText = findViewById(R.id.textTopQuantity);
    }

    private void setUpActions() {
        findViewById(R.id.buttonCloseDay).setOnClickListener(view -> confirmDailyReset());
        findViewById(R.id.navProducts).setOnClickListener(view -> openProductMenu());
        findViewById(R.id.navCart).setOnClickListener(view -> openCartScreen());
    }

    // read
    private void displayDailySales() {
        DailySalesSummary dailySalesSummary = databasePMT.getTodaySalesSummary();
        List<PhoneSalesRecord> phoneSalesRecords = databasePMT.getTodayPhoneSales();

        totalSoldText.setText(
                getResources().getQuantityString(
                        R.plurals.phone_sold_count,
                        dailySalesSummary.getTotalQuantitySold(),
                        dailySalesSummary.getTotalQuantitySold()
                )
        );
        profitLossText.setText(formatSignedMoney(dailySalesSummary.getProfitLoss()));
        profitLossText.setTextColor(getColor(
                dailySalesSummary.getProfitLoss() >= 0 ? R.color.profit : R.color.loss
        ));
        topPhoneText.setText(dailySalesSummary.getMostSoldPhone());
        topQuantityText.setText(
                dailySalesSummary.getMostSoldQuantity() + " sold"
        );

        displayPhoneSalesChart(phoneSalesRecords);
    }

    // create chart data
    private void displayPhoneSalesChart(List<PhoneSalesRecord> phoneSalesRecords) {
        List<BarEntry> phoneSalesEntries = new ArrayList<>();
        List<String> phoneLabels = new ArrayList<>();

        for (PhoneSalesRecord phoneSalesRecord : phoneSalesRecords) {
            if (phoneSalesRecord.getQuantitySold() <= 0) {
                continue;
            }

            phoneSalesEntries.add(new BarEntry(
                    phoneSalesEntries.size(),
                    phoneSalesRecord.getQuantitySold()
            ));
            phoneLabels.add(createShortPhoneLabel(phoneSalesRecord.getPhoneName()));
        }

        if (phoneSalesEntries.isEmpty()) {
            phoneSalesChart.clear();
            phoneSalesChart.setNoDataText("Complete a sale to display today's chart");
            phoneSalesChart.setNoDataTextColor(getColor(R.color.text_secondary));
            phoneSalesChart.invalidate();
            return;
        }

        BarDataSet phoneSalesDataSet = new BarDataSet(
                phoneSalesEntries,
                "Phones sold"
        );
        stylePhoneSalesDataSet(phoneSalesDataSet);

        BarData phoneSalesData = new BarData(phoneSalesDataSet);
        phoneSalesData.setBarWidth(BAR_WIDTH);
        phoneSalesChart.setData(phoneSalesData);

        stylePhoneSalesChart(phoneSalesChart);
        styleHorizontalAxis(phoneSalesChart.getXAxis(), phoneLabels);
        styleVerticalAxis(phoneSalesChart.getAxisLeft());
        phoneSalesChart.getAxisRight().setEnabled(false);
        phoneSalesChart.animateY(CHART_ANIMATION_DURATION);
        phoneSalesChart.invalidate();
    }

    private void stylePhoneSalesDataSet(BarDataSet phoneSalesDataSet) {
        phoneSalesDataSet.setColors(
                Color.parseColor("#3F51B5"),
                Color.parseColor("#00A8B8"),
                Color.parseColor("#7C4DFF"),
                Color.parseColor("#FF9800"),
                Color.parseColor("#16A34A")
        );
        phoneSalesDataSet.setValueTextColor(Color.parseColor("#172033"));
        phoneSalesDataSet.setValueTextSize(11f);
    }

    private void stylePhoneSalesChart(BarChart chart) {
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);
        chart.setPinchZoom(false);
        chart.setScaleEnabled(false);
        chart.setExtraBottomOffset(10f);
        chart.setFitBars(true);
    }

    private void styleHorizontalAxis(
            XAxis horizontalAxis,
            List<String> phoneLabels
    ) {
        horizontalAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        horizontalAxis.setValueFormatter(new IndexAxisValueFormatter(phoneLabels));
        horizontalAxis.setGranularity(1f);
        horizontalAxis.setLabelCount(phoneLabels.size());
        horizontalAxis.setLabelRotationAngle(phoneLabels.size() > 4 ? -20f : 0f);
        horizontalAxis.setTextColor(Color.parseColor("#67738B"));
        horizontalAxis.setTextSize(9f);
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

    // display output
    private String createShortPhoneLabel(String phoneName) {
        String shortLabel = phoneName
                .replace("Apple ", "")
                .replace("Samsung ", "")
                .replace("Xiaomi ", "")
                .replace("OPPO ", "")
                .replace(" 128GB", "")
                .replace(" 256GB", "");

        if (shortLabel.length() > 14) {
            return shortLabel.substring(0, 14);
        }

        return shortLabel;
    }

    // delete
    private void confirmDailyReset() {
        new AlertDialog.Builder(this)
                .setTitle("Close today's session?")
                .setMessage(
                        "This clears today's sales totals, profit or loss, most-sold ranking, " +
                                "chart data, and cart. Users, phone details, prices, and stock remain."
                )
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Close Day", (dialog, which) -> resetDailyTracking())
                .show();
    }

    // delete
    private void resetDailyTracking() {
        databasePMT.resetTodaySales();
        CartManager.clear();
        displayDailySales();
        Toast.makeText(this, "Today's tracking has been reset", Toast.LENGTH_SHORT).show();
    }

    // display output
    private String formatSignedMoney(double amount) {
        String sign = amount >= 0 ? "+" : "−";
        return sign + String.format(Locale.US, "RM %,.2f", Math.abs(amount));
    }

    private void openProductMenu() {
        startActivity(new Intent(this, ProductActivity.class));
    }

    private void openCartScreen() {
        startActivity(new Intent(this, CartActivity.class));
    }
}
