package com.techLabs.nbpdcl.view.survey.device;

import static com.techLabs.nbpdcl.Utils.ResponseDataUtils.getSafeDouble;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;

import com.google.gson.JsonObject;
import com.techLabs.nbpdcl.DataBase.Room.AppDatabase;
import com.techLabs.nbpdcl.DataBase.Room.CustomerData;
import com.techLabs.nbpdcl.R;
import com.techLabs.nbpdcl.Utils.Args;
import com.techLabs.nbpdcl.databinding.FragmentSpotLoadDialogBinding;
import com.techLabs.nbpdcl.view.survey.AddCustomerInfoDialog;

import org.json.JSONObject;

import java.util.List;
import java.util.concurrent.Executors;

public class SpotLoadDialogFragment extends Fragment {

    private final String[] locations = {"At ToNode", "At FromNode"};
    private final String[] status = {"Connected", "DisConnected"};
    private final String phaseType;
    private FragmentSpotLoadDialogBinding binding;
    private String phase;
    private AddCustomerInfoDialog addCustomerInfoDialog;
    private int location;
    private String loadType;
    private AppDatabase appDatabase;

    public SpotLoadDialogFragment(String phaseType, String phase, int location) {
        this.phaseType = phaseType;
        this.phase = phase;
        this.location = location;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSpotLoadDialogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        appDatabase = AppDatabase.getInstance(requireContext());
        getCustomerData();

        if (phaseType.equals("byPhase")) {
            binding.singlePhaseCheckedLayout.setVisibility(View.VISIBLE);
            binding.threePhaseCheckedLayout.setVisibility(View.GONE);
        } else {
            binding.singlePhaseCheckedLayout.setVisibility(View.GONE);
            binding.threePhaseCheckedLayout.setVisibility(View.VISIBLE);
        }

        ArrayAdapter<String> statusAdapters = new ArrayAdapter<>(requireActivity(), R.layout.custom_spinner, status);
        binding.statusSpinnerBar.setAdapter(statusAdapters);

        ArrayAdapter<String> locationAdapters = new ArrayAdapter<>(requireActivity(), R.layout.custom_spinner, locations);
        binding.locationSpinnerBar.setAdapter(locationAdapters);

        ArrayAdapter<CharSequence> formatAdapter = ArrayAdapter.createFromResource(
                requireActivity(),
                R.array.format,
                R.layout.custom_spinner
        );
        binding.formatSpinnerBar.setAdapter(formatAdapter);

        binding.formatSpinnerBar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedFormat = parent.getItemAtPosition(position).toString();
                setExtension(selectedFormat);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (location != 0) {
            if (location == 1) {
                binding.locationSpinnerBar.setSelection(0);
            } else {
                binding.locationSpinnerBar.setSelection(1);
            }
        }

        Args.getIsSpotloadValidate().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    checkDetails();
                }
            }
        });

        Args.getIsPhaseValidate().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (s != null) {
                    phase = s;
                }
            }
        });

        binding.customDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCustomerInfoDialog = new AddCustomerInfoDialog(requireActivity(), phaseType, phase, loadType);
                addCustomerInfoDialog.setCancelable(false);
                addCustomerInfoDialog.setOnDismissListener(dialog -> {
                    getCustomerData();
                });
                addCustomerInfoDialog.show();
            }
        });

    }

    private void getCustomerData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                final List<CustomerData> list = appDatabase.customerDataDao().getUniqueCustomers();
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (isAdded() && binding != null) {
                        updateUi(list);
                    }
                });
            } catch (Exception e) {
                Log.e("SpotLoad", "Database Error", e);
            }
        });
    }

    @SuppressLint("DefaultLocale")
    private void updateUi(List<CustomerData> dataList) {
        if (dataList == null || dataList.isEmpty()) return;
        double threePhaseRealPower = 0.0;
        double threePhaseTotalPowerFactor = 0.0;
        double threePhaseConsumption = 0.0;
        double threePhaseConnectedCapacity = 0.0;
        double threePhaseCustomer = 0.0;
        double powerFactorCount = 0;

        if (dataList.get(0).phaseType.equalsIgnoreCase("ThreePhase")) {
            binding.singlePhaseCheckedLayout.setVisibility(View.GONE);
            binding.threePhaseCheckedLayout.setVisibility(View.VISIBLE);
            for (int i = 0; i < dataList.size(); i++) {
                JsonObject kwObject = dataList.get(i).getActualKWObject();
                JsonObject pfObject = dataList.get(i).getActualPfObject();
                JsonObject kVaObject = dataList.get(i).getConnKVAObject();
                JsonObject custObject = dataList.get(i).getCustNoObject();

                threePhaseRealPower += getSafeDouble(kwObject, "7");
                threePhaseTotalPowerFactor += getSafeDouble(pfObject, "7");
                powerFactorCount++;

                threePhaseConnectedCapacity += getSafeDouble(kVaObject, "7");
                threePhaseCustomer += getSafeDouble(custObject, "7");
            }

            double averagePowerFactor =
                    powerFactorCount > 0 ? threePhaseTotalPowerFactor / powerFactorCount : 0.0;

            binding.threePhaseRealPowerTvEdt.setText(String.format("%.3f", threePhaseRealPower));
            binding.threePhasePowerFactorEdt.setText(String.format("%.3f", averagePowerFactor));
            binding.threePhaseConsumptionEdt.setText(String.format("%.3f", threePhaseConsumption));
            binding.spConnectedCapacityEdt.setText(String.format("%.3f", threePhaseConnectedCapacity));
            binding.spCustomersEdt.setText(String.format("%.3f", threePhaseCustomer));
        } else {
            binding.singlePhaseCheckedLayout.setVisibility(View.VISIBLE);
            binding.threePhaseCheckedLayout.setVisibility(View.GONE);
            if (phase.equalsIgnoreCase("1")) {
                double aKw = 0.0;
                double aPF = 0.0;
                double aConnKva = 0.0;
                double aCust = 0.0;
                for (int i = 0; i < dataList.size(); i++) {
                    JsonObject kwObject = dataList.get(i).getActualKWObject();
                    JsonObject pfObject = dataList.get(i).getActualPfObject();
                    JsonObject kVaObject = dataList.get(i).getConnKVAObject();
                    JsonObject custObject = dataList.get(i).getCustNoObject();

                    aKw += getSafeDouble(kwObject, "1");
                    aConnKva += getSafeDouble(kVaObject, "1");
                    aCust += getSafeDouble(custObject, "1");
                    /*threePhaseTotalPowerFactor += getSafeDouble(pfObject, "7");
                    powerFactorCount++;*/


                }

                binding.singlePhaseRealPowerAEdt.setText(String.valueOf(aKw));
                binding.singlePhaseRealPowerTotalEdt.setText(String.valueOf(aKw));
                binding.ccAEdt.setText(String.valueOf(aConnKva));
                binding.connectedCapacityTotalEdt.setText(String.valueOf(aConnKva));
                binding.customerAEdt.setText(String.valueOf(aCust));
                binding.customerTotalTv.setText(String.valueOf(aCust));

            } else if (phase.equalsIgnoreCase("2")) {
                double bKw = 0.0;
                double bPF = 0.0;
                double bConnKva = 0.0;
                double bCust = 0.0;
                for (int i = 0; i < dataList.size(); i++) {
                    JsonObject kwObject = dataList.get(i).getActualKWObject();
                    JsonObject pfObject = dataList.get(i).getActualPfObject();
                    JsonObject kVaObject = dataList.get(i).getConnKVAObject();
                    JsonObject custObject = dataList.get(i).getCustNoObject();

                    bKw += getSafeDouble(kwObject, "2");
                    bConnKva += getSafeDouble(kVaObject, "2");
                    bCust += getSafeDouble(custObject, "2");
                }

                binding.singlePhaseRealPowerBEdt.setText(String.valueOf(bKw));
                binding.singlePhaseRealPowerTotalEdt.setText(String.valueOf(bKw));
                binding.ccBEdt.setText(String.valueOf(bConnKva));
                binding.connectedCapacityTotalEdt.setText(String.valueOf(bConnKva));
                binding.customerBEdt.setText(String.valueOf(bCust));
                binding.customerTotalTv.setText(String.valueOf(bCust));

            } else if (phase.equalsIgnoreCase("3")) {
                double cKw = 0.0;
                double cPF = 0.0;
                double cConnKva = 0.0;
                double cCust = 0.0;
                for (int i = 0; i < dataList.size(); i++) {
                    JsonObject kwObject = dataList.get(i).getActualKWObject();
                    JsonObject pfObject = dataList.get(i).getActualPfObject();
                    JsonObject kVaObject = dataList.get(i).getConnKVAObject();
                    JsonObject custObject = dataList.get(i).getCustNoObject();

                    cKw += getSafeDouble(kwObject, "3");
                    cConnKva += getSafeDouble(kVaObject, "3");
                    cCust += getSafeDouble(custObject, "3");
                }

                binding.singlePhaseRealPowerCEdt.setText(String.valueOf(cKw));
                binding.singlePhaseRealPowerTotalEdt.setText(String.valueOf(cKw));
                binding.ccCEdt.setText(String.valueOf(cConnKva));
                binding.connectedCapacityTotalEdt.setText(String.valueOf(cConnKva));
                binding.customerCEdt.setText(String.valueOf(cCust));
                binding.customerTotalTv.setText(String.valueOf(cCust));

            } else if (phase.equalsIgnoreCase("4")) {
                double aKw = 0.0;
                double bKw = 0.0;
                double aPF = 0.0;
                double bPF = 0.0;
                double aConnKva = 0.0;
                double bConnKva = 0.0;
                double aCust = 0.0;
                double bCust = 0.0;
                for (int i = 0; i < dataList.size(); i++) {
                    JsonObject kwObject = dataList.get(i).getActualKWObject();
                    JsonObject pfObject = dataList.get(i).getActualPfObject();
                    JsonObject kVaObject = dataList.get(i).getConnKVAObject();
                    JsonObject custObject = dataList.get(i).getCustNoObject();

                    aKw += getSafeDouble(kwObject, "1");
                    bKw += getSafeDouble(kwObject, "2");
                    aConnKva += getSafeDouble(kVaObject, "1");
                    bConnKva += getSafeDouble(kVaObject, "2");
                    aCust += getSafeDouble(custObject, "1");
                    bCust += getSafeDouble(custObject, "2");
                }

                binding.singlePhaseRealPowerAEdt.setText(String.valueOf(aKw));
                binding.singlePhaseRealPowerBEdt.setText(String.valueOf(bKw));
                binding.singlePhaseRealPowerTotalEdt.setText(String.valueOf(aKw + bKw));
                binding.ccAEdt.setText(String.valueOf(aConnKva));
                binding.ccBEdt.setText(String.valueOf(bConnKva));
                binding.connectedCapacityTotalEdt.setText(String.valueOf(aConnKva + bConnKva));
                binding.customerAEdt.setText(String.valueOf(aCust));
                binding.customerBEdt.setText(String.valueOf(bCust));
                binding.customerTotalTv.setText(String.valueOf(aCust + bCust));
            } else if (phase.equalsIgnoreCase("5")) {
                double aKw = 0.0;
                double cKw = 0.0;
                double aPF = 0.0;
                double cPF = 0.0;
                double aConnKva = 0.0;
                double cConnKva = 0.0;
                double aCust = 0.0;
                double cCust = 0.0;
                for (int i = 0; i < dataList.size(); i++) {
                    JsonObject kwObject = dataList.get(i).getActualKWObject();
                    JsonObject pfObject = dataList.get(i).getActualPfObject();
                    JsonObject kVaObject = dataList.get(i).getConnKVAObject();
                    JsonObject custObject = dataList.get(i).getCustNoObject();

                    aKw += getSafeDouble(kwObject, "1");
                    cKw += getSafeDouble(kwObject, "3");
                    aConnKva += getSafeDouble(kVaObject, "1");
                    cConnKva += getSafeDouble(kVaObject, "3");
                    aCust += getSafeDouble(custObject, "1");
                    cCust += getSafeDouble(custObject, "3");
                }

                binding.singlePhaseRealPowerAEdt.setText(String.valueOf(aKw));
                binding.singlePhaseRealPowerCEdt.setText(String.valueOf(cKw));
                binding.singlePhaseRealPowerTotalEdt.setText(String.valueOf(aKw + cKw));
                binding.ccAEdt.setText(String.valueOf(aConnKva));
                binding.ccCEdt.setText(String.valueOf(cConnKva));
                binding.connectedCapacityTotalEdt.setText(String.valueOf(aConnKva + cConnKva));
                binding.customerAEdt.setText(String.valueOf(aCust));
                binding.customerCEdt.setText(String.valueOf(cCust));
                binding.customerTotalTv.setText(String.valueOf(aCust + cCust));
            } else if (phase.equalsIgnoreCase("6")) {
                double bKw = 0.0;
                double cKw = 0.0;
                double bPF = 0.0;
                double cPF = 0.0;
                double bConnKva = 0.0;
                double cConnKva = 0.0;
                double bCust = 0.0;
                double cCust = 0.0;
                for (int i = 0; i < dataList.size(); i++) {
                    JsonObject kwObject = dataList.get(i).getActualKWObject();
                    JsonObject pfObject = dataList.get(i).getActualPfObject();
                    JsonObject kVaObject = dataList.get(i).getConnKVAObject();
                    JsonObject custObject = dataList.get(i).getCustNoObject();

                    bKw += getSafeDouble(kwObject, "2");
                    cKw += getSafeDouble(kwObject, "3");
                    bConnKva += getSafeDouble(kVaObject, "2");
                    cConnKva += getSafeDouble(kVaObject, "3");
                    bCust += getSafeDouble(custObject, "2");
                    cCust += getSafeDouble(custObject, "3");
                }

                binding.singlePhaseRealPowerBEdt.setText(String.valueOf(bKw));
                binding.singlePhaseRealPowerCEdt.setText(String.valueOf(cKw));
                binding.singlePhaseRealPowerTotalEdt.setText(String.valueOf(bKw + cKw));
                binding.ccBEdt.setText(String.valueOf(bConnKva));
                binding.ccCEdt.setText(String.valueOf(cConnKva));
                binding.connectedCapacityTotalEdt.setText(String.valueOf(bConnKva + cConnKva));
                binding.customerBEdt.setText(String.valueOf(bCust));
                binding.customerCEdt.setText(String.valueOf(cCust));
                binding.customerTotalTv.setText(String.valueOf(bCust + cCust));
            } else {
                double aKw = 0.0;
                double bKw = 0.0;
                double cKw = 0.0;
                double aPF = 0.0;
                double bPF = 0.0;
                double cPF = 0.0;
                double aConnKva = 0.0;
                double bConnKva = 0.0;
                double cConnKva = 0.0;
                double aCust = 0.0;
                double bCust = 0.0;
                double cCust = 0.0;
                for (int i = 0; i < dataList.size(); i++) {
                    JsonObject kwObject = dataList.get(i).getActualKWObject();
                    JsonObject pfObject = dataList.get(i).getActualPfObject();
                    JsonObject kVaObject = dataList.get(i).getConnKVAObject();
                    JsonObject custObject = dataList.get(i).getCustNoObject();

                    aKw += getSafeDouble(kwObject, "1");
                    bKw += getSafeDouble(kwObject, "2");
                    cKw += getSafeDouble(kwObject, "3");
                    aConnKva += getSafeDouble(kVaObject, "1");
                    bConnKva += getSafeDouble(kVaObject, "2");
                    cConnKva += getSafeDouble(kVaObject, "3");
                    aCust += getSafeDouble(custObject, "1");
                    bCust += getSafeDouble(custObject, "2");
                    cCust += getSafeDouble(custObject, "3");
                }

                binding.singlePhaseRealPowerAEdt.setText(String.valueOf(aKw));
                binding.singlePhaseRealPowerBEdt.setText(String.valueOf(bKw));
                binding.singlePhaseRealPowerCEdt.setText(String.valueOf(cKw));
                binding.singlePhaseRealPowerTotalEdt.setText(String.valueOf(aKw + bKw + cKw));
                binding.ccAEdt.setText(String.valueOf(aConnKva));
                binding.ccBEdt.setText(String.valueOf(bConnKva));
                binding.ccCEdt.setText(String.valueOf(cConnKva));
                binding.connectedCapacityTotalEdt.setText(String.valueOf(aConnKva + bConnKva + cConnKva));
                binding.customerAEdt.setText(String.valueOf(aCust));
                binding.customerBEdt.setText(String.valueOf(bCust));
                binding.customerCEdt.setText(String.valueOf(cCust));
                binding.customerTotalTv.setText(String.valueOf(aCust + bCust + cCust));
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void setExtension(String selectedFormat) {
        switch (selectedFormat) {
            case "kVA & PF":
                binding.threePhaseRealPowerTv.setText("Apparent Power");
                binding.threePhaseRealPowerExtension.setText("kVA");

                binding.singlePhaseRealPowerTv.setText("Apparent Power");
                binding.singlePhaseRealPowerExtensionTv.setText("kVA");

                binding.threePhasePowerFactorTv.setText("Power Factor");
                binding.threePhasePowerFactorExtension.setText("%");

                binding.singlePhasePowerFactorTv.setText("Power Factor");
                binding.singlePhasePowerFactorExtensionTv.setText("%");
                loadType = "1";
                break;

            case "kW & kVAr":
                binding.threePhaseRealPowerTv.setText("Real Power");
                binding.threePhaseRealPowerExtension.setText("kW");

                binding.singlePhaseRealPowerTv.setText("Real Power");
                binding.singlePhaseRealPowerExtensionTv.setText("kW");

                binding.threePhasePowerFactorTv.setText("Reactive Factor");
                binding.threePhasePowerFactorExtension.setText("kVar");

                binding.singlePhasePowerFactorTv.setText("Reactive Factor");
                binding.singlePhasePowerFactorExtensionTv.setText("kVar");
                loadType = "0";
                break;

            case "kW & PF":
                binding.threePhaseRealPowerTv.setText("Real Power");
                binding.threePhaseRealPowerExtension.setText("kW");

                binding.singlePhaseRealPowerTv.setText("Real Power");
                binding.singlePhaseRealPowerExtensionTv.setText("kW");

                binding.threePhasePowerFactorTv.setText("Power Factor");
                binding.threePhasePowerFactorExtension.setText("%");

                binding.singlePhasePowerFactorTv.setText("Power Factor");
                binding.singlePhasePowerFactorExtensionTv.setText("%");
                loadType = "2";
                break;

        }
    }

    private void checkDetails() {
        binding.spNumberEdt.setError(null);
        boolean isCancel = false;
        View focusView = null;

        if (phaseType.equals("byPhase")) {
            if (binding.ccAEdt.getText().toString().trim().isEmpty()) {
                binding.ccAEdt.setError(Html.fromHtml("<font color='red'>All field required!</font>"));
                focusView = binding.ccAEdt;
                isCancel = true;
            }

            if (binding.ccBEdt.getText().toString().trim().isEmpty()) {
                binding.ccBEdt.setError(Html.fromHtml("<font color='red'>All field required!</font>"));
                focusView = binding.ccBEdt;
                isCancel = true;
            }

            if (binding.ccCEdt.getText().toString().trim().isEmpty()) {
                binding.ccCEdt.setError(Html.fromHtml("<font color='red'>All field required!</font>"));
                focusView = binding.ccCEdt;
                isCancel = true;
            }

            if (binding.customerAEdt.getText().toString().trim().isEmpty()) {
                binding.customerAEdt.setError(Html.fromHtml("<font color='red'>Customer Can't be empty! Please correct</font>"));
                focusView = binding.customerAEdt;
                isCancel = true;
            }

            if (binding.customerBEdt.getText().toString().trim().isEmpty()) {
                binding.customerBEdt.setError(Html.fromHtml("<font color='red'>Customer Can't be empty! Please correct</font>"));
                focusView = binding.customerBEdt;
                isCancel = true;
            }

            if (binding.customerCEdt.getText().toString().trim().isEmpty()) {
                binding.customerCEdt.setError(Html.fromHtml("<font color='red'>Customer Can't be empty! Please correct</font>"));
                focusView = binding.customerCEdt;
                isCancel = true;
            }

        } else {

            if (binding.spConnectedCapacityEdt.getText().toString().isEmpty()) {
                binding.spConnectedCapacityEdt.setError(Html.fromHtml("<font color='red'>Connected Capacity Can't be empty! Please correct</font>"));
                focusView = binding.spConnectedCapacityEdt;
                isCancel = true;
            }

            if (binding.spCustomersEdt.getText().toString().isEmpty()) {
                binding.spCustomersEdt.setError(Html.fromHtml("<font color='red'>Customer Can't be empty! Please correct</font>"));
                focusView = binding.spCustomersEdt;
                isCancel = true;
            }
        }

        if (isCancel) {
            focusView.requestFocus();
        } else {
            if (binding != null && binding.spNumberEdt.getText() != null && binding.locationSpinnerBar.getSelectedItem() != null && binding.statusSpinnerBar.getSelectedItem() != null) {
                Bundle bundle = new Bundle();
                bundle.putString("DeviceNumber", "0");
                bundle.putString("DeviceType", "20");

                if (binding.locationSpinnerBar.getSelectedItem().toString().equals("At ToNode")) {
                    bundle.putString("Location", "2");
                } else {
                    bundle.putString("Location", "1");
                }

                if (phaseType.equals("ThreePhase")) {
                    bundle.putString("PhaseType", "ThreePhase");
                } else {
                    bundle.putString("PhaseType", "SinglePhase");
                }

                bundle.putString("LoadValue", loadType);

                Args.setSpotloadParameter(bundle);
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        requireView().requestLayout();
    }


}