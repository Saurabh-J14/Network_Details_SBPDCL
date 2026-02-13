package com.techLabs.nbpdcl.view.fragment.shortCircuitAnalysis;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.techLabs.nbpdcl.R;
import com.techLabs.nbpdcl.Utils.PrefManager;
import com.techLabs.nbpdcl.Utils.ResponseDataUtils;
import com.techLabs.nbpdcl.databinding.FragmentShortCircuitBoxBinding;
import com.techLabs.nbpdcl.models.analysis.ShortCircuitBoxModel;
import com.techLabs.nbpdcl.retrofit.ApiInterface;
import com.techLabs.nbpdcl.retrofit.RetrofitClient;
import com.techLabs.nbpdcl.view.activity.LoginActivity;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ShortCircuitBox extends Fragment {

    private FragmentShortCircuitBoxBinding binding;
    private PrefManager prefManager;
    private BottomSheetDialog dialog;
    private Context mainContext;
    private ArrayList<String> networkList = new ArrayList<>();
    private String deviceNumber;
    private String deviceType;

    public ShortCircuitBox(Context mainContext, ArrayList<String> networkList, String deviceNumber, String deviceType) {
        this.mainContext = mainContext;
        this.networkList = networkList;
        this.deviceNumber = deviceNumber;
        this.deviceType = deviceType;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentShortCircuitBoxBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefManager = new PrefManager(mainContext);

        if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(mainContext)) {
            getShortCircuitBox();
        } else {
            final Dialog dialog = new Dialog(mainContext);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.no_internet_dialog);
            dialog.getWindow().setBackgroundDrawable(getContext().getDrawable(R.drawable.pop_background));
            LottieAnimationView lottieAnimationView = dialog.findViewById(R.id.animation_view);
            Button RetryBtn = dialog.findViewById(R.id.btnDialog);
            lottieAnimationView.playAnimation();
            RetryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(mainContext)) {
                        getShortCircuitBox();
                        dialog.dismiss();
                    }
                }
            });
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();
        }

        binding.imgClose.setOnClickListener(v -> {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.remove(ShortCircuitBox.this);
            transaction.commit();
        });

    }

    private void getShortCircuitBox() {
        JsonObject jsonObject = new JsonObject();
        JsonArray jsonArray = new Gson().toJsonTree(networkList).getAsJsonArray();
        jsonObject.addProperty("Username", prefManager.getUserName());
        jsonObject.add("NetworkId", jsonArray);
        jsonObject.addProperty("DeviceNumber", deviceNumber);
        jsonObject.addProperty("DeviceType", deviceType);
        jsonObject.addProperty("CYMDBNET", prefManager.getDBName());
        Retrofit retrofit = RetrofitClient.getClient(mainContext);
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<ShortCircuitBoxModel> call = apiInterface.ShortCircuitBox(jsonObject);
        call.enqueue(new Callback<ShortCircuitBoxModel>() {
            @Override
            public void onResponse(@NonNull Call<ShortCircuitBoxModel> call, @NonNull Response<ShortCircuitBoxModel> response) {
                if (response.code() == 200) {
                    try {
                        assert response.body() != null;
                        ShortCircuitBoxModel shortCircuitBoxModel = response.body();
                        if (shortCircuitBoxModel.getOutput() != null && !shortCircuitBoxModel.getOutput().isEmpty()) {
                            if (shortCircuitBoxModel.getOutput().get(0).getEqNo() != null && !shortCircuitBoxModel.getOutput().get(0).getEqNo().isEmpty() && !shortCircuitBoxModel.getOutput().get(0).getEqNo().equals("null")) {
                                binding.deviceNumber.setText(shortCircuitBoxModel.getOutput().get(0).getEqNo());
                            }

                            if (shortCircuitBoxModel.getOutput().get(0).getLLLampKmax() != null && !shortCircuitBoxModel.getOutput().get(0).getLLLampKmax().isEmpty() && !shortCircuitBoxModel.getOutput().get(0).getLLLampKmax().equals("null")) {
                                if (shortCircuitBoxModel.getOutput().get(0).getLLLampKmaxColor() != null && !shortCircuitBoxModel.getOutput().get(0).getLLLampKmaxColor().equals("null") && !shortCircuitBoxModel.getOutput().get(0).getLLLampKmaxColor().contains("#fff")) {
                                    binding.lll.setBackgroundColor(Color.parseColor(shortCircuitBoxModel.getOutput().get(0).getLLLampKmaxColor()));
                                    binding.lll.setText(shortCircuitBoxModel.getOutput().get(0).getLLLampKmax());
                                } else {
                                    binding.lll.setText(shortCircuitBoxModel.getOutput().get(0).getLLLampKmax());
                                }
                            } else {
                                binding.lll.setText("0.0");
                            }

                            if (shortCircuitBoxModel.getOutput().get(0).getLLGampKmax() != null && !shortCircuitBoxModel.getOutput().get(0).getLLGampKmax().isEmpty() && !shortCircuitBoxModel.getOutput().get(0).getLLGampKmax().equals("null")) {
                                if (shortCircuitBoxModel.getOutput().get(0).getLLampKmaxColor() != null && !shortCircuitBoxModel.getOutput().get(0).getLLampKmaxColor().equals("null") && !shortCircuitBoxModel.getOutput().get(0).getLLampKmaxColor().contains("#fff")) {
                                    binding.llg.setBackgroundColor(Color.parseColor(shortCircuitBoxModel.getOutput().get(0).getLLampKmaxColor()));
                                    binding.llg.setText(shortCircuitBoxModel.getOutput().get(0).getLLGampKmax());
                                } else {
                                    binding.llg.setText(shortCircuitBoxModel.getOutput().get(0).getLLGampKmax());
                                }
                            } else {
                                binding.llg.setText("0.0");
                            }

                            if (shortCircuitBoxModel.getOutput().get(0).getLLampKmax() != null && !shortCircuitBoxModel.getOutput().get(0).getLLampKmax().isEmpty() && !shortCircuitBoxModel.getOutput().get(0).getLLampKmax().equals("null")) {
                                if (shortCircuitBoxModel.getOutput().get(0).getLLampKmaxColor() != null && !shortCircuitBoxModel.getOutput().get(0).getLLampKmaxColor().equals("null") && !shortCircuitBoxModel.getOutput().get(0).getLLampKmaxColor().contains("#fff")) {
                                    binding.ll.setBackgroundColor(Color.parseColor(shortCircuitBoxModel.getOutput().get(0).getLLampKmaxColor()));
                                    binding.ll.setText(shortCircuitBoxModel.getOutput().get(0).getLLampKmax());
                                } else {
                                    binding.ll.setText(shortCircuitBoxModel.getOutput().get(0).getLLampKmax());
                                }
                            } else {
                                binding.ll.setText("0.0");
                            }

                            if (shortCircuitBoxModel.getOutput().get(0).getLGampKmax() != null && !shortCircuitBoxModel.getOutput().get(0).getLGampKmax().isEmpty() && !shortCircuitBoxModel.getOutput().get(0).getLGampKmax().equals("null")) {
                                if (shortCircuitBoxModel.getOutput().get(0).getLGampKmaxColor() != null && !shortCircuitBoxModel.getOutput().get(0).getLGampKmaxColor().equals("null") && !shortCircuitBoxModel.getOutput().get(0).getLGampKmaxColor().contains("#fff")) {
                                    binding.lg.setBackgroundColor(Color.parseColor(shortCircuitBoxModel.getOutput().get(0).getLGampKmaxColor()));
                                    binding.lg.setText(shortCircuitBoxModel.getOutput().get(0).getLGampKmax());
                                } else {
                                    binding.lg.setText(shortCircuitBoxModel.getOutput().get(0).getLGampKmax());
                                }
                            } else {
                                binding.lg.setText("0.0");
                            }

                            if (shortCircuitBoxModel.getOutput().get(0).getLGampKminZ() != null && !shortCircuitBoxModel.getOutput().get(0).getLGampKminZ().isEmpty() && !shortCircuitBoxModel.getOutput().get(0).getLGampKminZ().equals("null")) {
                                if (shortCircuitBoxModel.getOutput().get(0).getLGampKminZColor() != null && !shortCircuitBoxModel.getOutput().get(0).getLGampKminZColor().equals("null") && !shortCircuitBoxModel.getOutput().get(0).getLGampKminZColor().contains("#fff")) {
                                    binding.lgMin.setBackgroundColor(Color.parseColor(shortCircuitBoxModel.getOutput().get(0).getLGampKminZColor()));
                                    binding.lgMin.setText(shortCircuitBoxModel.getOutput().get(0).getLGampKminZ());
                                } else {
                                    binding.lgMin.setText(shortCircuitBoxModel.getOutput().get(0).getLGampKminZ());
                                }
                            } else {
                                binding.lgMin.setText("0.0");
                            }
                        }

                    } catch (Exception e) {
                        Log.d("Exception", Objects.requireNonNull(e.getLocalizedMessage()));;
                    }
                } else if (response.code() == 401){
                    prefManager.setIsUserLogin(false);
                    startActivity(new Intent(requireActivity(), LoginActivity.class));
                    requireActivity().finish();
                }else {
                    Snackbar snack = Snackbar.make(requireActivity().findViewById(android.R.id.content), response.message() + " - " + response.code(), Snackbar.LENGTH_LONG);
                    snack.show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ShortCircuitBoxModel> call, @NonNull Throwable t) {
                Snackbar snack = Snackbar.make(getActivity().findViewById(android.R.id.content), mainContext.getString(R.string.error_msg), Snackbar.LENGTH_LONG);
                snack.show();
            }
        });
    }
}