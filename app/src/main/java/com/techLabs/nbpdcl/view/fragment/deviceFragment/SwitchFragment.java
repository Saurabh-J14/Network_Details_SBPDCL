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
import com.techLabs.nbpdcl.Utils.callBack.IsCancel;
import com.techLabs.nbpdcl.Utils.callBack.IsClicked;
import com.techLabs.nbpdcl.Utils.callBack.SectionCallBack;
import com.techLabs.nbpdcl.Utils.callBack.Update;
import com.techLabs.nbpdcl.databinding.FragmentSwitchInfoBinding;
import com.techLabs.nbpdcl.models.EquipmentModel;
import com.techLabs.nbpdcl.models.device.Switch;
import com.techLabs.nbpdcl.retrofit.ApiInterface;
import com.techLabs.nbpdcl.retrofit.RetrofitClient;
import com.techLabs.nbpdcl.view.activity.LoginActivity;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class SwitchFragment extends Fragment implements IsClicked {

    public FragmentSwitchInfoBinding binding;
    private Context mainContext;
    private final JsonObject jsonObject;
    private PrefManager prefManager;
    private final String voltage;
    private final String[] typeList = {"Connected", "Disconnected"};
    private final String[] types = {"At From Node", "At To Node", "UNDEFINED"};
    private final String[] typeState = {"Close", "Open"};
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
    private Update update;
    private int status;
    private String networkId;
    private String deviceNumber;
    private String selectedEquipmentId;

    public SwitchFragment(SectionCallBack sectionCallBack,Update update,String networkId, String voltage, JsonObject jsonObject) {
        this.sectionCallBack = sectionCallBack;
        this.jsonObject = jsonObject;
        this.voltage = voltage;
        this.update = update;
        this.networkId = networkId;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSwitchInfoBinding.inflate(inflater, container, false);
        mainContext = getContext();
        prefManager = new PrefManager(mainContext);
        return binding.getRoot();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainContext = getContext();
        prefManager = new PrefManager(mainContext);
        binding.editSwitchLayout.setVisibility(View.GONE);
        if (prefManager.getUserType().equals("Edit")) {
            binding.equipIdSpinnerBar.setEnabled(true);
        } else if (prefManager.getEditMode().equals("Survey")) {
            binding.equipIdSpinnerBar.setEnabled(true);
            binding.numberTv.setEnabled(false);
        } else {
            binding.equipIdSpinnerBar.setEnabled(false);
            binding.numberTv.setEnabled(false);
        }

        if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(requireContext())) {
            getSwitchInfo();
            getEquipment(networkId,"Switch");

        } else {
            Snackbar.make(binding.getRoot(), R.string.no_internet_connection, Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getSwitchInfo();
                    getEquipment(networkId,"Switch");
                }
            }).show();
        }

        ArrayAdapter<String> typeAdapters = new ArrayAdapter<>(mainContext, R.layout.custom_spinner, typeList);
        binding.spinnerstatus.setAdapter(typeAdapters);

        ArrayAdapter<String> locationAdapters = new ArrayAdapter<>(mainContext, R.layout.custom_spinner, types);
        binding.spinnerlocation.setAdapter(locationAdapters);

        binding.equipIdSpinnerBar.setEnabled(true);
        binding.numberTv.setEnabled(false);
        binding.spinnerstatus.setEnabled(false);
        binding.spinnerlocation.setEnabled(false);
        binding.voltageTv.setEnabled(false);
        binding.spinnerstates.setEnabled(false);
        binding.remotelyControlled.setEnabled(false);
        binding.automated.setEnabled(false);
        binding.equipIdSpinnerBar.setOnClickListener(v ->
                binding.equipIdSpinnerBar.showDropDown()
        );

        binding.equipIdSpinnerBar.setOnItemClickListener((parent, itemView, position, id) -> {
            selectedEquipmentId = parent.getItemAtPosition(position).toString();
            binding.equipIdSpinnerBar.setText(selectedEquipmentId, false);
        });

    }

    private void getSwitchInfo() {
        binding.breakerInfoLayout.setVisibility(GONE);
        binding.shimmerLayout.setVisibility(VISIBLE);
        binding.shimmerView.startShimmer();
        jsonObject.addProperty("UserType", prefManager.getUserType());
        jsonObject.addProperty("CYMDBNET", prefManager.getDBName());
        Retrofit retrofit = RetrofitClient.getClient(mainContext);
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<Switch> call = apiInterface.getSwitchData(jsonObject);
        call.enqueue(new Callback<Switch>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<Switch> call, @NonNull Response<Switch> response) {
                if (response.code() == 200) {
                    try {
                        binding.breakerInfoLayout.setVisibility(VISIBLE);
                        binding.shimmerLayout.setVisibility(GONE);
                        binding.shimmerView.stopShimmer();
                        Switch sw = response.body();
                        assert sw != null;
                        if (sw.getOutput() != null) {
                            if (sw.getOutput().getPhase() != null) {
                                phase = sw.getOutput().getPhase().toString();
                            } else {
                                phase = "None";
                            }

                            if (sw.getOutput().getSectionId() != null) {
                                sectionID = sw.getOutput().getSectionId();
                            } else {
                                sectionID = "None";
                            }
                            if(sw.getOutput().getDeviceNumber() != null){
                                deviceNumber = sw.getOutput().getDeviceNumber();
                            }else{
                                deviceNumber = "None";
                            }
                            if (sw.getOutput().getStatus() != null) {
                                status = sw.getOutput().getStatus();
                            }

                            if (sw.getOutput().getFromNodeX() != null) {
                                fromX = sw.getOutput().getFromNodeX();
                            } else {
                                fromX = "None";
                            }

                            if (sw.getOutput().getFromNodeY() != null) {
                                fromY = sw.getOutput().getFromNodeY();
                            } else {
                                fromY = "None";
                            }

                            if (sw.getOutput().getFromNodeId() != null) {
                                fromNodeId = sw.getOutput().getFromNodeId();
                            } else {
                                fromNodeId = "None";
                            }

                            if (sw.getOutput().getToNodeX() != null) {
                                toNodeX = sw.getOutput().getToNodeX();
                            } else {
                                toNodeX = "None";
                            }

                            if (sw.getOutput().getToNodeY() != null) {
                                toNodeY = sw.getOutput().getToNodeY();
                            } else {
                                toNodeY = "None";
                            }

                            if (sw.getOutput().getToNodeId() != null) {
                                toNodeID = sw.getOutput().getToNodeId();
                            } else {
                                toNodeID = "None";
                            }

                            if (sw.getOutput().getDeviceTypeLine() != null) {
                                DeviceTypeLine = sw.getOutput().getDeviceTypeLine().toString();
                            } else {
                                DeviceTypeLine = "None";
                            }

                            if (sw.getOutput().getLineDeviceNumber() != null) {
                                LineDeviceNumber = sw.getOutput().getLineDeviceNumber();
                            } else {
                                LineDeviceNumber = "None";
                            }

                            if (sw.getOutput().getEquipmentId() != null && !sw.getOutput().getEquipmentId().isEmpty()
                                    && !sw.getOutput().getEquipmentId().equals("null")) {
                                if (selectedEquipmentId == null) {
                                    String[] item = {sw.getOutput().getEquipmentId()};
                                    ArrayAdapter<String> adapter = new ArrayAdapter<>(mainContext, R.layout.custom_spinner, item);
                                    binding.equipIdSpinnerBar.setAdapter(adapter);
                                    binding.equipIdSpinnerBar.setText(item[0], false);
                                }
                            }

                            if (!sw.getOutput().getDeviceNumber().isEmpty() && !sw.getOutput().getDeviceNumber().equals("null") && sw.getOutput().getDeviceNumber() != null) {
                                binding.numberTv.setText(sw.getOutput().getDeviceNumber());
                            }

                            if (sw.getOutput().getStatus() != null) {
                                if (sw.getOutput().getStatus() == 0) {
                                    binding.spinnerstatus.setSelection(0);
                                } else {
                                    binding.spinnerstatus.setSelection(1);
                                }
                            }

                            if (sw.getOutput().getDeviceNumber() != null && !sw.getOutput().getDeviceNumber().isEmpty() && !sw.getOutput().getDeviceNumber().equals("null")) {
                                binding.numberTv.setText(sw.getOutput().getDeviceNumber());
                            } else {
                                binding.numberTv.setText("UNDEFINED");
                            }

                            if (sw.getOutput().getReversible() != null) {
                                binding.reversibleChk.setChecked(sw.getOutput().getReversible().equals("1"));
                            }

                            if (sw.getOutput().getLocation() != null) {
                                if (sw.getOutput().getLocation() == 1) {
                                    binding.spinnerlocation.setSelection(0);
                                } else if (sw.getOutput().getLocation() == 2) {
                                    binding.spinnerlocation.setSelection(1);
                                } else {
                                    binding.spinnerlocation.setSelection(2);
                                }
                            }

                            if (sw.getOutput().getClosedPhase() != null) {
                                ArrayAdapter<String> typeAdapters = new ArrayAdapter<>(mainContext, R.layout.custom_spinner, typeState);
                                binding.spinnerstates.setAdapter(typeAdapters);
                            }

                            if (sw.getOutput().getRemoteControlled() != null) {
                                binding.remotelyControlled.setChecked(sw.getOutput().getRemoteControlled() == 1);
                            }

                            if (sw.getOutput().getAutomated() != null) {
                                binding.automated.setChecked(sw.getOutput().getAutomated() == 1);
                            }

                            if (sw.getOutput().getDisconnectedPhase() != null) {
                                if (sw.getOutput().getDisconnectedPhase().equals("0") || sw.getOutput().getDisconnectedPhase().equals("1")) {
                                    binding.connectedChk.setChecked(true);
                                }
                            } else {
                                binding.connectedChk.setChecked(false);
                            }

                            if (sw.getOutput().getDemandType() != null && sw.getOutput().getIsTotalDemand() != null) {

                                if (sw.getOutput().getDemandType().equals("0") && sw.getOutput().getIsTotalDemand().equals("0")) {
                                    binding.kwKvarTv.setText("KVA-PF");
                                    if (sw.getOutput().getVal1A() != null) {
                                        binding.aKwTv.setText(sw.getOutput().getVal1A() + "KVA");
                                    }

                                    if (sw.getOutput().getVal2A() != null) {
                                        binding.aKvarTv.setText(sw.getOutput().getVal2A() + "PF%");
                                    }

                                    if (sw.getOutput().getVal1B() != null) {
                                        binding.bKwTv.setText(sw.getOutput().getVal1B() + "KVA");
                                    }

                                    if (sw.getOutput().getVal2B() != null) {
                                        binding.bKvarTv.setText(sw.getOutput().getVal2B() + "PF%");
                                    }

                                    if (sw.getOutput().getVal1C() != null) {
                                        binding.cKwTv.setText(sw.getOutput().getVal1C() + "KVA");
                                    }

                                    if (sw.getOutput().getVal2C() != null) {
                                        binding.cKvarTv.setText(sw.getOutput().getVal2C() + "PF%");
                                    }

                                } else if (sw.getOutput().getDemandType().equals("2") && sw.getOutput().getIsTotalDemand().equals("0")) {
                                    binding.kwKvarTv.setText("KW-PF");
                                    if (sw.getOutput().getVal1A() != null) {
                                        binding.aKwTv.setText(sw.getOutput().getVal1A() + "KW");
                                    }

                                    if (sw.getOutput().getVal2A() != null) {
                                        binding.aKvarTv.setText(sw.getOutput().getVal2A() + "PF%");
                                    }

                                    if (sw.getOutput().getVal1B() != null) {
                                        binding.bKwTv.setText(sw.getOutput().getVal1B() + "KW");
                                    }

                                    if (sw.getOutput().getVal2B() != null) {
                                        binding.bKvarTv.setText(sw.getOutput().getVal2B() + "PF%");
                                    }

                                    if (sw.getOutput().getVal1C() != null) {
                                        binding.cKwTv.setText(sw.getOutput().getVal1C() + "KW");
                                    }

                                    if (sw.getOutput().getVal2C() != null) {
                                        binding.cKvarTv.setText(sw.getOutput().getVal2C() + "PF%");
                                    }
                                } else if (sw.getOutput().getDemandType().equals("3") && sw.getOutput().getIsTotalDemand().equals("0")) {
                                    binding.kwKvarTv.setText("KW-Kvar");
                                    if (sw.getOutput().getVal1A() != null) {
                                        binding.aKwTv.setText(sw.getOutput().getVal1A() + "KW");
                                    }

                                    if (sw.getOutput().getVal2A() != null) {
                                        binding.aKvarTv.setText(sw.getOutput().getVal2A() + "Kvar");
                                    }

                                    if (sw.getOutput().getVal1B() != null) {
                                        binding.bKwTv.setText(sw.getOutput().getVal1B() + "KW");
                                    }

                                    if (sw.getOutput().getVal2B() != null) {
                                        binding.bKvarTv.setText(sw.getOutput().getVal2B() + "Kvar");
                                    }

                                    if (sw.getOutput().getVal1C() != null) {
                                        binding.cKwTv.setText(sw.getOutput().getVal1C() + "KW");
                                    }

                                    if (sw.getOutput().getVal2C() != null) {
                                        binding.cKvarTv.setText(sw.getOutput().getVal2C() + "Kvar");
                                    }
                                }
                            } else {
                                binding.meterLayout.setVisibility(View.GONE);
                                binding.kwKvarTv.setText("KW-Kvar");
                                if (sw.getOutput().getVal1A() != null) {
                                    binding.aKwTv.setText(sw.getOutput().getVal1A() + "KW");
                                }

                                if (sw.getOutput().getVal2A() != null) {
                                    binding.aKvarTv.setText(sw.getOutput().getVal2A() + "Kvar");
                                }

                                if (sw.getOutput().getVal1B() != null) {
                                    binding.bKwTv.setText(sw.getOutput().getVal1B() + "KW");
                                }

                                if (sw.getOutput().getVal2B() != null) {
                                    binding.bKvarTv.setText(sw.getOutput().getVal2B() + "Kvar");
                                }

                                if (sw.getOutput().getVal1C() != null) {
                                    binding.cKwTv.setText(sw.getOutput().getVal1C() + "KW");
                                }

                                if (sw.getOutput().getVal2C() != null) {
                                    binding.cKvarTv.setText(sw.getOutput().getVal2C() + "Kvar");
                                }
                            }

                            if (voltage != null) {
                                binding.voltageTv.setText(voltage);
                            }

                            SendData(sectionID, phase, fromNodeId, fromX, fromY, toNodeID, toNodeX, toNodeY, DeviceTypeLine, LineDeviceNumber);
                        }
                    } catch (Exception e) {
                        Log.d("Exception", Objects.requireNonNull(e.getLocalizedMessage()));
                    }
                } else if (response.code() == 401) {
                    binding.breakerInfoLayout.setVisibility(VISIBLE);
                    binding.shimmerLayout.setVisibility(GONE);
                    binding.shimmerView.stopShimmer();
                    prefManager.setIsUserLogin(false);
                    requireActivity().startActivity(new Intent(requireContext(), LoginActivity.class));
                    requireActivity().finish();
                } else {
                    binding.breakerInfoLayout.setVisibility(VISIBLE);
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
            public void onFailure(@NonNull Call<Switch> call, @NonNull Throwable t) {
                binding.breakerInfoLayout.setVisibility(VISIBLE);
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

    private void getEquipment(String networkId, String Switch) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("NetworkId", networkId);
        jsonObject.addProperty("Type", "Equipment");
        jsonObject.addProperty("Subtype", Switch);
        jsonObject.addProperty("UserType", "Survey");
        jsonObject.addProperty("CYMDBNET", prefManager.getDBName());
        Retrofit retrofit = RetrofitClient.getClient(requireContext());
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<EquipmentModel> call = apiInterface.getEquipmentData(jsonObject);

        call.enqueue(new Callback<EquipmentModel>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<EquipmentModel> call, @NonNull Response<EquipmentModel> response) {
                if (response.code() == 200) {
                    EquipmentModel equipmentModel = response.body();
                    if (equipmentModel != null
                            && equipmentModel.getAllEquipmentId() != null
                            && equipmentModel.getAllEquipmentId().getEquipmentId() != null) {

                        String[] equipArray = equipmentModel
                                .getAllEquipmentId()
                                .getEquipmentId()
                                .toArray(new String[0]);

                        ArrayAdapter<String> adapters = new ArrayAdapter<>(
                                requireContext(), R.layout.custom_spinner, equipArray);
                        String currentSelected = binding.equipIdSpinnerBar.getText().toString();

                        binding.equipIdSpinnerBar.setAdapter(adapters);
                        binding.equipIdSpinnerBar.setThreshold(0);

                        if (equipArray.length > 0) {
                            if (!currentSelected.isEmpty()) {
                                binding.equipIdSpinnerBar.setText(currentSelected, false);
                            } else {
                                binding.equipIdSpinnerBar.setText(equipArray[0], false);
                            }
                        }
                    } else {
                        binding.equipIdSpinnerBar.setAdapter(null);
                    }
                } else {

                    @SuppressLint("InflateParams")
                    View layout = LayoutInflater.from(requireContext()).inflate(R.layout.toast_layout, null);
                    TextView Ok = layout.findViewById(R.id.okBtn);
                    TextView header = layout.findViewById(R.id.headerTv);
                    TextView description = layout.findViewById(R.id.descripTv);

                    header.setText(response.message() + " - " + response.code());
                    description.setText(requireContext().getString(R.string.error_msg));

                    Ok.setOnClickListener(v -> getEquipment(networkId, Switch));

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
                TextView header = layout.findViewById(R.id.headerTv);
                TextView description = layout.findViewById(R.id.descripTv);

                header.setText(requireContext().getString(R.string.error));
                description.setText(requireContext().getString(R.string.error_msg));

                Ok.setOnClickListener(v -> getEquipment(networkId, Switch));

                Toast toast = new Toast(requireContext());
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

    @Override
    public void isClicked(boolean isCancel) {
        if (update != null) {
            update.isUpdate(binding.equipIdSpinnerBar.getText().toString(), "13", deviceNumber, prefManager.getDBName(), String.valueOf(status));
        }

    }
}