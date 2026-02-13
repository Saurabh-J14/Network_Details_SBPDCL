package com.techLabs.nbpdcl.view.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.google.gson.JsonObject;
import com.techLabs.nbpdcl.R;
import com.techLabs.nbpdcl.Utils.PrefManager;
import com.techLabs.nbpdcl.Utils.ResponseDataUtils;
import com.techLabs.nbpdcl.databinding.FragmentSourceBinding;
import com.techLabs.nbpdcl.models.device.Source;
import com.techLabs.nbpdcl.retrofit.ApiInterface;
import com.techLabs.nbpdcl.retrofit.RetrofitClient;
import com.techLabs.nbpdcl.view.activity.LoginActivity;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SourceFragment extends Fragment {
    private FragmentSourceBinding binding;
    private PrefManager prefManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSourceBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View MainLayoutBackGround = requireActivity().getWindow().getDecorView().getRootView();
        MainLayoutBackGround.setBackground(requireContext().getDrawable(R.drawable.pop_layout_background));
        Bundle bundle = this.getArguments();
        prefManager = new PrefManager(getActivity());

        assert bundle != null;
        if (bundle.getString("NodeId") != null) {
            if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(requireContext())) {
                getSourceInfo(bundle.getString("NodeId"));
            } else {
                final Dialog dialog = new Dialog(requireContext());
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.no_internet_dialog);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(requireActivity().getDrawable(R.drawable.pop_background));
                LottieAnimationView lottieAnimationView = dialog.findViewById(R.id.animation_view);
                Button RetryBtn = dialog.findViewById(R.id.btnDialog);
                lottieAnimationView.playAnimation();
                RetryBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(requireActivity())) {
                            getSourceInfo(bundle.getString("NodeId"));
                            dialog.dismiss();
                        }
                    }
                });
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
                dialog.show();
            }
        }

        if (bundle.getString("Latitude") != null) {
            binding.latitude.setText(bundle.getString("Latitude"));
        }

        if (bundle.getString("Longitude") != null) {
            binding.longitude.setText(bundle.getString("Longitude"));
        }

        if (bundle.getString("NodeIdX") != null) {
            binding.nodeIdX.setText(bundle.getString("NodeIdX"));
        }

        if (bundle.getString("NodeIdY") != null) {
            binding.nodeIdY.setText(bundle.getString("NodeIdY"));
        }

        binding.nameEdt.setEnabled(false);
        binding.nameEdt.setTextColor(requireContext().getColor(R.color.black));
        binding.nodeIdX.setEnabled(false);
        binding.nodeIdX.setTextColor(requireContext().getColor(R.color.black));
        binding.nodeIdY.setEnabled(false);
        binding.nodeIdY.setTextColor(requireContext().getColor(R.color.black));
        binding.latitude.setEnabled(false);
        binding.latitude.setTextColor(requireContext().getColor(R.color.black));
        binding.longitude.setEnabled(false);
        binding.longitude.setTextColor(requireContext().getColor(R.color.black));

        binding.sourceType.setEnabled(false);
        binding.sourceType.setTextColor(requireContext().getColor(R.color.black));
        binding.eqpDeviceIdEdt.setEnabled(false);
        binding.eqpDeviceIdEdt.setTextColor(requireContext().getColor(R.color.black));
        binding.eqpNameEdt.setEnabled(false);
        binding.eqpNameEdt.setTextColor(requireContext().getColor(R.color.black));
        binding.rVoltage.setEnabled(false);
        binding.rVoltage.setTextColor(requireContext().getColor(R.color.black));
        binding.yVoltage.setEnabled(false);
        binding.yVoltage.setTextColor(requireContext().getColor(R.color.black));
        binding.bVoltage.setEnabled(false);
        binding.bVoltage.setTextColor(requireContext().getColor(R.color.black));
    }

    private void getSourceInfo(String nodeId) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("DeviceNumber", nodeId);
        jsonObject.addProperty("DeviceType", "43");
        jsonObject.addProperty("UserType", prefManager.getUserType());
        jsonObject.addProperty("CYMDBNET", prefManager.getDBName());
        Retrofit retrofit = RetrofitClient.getClient(requireContext());
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<Source> call = apiInterface.getSourceData(jsonObject);
        call.enqueue(new Callback<Source>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<Source> call, @NonNull Response<Source> response) {
                if (response.code() == 200) {
                    Source source = response.body();
                    assert source != null;
                    if (source.getOutput().getEquipmentId().equals("DEFAULT")) {
                        binding.eqpLayout.setVisibility(View.GONE);
                        binding.noEqpLayout.setVisibility(View.VISIBLE);

                        if (source.getOutput().getNodeId() != null && !source.getOutput().getNodeId().isBlank()) {
                            binding.nameEdt.setText(source.getOutput().getNodeId());
                        } else {
                            binding.nameEdt.setText(getString(R.string.undefined));
                        }

                    } else {
                        binding.noEqpLayout.setVisibility(View.GONE);
                        binding.eqpLayout.setVisibility(View.VISIBLE);
                        binding.sourceType.setText("Equipvalent (from eq Database)");

                        if (source.getOutput().getEquipmentId() != null && !source.getOutput().getEquipmentId().isBlank()) {
                            binding.eqpDeviceIdEdt.setText(source.getOutput().getEquipmentId());
                        } else {
                            binding.eqpDeviceIdEdt.setText(getString(R.string.undefined));
                        }

                        if (source.getOutput().getNetworkId() != null && !source.getOutput().getNetworkId().isBlank()) {
                            binding.eqpNameEdt.setText(source.getOutput().getNetworkId());
                        } else {
                            binding.eqpNameEdt.setText(getString(R.string.undefined));
                        }

                        if (source.getOutput().getNominalKVLL() != null && !source.getOutput().getNominalKVLL().isBlank()) {
                            binding.rVoltage.setText(source.getOutput().getNominalKVLL());
                            binding.yVoltage.setText(source.getOutput().getNominalKVLL());
                            binding.bVoltage.setText(source.getOutput().getNominalKVLL());
                        }

                        if (source.getOutput().getNodeId() != null && !source.getOutput().getNodeId().isBlank()) {
                            binding.nameEdt.setText(source.getOutput().getNodeId());
                        } else {
                            binding.nameEdt.setText(getString(R.string.undefined));
                        }

                    }

                } else if (response.code() == 401) {
                    prefManager.setIsUserLogin(false);
                    requireActivity().startActivity(new Intent(requireContext(), LoginActivity.class));
                    requireActivity().finish();
                } else {
                    @SuppressLint("InflateParams")
                    View layout = LayoutInflater.from(getActivity()).inflate(R.layout.toast_layout, null);
                    TextView Ok = layout.findViewById(R.id.okBtn);
                    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                    TextView header = layout.findViewById(R.id.headerTv);
                    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                    TextView description = layout.findViewById(R.id.descripTv);
                    header.setText(response.message() + " - " + response.code());
                    description.setText(getString(R.string.error_msg));
                    Ok.setOnClickListener(v -> {
                        getSourceInfo(nodeId);
                    });
                    Toast toast = new Toast(getActivity());
                    toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Source> call, @NonNull Throwable t) {
                @SuppressLint("InflateParams")
                View layout = LayoutInflater.from(getActivity()).inflate(R.layout.toast_layout, null);
                TextView Ok = layout.findViewById(R.id.okBtn);
                @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                TextView header = layout.findViewById(R.id.headerTv);
                @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                TextView description = layout.findViewById(R.id.descripTv);
                header.setText(requireContext().getString(R.string.error));
                description.setText(getString(R.string.error_msg));
                Ok.setOnClickListener(v -> {
                    getSourceInfo(nodeId);
                });
                Toast toast = new Toast(getActivity());
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
}