package com.techLabs.nbpdcl.view.survey.device;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.techLabs.nbpdcl.R;
import com.techLabs.nbpdcl.Utils.PrefManager;
import com.techLabs.nbpdcl.databinding.FragmentSourceNetoworkEditBinding;
import com.techLabs.nbpdcl.models.survey.ConsumerResponse;
import com.techLabs.nbpdcl.retrofit.ApiInterface;
import com.techLabs.nbpdcl.retrofit.RetrofitClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SourceNetworkEditFragment extends Fragment {
    private FragmentSourceNetoworkEditBinding binding;
    private final List<String> networkIds = new ArrayList<>();
    private final List<NetworkItem> networkItems = new ArrayList<>();

    private PrefManager prefManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSourceNetoworkEditBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefManager = new PrefManager(requireContext());
        setupCheckboxListener();
        fetchNetworkData();

        binding.networkName.setOnClickListener(v -> binding.networkName.showDropDown());

        binding.networkName.setOnItemClickListener((parent, v, position, id) -> {
            NetworkItem item = networkItems.get(position);
            binding.group1.setText(item.group1);
            binding.group2.setText(item.group2);
            binding.group3.setText(item.group3);
            binding.group4.setText(item.group4);
            binding.group5.setText(item.group5);
            binding.group6.setText(item.group6);
        });

    }

    private void fetchNetworkData() {

        Retrofit retrofit = RetrofitClient.getClient(requireContext());
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        Call<ConsumerResponse> call = apiInterface.getNetworkName("Bearer " + prefManager.getAccessToken());

        call.enqueue(new Callback<ConsumerResponse>() {
            @Override
            public void onResponse(@NonNull Call<ConsumerResponse> call, @NonNull Response<ConsumerResponse> response) {

                if (response.isSuccessful() && response.body() != null) {

                    ConsumerResponse.Output output = response.body().getOutput();

                    if (output.getNetworkid() != null && !output.getNetworkid().isEmpty()) {

                        networkItems.clear();
                        networkIds.clear();

                        for (int i = 0; i < output.getNetworkid().size(); i++) {

                            NetworkItem item = new NetworkItem();
                            item.networkId = output.getNetworkid().get(i);
                            item.group1 = output.getGroup1().get(i);
                            item.group2 = output.getGroup2().get(i);
                            item.group3 = output.getGroup3().get(i);
                            item.group4 = output.getGroup4().get(i);
                            item.group5 = output.getGroup5().get(i);
                            item.group6 = output.getGroup6().get(i);

                            networkItems.add(item);
                            networkIds.add(item.networkId);
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.custom_spinner, networkIds);
                        binding.networkName.setAdapter(adapter);
                    }
                }

            }

            @Override
            public void onFailure(@NonNull Call<ConsumerResponse> call,
                                  @NonNull Throwable t) {
                Snackbar.make(requireView(), "Failed to load network data", Snackbar.LENGTH_LONG).show();
            }
        });
    }


    public JSONObject getData() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("networkName", binding.networkName.getText().toString());
            jsonObject.put("networkType", binding.networkTypeSpinner.getSelectedItem().toString());
            jsonObject.put("ambientTemperature", binding.ambTemp.getText().toString());
            jsonObject.put("group1", binding.group1.getText().toString());
            jsonObject.put("group2", binding.group2.getText().toString());
            jsonObject.put("group3", binding.group3.getText().toString());
            jsonObject.put("groupFour", binding.group4.getText().toString());
            jsonObject.put("groupFive", binding.group5.getText().toString());
            jsonObject.put("groupSix", binding.group6.getText().toString());

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        return jsonObject;
    }

    public boolean checkAllFields() {
        boolean isValid = true;
        if (TextUtils.isEmpty(binding.networkName.getText())) {
            binding.networkName.setError("Network Name is required");
            binding.networkName.requestFocus();
            binding.networkName.post(() -> binding.networkName.requestFocus());
            isValid = false;
        }

        if (TextUtils.isEmpty(binding.group1.getText())) {
            binding.group1.setError("Area is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(binding.group2.getText())) {
            binding.group2.setError("Voltage Level is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(binding.group3.getText())) {
            binding.group3.setError("Region is required");
            isValid = false;
        }

        return isValid;
    }

    @SuppressLint("SetTextI18n")
    private void setupCheckboxListener() {
        binding.checkEdit.setOnCheckedChangeListener((buttonView, isChecked) -> {
            binding.ambTemp.setEnabled(isChecked);

            if (!isChecked && !TextUtils.isEmpty(binding.ambTemp.getText())) {
                binding.ambTemp.setText("77");
            }
        });

        binding.ambTemp.setEnabled(binding.checkEdit.isChecked());
    }

    @Override
    public void onResume() {
        super.onResume();
        requireView().requestLayout();
    }

    private static class NetworkItem {
        String networkId;
        String group1;
        String group2;
        String group3;
        String group4;
        String group5;
        String group6;
    }

}
