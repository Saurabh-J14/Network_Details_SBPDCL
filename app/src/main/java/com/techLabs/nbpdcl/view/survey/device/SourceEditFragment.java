package com.techLabs.nbpdcl.view.survey.device;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.techLabs.nbpdcl.R;
import com.techLabs.nbpdcl.Utils.PrefManager;
import com.techLabs.nbpdcl.Utils.ResponseDataUtils;
import com.techLabs.nbpdcl.databinding.FragmentSourceEditBinding;
import com.techLabs.nbpdcl.models.DeviceIdModel;
import com.techLabs.nbpdcl.models.VoltageModel;
import com.techLabs.nbpdcl.retrofit.ApiInterface;
import com.techLabs.nbpdcl.retrofit.RetrofitClient;
import com.techLabs.nbpdcl.view.activity.LoginActivity;

import org.json.JSONObject;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class SourceEditFragment extends Fragment {
    private FragmentSourceEditBinding binding;
    private final Double lat;
    private final Double lon;
    private PrefManager prefManager;

    public SourceEditFragment(Double lat, Double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSourceEditBinding.inflate(inflater, container, false);
        prefManager = new PrefManager(getActivity());
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(requireContext(), R.array.defaults, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.equipmentSpinner.setAdapter(adapter);
        binding.latitude.setText(String.valueOf(lat));
        binding.longitude.setText(String.valueOf(lon));

        if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(requireActivity())) {
            getDeviceIdEquimpemnt();
        } else {
            Snackbar.make(binding.getRoot(), "No Internet Connection", Snackbar.LENGTH_LONG).show();
        }

        binding.equipmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(requireActivity())) {
                    getDeviceVoltage(item);
                } else {
                    Snackbar.make(binding.getRoot(), "No Internet Connection", Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                String[] deviceId = {"DEFAULT"};
                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), R.layout.custom_spinner, deviceId);
                binding.equipmentSpinner.setAdapter(adapter);
            }
        });
    }

    private void getDeviceVoltage(String item) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("EquipmentId", item);
        jsonObject.addProperty("Type", "SourceVoltage");
        jsonObject.addProperty("Subtype", "Source");
        jsonObject.addProperty("UserType", "Survey");
        jsonObject.addProperty("CYMDBNET", prefManager.getDBName());
        Retrofit retrofit = RetrofitClient.getClient(requireContext());
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<VoltageModel> call = apiInterface.getVoltage(jsonObject);
        call.enqueue(new Callback<VoltageModel>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<VoltageModel> call, @NonNull Response<VoltageModel> response) {
                if (response.code() == 200) {
                    try {
                        VoltageModel voltageModel = response.body();
                        assert voltageModel != null;
                        if (voltageModel.getVoltage() != null) {
                            binding.voltageA.setText(voltageModel.getVoltage().toString());
                            binding.voltageB.setText(voltageModel.getVoltage().toString());
                            binding.voltageC.setText(voltageModel.getVoltage().toString());
                        }
                    } catch (Exception e) {
                        Log.d("Exception", Objects.requireNonNull(e.getLocalizedMessage()));
                    }
                }else if (response.code() == 401){
                    prefManager.setIsUserLogin(false);
                    startActivity(new Intent(requireActivity(), LoginActivity.class));
                    requireActivity().finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<VoltageModel> call, @NonNull Throwable t) {
                @SuppressLint("InflateParams")
                View layout = LayoutInflater.from(requireContext()).inflate(R.layout.toast_layout, null);
                TextView Ok = layout.findViewById(R.id.okBtn);
                @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                TextView header = layout.findViewById(R.id.headerTv);
                @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                TextView description = layout.findViewById(R.id.descripTv);
                header.setText(requireContext().getString(R.string.error));
                description.setText(requireContext().getString(R.string.error_msg));
                Ok.setOnClickListener(v -> {
                    getDeviceVoltage(item);
                });
                Toast toast = new Toast(requireContext());
                toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
            }
        });
    }

    private void getDeviceIdEquimpemnt() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("Type", "Equipment");
        jsonObject.addProperty("Subtype", "Source");
        jsonObject.addProperty("UserType", "Survey");
        jsonObject.addProperty("CYMDBNET", prefManager.getDBName());
        Retrofit retrofit = RetrofitClient.getClient(requireContext());
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<DeviceIdModel> call = apiInterface.getDeviceId(jsonObject);
        call.enqueue(new Callback<DeviceIdModel>() {
            @Override
            public void onResponse(@NonNull Call<DeviceIdModel> call, @NonNull Response<DeviceIdModel> response) {
                if (response.code() == 200) {
                    try {
                        DeviceIdModel deviceIdModel = response.body();
                        assert deviceIdModel != null;
                        String[] deviceId = deviceIdModel.getAllEquipmentId().getEquipmentId().toArray(new String[0]);
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), R.layout.custom_spinner, deviceId);
                        binding.equipmentSpinner.setAdapter(adapter);
                    } catch (Exception e) {
                        Log.d("Exception", Objects.requireNonNull(e.getLocalizedMessage()));
                    }
                }else if (response.code() == 401){
                    prefManager.setIsUserLogin(false);
                    startActivity(new Intent(requireActivity(), LoginActivity.class));
                    requireActivity().finish();
                }
            }

            @Override
            public void onFailure(@NonNull Call<DeviceIdModel> call, @NonNull Throwable t) {
                @SuppressLint("InflateParams")
                View layout = LayoutInflater.from(requireContext()).inflate(R.layout.toast_layout, null);
                TextView Ok = layout.findViewById(R.id.okBtn);
                @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                TextView header = layout.findViewById(R.id.headerTv);
                @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                TextView description = layout.findViewById(R.id.descripTv);
                header.setText(requireContext().getString(R.string.error));
                description.setText(requireContext().getString(R.string.error_msg));
                Ok.setOnClickListener(v -> {
                    getDeviceIdEquimpemnt();
                });
                Toast toast = new Toast(requireContext());
                toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
            }
        });
    }

    public boolean checkAllFields() {
        boolean isValid = true;

        if (TextUtils.isEmpty(binding.equipmentSpinner.getSelectedItem().toString())) {
            ((TextView) binding.equipmentSpinner.getSelectedView()).setError("Name is required");
            isValid = false;
        }

        return isValid;
    }

    public JSONObject getData() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("eqpID", binding.equipmentSpinner.getSelectedItem().toString());
            jsonObject.put("Latitude", binding.latitude.getText().toString());
            jsonObject.put("Longitude", binding.longitude.getText().toString());
        } catch (Exception e) {
            Log.d("Exception", Objects.requireNonNull(e.getLocalizedMessage()));
        }
        return jsonObject;
    }

    @Override
    public void onResume() {
        super.onResume();
        requireView().requestLayout();
    }

}
