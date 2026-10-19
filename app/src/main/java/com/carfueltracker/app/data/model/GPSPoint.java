package com.carfueltracker.app.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.Objects;

/**
 * Модель точки геолокации GPS.
 * Хранит координаты, высоту, скорость в точке, время и точность.
 */
@Entity(tableName = "gps_points",
        foreignKeys = @ForeignKey(
                entity = Trip.class,
                parentColumns = "id",
                childColumns = "tripId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("tripId")})
public class GPSPoint implements Serializable {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private long tripId;
    private double latitude;
    private double longitude;
    private double altitude;
    private float speedKmH;
    private long timestamp;
    private float accuracy;

    public GPSPoint() {
        this.timestamp = System.currentTimeMillis();
    }

    @Ignore
    public GPSPoint(long id, long tripId, double latitude, double longitude,
                    double altitude, float speedKmH, long timestamp, float accuracy) {
        this.id = id;
        this.tripId = tripId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
        this.speedKmH = Math.max(0, speedKmH);
        this.timestamp = timestamp;
        this.accuracy = Math.max(0, accuracy);
    }

    @Ignore
    public GPSPoint(long tripId, double latitude, double longitude,
                    double altitude, float speedKmH, long timestamp, float accuracy) {
        this(0, tripId, latitude, longitude, altitude, speedKmH, timestamp, accuracy);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTripId() {
        return tripId;
    }

    public void setTripId(long tripId) {
        this.tripId = tripId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public float getSpeedKmH() {
        return speedKmH;
    }

    public void setSpeedKmH(float speedKmH) {
        this.speedKmH = Math.max(0, speedKmH);
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = Math.max(0, accuracy);
    }

    /**
     * Проверка валидности географических координат.
     */
    public boolean isValidCoordinates() {
        return latitude >= -90.0 && latitude <= 90.0 &&
               longitude >= -180.0 && longitude <= 180.0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GPSPoint gpsPoint = (GPSPoint) o;
        return id == gpsPoint.id &&
                tripId == gpsPoint.tripId &&
                Double.compare(gpsPoint.latitude, latitude) == 0 &&
                Double.compare(gpsPoint.longitude, longitude) == 0 &&
                Double.compare(gpsPoint.altitude, altitude) == 0 &&
                Float.compare(gpsPoint.speedKmH, speedKmH) == 0 &&
                timestamp == gpsPoint.timestamp &&
                Float.compare(gpsPoint.accuracy, accuracy) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, tripId, latitude, longitude, altitude, speedKmH, timestamp, accuracy);
    }

    @Override
    public String toString() {
        return "GPSPoint{" +
                "id=" + id +
                ", tripId=" + tripId +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", altitude=" + altitude +
                ", speedKmH=" + speedKmH +
                ", timestamp=" + timestamp +
                ", accuracy=" + accuracy +
                '}';
    }
}
