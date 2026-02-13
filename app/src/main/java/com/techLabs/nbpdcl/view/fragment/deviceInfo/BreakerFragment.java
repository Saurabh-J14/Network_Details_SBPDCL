package com.techLabs.nbpdcl.view.fragment.deviceInfo;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Context;
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
import com.techLabs.nbpdcl.databinding.BreakerFragmentBinding;
import com.techLabs.nbpdcl.models.EquipmentModel;
import com.techLabs.nbpdcl.models.device.Breaker;
import com.techLabs.nbpdcl.retrofit.ApiInterface;
import com.techLabs.nbpdcl.retrofit.RetrofitClient;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class BreakerFragment extends Fragment implements IsClicked {
    private BreakerFragmentBinding binding;
    private Context mainContext;
    private PrefManager prefManager;
    private final JsonObject jsonObject;
    private final String voltage;
    private final String[] statusList = {"Connected", "Disconnected"};
    private final String[] location = {"At From Node", "At To Node", "UNDEFINED"};
    private final String[] state = {"Close", "Open"};
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
    private final Update update;
    private String deviceNumber;
    private int status;
    private final String networkId;
    private String currentEquipmentId;
    private ArrayList<String> equipmentItems;
    private ArrayAdapter<String> equipmentAdapter;

    public BreakerFragment(SectionCallBack sectionCallBack,Update update,String networkId,  String voltage, JsonObject jsonObject) {
        this.sectionCallBack = sectionCallBack;
        this.jsonObject = jsonObject;
        this.voltage = voltage;
        this.update = update;
        this.networkId = networkId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = BreakerFragmentBinding.inflate(inflater, container, false);
        mainContext = getContext();
        prefManager = new PrefManager(mainContext);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (prefManager.getUserType().equals("Edit")) {
            binding.editBreakerLayout.setVisibility(GONE);
        } else if (prefManager.getEditMode().equals("Survey")) {
            binding.editBreakerLayout.setVisibility(GONE);
            binding.equipIdSpinnerBar.setEnabled(true);
        } else {
            binding.editBreakerLayout.setVisibility(GONE);
            binding.equipIdSpinnerBar.setEnabled(false);
        }

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(mainContext, R.layout.custom_spinner, statusList);
        binding.spinnerstatus.setAdapter(statusAdapter);

        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(mainContext, R.layout.custom_spinner, location);
        binding.spinnerlocation.setAdapter(locationAdapter);

        ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(mainContext, R.layout.custom_spinner, state);
        binding.spinnerstate.setAdapter(stateAdapter);

        binding.equipIdSpinnerBar.setEnabled(true);
        binding.numberTv.setEnabled(false);
        binding.breakerType.setEnabled(false);
        binding.ratedCurrent.setEnabled(false);
        binding.breakerIntK.setEnabled(false);
        binding.spinnerstatus.setEnabled(false);
        binding.spinnerlocation.setEnabled(false);
        binding.voltageTv.setEnabled(false);
        binding.spinnerstate.setEnabled(false);
        binding.remotelyControlled.setEnabled(false);
        binding.automated.setEnabled(false);

        if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(mainContext)) {
            getBreakerInfo();
            getEquipment(networkId,"Breaker");

        } else {
            Snackbar.make(view, R.string.no_internet_connection, Snackbar.LENGTH_INDEFINITE)
                    .setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getBreakerInfo();
                            getEquipment(networkId,"Breaker");
                        }
                    }).show();
        }

    }

    private void getBreakerInfo() {
        binding.breakerInfoLayout.setVisibility(GONE);
        binding.shimmerLayout.setVisibility(VISIBLE);
        binding.shimmerView.startShimmer();
        jsonObject.addProperty("UserType", prefManager.getUserType());
        jsonObject.addProperty("CYMDBNET", prefManager.getDBName());
        Retrofit retrofit = RetrofitClient.getClient(mainContext);
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<Breaker> call = apiInterface.getBreakerData(jsonObject);
        call.enqueue(new Callback<Breaker>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<Breaker> call, @NonNull Response<Breaker> response) {
                if (response.code() == 200) {
                    try {
                        binding.breakerInfoLayout.setVisibility(VISIBLE);
                        binding.shimmerLayout.setVisibility(GONE);
                        binding.shimmerView.stopShimmer();
                        Breaker breaker = response.body();
                        assert breaker != null;
                        if (breaker.getOutput() != null) {

                            if (breaker.getOutput().getPhase() != null) {
                                phase = breaker.getOutput().getPhase().toString();
                            } else {
                                phase = "None";
                            }

                            if (breaker.getOutput().getSectionId() != null) {
                                sectionID = breaker.getOutput().getSectionId();
                            } else {
                                sectionID = "None";
                            }

                            if (breaker.getOutput().getDeviceNumber() != null) {
                                deviceNumber = breaker.getOutput().getDeviceNumber();
                            } else {
                                deviceNumber = "None";
                            }

                            if (breaker.getOutput().getStatus() != null) {
                                status = breaker.getOutput().getStatus();
                            }

                            if (breaker.getOutput().getFromNodeId() != null)    {
                                fromNodeId = breaker.getOutput().getFromNodeId();
                            } else {
                                fromNodeId = "None";
                            }

                            if (breaker.getOutput().getFromNodeX() != null) {
                                fromX = breaker.getOutput().getFromNodeX().toString();
                            } else {
                                fromX = "None";
                            }

                            if (breaker.getOutput().getFromNodeY() != null) {
                                fromY = breaker.getOutput().getFromNodeY().toString();
                            } else {
                                fromY = "None";
                            }

                            if (breaker.getOutput().getToNodeId() != null) {
                                toNodeID = breaker.getOutput().getToNodeId();
                            } else {
                                toNodeID = "None";
                            }

                            if (breaker.getOutput().getToNodeX() != null) {
                                toNodeX = breaker.getOutput().getToNodeX().toString();
                            } else {
                                toNodeY = "None";
                            }

                            if (breaker.getOutput().getToNodeY() != null) {
                                toNodeY = breaker.getOutput().getToNodeY().toString();
                            } else {
                                toNodeY = "None";
                            }

                            if (breaker.getOutput().getDeviceTypeLine() != null) {
                                DeviceTypeLine = breaker.getOutput().getDeviceTypeLine().toString();
                            } else {
                                DeviceTypeLine = "None";
                            }

                            if (breaker.getOutput().getLineDeviceNumber() != null) {
                                LineDeviceNumber = breaker.getOutput().getLineDeviceNumber();
                            } else {
                                LineDeviceNumber = "None";
                            }

//                            if (breaker.getOutput().getEquipmentId() != null && !breaker.getOutput().getEquipmentId().isEmpty() && !breaker.getOutput().getEquipmentId().equals("null")) {
//                                currentEquipmentId = breaker.getOutput().getEquipmentId();
//                                String[] item = {breaker.getOutput().getEquipmentId()};
//                                ArrayAdapter<String> adapter = new ArrayAdapter<>(mainContext, R.layout.custom_spinner, item);
//                                binding.equipIdSpinnerBar.setAdapter(adapter);
//                            }
                            if (breaker.getOutput().getEquipmentId() != null &&
                                    !breaker.getOutput().getEquipmentId().isEmpty() &&
                                    !breaker.getOutput().getEquipmentId().equals("null")) {
                                currentEquipmentId = breaker.getOutput().getEquipmentId();
                            }

                            if (breaker.getOutput().getDeviceNumber() != null && !breaker.getOutput().getDeviceNumber().isEmpty() && !breaker.getOutput().getDeviceNumber().equals("null")) {
                                binding.numberTv.setText(breaker.getOutput().getDeviceNumber());
                            } else {
                                binding.numberTv.setText("UNDEFINED");
                            }

                            if (breaker.getOutput().getModel() != null && !breaker.getOutput().getModel().isEmpty()) {
                                binding.breakerType.setText(breaker.getOutput().getModel());
                            }

                            if (breaker.getOutput().getRatedCurrent() != null && !breaker.getOutput().getRatedCurrent().toString().isEmpty()) {
                                binding.ratedCurrent.setText(breaker.getOutput().getRatedCurrent().toString() + "  " + "A");
                            }

                            if (breaker.getOutput().getInterruptingRating() != null && !breaker.getOutput().getInterruptingRating().toString().isEmpty()) {
                                binding.breakerIntK.setText(breaker.getOutput().getInterruptingRating().toString() + "  " + "KA");
                            }

                            if (breaker.getOutput().getStatus() != null) {
                                if (breaker.getOutput().getStatus() == 0) {
                                    binding.spinnerstatus.setSelection(0);
                                } else if (breaker.getOutput().getStatus() == 1) {
                                    binding.spinnerstatus.setSelection(1);
                                } else {
                                    binding.spinnerstatus.setSelection(2);
                                }
                            } else {
                                binding.spinnerstatus.setSelection(3);
                            }

                            if (breaker.getOutput().getReversible() != null) {
                                if (breaker.getOutput().getReversible() == 1) {
                                    binding.reversibleChk.setChecked(true);
                                }
                            } else {
                                binding.reversibleChk.setChecked(false);
                            }

                            if (breaker.getOutput().getLocation() != null) {
                                if (breaker.getOutput().getLocation() == 1) {
                                    binding.spinnerlocation.setSelection(0);
                                } else if (breaker.getOutput().getLocation() == 2) {
                                    binding.spinnerlocation.setSelection(1);
                                }
                            } else {
                                binding.spinnerlocation.setSelection(2);
                            }

                            if (breaker.getOutput().getClosedPhase() != null) {
                                if (breaker.getOutput().getClosedPhase() == 7) {
                                    binding.spinnerstate.setSelection(0);
                                } else if (breaker.getOutput().getClosedPhase() == 0) {
                                    binding.spinnerstate.setSelection(1);
                                }
                            } else {
                                binding.spinnerstate.setSelection(2);
                            }

                            if (breaker.getOutput().getRemoteControlled() != null) {
                                binding.remotelyControlled.setChecked(breaker.getOutput().getRemoteControlled() == 1);
                            }

                            if (breaker.getOutput().getAutomated() != null) {
                                binding.automated.setChecked(breaker.getOutput().getAutomated() == 1);
                            }

                            if (voltage != null) {
                                binding.voltageTv.setText(voltage);
                            }

                            SendData(sectionID, phase, fromNodeId, fromX, fromY, toNodeID, toNodeX, toNodeY, DeviceTypeLine, LineDeviceNumber);

                        }
                    } catch (Exception e) {
                        Log.d("exception", e.toString());
                    }
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
            public void onFailure(@NonNull Call<Breaker> call, @NonNull Throwable t) {
                binding.breakerInfoLayout.setVisibility(VISIBLE);
                binding.shimmerLayout.setVisibility(GONE);
                binding.shimmerView.stopShimmer();
                @SuppressLint("InflateParams") View layout = LayoutInflater.from(mainContext).inflate(R.layout.toast_layout, null);
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

    private void getEquipment(String networkId, String Breaker) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("NetworkId", networkId);
        jsonObject.addProperty("Type", "Equipment");
        jsonObject.addProperty("Subtype", Breaker);
        jsonObject.addProperty("UserType", "Survey");
        jsonObject.addProperty("CYMDBNET", prefManager.getDBName());
        Retrofit retrofit = RetrofitClient.getClient(requireContext());
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<EquipmentModel> call = apiInterface.getEquipmentData(jsonObject);

        call.enqueue(new Callback<EquipmentModel>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<EquipmentModel> call, @NonNull Response<EquipmentModel> response) {
                if (response.code() == 200 && response.body() != null) {
                    EquipmentModel model = response.body();
                    if (model.getAllEquipmentId() != null && !model.getAllEquipmentId().getEquipmentId().isEmpty()) {
                        equipmentItems = new ArrayList<>(model.getAllEquipmentId().getEquipmentId());

                        if (currentEquipmentId != null && !equipmentItems.contains(currentEquipmentId)) {
                            equipmentItems.add(0, currentEquipmentId);
                        }
                    } else {

                        equipmentItems = new ArrayList<>();
                        if (currentEquipmentId != null) {
                            equipmentItems.add(currentEquipmentId);
                        }
                    }

                    equipmentAdapter = new ArrayAdapter<>(requireContext(), R.layout.custom_spinner, equipmentItems);
                    binding.equipIdSpinnerBar.setAdapter(equipmentAdapter);

//                    binding.equipIdSpinnerBar.post(() -> {
//                        if (currentEquipmentId != null && !equipmentItems.isEmpty()) {
//                            int pos = equipmentItems.indexOf(currentEquipmentId);
//                            if (pos >= 0) {
//                                binding.equipIdSpinnerBar.setSelection(pos);
//                            }
//                        }
//                    });
                    binding.equipIdSpinnerBar.post(() -> {
                        if (currentEquipmentId != null && !equipmentItems.isEmpty()) {
                            int pos = equipmentItems.indexOf(currentEquipmentId);
                            if (pos >= 0 && pos < equipmentAdapter.getCount()) {
                                binding.equipIdSpinnerBar.setSelection(pos, false);
                            }
                        }
                    });
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

                Ok.setOnClickListener(v -> getEquipment(networkId, Breaker));

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
            update.isUpdate(binding.equipIdSpinnerBar.getSelectedItem().toString(), "8", deviceNumber, prefManager.getDBName(), String.valueOf(status));
        }
    }
}