package com.techLabs.nbpdcl.view.fragment.deviceFragment;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.techLabs.nbpdcl.R;
import com.techLabs.nbpdcl.Utils.PrefManager;
import com.techLabs.nbpdcl.Utils.ResponseDataUtils;
import com.techLabs.nbpdcl.Utils.callBack.SectionCallBack;
import com.techLabs.nbpdcl.databinding.FragmentSpotloadInfoBinding;
import com.techLabs.nbpdcl.models.device.SpotLoad;
import com.techLabs.nbpdcl.retrofit.ApiInterface;
import com.techLabs.nbpdcl.retrofit.RetrofitClient;
import com.techLabs.nbpdcl.view.LayerInfo.DeviceInfo.CustomerDetailsDialog;
import com.techLabs.nbpdcl.view.activity.LoginActivity;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SpotloadFragment extends Fragment {
    private FragmentSpotloadInfoBinding binding;
    private Context mainContext;
    private final JsonObject jsonObject;
    private PrefManager prefManager;
    private final String[] types = {"At From Node", "At To Node", "At Middle Node"};
    private final SectionCallBack sectionCallBack;
    private String sectionID;
    private String phase;
    private String fromNodeId;
    private String fromX;
    private String fromY;
    private String toNodeID;
    private String toNodeX;
    private String toNodeY;
    private String DeviceTypeLine;
    private String LineDeviceNumber;
    private boolean isMixed = false;
    private String customerType;
    private float realPower;
    private float powerFactor;
    private float consumption;
    private float connectedCapacity;
    private float customer;
    private float realPowerA;
    private float realPowerB;
    private float realPowerC;
    private float powerFactorA;
    private float powerFactorB;
    private float powerFactorC;
    private float consumptionA;
    private float consumptionB;
    private float consumptionC;
    private float connectedCapacityA;
    private float connectedCapacityB;
    private float connectedCapacityC;
    private float customerA;
    private float customerB;
    private float customerC;
    private List<SpotLoad.Output.CustomerData> customerData;

    public SpotloadFragment(SectionCallBack sectionCallBack, JsonObject jsonObject) {
        super();
        this.sectionCallBack = sectionCallBack;
        this.jsonObject = jsonObject;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSpotloadInfoBinding.inflate(inflater, container, false);
        mainContext = getContext();
        return binding.getRoot();
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainContext = getContext();
        prefManager = new PrefManager(mainContext);

        if (prefManager.getUserType().equals("Edit")) {
            binding.editSpotloadLayout.setVisibility(View.GONE);
        } else if (prefManager.getEditMode().equals("Survey")) {
            binding.editSpotloadLayout.setVisibility(View.GONE);
        } else {
            binding.editSpotloadLayout.setVisibility(View.GONE);
        }

        if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(mainContext)) {
            getSpotLoadInfo(jsonObject);
        } else {
            Snackbar.make(binding.getRoot(), R.string.no_internet_connection, Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getSpotLoadInfo(jsonObject);
                }
            }).show();
        }

        binding.customDetails.setOnClickListener(v -> {
            if (!binding.spNumberTv.getText().toString().trim().isEmpty()) {
                if (customerData != null && !customerData.isEmpty()) {
                    CustomerDetailsDialog custombtnDialog = new CustomerDetailsDialog(mainContext, customerData);
                    custombtnDialog.show();
                }
            }
        });

        binding.spLoadModelTv.setText("DEFAULT");

        binding.spNumberTv.setEnabled(false);
        binding.spinnerlocationss.setEnabled(false);
        binding.spLoadModelTv.setEnabled(false);
        binding.numOfCustomer.setEnabled(false);
        binding.spCustomerTypeTv.setEnabled(false);

        binding.realPowerA.setEnabled(false);
        binding.realPowerB.setEnabled(false);
        binding.realPowerC.setEnabled(false);
        binding.realPowerTotal.setEnabled(false);
        binding.powerFactorA.setEnabled(false);
        binding.powerFactorB.setEnabled(false);
        binding.powerFactorC.setEnabled(false);
        binding.powerFactorTotal.setEnabled(false);
        binding.consumptionA.setEnabled(false);
        binding.consumptionB.setEnabled(false);
        binding.consumptionC.setEnabled(false);
        binding.consumptionTotal.setEnabled(false);
        binding.connectedCapacityA.setEnabled(false);
        binding.connectedCapacityB.setEnabled(false);
        binding.connectedCapacityC.setEnabled(false);
        binding.connectedCapacityTotal.setEnabled(false);
        binding.customerA.setEnabled(false);
        binding.customerB.setEnabled(false);
        binding.customerC.setEnabled(false);
        binding.customerTotal.setEnabled(false);

        binding.spRealPowerTv.setEnabled(false);
        binding.spPowerFactorTv.setEnabled(false);
        binding.spConsumptionTv.setEnabled(false);
        binding.spConnectedCapacityTv.setEnabled(false);
        binding.spCustomersTv.setEnabled(false);

        binding.canclebtns.setOnClickListener(v -> {

        });

        binding.okbtns.setOnClickListener(v -> {

        });

    }

    private void getSpotLoadInfo(JsonObject jsonObject) {
        binding.spotLoadInfoLayout.setVisibility(GONE);
        binding.shimmerLayout.setVisibility(VISIBLE);
        binding.shimmerView.startShimmer();
        jsonObject.addProperty("UserType", prefManager.getUserType());
        jsonObject.addProperty("CYMDBNET", prefManager.getDBName());
        Retrofit retrofit = RetrofitClient.getClient(mainContext);
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<SpotLoad> call = apiInterface.getSpotLoadData(jsonObject);
        call.enqueue(new Callback<SpotLoad>() {
            @SuppressLint({"SetTextI18n", "DefaultLocale"})
            @Override
            public void onResponse(@NonNull Call<SpotLoad> call, @NonNull Response<SpotLoad> response) {
                if (response.code() == 200) {
                    try {
                        binding.spotLoadInfoLayout.setVisibility(VISIBLE);
                        binding.shimmerLayout.setVisibility(GONE);
                        binding.shimmerView.stopShimmer();
                        SpotLoad spotLoad = response.body();
                        assert spotLoad != null;
                        if (spotLoad.getOutput() != null) {

                            customerData = spotLoad.getOutput().get(0).getCustomerData();
                            if (spotLoad.getOutput().get(0).getPhase() != null) {
                                phase = spotLoad.getOutput().get(0).getPhase().toString();
                            } else {
                                phase = "None";
                            }

                            if (spotLoad.getOutput().get(0).getSectionId() != null) {
                                sectionID = spotLoad.getOutput().get(0).getSectionId();
                            } else {
                                sectionID = "None";
                            }

                            if (spotLoad.getOutput().get(0).getFromNodeX() != null) {
                                fromX = spotLoad.getOutput().get(0).getFromNodeX();
                            } else {
                                fromY = "None";
                            }

                            if (spotLoad.getOutput().get(0).getFromNodeY() != null) {
                                fromY = spotLoad.getOutput().get(0).getFromNodeY();
                            } else {
                                fromY = "None";
                            }

                            if (spotLoad.getOutput().get(0).getFromNodeId() != null) {
                                fromNodeId = spotLoad.getOutput().get(0).getFromNodeId();
                            } else {
                                fromNodeId = "None";
                            }

                            if (spotLoad.getOutput().get(0).getToNodeX() != null) {
                                toNodeX = spotLoad.getOutput().get(0).getToNodeX();
                            } else {
                                toNodeY = "None";
                            }

                            if (spotLoad.getOutput().get(0).getToNodeY() != null) {
                                toNodeY = spotLoad.getOutput().get(0).getToNodeY();
                            } else {
                                toNodeY = "None";
                            }

                            if (spotLoad.getOutput().get(0).getToNodeId() != null) {
                                toNodeID = spotLoad.getOutput().get(0).getToNodeId();
                            } else {
                                toNodeID = "None";
                            }

                            if (spotLoad.getOutput().get(0).getDeviceTypeLine() != null) {
                                DeviceTypeLine = spotLoad.getOutput().get(0).getDeviceTypeLine().toString();
                            } else {
                                DeviceTypeLine = "None";
                            }

                            if (spotLoad.getOutput().get(0).getLineDeviceNumber() != null) {
                                LineDeviceNumber = spotLoad.getOutput().get(0).getLineDeviceNumber();
                            } else {
                                LineDeviceNumber = "None";
                            }

                            if (spotLoad.getOutput().get(0).getDeviceNumber() != null) {
                                binding.spNumberTv.setText(spotLoad.getOutput().get(0).getDeviceNumber());
                            }

                            if (!spotLoad.getOutput().get(0).getCustomerData().isEmpty()) {
                                binding.numOfCustomer.setText(Integer.toString(spotLoad.getOutput().get(0).getCustomerData().size()));
                            }

                            if (spotLoad.getOutput().get(0).getLocation() != null) {
                                ArrayAdapter<String> typeAdapters = new ArrayAdapter<>(mainContext, R.layout.custom_spinner, types);
                                binding.spinnerlocationss.setAdapter(typeAdapters);
                            }

                            if (!spotLoad.getOutput().get(0).getCustomerData().isEmpty()) {
                                if (spotLoad.getOutput().get(0).getCustomerData().get(0).getPhase().get7() == 0) {
                                    binding.singlePhaseCheckedLayout.setVisibility(View.VISIBLE);
                                    binding.threePhaseCheckedLayout.setVisibility(View.GONE);

                                    for (int i = 0; i < spotLoad.getOutput().get(0).getCustomerData().size(); i++) {
                                        customerType = spotLoad.getOutput().get(0).getCustomerData().get(i).getConsumerClassId();
                                        realPowerA = realPowerA + spotLoad.getOutput().get(0).getCustomerData().get(i).getActualKW().get1();
                                        realPowerB = realPowerB + spotLoad.getOutput().get(0).getCustomerData().get(i).getActualKW().get2();
                                        realPowerC = realPowerC + spotLoad.getOutput().get(0).getCustomerData().get(i).getActualKW().get3();

                                        powerFactorA = powerFactorA + spotLoad.getOutput().get(0).getCustomerData().get(i).getPowerFactor().get1();
                                        powerFactorB = powerFactorB + spotLoad.getOutput().get(0).getCustomerData().get(i).getPowerFactor().get2();
                                        powerFactorC = powerFactorC + spotLoad.getOutput().get(0).getCustomerData().get(i).getPowerFactor().get3();

                                        consumptionA = consumptionA + spotLoad.getOutput().get(0).getCustomerData().get(i).getKWH().get1();
                                        consumptionB = consumptionB + spotLoad.getOutput().get(0).getCustomerData().get(i).getKWH().get2();
                                        consumptionC = consumptionC + spotLoad.getOutput().get(0).getCustomerData().get(i).getKWH().get3();

                                        connectedCapacityA = connectedCapacityA + spotLoad.getOutput().get(0).getCustomerData().get(i).getConnectedKVA().get1();
                                        connectedCapacityB = connectedCapacityB + spotLoad.getOutput().get(0).getCustomerData().get(i).getConnectedKVA().get2();
                                        connectedCapacityC = connectedCapacityC + spotLoad.getOutput().get(0).getCustomerData().get(i).getConnectedKVA().get3();

                                        customerA = customerA + spotLoad.getOutput().get(0).getCustomerData().get(i).getCustomerCount().get1();
                                        customerB = customerB + spotLoad.getOutput().get(0).getCustomerData().get(i).getCustomerCount().get2();
                                        customerC = customerC + spotLoad.getOutput().get(0).getCustomerData().get(i).getCustomerCount().get3();
                                        if (!isMixed) {
                                            if (customerType.contentEquals(customerType)) {
                                                isMixed = false;
                                            } else {
                                                isMixed = true;
                                            }
                                        }
                                    }
                                    binding.realPowerA.setText(String.format("%.3f", realPowerA));
                                    binding.realPowerB.setText(String.format("%.3f", realPowerB));
                                    binding.realPowerC.setText(String.format("%.3f", realPowerC));
                                    binding.realPowerTotal.setText(String.format("%.1f", realPowerA + realPowerB + realPowerC) + "kW");

                                    binding.powerFactorA.setText(String.format("%.3f", powerFactorA));
                                    binding.powerFactorB.setText(String.format("%.3f", powerFactorB));
                                    binding.powerFactorC.setText(String.format("%.3f", powerFactorC));
                                    binding.powerFactorTotal.setText(String.format("%.1f", powerFactorA + powerFactorB + powerFactorC) + "%");

                                    binding.consumptionA.setText(String.format("%.3f", consumptionA));
                                    binding.consumptionB.setText(String.format("%.3f", consumptionB));
                                    binding.consumptionC.setText(String.format("%.3f", consumptionC));
                                    binding.consumptionTotal.setText(String.format("%.1f", consumptionA + consumptionB + consumptionC) + "kWh");

                                    binding.connectedCapacityA.setText(String.format("%.3f", connectedCapacityA));
                                    binding.connectedCapacityB.setText(String.format("%.3f", connectedCapacityB));
                                    binding.connectedCapacityC.setText(String.format("%.3f", connectedCapacityC));
                                    binding.connectedCapacityTotal.setText(String.format("%.1f", connectedCapacityA + connectedCapacityB + connectedCapacityC) + "kVA");

                                    binding.customerA.setText(String.format("%.3f", customerA));
                                    binding.customerB.setText(String.format("%.3f", customerB));
                                    binding.customerC.setText(String.format("%.3f", customerC));
                                    binding.customerTotal.setText(String.format("%.1f", customerA + customerB + customerC));
                                } else {
                                    binding.singlePhaseCheckedLayout.setVisibility(View.GONE);
                                    binding.threePhaseCheckedLayout.setVisibility(View.VISIBLE);
                                    for (int i = 0; i < spotLoad.getOutput().get(0).getCustomerData().size(); i++) {
                                        customerType = spotLoad.getOutput().get(0).getCustomerData().get(i).getConsumerClassId();
                                        realPower = realPower + spotLoad.getOutput().get(0).getCustomerData().get(i).getActualKW().get7();
                                        powerFactor = powerFactor + spotLoad.getOutput().get(0).getCustomerData().get(i).getPowerFactor().get7();
                                        consumption = consumption + spotLoad.getOutput().get(0).getCustomerData().get(i).getKWH().get7();
                                        connectedCapacity = connectedCapacity + spotLoad.getOutput().get(0).getCustomerData().get(i).getConnectedKVA().get7();
                                        customer = customer + spotLoad.getOutput().get(0).getCustomerData().get(i).getCustomerCount().get7();
                                        if (!isMixed) {
                                            if (customerType.contentEquals(customerType)) {
                                                isMixed = false;
                                            } else {
                                                isMixed = true;
                                            }
                                        }
                                    }
                                    binding.spRealPowerTv.setText(String.format("%.3f", realPower));
                                    binding.spPowerFactorTv.setText(String.format("%.3f", powerFactor));
                                    binding.spConsumptionTv.setText(String.format("%.3f", consumption));
                                    binding.spConnectedCapacityTv.setText(String.format("%.3f", connectedCapacity));
                                    binding.spCustomersTv.setText(String.format("%.3f", customer));
                                }
                            }

                            if (isMixed) {
                                binding.spCustomerTypeTv.setText("Mixed");
                            } else {
                                binding.spCustomerTypeTv.setText(customerType);
                            }
                            SendData(sectionID, phase, fromNodeId, fromX, fromY, toNodeID, toNodeX, toNodeY, DeviceTypeLine, LineDeviceNumber);
                        }
                    } catch (Exception e) {
                        Log.d("Exception", Objects.requireNonNull(e.getLocalizedMessage()));
                    }
                } else if (response.code() == 401) {
                    binding.spotLoadInfoLayout.setVisibility(VISIBLE);
                    binding.shimmerLayout.setVisibility(GONE);
                    binding.shimmerView.stopShimmer();
                    prefManager.setIsUserLogin(false);
                    requireActivity().startActivity(new Intent(requireContext(), LoginActivity.class));
                    requireActivity().finish();
                } else {
                    binding.spotLoadInfoLayout.setVisibility(VISIBLE);
                    binding.shimmerLayout.setVisibility(GONE);
                    binding.shimmerView.stopShimmer();
                    @SuppressLint("InflateParams")
                    View layout = LayoutInflater.from(mainContext).inflate(R.layout.toast_layout, null);
                    TextView Ok = layout.findViewById(R.id.okBtn);
                    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                    TextView header = layout.findViewById(R.id.headerTv);
                    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                    TextView description = layout.findViewById(R.id.descripTv);
                    header.setText(response.message() + " - " + response.code());
                    description.setText(mainContext.getString(R.string.error_msg));
                    Toast toast = new Toast(mainContext);
                    toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<SpotLoad> call, @NonNull Throwable t) {
                binding.spotLoadInfoLayout.setVisibility(VISIBLE);
                binding.shimmerLayout.setVisibility(GONE);
                binding.shimmerView.stopShimmer();
                @SuppressLint("InflateParams")
                View layout = LayoutInflater.from(mainContext).inflate(R.layout.toast_layout, null);
                TextView Ok = layout.findViewById(R.id.okBtn);
                @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                TextView header = layout.findViewById(R.id.headerTv);
                @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                TextView description = layout.findViewById(R.id.descripTv);
                header.setText(mainContext.getString(R.string.error));
                description.setText(mainContext.getString(R.string.error_msg));
                Toast toast = new Toast(mainContext);
                toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
            }
        });
    }

    private void SendData(String sectionID, String phase, String fromNodeId, String fromX, String fromY, String toNodeID, String toNodeX, String toNodeY, String deviceTypeLine, String lineDeviceNumber) {
        if (sectionCallBack != null) {
            sectionCallBack.OnCableDataReceived(sectionID, phase, fromNodeId, fromX, fromY, toNodeID, toNodeX, toNodeY, deviceTypeLine, lineDeviceNumber);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        requireView().requestLayout();
    }

}