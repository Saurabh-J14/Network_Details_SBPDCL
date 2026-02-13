package com.techLabs.nbpdcl.view.survey.device;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.techLabs.nbpdcl.R;
import com.techLabs.nbpdcl.Utils.Args;
import com.techLabs.nbpdcl.Utils.PrefManager;
import com.techLabs.nbpdcl.Utils.ResponseDataUtils;
import com.techLabs.nbpdcl.databinding.FragmentBreakerDialogBinding;
import com.techLabs.nbpdcl.models.EquipmentModel;
import com.techLabs.nbpdcl.retrofit.ApiInterface;
import com.techLabs.nbpdcl.retrofit.RetrofitClient;
import com.techLabs.nbpdcl.view.activity.LoginActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class BreakerDialogFragment extends Fragment {
    private FragmentBreakerDialogBinding binding;
    private final String[] locations = {"At FromNode", "At ToNode"};
    private final String[] status = {"Connected", "DisConnected"};
    private final String networkId;
    private PrefManager prefManager;
    private final int location;

    public BreakerDialogFragment(String networkId, int location) {
        this.networkId = networkId;
        this.location = location;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentBreakerDialogBinding.inflate(inflater, container, false);
        prefManager = new PrefManager(getActivity());
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

        if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(requireContext())) {
            getBreakerEquipment(networkId, "Breaker");
        } else {
            Snackbar.make(binding.getRoot(), "No Internet Connection", Snackbar.LENGTH_LONG).show();
        }

        Args.getIsBreakerValidate().observe(getViewLifecycleOwner(), aBoolean -> {
            if (aBoolean) {
                checkDetails();
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

    private void getBreakerEquipment(String networkId, String breaker) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("NetworkId", networkId);
        jsonObject.addProperty("Type", "Equipment");
        jsonObject.addProperty("Subtype", breaker);
        jsonObject.addProperty("UserType", "Survey");
        jsonObject.addProperty("CYMDBNET", prefManager.getDBName());
        Retrofit retrofit = RetrofitClient.getClient(requireContext());
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<EquipmentModel> call = apiInterface.getEquipment(jsonObject);
        call.enqueue(new Callback<EquipmentModel>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<EquipmentModel> call, @NonNull Response<EquipmentModel> response) {
                if (response.code() == 200) {
                    EquipmentModel equipmentModel = response.body();
                    assert equipmentModel != null;
                    String[] equipArray = equipmentModel.getAllEquipmentId().getEquipmentId().toArray(new String[0]);
                    ArrayAdapter<String> adapters = new ArrayAdapter<>(requireContext(), R.layout.custom_spinner, equipArray);
                    binding.equipIdSpinnerBar.setAdapter(adapters);
                    binding.equipIdSpinnerBar.setThreshold(0);
                    binding.equipIdSpinnerBar.setText(equipArray[0], false);
                } else if (response.code() == 401) {
                    prefManager.setIsUserLogin(false);
                    requireActivity().startActivity(new Intent(requireContext(), LoginActivity.class));
                    requireActivity().finish();
                } else {
                    @SuppressLint("InflateParams")
                    View layout = LayoutInflater.from(requireContext()).inflate(R.layout.toast_layout, null);
                    TextView Ok = layout.findViewById(R.id.okBtn);
                    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                    TextView header = layout.findViewById(R.id.headerTv);
                    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                    TextView description = layout.findViewById(R.id.descripTv);
                    header.setText(response.message() + " - " + response.code());
                    description.setText(requireContext().getString(R.string.error_msg));
                    Ok.setOnClickListener(v -> {
                        getBreakerEquipment(networkId, breaker);
                    });
                    Toast toast = new Toast(requireContext());
                    toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<EquipmentModel> call, @NonNull Throwable t) {
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
                    getBreakerEquipment(networkId, breaker);
                });
                Toast toast = new Toast(requireContext());
                toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
            }
        });
    }

    private void checkDetails() {
        binding.deviceNumbersEdt.setError(null);
        boolean isCancel = false;
        View focusView = null;

        if (binding.deviceNumbersEdt.getText().toString().isEmpty()) {
            binding.deviceNumbersEdt.setError(Html.fromHtml("<font color='red'>Please Enter Device Number</font>"));
            focusView = binding.deviceNumbersEdt;
            isCancel = true;
        }

        if (isCancel) {
            focusView.requestFocus();
        } else {
            if (binding != null && binding.deviceNumbersEdt.getText() != null && binding.locationSpinnersBar.getSelectedItem() != null && binding.statusSpinnersBar.getSelectedItem() != null && binding.equipIdSpinnerBar.getText() != null) {
                Bundle bundle = new Bundle();
                bundle.putString("DeviceNumber", binding.deviceNumbersEdt.getText().toString().trim());
                bundle.putString("DeviceType", "8");
                if (binding.locationSpinnersBar.getSelectedItem().toString().equals("At ToNode")) {
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
                Args.setBreakerParameter(bundle);
            }
        }
    }

}