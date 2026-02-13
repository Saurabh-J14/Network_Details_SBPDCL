package com.techLabs.nbpdcl.view.fragment.loadFlowAnalysis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.techLabs.nbpdcl.Utils.PrefManager;
import com.techLabs.nbpdcl.Utils.callBack.LoadFlowArgument;
import com.techLabs.nbpdcl.adapters.NetworkIdAdapter;
import com.techLabs.nbpdcl.databinding.FragmentNetworksBinding;

import java.util.ArrayList;
import java.util.List;

public class NetworksFragment extends Fragment implements NetworkIdAdapter.OnNetworkSelectionChangedListener {

    private FragmentNetworksBinding binding;
    private LoadFlowArgument mListener;
    private PrefManager prefManager;
    private NetworkIdAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            mListener = (LoadFlowArgument) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity() + " must implement BottomSheetListener");
        }
        binding = FragmentNetworksBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefManager = new PrefManager(getActivity());

        Bundle bundle = this.getArguments();
        binding.recyclerView.setHasFixedSize(true);
        binding.recyclerView.setNestedScrollingEnabled(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if (bundle != null) {
            List<String> list = new ArrayList<String>((ArrayList<String>) bundle.get("Network"));
            adapter = new NetworkIdAdapter(list, this);
            binding.recyclerView.setAdapter(adapter);
        }

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
        binding.okBtn.setVisibility(View.VISIBLE);
        binding.cancelBtn.setVisibility(View.VISIBLE);
        updateVisible();
    }

    private void updateVisible() {
        binding.alert.setVisibility(adapter.isAnyCheckboxChecked() ? View.GONE : View.VISIBLE);
        binding.alert.requestFocus();
    }

    private void sendJsonObjectToActivity() {
        JsonObject jsonObject = new JsonObject();
        JsonObject dashBoardJsonObject = new JsonObject();
        List<String> selectedNetworks = adapter.getSelectedNetworks();

        if (!selectedNetworks.isEmpty()) {
            JsonArray jsonArray = new Gson().toJsonTree(selectedNetworks).getAsJsonArray();
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
            jsonObject.addProperty("TransformerTapOperationMode", "Normal");
            if (mListener != null) {
                mListener.onJsonObjectReceived(jsonObject, dashBoardJsonObject, selectedNetworks);
            }
        } else {
            Snackbar snack = Snackbar.make(binding.getRoot(), "Please Select Network!", Snackbar.LENGTH_LONG);
            snack.show();
        }
    }

    @Override
    public void onSelectionChanged(boolean isAnySelected) {
        updateVisible();
    }

    @Override
    public void onResume() {
        super.onResume();
        requireView().requestLayout();
    }

}