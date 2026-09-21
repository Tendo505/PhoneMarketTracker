package com.example.phonemarkettracker;

//values displayed by the overview.
public class DailySalesSummary {
    private final int totalQuantitySold;
    private final double totalCost;
    private final double totalRevenue;
    private final double profitLoss;
    private final String mostSoldPhone;
    private final int mostSoldQuantity;

    public DailySalesSummary(int totalQuantitySold, double totalCost, double totalRevenue,
                             double profitLoss, String mostSoldPhone, int mostSoldQuantity) {
        this.totalQuantitySold = totalQuantitySold;
        this.totalCost = totalCost;
        this.totalRevenue = totalRevenue;
        this.profitLoss = profitLoss;
        this.mostSoldPhone = mostSoldPhone;
        this.mostSoldQuantity = mostSoldQuantity;
    }

    public int getTotalQuantitySold() { return totalQuantitySold; }
    public double getTotalCost() { return totalCost; }
    public double getTotalRevenue() { return totalRevenue; }
    public double getProfitLoss() { return profitLoss; }
    public String getMostSoldPhone() { return mostSoldPhone; }
    public int getMostSoldQuantity() { return mostSoldQuantity; }
}
