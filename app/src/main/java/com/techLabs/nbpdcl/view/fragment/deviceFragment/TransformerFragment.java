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
import android.view.inputmethod.InputMethodManager;
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
import com.techLabs.nbpdcl.Utils.callBack.IsCancel;
import com.techLabs.nbpdcl.Utils.callBack.IsClicked;
import com.techLabs.nbpdcl.Utils.callBack.SectionCallBack;
import com.techLabs.nbpdcl.Utils.callBack.Update;
import com.techLabs.nbpdcl.databinding.FragmentTransformerDetailBinding;
import com.techLabs.nbpdcl.models.EquipmentModel;
import com.techLabs.nbpdcl.models.device.Transformer;
import com.techLabs.nbpdcl.retrofit.ApiInterface;
import com.techLabs.nbpdcl.retrofit.RetrofitClient;
import com.techLabs.nbpdcl.view.activity.LoginActivity;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TransformerFragment extends Fragment implements IsClicked {
    private final JsonObject jsonObject;
    private final String voltage;
    private final String[] typeList = {"Connected", "Disconnected"};
    private final String[] types = {"At From Node", "At To Node", "UNDEFINED"};
    private final SectionCallBack sectionCallBack;
    private FragmentTransformerDetailBinding binding;
    private Context mainContext;
    private PrefManager prefManager;
    private IsCancel isCancel;
    private Update update;
    private String sectionID;
    private String deviceNumber;
    private String phase;
    private String fromNodeId;
    private String fromX;
    private String fromY;
    private String toNodeID;
    private String toNodeX;
    private String toNodeY;
    private String DeviceTypeLine;
    private String LineDeviceNumber;
    private String networkId;
    private int status;

    public TransformerFragment(SectionCallBack sectionCallBack, Update update, IsCancel isCancel, String networkId, String voltage, JsonObject jsonObject) {
        this.sectionCallBack = sectionCallBack;
        this.update = update;
        this.isCancel = isCancel;
        this.networkId = networkId;
        this.jsonObject = jsonObject;
        this.voltage = voltage;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentTransformerDetailBinding.inflate(inflater, container, false);
        mainContext = getContext();
        prefManager = new PrefManager(mainContext);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.editTransformerLayout.setVisibility(View.GONE);

        if (prefManager.getEditMode().equalsIgnoreCase("Edit")) {
            binding.equipIdSpinnerBar.setEnabled(false);
        } else if (prefManager.getEditMode().equalsIgnoreCase("Survey")) {
            binding.equipIdSpinnerBar.setEnabled(true);
        } else {
            binding.equipIdSpinnerBar.setEnabled(false);
        }

        ArrayAdapter<String> typeAdapters = new ArrayAdapter<>(mainContext, R.layout.custom_spinner, typeList);
        binding.spinnerstatus.setAdapter(typeAdapters);

        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(mainContext, R.layout.custom_spinner, types);
        binding.spinnerlocations.setAdapter(locationAdapter);

        if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(requireContext())) {
            getTransformerDetails();
            getEquipment(networkId, "Transformer");
        } else {
            Snackbar.make(view, R.string.no_internet_connection, Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getTransformerDetails();
                    getEquipment(networkId, "Transformer");
                }
            }).show();
        }

        binding.equipIdSpinnerBar.setOnClickListener(v -> {
            binding.equipIdSpinnerBar.showDropDown();
            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(binding.equipIdSpinnerBar, InputMethodManager.SHOW_IMPLICIT);
        });

        binding.spNumberTv.setEnabled(false);
        binding.spinnerstatus.setEnabled(false);
        binding.spinnerlocations.setEnabled(false);
        binding.voltageTransformerTv.setEnabled(false);
        binding.dtPrimaryTapATv.setEnabled(false);
        binding.dtPrimaryTapBTv.setEnabled(false);
        binding.dtSecondaryTapATv.setEnabled(false);
        binding.dtSecondaryTapBTv.setEnabled(false);
        binding.dtPrimaryTv.setEnabled(false);
        binding.dtFaultIndicatorTv.setEnabled(false);

    }

    private void getEquipment(String networkId, String transformer) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("NetworkId", networkId);
        jsonObject.addProperty("Type", "Equipment");
        jsonObject.addProperty("Subtype", transformer);
        jsonObject.addProperty("UserType", "Survey");
        jsonObject.addProperty("CYMDBNET", prefManager.getDBName());
        Retrofit retrofit = RetrofitClient.getClient(requireContext());
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<EquipmentModel> call = apiInterface.getEquipmentSurveyData(jsonObject);
        call.enqueue(new Callback<EquipmentModel>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<EquipmentModel> call, @NonNull Response<EquipmentModel> response) {
                if (response.code() == 200) {
                    EquipmentModel equipmentModel = response.body();
                    assert equipmentModel != null;
                    String[] equipArray = equipmentModel.getAllEquipmentId().getEquipmentId().toArray(new String[0]);
                    ArrayAdapter<String> adapters = new ArrayAdapter<>(requireContext(), R.layout.custom_spinner, equipArray);
                    binding.equipIdSpinnerBar.setAdapter(adapters);
                    binding.equipIdSpinnerBar.setThreshold(0);
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
                        getEquipment(networkId, transformer);
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
                    getEquipment(networkId, transformer);
                });
                Toast toast = new Toast(requireContext());
                toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
            }
        });
    }

    private void getTransformerDetails() {
        binding.dtInfoLayout.setVisibility(GONE);
        binding.shimmerLayout.setVisibility(VISIBLE);
        binding.shimmerView.startShimmer();
        jsonObject.addProperty("UserType", prefManager.getUserType());
        jsonObject.addProperty("CYMDBNET", prefManager.getDBName());
        Retrofit retrofit = RetrofitClient.getClient(mainContext);
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<Transformer> call = apiInterface.getTransformerData(jsonObject);
        call.enqueue(new Callback<Transformer>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<Transformer> call, @NonNull Response<Transformer> response) {
                if (response.code() == 200) {
                    try {
                        binding.dtInfoLayout.setVisibility(VISIBLE);
                        binding.shimmerLayout.setVisibility(GONE);
                        binding.shimmerView.stopShimmer();
                        Transformer transformer = response.body();
                        assert transformer != null;
                        if (transformer.getOutput() != null && !transformer.getOutput().toString().isEmpty()) {
                            if (transformer.getOutput().getPhase() != null) {
                                phase = transformer.getOutput().getPhase().toString();
                            } else {
                                phase = "None";
                            }

                            if (transformer.getOutput().getSectionId() != null) {
                                sectionID = transformer.getOutput().getSectionId();
                            } else {
                                sectionID = "None";
                            }

                            if (transformer.getOutput().getDeviceNumber() != null) {
                                deviceNumber = transformer.getOutput().getDeviceNumber();
                            }

                            if (transformer.getOutput().getFromNodeId() != null) {
                                fromNodeId = transformer.getOutput().getFromNodeId();
                            } else {
                                fromNodeId = "None";
                            }

                            if (transformer.getOutput().getToNodeId() != null) {
                                toNodeID = transformer.getOutput().getToNodeId();
                            } else {
                                toNodeID = "None";
                            }

                            if (transformer.getOutput().getFromNodeX() != null) {
                                fromX = transformer.getOutput().getFromNodeX();
                            } else {
                                fromX = "None";
                            }

                            if (transformer.getOutput().getFromNodeY() != null) {
                                fromY = transformer.getOutput().getFromNodeY();
                            } else {
                                fromY = "None";
                            }

                            if (transformer.getOutput().getToNodeX() != null) {
                                toNodeX = transformer.getOutput().getToNodeX();
                            } else {
                                toNodeX = "None";
                            }

                            if (transformer.getOutput().getToNodeY() != null) {
                                toNodeY = transformer.getOutput().getToNodeY();
                            } else {
                                toNodeY = "None";
                            }

                            if (transformer.getOutput().getDeviceTypeLine() != null) {
                                DeviceTypeLine = transformer.getOutput().getDeviceTypeLine().toString();
                            } else {
                                DeviceTypeLine = "None";
                            }

                            if (transformer.getOutput().getLineDeviceNumber() != null) {
                                LineDeviceNumber = transformer.getOutput().getLineDeviceNumber();
                            } else {
                                LineDeviceNumber = "None";
                            }

                            if (transformer.getOutput().getEquipmentId() != null) {
                                binding.equipIdSpinnerBar.setText(transformer.getOutput().getEquipmentId(), false);
                            }

                            if (transformer.getOutput().getStatus() != null) {
                                status = transformer.getOutput().getStatus();
                            }

                            if (!transformer.getOutput().getDeviceNumber().isEmpty() && !transformer.getOutput().getDeviceNumber().equals("null") && transformer.getOutput().getDeviceNumber() != null) {
                                binding.spNumberTv.setText(transformer.getOutput().getDeviceNumber());
                                binding.spNumberTv.setText(transformer.getOutput().getDeviceNumber());
                            }

                            if (transformer.getOutput().getStatus() != null) {
                                if (transformer.getOutput().getStatus() == 0) {
                                    binding.spinnerstatus.setSelection(0);
                                } else {
                                    binding.spinnerstatus.setSelection(1);
                                }
                            }

                            if (transformer.getOutput().getReversible() != null && !transformer.getOutput().getReversible().toString().isEmpty() && !transformer.getOutput().getReversible().toString().equals("null")) {
                                binding.reversibleChk.setChecked(transformer.getOutput().getReversible() == 1);
                            } else {
                                binding.reversibleChk.setChecked(false);
                            }

                            if (!transformer.getOutput().getLocation().toString().isEmpty() && !transformer.getOutput().getLocation().toString().equals("null") && transformer.getOutput().getLocation() != null) {
                                if (transformer.getOutput().getLocation() == 1) {
                                    binding.spinnerlocations.setSelection(0);
                                } else if (transformer.getOutput().getLocation() == 2) {
                                    binding.spinnerlocations.setSelection(1);
                                } else {
                                    binding.spinnerlocations.setSelection(0);
                                }
                            }

                            if (!transformer.getOutput().getFaultIndicator().toString().isEmpty() && !transformer.getOutput().getFaultIndicator().toString().equals("null") && transformer.getOutput().getFaultIndicator() != null) {
                                if (transformer.getOutput().getFaultIndicator() == 1) {
                                    binding.dtFaultIndicatorTv.setText("Visual Fault Indicator");
                                } else if (transformer.getOutput().getFaultIndicator() == 2) {
                                    binding.dtFaultIndicatorTv.setText("Remote Fault Indicator");
                                } else {
                                    binding.dtFaultIndicatorTv.setText("No Fault Indicator");
                                }
                            }

                            if (transformer.getOutput().getPrimaryTapSettingPercent() != null) {
                                binding.dtPrimaryTapATv.setText(transformer.getOutput().getPrimaryTapSettingPercent() + " " + "%");
                            }

                            if (transformer.getOutput().getPrimaryVoltageKVLL() != null) {
                                binding.dtPrimaryTapBTv.setText(transformer.getOutput().getPrimaryVoltageKVLL() + " " + "KVLL");
                            }

                            if (transformer.getOutput().getSecondaryTapSettingPercent() != null) {
                                binding.dtSecondaryTapATv.setText(transformer.getOutput().getSecondaryTapSettingPercent() + " " + "%");
                            }

                            if (transformer.getOutput().getSecondaryVoltageKVLL() != null && !transformer.getOutput().getSecondaryVoltageKVLL().isEmpty() && !transformer.getOutput().getSecondaryVoltageKVLL().equals("null")) {
                                binding.dtSecondaryTapBTv.setText(transformer.getOutput().getSecondaryVoltageKVLL() + " " + "KVLL");
                            }

                            if (transformer.getOutput().getDemandType() != null && transformer.getOutput().getIsTotalDemand() != null) {
                                if (transformer.getOutput().getMeterIndex() != null && !transformer.getOutput().getMeterIndex().isEmpty()) {
                                    binding.meterIndex.setText(transformer.getOutput().getMeterIndex());
                                }

                                if (transformer.getOutput().getRefrenceTime() != null && !transformer.getOutput().getRefrenceTime().isEmpty()) {
                                    binding.refrenceDate.setText(transformer.getOutput().getRefrenceTime());
                                }

                                if (transformer.getOutput().getVal1A() != null) {
                                    binding.fuseAtNodeval1ATv.setText(transformer.getOutput().getVal1A());
                                }

                                if (transformer.getOutput().getVal2A() != null) {
                                    binding.fuseAtNodeval2ATv.setText(transformer.getOutput().getVal1B());
                                }

                                if (transformer.getOutput().getVal1A() != null) {
                                    binding.fuseAtNodeval3ATv.setText(transformer.getOutput().getVal1C());

                                }

                                if (transformer.getOutput().getVal1A() != null) {
                                    binding.fuseAtNodeval1BTv.setText(transformer.getOutput().getVal2A());
                                }

                                if (transformer.getOutput().getVal1A() != null) {
                                    binding.fuseAtNodeval2BTv.setText(transformer.getOutput().getVal2B());
                                }

                                if (transformer.getOutput().getVal1A() != null) {
                                    binding.fuseAtNodeval3BTv.setText(transformer.getOutput().getVal2C());
                                }

                            } else {
                                binding.meterLayout.setVisibility(View.GONE);
                            }

                            if (voltage != null) {
                                binding.voltageTransformerTv.setText(voltage);
                            }
                            SendData(sectionID, phase, fromNodeId, fromX, fromY, toNodeID, toNodeX, toNodeY, DeviceTypeLine, LineDeviceNumber);
                        }
                    } catch (Exception e) {
                        Log.d("Exception", Objects.requireNonNull(e.getLocalizedMessage()));
                    }
                } else if (response.code() == 401) {
                    binding.dtInfoLayout.setVisibility(VISIBLE);
                    binding.shimmerLayout.setVisibility(GONE);
                    binding.shimmerView.stopShimmer();
                    prefManager.setIsUserLogin(false);
                    requireActivity().startActivity(new Intent(requireContext(), LoginActivity.class));
                    requireActivity().finish();
                } else {
                    binding.dtInfoLayout.setVisibility(VISIBLE);
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
            public void onFailure(@NonNull Call<Transformer> call, @NonNull Throwable t) {
                binding.dtInfoLayout.setVisibility(VISIBLE);
                binding.shimmerLayout.setVisibility(GONE);
                binding.shimmerView.stopShimmer();
                @SuppressLint("InflateParams")
                View layout = LayoutInflater.from(mainContext).inflate(R.layout.toast_layout, null);
                TextView Ok = layout.findViewById(R.id.okBtn);
                @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView header = layout.findViewById(R.id.headerTv);
                @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView description = layout.findViewById(R.id.descripTv);
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

    @Override
    public void isClicked(boolean isCancel) {
        if (update != null) {
            update.isUpdate(binding.equipIdSpinnerBar.getText().toString(), "5", deviceNumber, prefManager.getDBName(), String.valueOf(status));
        }
    }
}