package com.carfueltracker.app;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.carfueltracker.app.databinding.ActivityMainBinding;
import com.carfueltracker.app.ui.home.HomeFragment;
import com.carfueltracker.app.ui.placeholder.PlaceholderFragment;

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
                loadFragment(PlaceholderFragment.newInstance(getString(R.string.tab_map_placeholder)));
                return true;
            } else if (itemId == R.id.nav_history) {
                loadFragment(PlaceholderFragment.newInstance(getString(R.string.tab_history_placeholder)));
                return true;
            } else if (itemId == R.id.nav_statistics) {
                loadFragment(PlaceholderFragment.newInstance(getString(R.string.tab_statistics_placeholder)));
                return true;
            } else if (itemId == R.id.nav_settings) {
                loadFragment(PlaceholderFragment.newInstance(getString(R.string.tab_settings_placeholder)));
                return true;
            }
            return false;
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }
}
