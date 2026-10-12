package com.carfueltracker.app.data.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Модель записи о заправке автомобиля.
 * Хранит дату, объём топлива в литрах, цену за литр, общую стоимость,
 * пробег на одометре, АЗС и примечания.
 */
public class Refuel implements Serializable {

    private long id;
    private long timestamp;
    private double liters;
    private double pricePerLiter;
    private double totalCost;
    private double mileageKm;
    private String fuelStation;
    private String fuelType;
    private String notes;

    public Refuel() {
        this.timestamp = System.currentTimeMillis();
        this.fuelStation = "";
        this.fuelType = "АИ-95";
        this.notes = "";
    }

    public Refuel(long id, long timestamp, double liters, double pricePerLiter,
                  double totalCost, double mileageKm, String fuelStation,
                  String fuelType, String notes) {
        this.id = id;
        this.timestamp = timestamp;
        this.liters = Math.max(0, liters);
        this.pricePerLiter = Math.max(0, pricePerLiter);
        this.totalCost = totalCost > 0 ? totalCost : (this.liters * this.pricePerLiter);
        this.mileageKm = Math.max(0, mileageKm);
        this.fuelStation = fuelStation != null ? fuelStation : "";
        this.fuelType = fuelType != null ? fuelType : "АИ-95";
        this.notes = notes != null ? notes : "";
    }

    public Refuel(long timestamp, double liters, double pricePerLiter,
                  double mileageKm, String fuelStation, String fuelType, String notes) {
        this(0, timestamp, liters, pricePerLiter, liters * pricePerLiter, mileageKm, fuelStation, fuelType, notes);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public double getLiters() {
        return liters;
    }

    public void setLiters(double liters) {
        this.liters = Math.max(0, liters);
        recalculateTotalCost();
    }

    public double getPricePerLiter() {
        return pricePerLiter;
    }

    public void setPricePerLiter(double pricePerLiter) {
        this.pricePerLiter = Math.max(0, pricePerLiter);
        recalculateTotalCost();
    }

    public double getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(double totalCost) {
        this.totalCost = Math.max(0, totalCost);
    }

    public void recalculateTotalCost() {
        if (liters > 0 && pricePerLiter > 0) {
            this.totalCost = Math.round(liters * pricePerLiter * 100.0) / 100.0;
        }
    }

    public double getMileageKm() {
        return mileageKm;
    }

    public void setMileageKm(double mileageKm) {
        this.mileageKm = Math.max(0, mileageKm);
    }

    public String getFuelStation() {
        return fuelStation;
    }

    public void setFuelStation(String fuelStation) {
        this.fuelStation = fuelStation != null ? fuelStation : "";
    }

    public String getFuelType() {
        return fuelType;
    }

    public void setFuelType(String fuelType) {
        this.fuelType = fuelType != null ? fuelType : "АИ-95";
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes != null ? notes : "";
    }

    /**
     * Валидация корректности записи о заправке.
     */
    public boolean isValid() {
        return timestamp > 0 && liters > 0 && pricePerLiter >= 0 && totalCost >= 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Refuel refuel = (Refuel) o;
        return id == refuel.id &&
                timestamp == refuel.timestamp &&
                Double.compare(refuel.liters, liters) == 0 &&
                Double.compare(refuel.pricePerLiter, pricePerLiter) == 0 &&
                Double.compare(refuel.totalCost, totalCost) == 0 &&
                Double.compare(refuel.mileageKm, mileageKm) == 0 &&
                Objects.equals(fuelStation, refuel.fuelStation) &&
                Objects.equals(fuelType, refuel.fuelType) &&
                Objects.equals(notes, refuel.notes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, timestamp, liters, pricePerLiter, totalCost, mileageKm, fuelStation, fuelType, notes);
    }

    @Override
    public String toString() {
        return "Refuel{" +
                "id=" + id +
                ", timestamp=" + timestamp +
                ", liters=" + liters +
                ", pricePerLiter=" + pricePerLiter +
                ", totalCost=" + totalCost +
                ", mileageKm=" + mileageKm +
                ", fuelStation='" + fuelStation + '\'' +
                ", fuelType='" + fuelType + '\'' +
                ", notes='" + notes + '\'' +
                '}';
    }
}
