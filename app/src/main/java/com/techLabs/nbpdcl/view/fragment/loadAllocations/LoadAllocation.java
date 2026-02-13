package com.techLabs.nbpdcl.view.fragment.loadAllocations;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.techLabs.nbpdcl.R;
import com.techLabs.nbpdcl.Utils.PrefManager;
import com.techLabs.nbpdcl.Utils.ResponseDataUtils;
import com.techLabs.nbpdcl.Utils.callBack.LoadAllocationArgument;
import com.techLabs.nbpdcl.databinding.FragmentLoadAllocationBinding;
import com.techLabs.nbpdcl.models.SelectedFeedersModel;
import com.techLabs.nbpdcl.models.analysis.AnalysisInformationModel;
import com.techLabs.nbpdcl.retrofit.ApiInterface;
import com.techLabs.nbpdcl.retrofit.RetrofitClient;
import com.techLabs.nbpdcl.view.activity.LoginActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoadAllocation extends BottomSheetDialogFragment {

    private FragmentLoadAllocationBinding binding;
    private final Context mainContext;
    private BottomSheetDialog dialog;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private PrefManager prefManager;
    private final List<String> networksList = new ArrayList<>();
    private String[] networks;
    private final List<String> metersList = new ArrayList<>();
    private String[] meters;
    private LoadAllocationArgument loadAllocationArgument;

    public LoadAllocation(Context context) {
        this.mainContext = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        return dialog;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            loadAllocationArgument = (LoadAllocationArgument) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity() + " must implement BottomSheetListener");
        }
        binding = FragmentLoadAllocationBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefManager = new PrefManager(mainContext);

        if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(mainContext)) {
            getSelectedFeeder();
        } else {
            final Dialog dialog = new Dialog(mainContext);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.no_internet_dialog);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(mainContext.getDrawable(R.drawable.pop_background));
            LottieAnimationView lottieAnimationView = dialog.findViewById(R.id.animation_view);
            Button RetryBtn = dialog.findViewById(R.id.btnDialog);
            lottieAnimationView.playAnimation();
            RetryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(mainContext)) {
                        getSelectedFeeder();
                        dialog.dismiss();
                    }
                }
            });
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();
        }

        bottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        CoordinatorLayout layout = dialog.findViewById(R.id.bottomSheetLayout);
        assert layout != null;
        layout.setMinimumHeight(Resources.getSystem().getDisplayMetrics().heightPixels);

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HALF_EXPANDED + 5) {

                } else if (newState == BottomSheetBehavior.PEEK_HEIGHT_AUTO) {

                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(mainContext)) {
            getDownStreamInformation();
        } else {
            final Dialog dialog = new Dialog(mainContext);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.no_internet_dialog);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(mainContext.getDrawable(R.drawable.pop_background));
            LottieAnimationView lottieAnimationView = dialog.findViewById(R.id.animation_view);
            Button RetryBtn = dialog.findViewById(R.id.btnDialog);
            lottieAnimationView.playAnimation();
            RetryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(mainContext)) {
                        getDownStreamInformation();
                        dialog.dismiss();
                    }
                }
            });
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();
        }

        binding.okBtn.setOnClickListener(v -> {
            SendLoadAllocationArguments();
        });

        binding.cancelBtn.setOnClickListener(v -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("LoadAllocation", false);
            if (loadAllocationArgument != null) {
                loadAllocationArgument.onLoadAllocationCallBack(jsonObject);
            }
        });

    }

    private void SendLoadAllocationArguments() {
        JsonObject jsonObject = new JsonObject();
        JsonArray jsonArray = new JsonArray();
        jsonArray.add("GA04");
        jsonObject.addProperty("Username", prefManager.getUserName());
        jsonObject.add("NetworkId", jsonArray);
        jsonObject.addProperty("Method", "0");
        jsonObject.addProperty("LoadValueType", "0");
        jsonObject.addProperty("IsTotalDemand", "0");
        jsonObject.addProperty("DemandAValue1", "0");
        jsonObject.addProperty("DemandAValue2", "0");
        jsonObject.addProperty("DemandBValue1", "0");
        jsonObject.addProperty("DemandBValue2", "0");
        jsonObject.addProperty("DemandCValue1", "0");
        jsonObject.addProperty("DemandCValue2", "0");
        jsonObject.addProperty("DemandTotalValue1", "0");
        jsonObject.addProperty("DemandTotalValue2", "0");
    }

    private void getDownStreamInformation() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("Username", prefManager.getUserName());
        jsonObject.addProperty("NetworkId", "GA10");
        jsonObject.addProperty("AnalysisType", "LoadAllocation");
        jsonObject.addProperty("InforamtionName", "DownstreamInformation");
        jsonObject.addProperty("InformationType", "ConnectedKVA");
        jsonObject.addProperty("NodeId", "TOPO_HEADNODE_ID_000005");
        Retrofit retrofit = RetrofitClient.getClient(mainContext);
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<AnalysisInformationModel> call = apiInterface.AnalysisInformatio(jsonObject);
        call.enqueue(new Callback<AnalysisInformationModel>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<AnalysisInformationModel> call, @NonNull Response<AnalysisInformationModel> response) {
                if (response.code() == 200) {
                    AnalysisInformationModel analysisInformationModel = response.body();

                    assert analysisInformationModel != null;
                    if (analysisInformationModel.getOutput().getA() != null) {
                        binding.dSA.setText(analysisInformationModel.getOutput().getA());
                    }

                    if (analysisInformationModel.getOutput().getB() != null) {
                        binding.dSB.setText(analysisInformationModel.getOutput().getB());
                    }

                    if (analysisInformationModel.getOutput().getC() != null) {
                        binding.dSC.setText(analysisInformationModel.getOutput().getC());
                    }

                    if (analysisInformationModel.getOutput().getTotal() != null) {
                        binding.dSTotal.setText(analysisInformationModel.getOutput().getTotal());
                    }

                } else if (response.code() == 401){
                    prefManager.setIsUserLogin(false);
                    startActivity(new Intent(requireActivity(), LoginActivity.class));
                    requireActivity().finish();
                }else {
                    @SuppressLint("InflateParams")
                    View layout = LayoutInflater.from(getActivity()).inflate(R.layout.toast_layout, null);
                    TextView Ok = layout.findViewById(R.id.okBtn);
                    @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView header = layout.findViewById(R.id.headerTv);
                    @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView description = layout.findViewById(R.id.descripTv);
                    header.setText(response.message() + " - " + response.code());
                    description.setText(getString(R.string.error_msg));
                    Ok.setOnClickListener(v -> {
                        getDownStreamInformation();
                    });
                    Toast toast = new Toast(getActivity());
                    toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<AnalysisInformationModel> call, @NonNull Throwable t) {
                @SuppressLint("InflateParams")
                View layout = LayoutInflater.from(getActivity()).inflate(R.layout.toast_layout, null);
                TextView Ok = layout.findViewById(R.id.okBtn);
                @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView header = layout.findViewById(R.id.headerTv);
                @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView description = layout.findViewById(R.id.descripTv);
                header.setText(requireActivity().getString(R.string.error));
                description.setText(getString(R.string.error_msg));
                Ok.setOnClickListener(v -> {
                    getDownStreamInformation();
                });
                Toast toast = new Toast(getActivity());
                toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
            }
        });
    }

    private void getSelectedFeeder() {
        Bundle bundle = this.getArguments();
        assert bundle != null;
        if (bundle.get("Network") != null) {
            JsonObject jsonObject = new JsonObject();
            JsonArray jsonArray = new Gson().toJsonTree(bundle.get("Network")).getAsJsonArray();
            jsonObject.add("NetworkId", jsonArray);
            jsonObject.addProperty("UserType", prefManager.getUserType());
            Retrofit retrofit = RetrofitClient.getClient(mainContext);
            ApiInterface apiInterface = retrofit.create(ApiInterface.class);
            Call<SelectedFeedersModel> call = apiInterface.getSelectedFeeder(jsonObject);
            call.enqueue(new Callback<SelectedFeedersModel>() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onResponse(@NonNull Call<SelectedFeedersModel> call, @NonNull Response<SelectedFeedersModel> response) {
                    if (response.code() == 200) {
                        SelectedFeedersModel selectedFeedersModel = response.body();
                        assert selectedFeedersModel != null;
                        if (selectedFeedersModel.getOutput() != null && !selectedFeedersModel.getOutput().isEmpty() && selectedFeedersModel.getOutput().size() > 0) {
                            for (int i = 0; i < selectedFeedersModel.getOutput().size(); i++) {
                                if (selectedFeedersModel.getOutput().get(i).getGroup4() != null) {
                                    networksList.add(selectedFeedersModel.getOutput().get(i).getGroup4());
                                }

                                if (selectedFeedersModel.getOutput().get(i).getMeterDeviceNumber() != null) {
                                    metersList.add(selectedFeedersModel.getOutput().get(i).getMeterDeviceNumber());
                                }

                            }
                            setMeterData();
                        }
                    } else if (response.code() == 401){
                        prefManager.setIsUserLogin(false);
                        startActivity(new Intent(requireActivity(), LoginActivity.class));
                        requireActivity().finish();
                    }else {
                        @SuppressLint("InflateParams")
                        View layout = LayoutInflater.from(getActivity()).inflate(R.layout.toast_layout, null);
                        TextView Ok = layout.findViewById(R.id.okBtn);
                        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView header = layout.findViewById(R.id.headerTv);
                        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView description = layout.findViewById(R.id.descripTv);
                        header.setText(response.message() + " - " + response.code());
                        description.setText(getString(R.string.error_msg));
                        Ok.setOnClickListener(v -> {
                            getSelectedFeeder();
                        });
                        Toast toast = new Toast(getActivity());
                        toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                        toast.setDuration(Toast.LENGTH_LONG);
                        toast.setView(layout);
                        toast.show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<SelectedFeedersModel> call, @NonNull Throwable t) {
                    @SuppressLint("InflateParams")
                    View layout = LayoutInflater.from(getActivity()).inflate(R.layout.toast_layout, null);
                    TextView Ok = layout.findViewById(R.id.okBtn);
                    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                    TextView header = layout.findViewById(R.id.headerTv);
                    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                    TextView description = layout.findViewById(R.id.descripTv);
                    header.setText(requireActivity().getString(R.string.error));
                    description.setText(getString(R.string.error_msg));
                    Ok.setOnClickListener(v -> {
                        getSelectedFeeder();
                    });
                    Toast toast = new Toast(getActivity());
                    toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();
                }
            });
        }
    }

    private void setMeterData() {
        if (!networksList.isEmpty()) {
            networks = new String[]{Arrays.toString(networksList.toArray())};
            ArrayAdapter<String> adapters = new ArrayAdapter<>(mainContext, R.layout.custom_spinner, networks);
            binding.networkSpinner.setAdapter(adapters);
        }

        if (!metersList.isEmpty()) {
            meters = new String[]{Arrays.toString(metersList.toArray())};
            ArrayAdapter<String> adapters = new ArrayAdapter<>(mainContext, R.layout.custom_spinner, meters);
            binding.meterSpinner.setAdapter(adapters);
        }
    }
}