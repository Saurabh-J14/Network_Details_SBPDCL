package com.techLabs.nbpdcl.view.survey.device;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
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
import androidx.lifecycle.Observer;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.techLabs.nbpdcl.R;
import com.techLabs.nbpdcl.Utils.Args;
import com.techLabs.nbpdcl.Utils.PrefManager;
import com.techLabs.nbpdcl.Utils.ResponseDataUtils;
import com.techLabs.nbpdcl.databinding.FragmentTransformerDialogBinding;
import com.techLabs.nbpdcl.models.EquipmentModel;
import com.techLabs.nbpdcl.retrofit.ApiInterface;
import com.techLabs.nbpdcl.retrofit.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TransformerDialogFragment extends Fragment {

    private FragmentTransformerDialogBinding binding;
    private final String[] locations = {"At FromNode", "At ToNode"};
    private final String[] status = {"Connected", "DisConnected"};
    private final String networkId;
    private PrefManager prefManager;
    private String[] equipArray;
    private int location;

    public TransformerDialogFragment(String networkId, int location) {
        this.networkId = networkId;
        this.location = location;

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTransformerDialogBinding.inflate(inflater, container, false);
        prefManager = new PrefManager(getActivity());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ArrayAdapter<String> statusAdapters = new ArrayAdapter<>(requireActivity(), R.layout.custom_spinner, status);
        binding.statusSpinnerBar.setAdapter(statusAdapters);

        ArrayAdapter<String> locationAdapters = new ArrayAdapter<>(requireActivity(), R.layout.custom_spinner, locations);
        binding.locationSpinnerBar.setAdapter(locationAdapters);

        if (location != 0) {
            if (location == 1) {
                binding.locationSpinnerBar.setSelection(0);
            } else {
                binding.locationSpinnerBar.setSelection(1);
            }
        }

        if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(requireActivity())) {
            getTransformerEquipment(networkId, "Transformer");
        } else {
            Snackbar.make(binding.getRoot(), "No Internet Connection", Snackbar.LENGTH_LONG).show();
        }

        Args.getIsTransformerValidate().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
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

    private void getTransformerEquipment(String networkId, String transformer) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("NetworkId", networkId);
        jsonObject.addProperty("Type", "Equipment");
        jsonObject.addProperty("Subtype", transformer);
        jsonObject.addProperty("UserType", "Survey");
        jsonObject.addProperty("CYMDBNET", prefManager.getDBName());
        Retrofit retrofit = RetrofitClient.getClient(requireContext());
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<EquipmentModel> call = apiInterface.getEquipmentSurveyData(jsonObject);
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
                        getTransformerEquipment(networkId, transformer);
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
                    getTransformerEquipment(networkId, transformer);
                });
                Toast toast = new Toast(requireContext());
                toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        requireView().requestLayout();
    }

    private void checkDetails() {
        if (binding != null && binding.equipIdSpinnerBar.getText() != null && binding.deviceNumberEdt.getText() != null && binding.statusSpinnerBar.getSelectedItem() != null && binding.locationSpinnerBar.getSelectedItem() != null) {
            Bundle bundle = new Bundle();
            bundle.putString("DeviceNumber", binding.deviceNumberEdt.getText().toString());
            bundle.putString("DeviceType", "5");
            if (binding.locationSpinnerBar.getSelectedItem().equals("At ToNode")) {
                bundle.putString("Location", "2");
            } else {
                bundle.putString("Location", "1");
            }

            if (binding.statusSpinnerBar.getSelectedItem().equals("Connected")) {
                bundle.putString("Status", "0");
            } else {
                bundle.putString("Status", "1");
            }

            if (!binding.dtNameEdt.getText().toString().trim().isEmpty()) {
                bundle.putString("dtName", binding.dtNameEdt.getText().toString().trim());
            } else {
                bundle.putString("dtName", "");
            }
            bundle.putString("EquipmentID", binding.equipIdSpinnerBar.getText().toString());

            Args.setTransformerParameter(bundle);
        }
    }
}