package com.example.phonemarkettracker;


import org.junit.After;
import org.junit.Test;
import java.util.Arrays;
import java.util.Collections;
import static org.junit.Assert.*;

public class ProcessTest {
    private static final double DELTA = 0.001;

    @After
    public void clearCart() {
        CartManager.clearCart();
    }

    @Test
    public void emptyCartHasZeroTotals() {
        assertEquals(0, SalesCalculator.calculateTotalCost(Collections.emptyList()), DELTA);
        assertEquals(0, SalesCalculator.calculateTotalRevenue(Collections.emptyList()), DELTA);
        assertEquals(0, SalesCalculator.calculateProfitLoss(Collections.emptyList()), DELTA);
    }

    @Test
    public void itemFormulasPreserveLossAndQuantity() {
        CartItem item = new CartItem(new Phone(1, "Test", "Phone", 100, 80, 3));
        item.increaseQuantity();
        assertEquals(200, SalesCalculator.calculateItemCost(item), DELTA);
        assertEquals(160, SalesCalculator.calculateItemRevenue(item), DELTA);
        assertEquals(-40, SalesCalculator.calculateItemProfitLoss(item), DELTA);
    }

    @Test
    public void emptyDayKeepsNoSalesMessage() {
        DailySalesSummary summary = ChartActivity.summarizeDailySales(
                new SalesCalculator.Totals(0, 0, 0), Collections.emptyList());
        assertEquals(0, summary.getTotalQuantitySold());
        assertEquals(0, summary.getMostSoldQuantity());
        assertEquals("No sales yet", summary.getMostSoldPhone());
    }

    @Test
    public void zeroSalesPhonesDoNotBecomeTopPhone() {
        DailySalesSummary summary = ChartActivity.summarizeDailySales(
                new SalesCalculator.Totals(0, 0, 0),
                Arrays.asList(new PhoneSalesRecord("A", 0), new PhoneSalesRecord("B", 0)));
        assertEquals("No sales yet", summary.getMostSoldPhone());
        assertEquals(0, summary.getTotalQuantitySold());
    }

    @Test
    public void dailyTotalsAndTieOrderArePreserved() {
        DailySalesSummary summary = ChartActivity.summarizeDailySales(
                new SalesCalculator.Totals(100, 90, -10),
                Arrays.asList(new PhoneSalesRecord("A", 3),
                        new PhoneSalesRecord("B", 3), new PhoneSalesRecord("C", 1)));
        assertEquals(7, summary.getTotalQuantitySold());
        assertEquals("A", summary.getMostSoldPhone());
        assertEquals(3, summary.getMostSoldQuantity());
        assertEquals(100, summary.getTotalCost(), DELTA);
        assertEquals(90, summary.getTotalRevenue(), DELTA);
        assertEquals(-10, summary.getProfitLoss(), DELTA);
    }

    @Test
    public void largestQuantityWinsEvenWhenNotFirst() {
        DailySalesSummary summary = ChartActivity.summarizeDailySales(
                new SalesCalculator.Totals(0, 0, 0),
                Arrays.asList(new PhoneSalesRecord("A", 1), new PhoneSalesRecord("B", 6)));
        assertEquals("B", summary.getMostSoldPhone());
        assertEquals(6, summary.getMostSoldQuantity());
    }

    @Test
    public void repeatedSelectionKeepsOneCartItemAndCountsUnits() {
        CartManager.clearCart();
        Phone phone = new Phone(1, "Test", "Phone", 100, 120, 2);
        assertTrue(CartManager.addPhone(phone));
        assertTrue(CartManager.addPhone(phone));
        assertFalse(CartManager.addPhone(phone));
        assertEquals(1, CartManager.getItems().size());
        assertEquals(2, CartManager.getTotalQuantity());
        CartManager.decreaseQuantity(1);
        assertEquals(1, CartManager.getTotalQuantity());
        CartManager.decreaseQuantity(1);
        assertTrue(CartManager.getItems().isEmpty());
    }

    @Test
    public void loginValidationKeepsOriginalErrorOrder() {
        assertEquals("Enter your email address", LoginActivity.validateLogin("", ""));
        assertEquals("Enter your password", LoginActivity.validateLogin("a@b.com", ""));
        assertNull(LoginActivity.validateLogin("a@b.com", "password"));
    }
}
