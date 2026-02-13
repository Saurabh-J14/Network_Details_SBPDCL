package com.techLabs.nbpdcl.view.fragment.loadFlowAnalysis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.techLabs.nbpdcl.R;
import com.techLabs.nbpdcl.Utils.PrefManager;
import com.techLabs.nbpdcl.Utils.callBack.LoadFlowArgument;
import com.techLabs.nbpdcl.databinding.FragmentControlsBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class ControlsFragment extends Fragment {

    private FragmentControlsBinding binding;
    private final String[] TransformerTabOperation = {"Normal Tap Operation", "Infinite Taps", "Lock Taps at their specified positions", "Disable Tap Changer"};
    private final String[] RegulatorTabOperation = {"Normal Tap Operation", "Normal Tap Operation - Lowest Tap In Range",
            "Normal Tap Operation - Highest Tap In Range", "Infinite Taps",
            "Lock Taps At Their Specified Positions", "Disable Tap Changer"};
    private LoadFlowArgument mListener;
    private PrefManager prefManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            mListener = (LoadFlowArgument) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity() + " must implement BottomSheetListener");
        }
        binding = FragmentControlsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefManager = new PrefManager(getActivity());

        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireActivity(), R.layout.custom_spinner, TransformerTabOperation);
        binding.transformerTabOperation.setAdapter(adapter);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(requireActivity(), R.layout.custom_spinner, RegulatorTabOperation);
        binding.regularTabOperation.setAdapter(adapter1);

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
        if (bundle != null) {
            if (bundle.get("Network") != null) {
                List<String> list = new ArrayList<String>((ArrayList<String>) Objects.requireNonNull(bundle.get("Network")));
                JsonArray jsonArray = new Gson().toJsonTree(list).getAsJsonArray();
                jsonObject.add("NetworkId", jsonArray);
                dashBoardJsonObject.add("NetworkId", jsonArray);
                dashBoardJsonObject.addProperty("UserType", prefManager.getUserType());
                jsonObject.addProperty("Username", prefManager.getUserName());
                jsonObject.addProperty("CYMDBNET", prefManager.getDBName());
                jsonObject.addProperty("method", "VoltageDropUnbalanced");
                jsonObject.addProperty("Tolerance", "0.01");
                jsonObject.addProperty("Iterations", "60");
                jsonObject.addProperty("AmbientTemperature", "77");
                jsonObject.addProperty("hour", "12");
                jsonObject.addProperty("minute", "15");

                if (binding.transformerTabOperation.getSelectedItem() != null) {
                    if (binding.transformerTabOperation.getSelectedItem().toString().contains("Normal Tap Operation")) {
                        jsonObject.addProperty("TransformerTapOperationMode", "Normal");
                    } else if (binding.transformerTabOperation.getSelectedItem().toString().contains("Infinite Taps")) {
                        jsonObject.addProperty("TransformerTapOperationMode", "Infinite");
                    } else if (binding.transformerTabOperation.getSelectedItem().toString().contains("Lock Taps at their specified positions")) {
                        jsonObject.addProperty("TransformerTapOperationMode", "Lock");
                    } else if (binding.transformerTabOperation.getSelectedItem().toString().contains("Disable Tap Changer")) {
                        jsonObject.addProperty("TransformerTapOperationMode", "Disable");
                    }
                } else {
                    jsonObject.addProperty("TransformerTapOperationMode", "Normal");
                }

                if (mListener != null) {
                    mListener.onJsonObjectReceived(jsonObject, dashBoardJsonObject, list);
                }

            } else {
                Toast.makeText(getActivity(), "Please Select Network", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        requireView().requestLayout();
    }
}