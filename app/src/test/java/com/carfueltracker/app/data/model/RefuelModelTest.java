package com.carfueltracker.app.data.model;

import org.junit.Test;
import static org.junit.Assert.*;

public class RefuelModelTest {

    @Test
    public void testRefuelCalculationAndValidation() {
        long now = 1700000000000L;
        Refuel refuel = new Refuel(now, 40.0, 510.0, 125000.0, "CPS Petrol", "АИ-95", "Полный бак");

        assertEquals(now, refuel.getTimestamp());
        assertEquals(40.0, refuel.getLiters(), 0.001);
        assertEquals(510.0, refuel.getPricePerLiter(), 0.001);
        assertEquals(20400.0, refuel.getTotalCost(), 0.001);
        assertEquals(125000.0, refuel.getMileageKm(), 0.001);
        assertEquals("CPS Petrol", refuel.getFuelStation());
        assertEquals("АИ-95", refuel.getFuelType());
        assertEquals("Полный бак", refuel.getNotes());
        assertTrue(refuel.isValid());
    }

    @Test
    public void testRecalculateTotalCostOnLitersChange() {
        Refuel refuel = new Refuel();
        refuel.setPricePerLiter(500.0);
        refuel.setLiters(20.0);

        assertEquals(10000.0, refuel.getTotalCost(), 0.001);

        refuel.setLiters(30.0);
        assertEquals(15000.0, refuel.getTotalCost(), 0.001);
    }

    @Test
    public void testInvalidRefuel() {
        Refuel refuel = new Refuel();
        refuel.setLiters(0);
        assertFalse(refuel.isValid());
    }
}
