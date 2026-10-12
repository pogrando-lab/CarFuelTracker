package com.carfueltracker.app.data.model;

import org.junit.Test;
import static org.junit.Assert.*;

public class GPSPointModelTest {

    @Test
    public void testGPSPointConstructorAndCoordinates() {
        long timestamp = 1700000000000L;
        GPSPoint point = new GPSPoint(10, 1, 40.1792, 44.4991, 1000.5, 60.0f, timestamp, 4.5f);

        assertEquals(10, point.getId());
        assertEquals(1, point.getTripId());
        assertEquals(40.1792, point.getLatitude(), 0.00001);
        assertEquals(44.4991, point.getLongitude(), 0.00001);
        assertEquals(1000.5, point.getAltitude(), 0.01);
        assertEquals(60.0f, point.getSpeedKmH(), 0.01f);
        assertEquals(timestamp, point.getTimestamp());
        assertEquals(4.5f, point.getAccuracy(), 0.01f);
        assertTrue(point.isValidCoordinates());
    }

    @Test
    public void testInvalidCoordinates() {
        GPSPoint invalidPoint = new GPSPoint(0, 1, 95.0, 44.4991, 0, 0, System.currentTimeMillis(), 5.0f);
        assertFalse(invalidPoint.isValidCoordinates());

        invalidPoint.setLatitude(40.0);
        invalidPoint.setLongitude(190.0);
        assertFalse(invalidPoint.isValidCoordinates());

        invalidPoint.setLongitude(-185.0);
        assertFalse(invalidPoint.isValidCoordinates());
    }
}
