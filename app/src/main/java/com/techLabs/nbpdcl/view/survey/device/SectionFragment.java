package com.techLabs.nbpdcl.view.survey.device;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
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
import com.techLabs.nbpdcl.databinding.FragmentSectionBinding;
import com.techLabs.nbpdcl.models.EquipmentModel;
import com.techLabs.nbpdcl.retrofit.ApiInterface;
import com.techLabs.nbpdcl.retrofit.RetrofitClient;
import com.techLabs.nbpdcl.view.activity.LoginActivity;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SectionFragment extends Fragment {

    private final String[] typeList = {"Cable", "Overhead", "UnbalanceOverhead"};
    private final String[] statusList = {"Connected", "Disconnected"};
    private FragmentSectionBinding binding;
    private PrefManager prefManager;

    // FIX 1: Add a variable to track the last loaded type to prevent duplicate calls
    private String lastSelectedType = "";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSectionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefManager = new PrefManager(getActivity());

        ArrayAdapter<String> typeAdapters = new ArrayAdapter<>(requireActivity(), R.layout.custom_spinner, typeList);
        binding.typeSpinnerBar.setAdapter(typeAdapters);

        ArrayAdapter<String> statusAdapters = new ArrayAdapter<>(requireActivity(), R.layout.custom_spinner, statusList);
        binding.statusSpinnersBar.setAdapter(statusAdapters);

        Bundle args = getArguments();
        if (args != null) {
            ArrayList<GeoPoint> geoPoints = args.getParcelableArrayList("Coordinates");
            assert geoPoints != null;
            ResponseDataUtils.calculateTotalDistance(geoPoints);
            binding.lengthTv.setText(ResponseDataUtils.calculateTotalDistance(geoPoints) + "  M");
        }

        binding.typeSpinnerBar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedType = binding.typeSpinnerBar.getSelectedItem().toString();

                // FIX 2: Check if the selection is actually different from the last one
                // This prevents the code from executing multiple times during initialization or layout refreshes.
                if (!selectedType.equals(lastSelectedType)) {
                    if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(requireActivity())) {
                        lastSelectedType = selectedType; // Update the tracker
                        getEquipment(selectedType);
                    } else {
                        // If no internet, we might want to reset lastSelectedType so they can try clicking it again later
                        // or leave it to prevent spamming.
                        Snackbar.make(binding.getRoot(), "No Internet Connection", Snackbar.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Args.getIsSectionValidate().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    checkData();
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
        // Caution: This requestLayout() can sometimes trigger Spinner onItemSelected again.
        // Our 'lastSelectedType' fix handles this, but use requestLayout() sparingly.
        requireView().requestLayout();
    }

    @SuppressLint("SetTextI18n")
    private void getEquipment(String eqType) {
        if (eqType.equals("Cable")) {
            binding.deviceNumbersEdt.setText("CA");
            binding.eqIdTv.setText("Cable ID");
        } else if (eqType.equals("Overhead")) {
            binding.deviceNumbersEdt.setText("OH");
            binding.eqIdTv.setText("Line ID");
        } else {
            binding.deviceNumbersEdt.setText("OU");
            binding.eqIdTv.setText("Line ID");
        }
        Bundle bundle = this.getArguments();
        assert bundle != null;
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("NetworkId", bundle.getString("Network"));
        jsonObject.addProperty("Type", "Equipment");
        jsonObject.addProperty("Subtype", eqType);
        jsonObject.addProperty("UserType", "Survey");
        jsonObject.addProperty("CYMDBNET", prefManager.getDBName());
        Retrofit retrofit = RetrofitClient.getClient(requireContext());
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<EquipmentModel> call = apiInterface.getEquipmentData(jsonObject);
        call.enqueue(new Callback<EquipmentModel>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<EquipmentModel> call, @NonNull Response<EquipmentModel> response) {
                if (response.code() == 200) {
                    try {
                        EquipmentModel equipmentModel = response.body();
                        assert equipmentModel != null;
                        if (!equipmentModel.getAllEquipmentId().getEquipmentId().isEmpty()) {
                            String[] equipArray = equipmentModel.getAllEquipmentId().getEquipmentId().toArray(new String[0]);
                            ArrayAdapter<String> adapters = new ArrayAdapter<>(requireContext(), R.layout.custom_spinner, equipArray);
                            binding.equipIdSpinnerBar.setAdapter(adapters);
                            binding.equipIdSpinnerBar.setThreshold(0);
                            binding.equipIdSpinnerBar.setText(equipArray[0], false);
                            if (eqType.equals("Cable") && prefManager.getCable() != null && !prefManager.getCable().equals("null") && !prefManager.getCable().isBlank()) {
                                EquipTop(equipArray, prefManager.getCable());
                                Log.d("getCableEquipID", prefManager.getCable());

                            } else if (eqType.equals("Overhead") && prefManager.getOverhead() != null && !prefManager.getOverhead().equals("null") && !prefManager.getOverhead().isBlank()) {
                                EquipTop(equipArray, prefManager.getOverhead());
                            } else {
                                if (prefManager.getUnBalenced() != null && !prefManager.getUnBalenced().equals("null") && !prefManager.getUnBalenced().isBlank()) {
                                    EquipTop(equipArray, prefManager.getUnBalenced());
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.d("Exception", Objects.requireNonNull(e.getLocalizedMessage()));
                    }
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
                        getEquipment(eqType);
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
                    getEquipment(eqType);
                });
                Toast toast = new Toast(requireContext());
                toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
            }
        });
    }

    private void checkData() {
        binding.deviceNumbersEdt.setError(null);
        boolean isCancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(binding.deviceNumbersEdt.getText().toString().trim())) {
            binding.deviceNumbersEdt.setError(Html.fromHtml("<font color='red'>Please Enter Device Number</font>"));
            focusView = binding.deviceNumbersEdt;
            isCancel = true;
        }

        if (isCancel) {
            focusView.requestFocus();
        } else {
            if (binding.typeSpinnerBar.getSelectedItem() != null && binding.deviceNumbersEdt.getText() != null && binding.equipIdSpinnerBar.getText() != null) {
                Bundle bundle = new Bundle();
                if (binding.typeSpinnerBar.getSelectedItem().toString().equals("Cable")) {
                    bundle.putString("DeviceType", "1");
                    if (binding.equipIdSpinnerBar.getText().toString().trim() != null && !binding.equipIdSpinnerBar.getText().toString().trim().isBlank()){
                        Log.d("setCableEquipID", binding.equipIdSpinnerBar.getText().toString());
                        prefManager.setCable(binding.equipIdSpinnerBar.getText().toString());
                    }

                } else if (binding.typeSpinnerBar.getSelectedItem().toString().equals("Overhead")) {
                    bundle.putString("DeviceType", "2");
                    prefManager.setOverhead(binding.equipIdSpinnerBar.getText().toString());
                } else {
                    bundle.putString("DeviceType", "23");
                    prefManager.setUnBalenced(binding.equipIdSpinnerBar.getText().toString());
                }

                if (binding.statusSpinnersBar.getSelectedItem().equals("Connected")) {
                    bundle.putString("Status", "0");
                } else {
                    bundle.putString("Status", "1");
                }

                bundle.putString("DeviceNumber", binding.deviceNumbersEdt.getText().toString().trim());

                bundle.putString("CableID", binding.equipIdSpinnerBar.getText().toString());
                Args.setSectionParameter(bundle);
            }
        }
    }

    private String[] EquipTop(String[] array, String searchItem) {

        int index = -1;

        for (int i = 0; i < array.length; i++) {
            if (array[i].equalsIgnoreCase(searchItem)) {
                index = i;
                break;
            }
        }

        if (index <= 0) {
            return array;
        }

        String[] newArray = new String[array.length];
        newArray[0] = array[index];

        int j = 1;
        for (int i = 0; i < array.length; i++) {
            if (i != index) {
                newArray[j++] = array[i];
            }
        }

        return newArray;
    }


}