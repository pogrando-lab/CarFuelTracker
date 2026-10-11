package com.carfueltracker.app.ui.home;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.carfueltracker.app.R;
import com.carfueltracker.app.databinding.FragmentHomeBinding;

/**
 * Fragment displaying the main dashboard:
 * GPS status, current trip metrics (time, distance, speed, fuel, cost)
 * and trip start/stop control.
 */
public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private boolean isTripRunning = false;

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

        initViews();
        setupListeners();
    }

    private void initViews() {
        updateTripStateUI(false);
    }

    private void setupListeners() {
        binding.btnTripAction.setOnClickListener(v -> {
            isTripRunning = !isTripRunning;
            updateTripStateUI(isTripRunning);
        });
    }

    /**
     * Updates UI elements based on whether a trip is active.
     */
    public void updateTripStateUI(boolean active) {
        if (!isAdded() || binding == null) {
            return;
        }

        if (active) {
            binding.tvGpsStatus.setText(R.string.status_gps_active);
            binding.tvGpsStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.status_green));

            binding.tvTripNumber.setText(getString(R.string.trip_number_placeholder));
            binding.btnTripAction.setText(R.string.btn_stop_trip);
            binding.btnTripAction.setBackgroundTintList(
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.status_red))
            );
        } else {
            binding.tvGpsStatus.setText(R.string.status_gps_idle);
            binding.tvGpsStatus.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_secondary));

            binding.btnTripAction.setText(R.string.btn_start_trip);
            binding.btnTripAction.setBackgroundTintList(
                    ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.primary))
            );
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
