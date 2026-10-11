package com.carfueltracker.app.ui.placeholder;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.carfueltracker.app.databinding.FragmentPlaceholderBinding;

/**
 * Placeholder fragment for tabs that will be fully implemented in subsequent stages.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_MESSAGE = "arg_message";
    private FragmentPlaceholderBinding binding;

    public static PlaceholderFragment newInstance(String message) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentPlaceholderBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            String message = getArguments().getString(ARG_MESSAGE, "");
            binding.tvPlaceholderMessage.setText(message);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
