package com.carfueltracker.app.data.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Модель настроек приложения и параметров автомобиля.
 * Хранит нормы расхода, цены, единицы измерения, валюту и флаги интерфейса.
 */
public class AppSettings implements Serializable {

    public static final String UNIT_DISTANCE_KM = "km";
    public static final String UNIT_DISTANCE_MILES = "mi";

    public static final String UNIT_SPEED_KMH = "km/h";
    public static final String UNIT_SPEED_MPH = "mph";

    public static final String UNIT_FUEL_LITERS = "L";
    public static final String UNIT_FUEL_GALLONS = "gal";

    public static final String DEFAULT_CURRENCY = "֏"; // Армянский драм
    public static final double DEFAULT_CONSUMPTION_L_100KM = 8.5;
    public static final double DEFAULT_FUEL_PRICE_PER_LITER = 500.0; // 500 ֏

    private double consumptionPer100Km;
    private double fuelPricePerLiter;
    private String distanceUnit;
    private String speedUnit;
    private String fuelUnit;
    private String currency;
    private boolean darkThemeEnabled;
    private boolean autoStartTracking;
    private boolean keepScreenOn;
    private long gpsIntervalMs;

    public AppSettings() {
        this.consumptionPer100Km = DEFAULT_CONSUMPTION_L_100KM;
        this.fuelPricePerLiter = DEFAULT_FUEL_PRICE_PER_LITER;
        this.distanceUnit = UNIT_DISTANCE_KM;
        this.speedUnit = UNIT_SPEED_KMH;
        this.fuelUnit = UNIT_FUEL_LITERS;
        this.currency = DEFAULT_CURRENCY;
        this.darkThemeEnabled = true;
        this.autoStartTracking = false;
        this.keepScreenOn = true;
        this.gpsIntervalMs = 1000L;
    }

    public static AppSettings createDefault() {
        return new AppSettings();
    }

    public double getConsumptionPer100Km() {
        return consumptionPer100Km;
    }

    public void setConsumptionPer100Km(double consumptionPer100Km) {
        this.consumptionPer100Km = Math.max(0.1, consumptionPer100Km);
    }

    public double getFuelPricePerLiter() {
        return fuelPricePerLiter;
    }

    public void setFuelPricePerLiter(double fuelPricePerLiter) {
        this.fuelPricePerLiter = Math.max(0, fuelPricePerLiter);
    }

    public String getDistanceUnit() {
        return distanceUnit;
    }

    public void setDistanceUnit(String distanceUnit) {
        this.distanceUnit = distanceUnit != null ? distanceUnit : UNIT_DISTANCE_KM;
    }

    public String getSpeedUnit() {
        return speedUnit;
    }

    public void setSpeedUnit(String speedUnit) {
        this.speedUnit = speedUnit != null ? speedUnit : UNIT_SPEED_KMH;
    }

    public String getFuelUnit() {
        return fuelUnit;
    }

    public void setFuelUnit(String fuelUnit) {
        this.fuelUnit = fuelUnit != null ? fuelUnit : UNIT_FUEL_LITERS;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency != null ? currency : DEFAULT_CURRENCY;
    }

    public boolean isDarkThemeEnabled() {
        return darkThemeEnabled;
    }

    public void setDarkThemeEnabled(boolean darkThemeEnabled) {
        this.darkThemeEnabled = darkThemeEnabled;
    }

    public boolean isAutoStartTracking() {
        return autoStartTracking;
    }

    public void setAutoStartTracking(boolean autoStartTracking) {
        this.autoStartTracking = autoStartTracking;
    }

    public boolean isKeepScreenOn() {
        return keepScreenOn;
    }

    public void setKeepScreenOn(boolean keepScreenOn) {
        this.keepScreenOn = keepScreenOn;
    }

    public long getGpsIntervalMs() {
        return gpsIntervalMs;
    }

    public void setGpsIntervalMs(long gpsIntervalMs) {
        this.gpsIntervalMs = Math.max(500L, gpsIntervalMs);
    }

    public boolean isValid() {
        return consumptionPer100Km > 0 &&
               fuelPricePerLiter >= 0 &&
               gpsIntervalMs >= 500 &&
               currency != null && !currency.trim().isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppSettings that = (AppSettings) o;
        return Double.compare(that.consumptionPer100Km, consumptionPer100Km) == 0 &&
                Double.compare(that.fuelPricePerLiter, fuelPricePerLiter) == 0 &&
                darkThemeEnabled == that.darkThemeEnabled &&
                autoStartTracking == that.autoStartTracking &&
                keepScreenOn == that.keepScreenOn &&
                gpsIntervalMs == that.gpsIntervalMs &&
                Objects.equals(distanceUnit, that.distanceUnit) &&
                Objects.equals(speedUnit, that.speedUnit) &&
                Objects.equals(fuelUnit, that.fuelUnit) &&
                Objects.equals(currency, that.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(consumptionPer100Km, fuelPricePerLiter, distanceUnit, speedUnit,
                fuelUnit, currency, darkThemeEnabled, autoStartTracking, keepScreenOn, gpsIntervalMs);
    }

    @Override
    public String toString() {
        return "AppSettings{" +
                "consumptionPer100Km=" + consumptionPer100Km +
                ", fuelPricePerLiter=" + fuelPricePerLiter +
                ", distanceUnit='" + distanceUnit + '\'' +
                ", speedUnit='" + speedUnit + '\'' +
                ", fuelUnit='" + fuelUnit + '\'' +
                ", currency='" + currency + '\'' +
                ", darkThemeEnabled=" + darkThemeEnabled +
                ", autoStartTracking=" + autoStartTracking +
                ", keepScreenOn=" + keepScreenOn +
                ", gpsIntervalMs=" + gpsIntervalMs +
                '}';
    }
}
