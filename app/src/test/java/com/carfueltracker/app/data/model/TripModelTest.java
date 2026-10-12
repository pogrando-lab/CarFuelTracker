package com.carfueltracker.app.data.model;

import org.junit.Test;
import static org.junit.Assert.*;

public class TripModelTest {

    @Test
    public void testTripDefaultConstructor() {
        Trip trip = new Trip();
        assertTrue(trip.isActive());
        assertTrue(trip.getStartTime() > 0);
        assertEquals(0.0, trip.getDistanceKm(), 0.001);
        assertEquals(0.0, trip.getAvgSpeedKmH(), 0.001);
        assertEquals(0.0, trip.getMaxSpeedKmH(), 0.001);
        assertEquals(0.0, trip.getFuelConsumedLiters(), 0.001);
        assertEquals(0.0, trip.getCost(), 0.001);
        assertNotNull(trip.getNotes());
        assertTrue(trip.isValid());
    }

    @Test
    public void testTripParameterizedConstructorAndGetters() {
        long start = 1700000000000L;
        long end = 1700003600000L;
        Trip trip = new Trip(1, start, end, 65.5, 65.5, 110.0, 5.5, 2750.0, "Поездка в Дилижан", false);

        assertEquals(1, trip.getId());
        assertEquals(start, trip.getStartTime());
        assertEquals(end, trip.getEndTime());
        assertEquals(65.5, trip.getDistanceKm(), 0.001);
        assertEquals(65.5, trip.getAvgSpeedKmH(), 0.001);
        assertEquals(110.0, trip.getMaxSpeedKmH(), 0.001);
        assertEquals(5.5, trip.getFuelConsumedLiters(), 0.001);
        assertEquals(2750.0, trip.getCost(), 0.001);
        assertEquals("Поездка в Дилижан", trip.getNotes());
        assertFalse(trip.isActive());
        assertEquals(3600000L, trip.getDurationMillis());
        assertTrue(trip.isValid());
    }

    @Test
    public void testNegativeValuesClamped() {
        Trip trip = new Trip();
        trip.setDistanceKm(-15.0);
        trip.setAvgSpeedKmH(-50.0);
        trip.setMaxSpeedKmH(-100.0);
        trip.setFuelConsumedLiters(-10.0);
        trip.setCost(-500.0);

        assertEquals(0.0, trip.getDistanceKm(), 0.001);
        assertEquals(0.0, trip.getAvgSpeedKmH(), 0.001);
        assertEquals(0.0, trip.getMaxSpeedKmH(), 0.001);
        assertEquals(0.0, trip.getFuelConsumedLiters(), 0.001);
        assertEquals(0.0, trip.getCost(), 0.001);
    }
}
