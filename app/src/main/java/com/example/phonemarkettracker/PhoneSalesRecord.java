package com.example.phonemarkettracker;

//one model and its sold quantity.
public class PhoneSalesRecord {
    private final String phoneName;
    private final int quantitySold;

    public PhoneSalesRecord(String phoneName, int quantitySold) {
        this.phoneName = phoneName;
        this.quantitySold = quantitySold;
    }

    public String getPhoneName() { return phoneName; }
    public int getQuantitySold() { return quantitySold; }
}
