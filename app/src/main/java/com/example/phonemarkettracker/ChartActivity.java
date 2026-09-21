package com.example.phonemarkettracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;


import android.graphics.Color;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;


import java.util.Locale;

//displays today's persisted sales, profit or loss, and most-sold phones.
public class ChartActivity extends Activity {
    private static final float BAR_WIDTH = 0.58f;
    private static final int CHART_ANIMATION_DURATION = 700;

    private DatabasePMT databasePMT;
    private BarChart phoneSalesChart;
    private TextView totalSoldText;
    private TextView profitLossText;
    private TextView topPhoneText;
    private TextView topQuantityText;

    //1.screen setup
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);

        databasePMT = new DatabasePMT(this);
        connectViews();
        setUpActions();
    }

    @Override
    protected void onResume() {
        super.onResume();
        displayDailySales();
    }

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

    //2.read daily results and display chart
    private void displayDailySales() {
        String date = LocalDate.now().toString();
        SalesCalculator.Totals totals = databasePMT.getSalesTotals(date);
        List<PhoneSalesRecord> phoneSalesRecords = databasePMT.getPhoneSales(date);
        DailySalesSummary dailySalesSummary = summarizeDailySales(totals, phoneSalesRecords);

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

    static DailySalesSummary summarizeDailySales(SalesCalculator.Totals totals,
                                                List<PhoneSalesRecord> phoneSalesRecords) {
        int totalQuantitySold = 0;
        String mostSoldPhone = "No sales yet";
        int mostSoldQuantity = 0;

        for (PhoneSalesRecord record : phoneSalesRecords) {
            //jumlah unit terjual.
            totalQuantitySold += record.getQuantitySold();
            //if equal keep the first one as first
            if (record.getQuantitySold() > mostSoldQuantity) {
                mostSoldQuantity = record.getQuantitySold();
                mostSoldPhone = record.getPhoneName();
            }
        }
        return new DailySalesSummary(totalQuantitySold, totals.getTotalCost(),
                totals.getTotalRevenue(), totals.getProfitLoss(), mostSoldPhone, mostSoldQuantity);
    }

    //3.process: confirm and reset today
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

    private void resetDailyTracking() {
        databasePMT.deleteSales(LocalDate.now().toString());
        CartManager.clearCart();
        displayDailySales();
        Toast.makeText(this, "Today's tracking has been reset", Toast.LENGTH_SHORT).show();
    }

    //4.output formatting for profit loss
    private String formatSignedMoney(double amount) {
        String sign = amount >= 0 ? "+" : "−";
        return sign + String.format(Locale.US, "RM %,.2f", Math.abs(amount));
    }

    //5.chart bars and labels (style)
    private void displayPhoneSalesChart(List<PhoneSalesRecord> phoneSalesRecords) {
        List<BarEntry> phoneSalesEntries = new ArrayList<>();
        List<String> phoneLabels = new ArrayList<>();

        for (PhoneSalesRecord phoneSalesRecord : phoneSalesRecords) {
            if (phoneSalesRecord.getQuantitySold() <= 0) {
                continue;
            }

            phoneSalesEntries.add(new BarEntry(
                    //x = index bar, y = unit terjual.
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

    //shorten displayed names
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

    //chart appearance
    private void stylePhoneSalesDataSet(BarDataSet phoneSalesDataSet) {
        phoneSalesDataSet.setColors(
                Color.parseColor("#3F51B5"), //deepblueindigo
                Color.parseColor("#00A8B8"), //cyan
                Color.parseColor("#7C4DFF"), //purple
                Color.parseColor("#FF9800"),  //bright orange
                Color.parseColor("#16A34A")    //green
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


    //6.bot nav
    private void openProductMenu() {
        startActivity(new Intent(this, ProductActivity.class));
    }

    private void openCartScreen() {
        startActivity(new Intent(this, CartActivity.class));
    }
}
