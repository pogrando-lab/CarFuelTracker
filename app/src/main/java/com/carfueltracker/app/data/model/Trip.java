package com.carfueltracker.app.data.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Модель данных автомобильной поездки.
 * Хранит основные показатели: время, дистанцию, скорости, расход и стоимость.
 */
public class Trip implements Serializable {

    private long id;
    private long startTime;
    private long endTime;
    private double distanceKm;
    private double avgSpeedKmH;
    private double maxSpeedKmH;
    private double fuelConsumedLiters;
    private double cost;
    private String notes;
    private boolean isActive;

    public Trip() {
        this.startTime = System.currentTimeMillis();
        this.isActive = true;
        this.notes = "";
    }

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
                isActive == trip.isActive &&
                Objects.equals(notes, trip.notes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, startTime, endTime, distanceKm, avgSpeedKmH, maxSpeedKmH,
                fuelConsumedLiters, cost, notes, isActive);
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
