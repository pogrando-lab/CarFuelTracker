package com.carfueltracker.app.ui.home;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.carfueltracker.app.data.model.AppSettings;
import com.carfueltracker.app.data.model.FuelConsumption;
import com.carfueltracker.app.ui.FuelDisplayFormatter;
import com.carfueltracker.app.data.model.GPSPoint;
import com.carfueltracker.app.data.model.Trip;
import com.carfueltracker.app.data.location.GpsMovementFilter;
import com.carfueltracker.app.data.repository.CarFuelRepository;
import com.carfueltracker.app.data.repository.RepositoryCallback;
import com.carfueltracker.app.R;
import com.carfueltracker.app.databinding.FragmentHomeBinding;
import com.carfueltracker.app.service.TripTrackingService;

import android.content.BroadcastReceiver;

import java.util.Locale;

/**
 * Fragment displaying the main dashboard:
 * GPS status, current trip metrics (time, distance, speed, fuel, cost)
 * and trip start/stop control.
 */
public class HomeFragment extends Fragment {

    private static final float MAX_FINE_LOCATION_ACCURACY_METERS = 50f;
    private static final float MAX_COARSE_LOCATION_ACCURACY_METERS = 250f;

    private FragmentHomeBinding binding;
    private CarFuelRepository repository;
    private Trip activeTrip;
    private AppSettings appSettings;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Location lastLocation;
    private float currentSpeedKmH;
    private ActivityResultLauncher<String[]> locationPermissionLauncher;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private boolean pendingTripStart;
    private final Runnable tripClock = new Runnable() {
        @Override
        public void run() {
            if (activeTrip == null || binding == null) {
                return;
            }
            updateTripTime();
            refreshActiveTrip();
            mainHandler.postDelayed(this, 1000L);
        }
    };
    private boolean trackingReceiverRegistered;
    private final BroadcastReceiver trackingReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (activeTrip != null && intent.getLongExtra(
                    TripTrackingService.EXTRA_TRIP_ID, -1L) == activeTrip.getId()) {
                currentSpeedKmH = intent.getFloatExtra(TripTrackingService.EXTRA_SPEED_KMH, 0f);
                updateTripMetrics();
            }
        }
    };

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        locationPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                permissions -> {
                    boolean granted = hasLocationPermission();
                    if (!granted) {
                        pendingTripStart = false;
                        if (binding != null) {
                            binding.btnTripAction.setEnabled(true);
                            if (activeTrip != null) {
                                setGpsStatus(R.string.status_gps_permission);
                            }
                        }
                        Toast.makeText(requireContext(), R.string.location_permission_required,
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (!hasNotificationPermission()) {
                        pendingTripStart = false;
                        Toast.makeText(requireContext(), R.string.notification_permission_required,
                                Toast.LENGTH_LONG).show();
                        return;
                    }
                    if (pendingTripStart && activeTrip == null) {
                        pendingTripStart = false;
                        startTrip();
                    } else if (activeTrip != null) {
                        ensureTrackingServiceRunning();
                    }
                });
    }

    private void setGpsStatus(int statusText) {
        if (binding != null) {
            binding.tvGpsStatus.setText(statusText);
            binding.tvGpsStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary));
        }
    }

    private void setGpsStatus(int statusText, int colorResource) {
        if (binding != null) {
            binding.tvGpsStatus.setText(statusText);
            binding.tvGpsStatus.setTextColor(ContextCompat.getColor(requireContext(), colorResource));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
                             binding = FragmentHomeBinding.inflate(inflater, container, false);
                             return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        repository = CarFuelRepository.getInstance(requireContext());
        initViews();
        setupListeners();
        binding.btnTripAction.setEnabled(false);
        loadSettings();
        loadActiveTrip();
    }

    private void initViews() {
        updateTripStateUI(false);
    }

    private void setupListeners() {
        binding.btnTripAction.setOnClickListener(v -> {
            binding.btnTripAction.setEnabled(false);
            if (activeTrip == null) {
                if (hasLocationPermission() && hasNotificationPermission()) {
                    startTrip();
                } else {
                    pendingTripStart = true;
                    locationPermissionLauncher.launch(getTrackingPermissions());
                }
            } else {
                stopTrip();
            }
        });
    }

    private void loadSettings() {
        repository.getSettings(new RepositoryCallback<AppSettings>() {
            @Override
            public void onSuccess(AppSettings settings) {
                if (binding == null || !isAdded()) {
                    return;
                }
                appSettings = settings != null ? settings : AppSettings.createDefault();
            }

            @Override
            public void onError(Exception exception) {
                showRepositoryError(exception, false);
            }
        });
    }

    private void loadActiveTrip() {
        repository.getActiveTrip(new RepositoryCallback<Trip>() {
            @Override
            public void onSuccess(Trip trip) {
                if (binding == null || !isAdded()) {
                    return;
                }
                activeTrip = trip;
                updateTripStateUI(activeTrip != null);
                if (activeTrip != null) {
                    startTripClock();
                    if (hasLocationPermission() && hasNotificationPermission()) {
                        ensureTrackingServiceRunning();
                    } else {
                        locationPermissionLauncher.launch(getTrackingPermissions());
                    }
                }
            }

            @Override
            public void onError(Exception exception) {
                showRepositoryError(exception, false);
            }
        });
    }

    private void startTrip() {
        currentSpeedKmH = 0f;
        Context applicationContext = requireContext().getApplicationContext();
        Trip trip = new Trip();
        repository.insertTrip(trip, new RepositoryCallback<Long>() {
            @Override
            public void onSuccess(Long tripId) {
                trip.setId(tripId);
                activeTrip = trip;
                Intent intent = new Intent(applicationContext, TripTrackingService.class)
                        .setAction(TripTrackingService.ACTION_START)
                        .putExtra(TripTrackingService.EXTRA_TRIP_ID, tripId);
                try {
                    ContextCompat.startForegroundService(applicationContext, intent);
                } catch (IllegalStateException | SecurityException exception) {
                    android.util.Log.e("HomeFragment", "Could not start trip tracking service", exception);
                    showRepositoryError(exception);
                }
                if (binding == null || !isAdded()) {
                    return;
                }
                updateTripStateUI(true);
                startTripClock();
            }

            @Override
            public void onError(Exception exception) {
                showRepositoryError(exception);
            }
        });
    }

    private void stopTrip() {
        if (activeTrip == null || binding == null) {
            return;
        }
        binding.btnTripAction.setEnabled(false);
        Intent intent = new Intent(requireContext(), TripTrackingService.class)
                .setAction(TripTrackingService.ACTION_STOP);
        try {
            requireContext().startService(intent);
        } catch (IllegalStateException | SecurityException exception) {
            showRepositoryError(exception);
        }
    }

    private boolean hasLocationPermission() {
        Context context = requireContext();
        return hasFineLocationPermission()
                || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private boolean hasFineLocationPermission() {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private boolean hasNotificationPermission() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
                || ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED;
    }

    private String[] getTrackingPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.POST_NOTIFICATIONS};
        }
        return new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
    }

    private void ensureTrackingServiceRunning() {
        if (activeTrip == null || repository == null) {
            return;
        }
        startTrackingService(activeTrip.getId());
    }

    private void startTrackingService(long tripId) {
        Intent intent = new Intent(requireContext(), TripTrackingService.class)
                .setAction(TripTrackingService.ACTION_START)
                .putExtra(TripTrackingService.EXTRA_TRIP_ID, tripId);
        try {
            ContextCompat.startForegroundService(requireContext(), intent);
        } catch (IllegalStateException | SecurityException exception) {
            showRepositoryError(exception, false);
        }
    }

    private void refreshActiveTrip() {
        if (activeTrip == null || repository == null) {
            return;
        }
        long tripId = activeTrip.getId();
        repository.getTrip(tripId, new RepositoryCallback<Trip>() {
            @Override
            public void onSuccess(Trip trip) {
                if (binding == null || !isAdded() || activeTrip == null
                        || activeTrip.getId() != tripId) {
                    return;
                }
                if (trip == null || !trip.isActive()) {
                    activeTrip = null;
                    currentSpeedKmH = 0f;
                    mainHandler.removeCallbacks(tripClock);
                    updateTripStateUI(false);
                    return;
                }
                activeTrip = trip;
                updateTripMetrics();
            }

            @Override
            public void onError(Exception exception) {
                showRepositoryError(exception, false);
            }
        });
    }

    private void startLocationUpdates() {
        if (activeTrip == null || locationManager == null || !hasLocationPermission()
                || locationListener != null) {
            return;
        }
        try {
            String provider = null;
            if (hasFineLocationPermission() && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                provider = LocationManager.GPS_PROVIDER;
            } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                provider = LocationManager.NETWORK_PROVIDER;
            }
            if (provider == null) {
                setGpsStatus(R.string.status_gps_unavailable);
                Toast.makeText(requireContext(), R.string.location_provider_unavailable,
                        Toast.LENGTH_LONG).show();
                return;
            }

            setGpsStatus(hasFineLocationPermission()
                    ? R.string.status_gps_searching : R.string.status_gps_approximate,
                    R.color.status_amber);
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    recordLocation(location);
                }
            };
            long interval = appSettings != null ? appSettings.getGpsIntervalMs() : 1000L;
            locationManager.requestLocationUpdates(provider, interval, 0f, locationListener, Looper.getMainLooper());
        } catch (SecurityException exception) {
            locationListener = null;
            showRepositoryError(exception, false);
        }
    }

    private void stopLocationUpdates() {
        if (locationManager != null && locationListener != null) {
            try {
                locationManager.removeUpdates(locationListener);
            } catch (SecurityException exception) {
                showRepositoryError(exception, false);
            }
            locationListener = null;
        }
    }

    private void recordLocation(Location location) {
        if (activeTrip == null) {
            return;
        }
        boolean preciseLocation = hasFineLocationPermission();
        float maximumAccuracy = preciseLocation
                ? MAX_FINE_LOCATION_ACCURACY_METERS : MAX_COARSE_LOCATION_ACCURACY_METERS;
        if (location.getAccuracy() > maximumAccuracy) {
            setGpsStatus(R.string.status_gps_low_accuracy, R.color.status_amber);
            return;
        }
        setGpsStatus(preciseLocation
                ? R.string.status_gps_active : R.string.status_gps_approximate,
                preciseLocation ? R.color.status_green : R.color.status_amber);

        float reportedSpeedKmH = location.hasSpeed() ? location.getSpeed() * 3.6f : 0f;
        float speedKmH = reportedSpeedKmH;
        boolean shouldRecordPoint = lastLocation == null;
        boolean movementDetected = false;
        if (lastLocation != null) {
            float segmentMeters = lastLocation.distanceTo(location);
            long segmentMillis = location.getTime() - lastLocation.getTime();
            long requestedInterval = appSettings != null ? appSettings.getGpsIntervalMs() : 1000L;
            long maxSegmentDuration = Math.max(15_000L, requestedInterval * 3L);
            if (GpsMovementFilter.isValidSegmentDuration(segmentMillis, maxSegmentDuration)) {
                speedKmH = GpsMovementFilter.calculateSpeedKmH(
                        segmentMeters, segmentMillis, reportedSpeedKmH, location.hasSpeed());
                movementDetected = GpsMovementFilter.isMovement(segmentMeters,
                        lastLocation.getAccuracy(), location.getAccuracy(), speedKmH);
                if (movementDetected) {
                    activeTrip.setDistanceKm(activeTrip.getDistanceKm() + segmentMeters / 1000d);
                    shouldRecordPoint = true;
                }
                currentSpeedKmH = movementDetected ? speedKmH : 0f;
            } else {
                speedKmH = 0f;
                currentSpeedKmH = 0f;
            }
            if (segmentMillis > 0) {
                lastLocation = new Location(location);
            }
        } else {
            lastLocation = new Location(location);
            currentSpeedKmH = 0f;
        }

        if (movementDetected) {
            activeTrip.setMaxSpeedKmH(Math.max(activeTrip.getMaxSpeedKmH(), currentSpeedKmH));
        }
        long durationMillis = activeTrip.getDurationMillis();
        if (durationMillis > 0) {
            activeTrip.setAvgSpeedKmH(activeTrip.getDistanceKm() * 3600000d / durationMillis);
        }
        AppSettings settings = appSettings != null ? appSettings : AppSettings.createDefault();
        applyFuelEstimate(activeTrip, settings);

        if (shouldRecordPoint) {
            GPSPoint point = new GPSPoint(activeTrip.getId(),
                    location.getLatitude(), location.getLongitude(),
                    location.getAltitude(), currentSpeedKmH,
                    location.getTime(), location.getAccuracy());
            repository.insertGpsPoint(point, new RepositoryCallback<Long>() {
                @Override
                public void onSuccess(Long pointId) {
                }

                @Override
                public void onError(Exception exception) {
                    showRepositoryError(exception, false);
                }
            });
        }
        repository.updateTrip(createTripSnapshot(activeTrip), new RepositoryCallback<Integer>() {
            @Override
            public void onSuccess(Integer updatedRows) {
                if (updatedRows == 0 && activeTrip != null) {
                    showRepositoryError(new IllegalStateException(
                            "Active trip was not found in the database"), false);
                }
            }

            @Override
            public void onError(Exception exception) {
                showRepositoryError(exception, false);
            }
        });
        updateTripMetrics();
    }

    private Trip createTripSnapshot(Trip trip) {
        Trip snapshot = new Trip(trip.getId(), trip.getStartTime(), trip.getEndTime(), trip.getDistanceKm(),
                trip.getAvgSpeedKmH(), trip.getMaxSpeedKmH(), trip.getFuelConsumedLiters(),
                trip.getCost(), trip.getNotes(), trip.isActive());
        snapshot.setPrimaryLiquidFuelAmount(trip.getPrimaryLiquidFuelAmount());
        snapshot.setPrimaryLiquidFuelType(trip.getPrimaryLiquidFuelType());
        snapshot.setGasConsumedAmount(trip.getGasConsumedAmount());
        snapshot.setElectricityConsumedKwh(trip.getElectricityConsumedKwh());
        snapshot.setGasType(trip.getGasType());
        snapshot.setHiddenFromHistory(trip.isHiddenFromHistory());
        return snapshot;
    }

    private void applyFuelEstimate(Trip trip, AppSettings settings) {
        FuelConsumption estimate = FuelConsumption.estimate(trip.getDistanceKm(), settings);
        trip.setPrimaryLiquidFuelAmount(estimate.getPrimaryLiquidFuelAmount());
        trip.setPrimaryLiquidFuelType(estimate.getPrimaryLiquidFuelType());
        trip.setGasConsumedAmount(estimate.getGasAmount());
        trip.setGasType(estimate.getGasType());
        trip.setElectricityConsumedKwh(estimate.getElectricityKwh());
        trip.setFuelConsumedLiters(estimate.getLegacyLiquidVolumeLiters());
        trip.setCost(estimate.getTotalCost());
    }

    private void startTripClock() {
        mainHandler.removeCallbacks(tripClock);
        updateTripTime();
        mainHandler.postDelayed(tripClock, 1000L);
    }

    private void updateTripMetrics() {
        if (binding == null || activeTrip == null) {
            return;
        }
        binding.tvTripNumber.setText(getString(R.string.trip_number_format, activeTrip.getId()));
        binding.tvTripDistance.setText(getString(R.string.distance_format, activeTrip.getDistanceKm()));
        binding.tvCurrentSpeed.setText(getString(R.string.speed_format, currentSpeedKmH));
        binding.tvAvgSpeed.setText(getString(R.string.speed_format, activeTrip.getAvgSpeedKmH()));
        binding.tvMaxSpeed.setText(getString(R.string.speed_format, activeTrip.getMaxSpeedKmH()));
        binding.tvTripFuel.setText(FuelDisplayFormatter.format(requireContext(), activeTrip));
        binding.tvTripCost.setText(getString(R.string.cost_format,
                activeTrip.getCost(), appSettings != null ? appSettings.getCurrency() : AppSettings.DEFAULT_CURRENCY,
                activeTrip.getDistanceKm() > 0d
                        ? activeTrip.getCost() / activeTrip.getDistanceKm() : 0d));
    }

    private void updateTripTime() {
        if (binding == null || activeTrip == null) {
            return;
        }
        long totalSeconds = activeTrip.getDurationMillis() / 1000L;
        long hours = totalSeconds / 3600L;
        long minutes = (totalSeconds % 3600L) / 60L;
        long seconds = totalSeconds % 60L;
        binding.tvTripTime.setText(String.format(Locale.getDefault(), "%02d:%02d:%02d",
                hours, minutes, seconds));
    }

    @Override
    public void onStart() {
        super.onStart();
        ContextCompat.registerReceiver(requireContext(), trackingReceiver,
                new IntentFilter(TripTrackingService.ACTION_TRIP_UPDATED),
                ContextCompat.RECEIVER_NOT_EXPORTED);
        trackingReceiverRegistered = true;
        if (repository != null) {
            loadSettings();
        }
        if (activeTrip != null && hasLocationPermission() && hasNotificationPermission()) {
            ensureTrackingServiceRunning();
            startTripClock();
        }
    }

    @Override
    public void onStop() {
        if (trackingReceiverRegistered) {
            requireContext().unregisterReceiver(trackingReceiver);
            trackingReceiverRegistered = false;
        }
        super.onStop();
    }

    private void showRepositoryError(Exception exception) {
        showRepositoryError(exception, true);
    }

    private void showRepositoryError(Exception exception, boolean enableAction) {
        if (!isAdded()) {
            return;
        }
        if (binding != null && enableAction) {
            binding.btnTripAction.setEnabled(true);
        }
        String message = exception.getLocalizedMessage();
        Toast.makeText(requireContext(), message != null ? message : exception.toString(),
                Toast.LENGTH_LONG).show();
    }

    /**
     * Updates UI elements based on whether a trip is active.
     */
    public void updateTripStateUI(boolean active) {
        if (!isAdded() || binding == null) {
            return;
        }

        if (active) {
            if (!hasLocationPermission()) {
                setGpsStatus(R.string.status_gps_permission);
            } else if (hasFineLocationPermission()) {
                setGpsStatus(R.string.status_gps_searching, R.color.status_amber);
            } else {
                setGpsStatus(R.string.status_gps_approximate, R.color.status_amber);
            }

            binding.tvTripNumber.setText(getString(R.string.trip_number_placeholder));
            binding.btnTripAction.setText(R.string.btn_stop_trip);
            binding.btnTripAction.setBackgroundTintList(
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.status_red))
            );
            updateTripMetrics();
            startTripClock();
        } else {
            binding.tvGpsStatus.setText(R.string.status_gps_idle);
            binding.tvGpsStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary));

            binding.btnTripAction.setText(R.string.btn_start_trip);
            binding.btnTripAction.setBackgroundTintList(
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.primary))
            );
            binding.tvTripNumber.setText(R.string.trip_number_placeholder);
            binding.tvTripTime.setText(R.string.time_default);
            binding.tvTripDistance.setText(R.string.distance_default);
            binding.tvCurrentSpeed.setText(R.string.speed_default);
            binding.tvAvgSpeed.setText(R.string.avg_speed_default);
            binding.tvMaxSpeed.setText(R.string.max_speed_default);
            binding.tvTripFuel.setText(R.string.fuel_default);
            binding.tvTripCost.setText(R.string.cost_default);
        }
        binding.btnTripAction.setEnabled(true);
    }

    @Override
    public void onDestroyView() {
        mainHandler.removeCallbacks(tripClock);
        super.onDestroyView();
        binding = null;
    }
}
