package com.carfueltracker.app;

import android.view.WindowManager;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.carfueltracker.app.databinding.ActivityMainBinding;
import com.carfueltracker.app.ui.map.MapFragment;
import com.carfueltracker.app.ui.home.HomeFragment;
import com.carfueltracker.app.ui.history.HistoryFragment;
import com.carfueltracker.app.ui.settings.SettingsFragment;
import com.carfueltracker.app.ui.settings.GboSettingsFragment;
import com.carfueltracker.app.ui.statistics.StatisticsFragment;
import com.carfueltracker.app.ui.refuels.RefuelsFragment;
import com.carfueltracker.app.data.model.AppSettings;
import com.carfueltracker.app.data.repository.CarFuelRepository;
import com.carfueltracker.app.data.repository.RepositoryCallback;

/**
 * Main activity of Car Fuel Tracker.
 * Manages the bottom navigation bar and coordinates fragments for:
 * - Home (Dashboard)
 * - Map
 * - History
 * - Statistics
 * - Settings
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private final HomeFragment homeFragment = new HomeFragment();
    private boolean activityResumed;
    private boolean keepScreenOnEnabled = true;
    private long pendingMapTripId = -1L;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupNavigation();

        if (savedInstanceState == null) {
            loadFragment(homeFragment);
        }
    }

    private void setupNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.nav_home) {
                loadFragment(homeFragment);
                return true;
            } else if (itemId == R.id.nav_map) {
                loadFragment(MapFragment.newInstance(pendingMapTripId));
                pendingMapTripId = -1L;
                return true;
            } else if (itemId == R.id.nav_history) {
                loadFragment(new HistoryFragment());
                return true;
            } else if (itemId == R.id.nav_statistics) {
                loadFragment(new StatisticsFragment());
                return true;
            } else if (itemId == R.id.nav_settings) {
                loadFragment(new SettingsFragment());
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().popBackStackImmediate(
                null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    public void showRefuels() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new RefuelsFragment())
                .addToBackStack("refuels")
                .commit();
    }

    public void showGboSettings() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new GboSettingsFragment())
                .addToBackStack("gbo_settings")
                .commit();
    }

    public void showTripOnMap(long tripId) {
        pendingMapTripId = tripId;
        binding.bottomNavigation.setSelectedItemId(R.id.nav_map);
    }

    @Override
    protected void onResume() {
        super.onResume();
        activityResumed = true;
        CarFuelRepository.getInstance(this).getSettings(new RepositoryCallback<AppSettings>() {
            @Override
            public void onSuccess(AppSettings settings) {
                if (settings != null) {
                    keepScreenOnEnabled = settings.isKeepScreenOn();
                    applyKeepScreenOnFlag();
                }
            }

            @Override
            public void onError(Exception exception) {
                android.util.Log.e("MainActivity", "Could not load screen timeout setting", exception);
            }
        });
        applyKeepScreenOnFlag();
    }

    @Override
    protected void onPause() {
        activityResumed = false;
        applyKeepScreenOnFlag();
        super.onPause();
    }

    public void setKeepScreenOnEnabled(boolean enabled) {
        keepScreenOnEnabled = enabled;
        applyKeepScreenOnFlag();
    }

    private void applyKeepScreenOnFlag() {
        if (activityResumed && keepScreenOnEnabled) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }
}
