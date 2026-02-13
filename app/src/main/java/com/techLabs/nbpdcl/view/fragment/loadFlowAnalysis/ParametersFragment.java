package com.techLabs.nbpdcl.view.fragment.loadFlowAnalysis;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.techLabs.nbpdcl.R;
import com.techLabs.nbpdcl.Utils.PrefManager;
import com.techLabs.nbpdcl.Utils.callBack.LoadFlowArgument;
import com.techLabs.nbpdcl.databinding.FragmentParametersBinding;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

public class ParametersFragment extends Fragment {

    private FragmentParametersBinding binding;
    private final LinkedHashMap<String, String> CalculationMethodMap = new LinkedHashMap<>();
    private LoadFlowArgument mListener;
    private TimePickerDialog mTimePicker;
    private String hours = null;
    private String minutes = null;
    private PrefManager prefManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            mListener = (LoadFlowArgument) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity() + " must implement BottomSheetListener");
        }
        binding = FragmentParametersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefManager = new PrefManager(getActivity());

        CalculationMethodMap.put("Voltage Drop -Unbalanced", "VoltageDropUnbalanced");
        CalculationMethodMap.put("Voltage Drop -Balanced", "VoltageDropBalanced");
        CalculationMethodMap.put("Fast Decoupled -Balanced", "FastDecoupled");
        CalculationMethodMap.put("Gauss Seidel -Balanced", "GaussSeidel");
        CalculationMethodMap.put("Newton Raphson -Balanced", "NewtonPhonographs");
        CalculationMethodMap.put("Newton Raphson -Unbalanced", "NewtonRaphsonUnbalanced");

        ArrayList<String> spinnerData = new ArrayList<>(CalculationMethodMap.keySet());

        ArrayAdapter<String> calculationAdapters = new ArrayAdapter<>(requireActivity(), R.layout.custom_spinner, spinnerData);
        binding.calculationMethod.setAdapter(calculationAdapters);
        binding.calculationMethod.setSelection(5);

        binding.timeEdt.setOnClickListener(view1 -> {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                    hours = String.valueOf(selectedHour);
                    minutes = String.valueOf(selectedMinute);
                    binding.timeEdt.setText(selectedHour + ":" + selectedMinute);
                }
            }, hour, minute, true);
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();
        });

        binding.okBtn.setOnClickListener(view1 -> {
            sendJsonObjectToActivity();
        });

        binding.cancelBtn.setOnClickListener(view1 -> {
            List<String> list = new ArrayList<>();
            JsonObject jsonObject = new JsonObject();
            JsonObject dashBoardJsonObject = new JsonObject();
            jsonObject.addProperty("isLoadFlow", false);
            if (mListener != null) {
                mListener.onJsonObjectReceived(jsonObject, dashBoardJsonObject, list);
            }
        });

    }

    private void sendJsonObjectToActivity() {
        JsonObject jsonObject = new JsonObject();
        JsonObject dashBoardJsonObject = new JsonObject();

        Bundle bundle = this.getArguments();
        assert bundle != null;
        if (bundle.get("Network") != null) {
            List<String> list = new ArrayList<String>((ArrayList<String>) bundle.get("Network"));
            JsonArray jsonArray = new Gson().toJsonTree(list).getAsJsonArray();
            jsonObject.add("NetworkId", jsonArray);
            dashBoardJsonObject.add("NetworkId", jsonArray);
            dashBoardJsonObject.addProperty("UserType", prefManager.getUserType());
            jsonObject.addProperty("Username", prefManager.getUserName());
            jsonObject.addProperty("CYMDBNET", prefManager.getDBName());
            if (CalculationMethodMap.get(binding.calculationMethod.getSelectedItem()) != null) {
                jsonObject.addProperty("method", CalculationMethodMap.get(binding.calculationMethod.getSelectedItem()));
            } else {
                jsonObject.addProperty("method", "VoltageDropUnbalanced");
            }

            if (binding.toleRanceEdt.getText() != null && !binding.toleRanceEdt.getText().toString().isEmpty()) {
                jsonObject.addProperty("Tolerance", binding.toleRanceEdt.getText().toString());
            } else {
                jsonObject.addProperty("Tolerance", "0.01");
            }

            if (!binding.itrationsEdt.getText().toString().trim().isEmpty()) {
                jsonObject.addProperty("Iterations", binding.itrationsEdt.getText().toString().trim());
            } else {
                jsonObject.addProperty("Iterations", "60");
            }

            if (!binding.ambTempEdt.getText().toString().trim().isEmpty()) {
                jsonObject.addProperty("AmbientTemperature", binding.ambTempEdt.getText().toString().trim());
            } else {
                jsonObject.addProperty("AmbientTemperature", "77");
            }

            if (hours != null) {
                jsonObject.addProperty("hour", hours);
            } else {
                jsonObject.addProperty("hour", "12");
            }

            if (minutes != null) {
                jsonObject.addProperty("minute", minutes);
            } else {
                jsonObject.addProperty("minute", "15");
            }

            jsonObject.addProperty("TransformerTapOperationMode", "Normal");
            if (mListener != null) {
                mListener.onJsonObjectReceived(jsonObject, dashBoardJsonObject, list);
            }
        } else {
            Snackbar snack = Snackbar.make(binding.getRoot(), "Please Select Network!", Snackbar.LENGTH_LONG);
            snack.show();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        requireView().requestLayout();
    }
}