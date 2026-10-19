package com.carfueltracker.app.data.model;

import androidx.room.Entity;
import androidx.room.ColumnInfo;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Objects;

/**
 * Модель настроек приложения и параметров автомобиля.
 * Хранит нормы расхода, цены, единицы измерения, валюту и флаги интерфейса.
 */
@Entity(tableName = "app_settings")
public class AppSettings implements Serializable {

    public static final String UNIT_DISTANCE_KM = "km";
    public static final String UNIT_DISTANCE_MILES = "mi";

    public static final String UNIT_SPEED_KMH = "km/h";
    public static final String UNIT_SPEED_MPH = "mph";

    public static final String UNIT_FUEL_LITERS = "L";
    public static final String UNIT_FUEL_GALLONS = "gal";

    public static final String FUEL_TYPE_GASOLINE = "GASOLINE";
    public static final String FUEL_TYPE_DIESEL = "DIESEL";
    public static final String FUEL_TYPE_GAS = "GAS";
    public static final String FUEL_TYPE_HYBRID = "HYBRID";
    public static final String FUEL_TYPE_ELECTRIC = "ELECTRIC";
    public static final String GAS_TYPE_LPG = "LPG";
    public static final String GAS_TYPE_CNG = "CNG";

    public static final String DEFAULT_CURRENCY = "֏"; // Армянский драм
    public static final double DEFAULT_CONSUMPTION_L_100KM = 8.5;
    public static final double DEFAULT_FUEL_PRICE_PER_LITER = 500.0; // 500 ֏

