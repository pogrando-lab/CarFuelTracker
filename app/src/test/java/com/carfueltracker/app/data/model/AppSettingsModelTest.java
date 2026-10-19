package com.carfueltracker.app.data.model;

import org.junit.Test;
import static org.junit.Assert.*;

public class AppSettingsModelTest {

    @Test
    public void testDefaultSettings() {
        AppSettings settings = AppSettings.createDefault();

        assertEquals(8.5, settings.getConsumptionPer100Km(), 0.001);
        assertEquals(500.0, settings.getFuelPricePerLiter(), 0.001);
        assertEquals("km", settings.getDistanceUnit());
        assertEquals("km/h", settings.getSpeedUnit());
        assertEquals("L", settings.getFuelUnit());
        assertEquals("֏", settings.getCurrency());
        assertTrue(settings.isDarkThemeEnabled());
        assertFalse(settings.isAutoStartTracking());
        assertTrue(settings.isKeepScreenOn());
        assertEquals(1000L, settings.getGpsIntervalMs());
        assertEquals(AppSettings.FUEL_TYPE_GASOLINE, settings.getVehicleFuelType());
        assertEquals(AppSettings.GAS_TYPE_LPG, settings.getGasType());
        assertEquals(15d, settings.getGasConsumptionAdjustmentPercent(), 0.001d);
        assertTrue(settings.isValid());
    }

    @Test
    public void clampsGasAdjustmentAndPersistsDualFuelOptions() {
        AppSettings settings = AppSettings.createDefault();
        settings.setVehicleFuelType(AppSettings.FUEL_TYPE_HYBRID);
        settings.setGasType(AppSettings.GAS_TYPE_CNG);
        settings.setGasConsumptionAdjustmentPercent(120d);
        settings.setGboEnabled(true);
        settings.setGasolineWarmupFirst(false);

        assertEquals(AppSettings.FUEL_TYPE_HYBRID, settings.getVehicleFuelType());
        assertEquals(AppSettings.GAS_TYPE_CNG, settings.getGasType());
        assertEquals(100d, settings.getGasConsumptionAdjustmentPercent(), 0.001d);
        assertTrue(settings.isGboEnabled());
        assertFalse(settings.isGasolineWarmupFirst());
    }

    @Test
    public void testCustomSettingsValidation() {
        AppSettings settings = new AppSettings();
        settings.setConsumptionPer100Km(7.2);
        settings.setFuelPricePerLiter(480.0);
        settings.setCurrency("֏");
        settings.setGpsIntervalMs(2000L);

        assertEquals(7.2, settings.getConsumptionPer100Km(), 0.001);
        assertEquals(480.0, settings.getFuelPricePerLiter(), 0.001);
        assertEquals("֏", settings.getCurrency());
        assertEquals(2000L, settings.getGpsIntervalMs());
        assertTrue(settings.isValid());
    }

    @Test
    public void testClampingAndValidation() {
        AppSettings settings = new AppSettings();
        settings.setConsumptionPer100Km(-5.0);
        assertEquals(0.1, settings.getConsumptionPer100Km(), 0.001);

        settings.setFuelPricePerLiter(-100.0);
        assertEquals(0.0, settings.getFuelPricePerLiter(), 0.001);

        settings.setGpsIntervalMs(100L);
        assertEquals(500L, settings.getGpsIntervalMs());
    }
}
