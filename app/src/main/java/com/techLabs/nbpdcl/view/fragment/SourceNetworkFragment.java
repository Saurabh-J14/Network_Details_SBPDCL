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
import android.widget.ArrayAdapter;
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
import com.techLabs.nbpdcl.databinding.FragmentSourceNetworkBinding;
import com.techLabs.nbpdcl.models.device.Source;
import com.techLabs.nbpdcl.retrofit.ApiInterface;
import com.techLabs.nbpdcl.retrofit.RetrofitClient;
import com.techLabs.nbpdcl.view.activity.LoginActivity;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SourceNetworkFragment extends Fragment {
    private final String[] networkTypeList = {"Feeder", "Substation", "UNDEFINED"};
    private FragmentSourceNetworkBinding binding;
    private PrefManager prefManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSourceNetworkBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        View MainLayoutBackGround = requireActivity().getWindow().getDecorView().getRootView();
        MainLayoutBackGround.setBackground(requireContext().getDrawable(R.drawable.pop_layout_background));
        prefManager = new PrefManager(getActivity());
        Bundle bundle = this.getArguments();

        if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(requireContext())) {
            assert bundle != null;
            getNetworkInfo(bundle.getString("NodeId"));
        } else {
            final Dialog dialog = new Dialog(requireContext());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.no_internet_dialog);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(requireContext().getDrawable(R.drawable.pop_background));
            LottieAnimationView lottieAnimationView = dialog.findViewById(R.id.animation_view);
            Button RetryBtn = dialog.findViewById(R.id.btnDialog);
            lottieAnimationView.playAnimation();
            RetryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(requireContext())) {
                        assert bundle != null;
                        getNetworkInfo(bundle.getString("NodeId"));
                        dialog.dismiss();
                    }
                }
            });
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();
        }

        ArrayAdapter<String> networkTypeAdapter = new ArrayAdapter<>(requireContext(), R.layout.custom_spinner, networkTypeList);
        binding.networkTypeSpinner.setAdapter(networkTypeAdapter);

        binding.networkName.setEnabled(false);
        binding.networkName.setTextColor(requireContext().getColor(R.color.black));
        binding.networkTypeSpinner.setEnabled(false);
        binding.ambTemp.setEnabled(false);
        binding.ambTemp.setTextColor(requireContext().getColor(R.color.black));
        binding.areaEdt.setEnabled(false);
        binding.areaEdt.setTextColor(requireContext().getColor(R.color.black));
        binding.voltageLevelEdt.setEnabled(false);
        binding.voltageLevelEdt.setTextColor(requireContext().getColor(R.color.black));
        binding.regionEdt.setEnabled(false);
        binding.regionEdt.setTextColor(requireContext().getColor(R.color.black));

    }

    private void getNetworkInfo(String nodeId) {
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
                    if (source.getOutput().getNetworkId() != null && !source.getOutput().getNetworkId().isBlank()) {
                        binding.networkName.setText(source.getOutput().getNetworkId());
                    } else {
                        binding.networkName.setText(getString(R.string.undefined));
                    }

                    if (source.getOutput().getNetworkType() != null && !source.getOutput().getNetworkType().isBlank()) {
                        if (source.getOutput().getNetworkType().equals("0")) {
                            binding.networkTypeSpinner.getItemAtPosition(0);
                        } else {
                            binding.networkTypeSpinner.getItemAtPosition(1);
                        }
                    } else {
                        binding.networkTypeSpinner.getItemAtPosition(2);
                    }

                    if (source.getOutput().getAmbientTemperature() != null && !source.getOutput().getAmbientTemperature().isBlank()) {
                        binding.ambTemp.setText(source.getOutput().getAmbientTemperature());
                    } else {
                        binding.ambTemp.setText("");
                    }

                    if (source.getOutput().getGroup1() != null && !source.getOutput().getGroup1().isBlank()) {
                        binding.areaEdt.setText(source.getOutput().getGroup1());
                    } else {
                        binding.areaEdt.setText("");
                    }

                    if (source.getOutput().getGroup2() != null && !source.getOutput().getGroup2().isBlank()) {
                        binding.voltageLevelEdt.setText(source.getOutput().getGroup2());
                    } else {
                        binding.voltageLevelEdt.setText("");
                    }

                    if (source.getOutput().getGroup3() != null && !source.getOutput().getGroup3().isBlank()) {
                        binding.regionEdt.setText(source.getOutput().getGroup3());
                    } else {
                        binding.regionEdt.setText("");
                    }

                    if (source.getOutput().getGroup4() != null && !source.getOutput().getGroup4().isBlank()) {
                        binding.group4.setText(source.getOutput().getGroup4());
                    } else {
                        binding.group4.setText("");
                    }

                    if (source.getOutput().getGroup5() != null && !source.getOutput().getGroup5().isBlank()) {
                        binding.group5.setText(source.getOutput().getGroup5());
                    } else {
                        binding.group5.setText("");
                    }

                } else if (response.code() == 401) {
                    prefManager.setIsUserLogin(false);
                    requireActivity().startActivity(new Intent(requireContext(), LoginActivity.class));
                    requireActivity().finish();
                } else {
                    @SuppressLint("InflateParams")
                    View layout = LayoutInflater.from(getActivity()).inflate(R.layout.toast_layout, null);
                    TextView Ok = layout.findViewById(R.id.okBtn);
                    @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView header = layout.findViewById(R.id.headerTv);
                    @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView description = layout.findViewById(R.id.descripTv);
                    header.setText(response.message() + " - " + response.code());
                    description.setText(getString(R.string.error_msg));
                    Ok.setOnClickListener(v -> {
                        getNetworkInfo(nodeId);
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
                    getNetworkInfo(nodeId);
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