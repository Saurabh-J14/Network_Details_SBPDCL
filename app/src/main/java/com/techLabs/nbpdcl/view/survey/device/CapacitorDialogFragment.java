package com.techLabs.nbpdcl.view.survey.device;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.techLabs.nbpdcl.R;
import com.techLabs.nbpdcl.Utils.Args;
import com.techLabs.nbpdcl.databinding.FragmentCapacitorDialogBinding;

public class CapacitorDialogFragment extends Fragment {

    private FragmentCapacitorDialogBinding binding;
    private final String[] locations = {"At FromNode", "At ToNode"};
    private final String[] status = {"Connected", "DisConnected"};
    private final String networkId;
    private final int location;

    public CapacitorDialogFragment(String networkId, int location) {
        this.networkId = networkId;
        this.location = location;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCapacitorDialogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayAdapter<String> statusAdapters = new ArrayAdapter<>(requireActivity(), R.layout.custom_spinner, status);
        binding.statusSpinnersBar.setAdapter(statusAdapters);

        ArrayAdapter<String> locationAdapters = new ArrayAdapter<>(requireActivity(), R.layout.custom_spinner, locations);
        binding.locationSpinnersBar.setAdapter(locationAdapters);

        if (location != 0) {
            if (location == 1) {
                binding.locationSpinnersBar.setSelection(0);
            } else {
                binding.locationSpinnersBar.setSelection(1);
            }
        }

        Args.getIsCapacitorValidate().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    checkDetails();
                }
            }
        });

        binding.equipIdSpinnerBar.setOnClickListener(v -> {
            binding.equipIdSpinnerBar.showDropDown();
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(binding.equipIdSpinnerBar, InputMethodManager.SHOW_IMPLICIT);
        });

        binding.equipIdSpinnerBar.post(() -> {
            int height = binding.equipIdSpinnerBar.getHeight();
            binding.equipIdSpinnerBar.setDropDownVerticalOffset(-height); // Try to make it appear above
            binding.equipIdSpinnerBar.showDropDown();
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        requireView().requestLayout();
    }

    private void checkDetails() {
        if (binding != null && binding.deviceNumbersEdt.getText() != null && binding.locationSpinnersBar.getSelectedItem() != null && binding.statusSpinnersBar.getSelectedItem() != null && binding.equipIdSpinnerBar.getText() != null) {
            Bundle bundle = new Bundle();
            bundle.putString("DeviceNumber", binding.deviceNumbersEdt.getText().toString().trim());
            bundle.putString("DeviceType", "17");
            if (binding.locationSpinnersBar.getSelectedItem().equals("At ToNode")) {
                bundle.putString("Location", "2");
            } else {
                bundle.putString("Location", "1");
            }

            if (binding.statusSpinnersBar.getSelectedItem().equals("Connected")) {
                bundle.putString("Status", "0");
            } else {
                bundle.putString("Status", "1");
            }

            bundle.putString("EquipmentID", binding.equipIdSpinnerBar.getText().toString());
            Args.setCapacitorParameter(bundle);
        }
    }
}