    @PrimaryKey
    private int id = 1;
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
    @ColumnInfo(defaultValue = "'GASOLINE'")
    private String vehicleFuelType;
    @ColumnInfo(defaultValue = "'LPG'")
    private String gasType;
    @ColumnInfo(defaultValue = "10.0")
    private double gasConsumptionPer100Km;
    @ColumnInfo(defaultValue = "250.0")
    private double gasPricePerUnit;
    @ColumnInfo(defaultValue = "15.0")
    private double gasConsumptionAdjustmentPercent;
    @ColumnInfo(defaultValue = "18.0")
    private double electricityConsumptionPer100Km;
    @ColumnInfo(defaultValue = "50.0")
    private double electricityPricePerKwh;
    @ColumnInfo(defaultValue = "0")
    private boolean gboEnabled;
    @ColumnInfo(defaultValue = "1")
    private boolean gasolineWarmupFirst;

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
        this.vehicleFuelType = FUEL_TYPE_GASOLINE;
        this.gasType = GAS_TYPE_LPG;
        this.gasConsumptionPer100Km = 10.0;
        this.gasPricePerUnit = 250.0;
        this.gasConsumptionAdjustmentPercent = 15.0;
        this.electricityConsumptionPer100Km = 18.0;
        this.electricityPricePerKwh = 50.0;
        this.gboEnabled = false;
        this.gasolineWarmupFirst = true;
    }

    public static AppSettings createDefault() {
        return new AppSettings();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getVehicleFuelType() {
        return vehicleFuelType;
    }

    public void setVehicleFuelType(String vehicleFuelType) {
        if (FUEL_TYPE_DIESEL.equals(vehicleFuelType) || FUEL_TYPE_GAS.equals(vehicleFuelType)
                || FUEL_TYPE_HYBRID.equals(vehicleFuelType) || FUEL_TYPE_ELECTRIC.equals(vehicleFuelType)
                || FUEL_TYPE_GASOLINE.equals(vehicleFuelType)) {
            this.vehicleFuelType = vehicleFuelType;
        } else {
            this.vehicleFuelType = FUEL_TYPE_GASOLINE;
        }
    }

    public String getGasType() {
        return gasType;
    }

    public void setGasType(String gasType) {
        this.gasType = GAS_TYPE_CNG.equals(gasType) ? GAS_TYPE_CNG : GAS_TYPE_LPG;
    }

    public double getGasConsumptionPer100Km() {
        return gasConsumptionPer100Km;
    }

    public void setGasConsumptionPer100Km(double gasConsumptionPer100Km) {
        this.gasConsumptionPer100Km = Double.isFinite(gasConsumptionPer100Km)
                ? Math.max(0d, gasConsumptionPer100Km) : 0d;
    }

    public double getGasPricePerUnit() {
        return gasPricePerUnit;
    }

    public void setGasPricePerUnit(double gasPricePerUnit) {
        this.gasPricePerUnit = Double.isFinite(gasPricePerUnit)
                ? Math.max(0d, gasPricePerUnit) : 0d;
    }

    public double getGasConsumptionAdjustmentPercent() {
        return gasConsumptionAdjustmentPercent;
    }

    public void setGasConsumptionAdjustmentPercent(double percent) {
        gasConsumptionAdjustmentPercent = Double.isFinite(percent)
                ? Math.max(0d, Math.min(100d, percent)) : 0d;
    }

    public double getElectricityConsumptionPer100Km() {
        return electricityConsumptionPer100Km;
    }

    public void setElectricityConsumptionPer100Km(double consumption) {
        electricityConsumptionPer100Km = Double.isFinite(consumption) ? Math.max(0d, consumption) : 0d;
    }

    public double getElectricityPricePerKwh() {
        return electricityPricePerKwh;
    }

    public void setElectricityPricePerKwh(double price) {
        electricityPricePerKwh = Double.isFinite(price) ? Math.max(0d, price) : 0d;
    }

    public boolean isGboEnabled() {
        return gboEnabled;
    }

    public void setGboEnabled(boolean enabled) {
        gboEnabled = enabled;
    }

    public boolean isGasolineWarmupFirst() {
        return gasolineWarmupFirst;
    }

    public void setGasolineWarmupFirst(boolean gasolineWarmupFirst) {
        this.gasolineWarmupFirst = gasolineWarmupFirst;
    }

    public boolean isValid() {
        boolean gasValid = !gboEnabled && !FUEL_TYPE_GAS.equals(vehicleFuelType)
                && !FUEL_TYPE_HYBRID.equals(vehicleFuelType)
                || Double.isFinite(gasConsumptionPer100Km) && gasConsumptionPer100Km > 0d
                && Double.isFinite(gasPricePerUnit) && gasPricePerUnit >= 0d;
        boolean electricValid = !FUEL_TYPE_ELECTRIC.equals(vehicleFuelType)
                || Double.isFinite(electricityConsumptionPer100Km)
                && electricityConsumptionPer100Km > 0d
                && Double.isFinite(electricityPricePerKwh) && electricityPricePerKwh >= 0d;
        return Double.isFinite(consumptionPer100Km) && consumptionPer100Km > 0 &&
               Double.isFinite(fuelPricePerLiter) && fuelPricePerLiter >= 0 &&
               gasValid && electricValid &&
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
                Double.compare(that.gasConsumptionPer100Km, gasConsumptionPer100Km) == 0 &&
                Double.compare(that.gasPricePerUnit, gasPricePerUnit) == 0 &&
                Double.compare(that.gasConsumptionAdjustmentPercent, gasConsumptionAdjustmentPercent) == 0 &&
                Double.compare(that.electricityConsumptionPer100Km, electricityConsumptionPer100Km) == 0 &&
                Double.compare(that.electricityPricePerKwh, electricityPricePerKwh) == 0 &&
                gboEnabled == that.gboEnabled &&
                gasolineWarmupFirst == that.gasolineWarmupFirst &&
                Objects.equals(distanceUnit, that.distanceUnit) &&
                Objects.equals(speedUnit, that.speedUnit) &&
                Objects.equals(fuelUnit, that.fuelUnit) &&
                Objects.equals(currency, that.currency) &&
                Objects.equals(vehicleFuelType, that.vehicleFuelType) &&
                Objects.equals(gasType, that.gasType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(consumptionPer100Km, fuelPricePerLiter, distanceUnit, speedUnit,
                fuelUnit, currency, darkThemeEnabled, autoStartTracking, keepScreenOn, gpsIntervalMs,
                vehicleFuelType, gasType, gasConsumptionPer100Km, gasPricePerUnit,
                gasConsumptionAdjustmentPercent, electricityConsumptionPer100Km,
                electricityPricePerKwh, gboEnabled, gasolineWarmupFirst);
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
