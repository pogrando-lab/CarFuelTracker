package com.carfueltracker.app.data.model;

import androidx.room.Entity;
import androidx.room.ColumnInfo;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Objects;

/**
 * Модель данных автомобильной поездки.
 * Хранит основные показатели: время, дистанцию, скорости, расход и стоимость.
 */
@Entity(tableName = "trips")
public class Trip implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private long startTime;
    private long endTime;
    private double distanceKm;
    private double avgSpeedKmH;
    private double maxSpeedKmH;
    private double fuelConsumedLiters;
    private double cost;
    @ColumnInfo(defaultValue = "0")
    private double primaryLiquidFuelAmount;
    @ColumnInfo(defaultValue = "0")
    private double gasConsumedAmount;
    @ColumnInfo(defaultValue = "0")
    private double electricityConsumedKwh;
    @ColumnInfo(defaultValue = "'LPG'")
    private String gasType;
    @ColumnInfo(defaultValue = "'GASOLINE'")
    private String primaryLiquidFuelType;
    private String notes;
    private boolean isActive;
    @ColumnInfo(defaultValue = "0")
    private boolean hiddenFromHistory;

    public Trip() {
        this.startTime = System.currentTimeMillis();
        this.isActive = true;
        this.notes = "";
        this.gasType = AppSettings.GAS_TYPE_LPG;
    }

    @Ignore
    public Trip(long id, long startTime, long endTime, double distanceKm,
                double avgSpeedKmH, double maxSpeedKmH, double fuelConsumedLiters,
                double cost, String notes, boolean isActive) {
        this.id = id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.distanceKm = Math.max(0, distanceKm);
        this.avgSpeedKmH = Math.max(0, avgSpeedKmH);
        this.maxSpeedKmH = Math.max(0, maxSpeedKmH);
        this.fuelConsumedLiters = Math.max(0, fuelConsumedLiters);
        this.cost = Math.max(0, cost);
        this.primaryLiquidFuelAmount = 0d;
        this.gasConsumedAmount = 0d;
        this.electricityConsumedKwh = 0d;
        this.gasType = AppSettings.GAS_TYPE_LPG;
        this.primaryLiquidFuelType = AppSettings.FUEL_TYPE_GASOLINE;
        this.notes = notes != null ? notes : "";
        this.isActive = isActive;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public void setDistanceKm(double distanceKm) {
        this.distanceKm = Math.max(0, distanceKm);
    }

    public double getAvgSpeedKmH() {
        return avgSpeedKmH;
    }

    public void setAvgSpeedKmH(double avgSpeedKmH) {
        this.avgSpeedKmH = Math.max(0, avgSpeedKmH);
    }

    public double getMaxSpeedKmH() {
        return maxSpeedKmH;
    }

    public void setMaxSpeedKmH(double maxSpeedKmH) {
        this.maxSpeedKmH = Math.max(0, maxSpeedKmH);
    }

    public double getFuelConsumedLiters() {
        return fuelConsumedLiters;
    }

    public void setFuelConsumedLiters(double fuelConsumedLiters) {
        this.fuelConsumedLiters = Math.max(0, fuelConsumedLiters);
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = Math.max(0, cost);
    }

    public double getPrimaryLiquidFuelAmount() {
        return primaryLiquidFuelAmount;
    }

    public void setPrimaryLiquidFuelAmount(double amount) {
        primaryLiquidFuelAmount = Math.max(0d, amount);
    }

    public String getPrimaryLiquidFuelType() {
        return primaryLiquidFuelType;
    }

    public void setPrimaryLiquidFuelType(String type) {
        primaryLiquidFuelType = AppSettings.FUEL_TYPE_DIESEL.equals(type)
                ? AppSettings.FUEL_TYPE_DIESEL : AppSettings.FUEL_TYPE_GASOLINE;
    }

    public double getGasConsumedAmount() {
        return gasConsumedAmount;
    }

    public void setGasConsumedAmount(double amount) {
        gasConsumedAmount = Math.max(0d, amount);
    }

    public double getElectricityConsumedKwh() {
        return electricityConsumedKwh;
    }

    public void setElectricityConsumedKwh(double amount) {
        electricityConsumedKwh = Math.max(0d, amount);
    }

    public String getGasType() {
        return gasType;
    }

    public void setGasType(String gasType) {
        this.gasType = AppSettings.GAS_TYPE_CNG.equals(gasType)
                ? AppSettings.GAS_TYPE_CNG : AppSettings.GAS_TYPE_LPG;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes != null ? notes : "";
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public boolean isHiddenFromHistory() {
        return hiddenFromHistory;
    }

    public void setHiddenFromHistory(boolean hiddenFromHistory) {
        this.hiddenFromHistory = hiddenFromHistory;
    }

    /**
     * Возвращает продолжительность поездки в миллисекундах.
     */
    public long getDurationMillis() {
        if (isActive || endTime <= startTime) {
            return Math.max(0, System.currentTimeMillis() - startTime);
        }
        return Math.max(0, endTime - startTime);
    }

    /**
     * Валидация корректности данных поездки.
     */
    public boolean isValid() {
        return startTime > 0 && distanceKm >= 0 && avgSpeedKmH >= 0 && maxSpeedKmH >= 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Trip trip = (Trip) o;
        return id == trip.id &&
                startTime == trip.startTime &&
                endTime == trip.endTime &&
                Double.compare(trip.distanceKm, distanceKm) == 0 &&
                Double.compare(trip.avgSpeedKmH, avgSpeedKmH) == 0 &&
                Double.compare(trip.maxSpeedKmH, maxSpeedKmH) == 0 &&
                Double.compare(trip.fuelConsumedLiters, fuelConsumedLiters) == 0 &&
                Double.compare(trip.cost, cost) == 0 &&
                Double.compare(trip.primaryLiquidFuelAmount, primaryLiquidFuelAmount) == 0 &&
                Double.compare(trip.gasConsumedAmount, gasConsumedAmount) == 0 &&
                Double.compare(trip.electricityConsumedKwh, electricityConsumedKwh) == 0 &&
                isActive == trip.isActive &&
                hiddenFromHistory == trip.hiddenFromHistory &&
                Objects.equals(notes, trip.notes) &&
                Objects.equals(gasType, trip.gasType) &&
                Objects.equals(primaryLiquidFuelType, trip.primaryLiquidFuelType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, startTime, endTime, distanceKm, avgSpeedKmH, maxSpeedKmH,
                fuelConsumedLiters, cost, primaryLiquidFuelAmount, gasConsumedAmount,
                electricityConsumedKwh, gasType, primaryLiquidFuelType, notes, isActive, hiddenFromHistory);
    }

    @Override
    public String toString() {
        return "Trip{" +
                "id=" + id +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", distanceKm=" + distanceKm +
                ", avgSpeedKmH=" + avgSpeedKmH +
                ", maxSpeedKmH=" + maxSpeedKmH +
                ", fuelConsumedLiters=" + fuelConsumedLiters +
                ", cost=" + cost +
                ", notes='" + notes + '\'' +
                ", isActive=" + isActive +
                '}';
    }
}
