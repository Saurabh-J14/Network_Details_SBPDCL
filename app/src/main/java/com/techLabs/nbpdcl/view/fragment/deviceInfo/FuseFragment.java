package com.techLabs.nbpdcl.view.fragment.deviceInfo;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
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
import com.techLabs.nbpdcl.Utils.callBack.Update;
import com.techLabs.nbpdcl.databinding.FragmentFuseInfoBinding;
import com.techLabs.nbpdcl.models.device.Fuse;
import com.techLabs.nbpdcl.retrofit.ApiInterface;
import com.techLabs.nbpdcl.retrofit.RetrofitClient;
import com.techLabs.nbpdcl.view.activity.LoginActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class FuseFragment extends Fragment {
    private FragmentFuseInfoBinding binding;
    private final SectionCallBack sectionCallBack;
    private PrefManager prefManager;
    private final JsonObject jsonObject;
    private final String voltage;
    private final String[] statusList = {"Connected", "Disconnected"};
    private final String[] locationList = {"At From Node", "At To Node", "UNDEFINED"};
    private final String[] typeState = {"Close", "Open"};
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

    public FuseFragment(SectionCallBack sectionCallBack,Update update, String voltage, JsonObject jsonObject) {
        this.sectionCallBack = sectionCallBack;
        this.jsonObject = jsonObject;
        this.voltage = voltage;
        this.update = update;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFuseInfoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefManager = new PrefManager(getActivity());

        if (prefManager.getUserType().equals("Edit")) {
            binding.editLayout.setVisibility(View.GONE);
        } else if (prefManager.getEditMode().equals("Survey")) {
            binding.editLayout.setVisibility(View.GONE);
        } else {
            binding.editLayout.setVisibility(View.GONE);
        }

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(requireContext(), R.layout.custom_spinner, statusList);
        binding.spinnerstatus.setAdapter(statusAdapter);

        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(requireContext(), R.layout.custom_spinner, locationList);
        binding.spinnerslocations.setAdapter(locationAdapter);

        binding.equipIdSpinnerBar.setEnabled(false);
        binding.numberTv.setEnabled(false);
        binding.spinnerstatus.setEnabled(false);
        binding.spinnerslocations.setEnabled(false);
        binding.voltageTv.setEnabled(false);
        binding.spinnerstates.setEnabled(false);
        binding.ratedCurrent.setEnabled(false);
        binding.ratedVoltage.setEnabled(false);

        if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(requireContext())) {
            getFuseInfo();
        } else {
            Snackbar.make(binding.getRoot(), R.string.no_internet_connection, Snackbar.LENGTH_INDEFINITE).setAction("Retry", v -> getFuseInfo()).show();
        }

    }

    private void getFuseInfo() {
        binding.breakerInfoLayout.setVisibility(GONE);
        binding.shimmerLayout.setVisibility(VISIBLE);
        binding.shimmerView.startShimmer();
        jsonObject.addProperty("UserType", prefManager.getUserType());
        jsonObject.addProperty("CYMDBNET", prefManager.getDBName());
        Retrofit retrofit = RetrofitClient.getClient(requireContext());
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<Fuse> call = apiInterface.getFuseData(jsonObject);
        call.enqueue(new Callback<Fuse>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<Fuse> call, @NonNull Response<Fuse> response) {
                if (response.code() == 200) {
                    try {
                        binding.breakerInfoLayout.setVisibility(VISIBLE);
                        binding.shimmerLayout.setVisibility(GONE);
                        binding.shimmerView.stopShimmer();
                        Fuse fuse = response.body();
                        assert fuse != null;
                        if (fuse.getOutput() != null) {

                            if (fuse.getOutput().getPhase() != null) {
                                phase = fuse.getOutput().getPhase().toString();
                            } else {
                                phase = "None";
                            }

                            if (fuse.getOutput().getSectionId() != null) {
                                sectionID = fuse.getOutput().getSectionId();
                            } else {
                                sectionID = "None";
                            }
                            if (fuse.getOutput().getDeviceNumber() != null) {
                                deviceNumber = fuse.getOutput().getDeviceNumber();
                            } else {
                                deviceNumber = "None";
                            }

                            if (fuse.getOutput().getFromNodeX() != null) {
                                fromX = fuse.getOutput().getFromNodeX();
                            } else {
                                fromX = "None";
                            }

                            if (fuse.getOutput().getFromNodeY() != null) {
                                fromY = fuse.getOutput().getFromNodeY();
                            } else {
                                fromY = "None";
                            }
                            if (fuse.getOutput().getStatus() != null) {
                                status = fuse.getOutput().getStatus();
                            }

                            if (fuse.getOutput().getFromNodeId() != null) {
                                fromNodeId = fuse.getOutput().getFromNodeId();
                            } else {
                                fromNodeId = "None";
                            }

                            if (fuse.getOutput().getToNodeX() != null) {
                                toNodeX = fuse.getOutput().getToNodeX();
                            } else {
                                toNodeX = "None";
                            }

                            if (fuse.getOutput().getToNodeY() != null) {
                                toNodeY = fuse.getOutput().getToNodeY();
                            } else {
                                toNodeY = "None";
                            }

                            if (fuse.getOutput().getToNodeId() != null) {
                                toNodeID = fuse.getOutput().getToNodeId();
                            } else {
                                toNodeID = "None";
                            }

                            if (fuse.getOutput().getDeviceTypeLine() != null) {
                                DeviceTypeLine = fuse.getOutput().getDeviceTypeLine().toString();
                            } else {
                                DeviceTypeLine = "None";
                            }

                            if (fuse.getOutput().getLineDeviceNumber() != null) {
                                LineDeviceNumber = fuse.getOutput().getLineDeviceNumber();
                            } else {
                                LineDeviceNumber = "None";
                            }

                            if (fuse.getOutput().getEquipmentId() != null && !fuse.getOutput().getEquipmentId().isEmpty() && !fuse.getOutput().getEquipmentId().equals("null")) {
                                String[] item = {fuse.getOutput().getEquipmentId()};
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.custom_spinner, item);
                                binding.equipIdSpinnerBar.setAdapter(adapter);
                            }

                            if (fuse.getOutput().getDeviceNumber() != null && !fuse.getOutput().getDeviceNumber().isEmpty() && !fuse.getOutput().getDeviceNumber().equals("null")) {
                                binding.numberTv.setText(fuse.getOutput().getDeviceNumber());
                            }

                            if (fuse.getOutput().getStatus() != null) {
                                if (fuse.getOutput().getStatus() == 0) {
                                    binding.spinnerstatus.setSelection(0);
                                } else {
                                    binding.spinnerstatus.setSelection(1);
                                }
                            }

                            if (fuse.getOutput().getRatedCurrent() != null && !fuse.getOutput().getRatedCurrent().isEmpty()) {
                                binding.ratedCurrent.setText(fuse.getOutput().getRatedCurrent() + " " + "A");
                            }

                            if (fuse.getOutput().getRatedVoltage() != null && !fuse.getOutput().getRatedVoltage().isEmpty()) {
                                binding.ratedVoltage.setText(fuse.getOutput().getRatedVoltage() + " " + "kV");
                            }

                            if (fuse.getOutput().getReversible() != null) {
                                if (fuse.getOutput().getReversible() == 1) {
                                    binding.reversibleChk.setChecked(true);
                                }
                            } else {
                                binding.reversibleChk.setChecked(false);
                            }

                            if (fuse.getOutput().getLocation() != null) {
                                if (fuse.getOutput().getLocation() == 1) {
                                    binding.spinnerslocations.setSelection(0);
                                } else if (fuse.getOutput().getLocation() == 2) {
                                    binding.spinnerslocations.setSelection(1);
                                }
                            }

                            if (fuse.getOutput().getClosedPhase() != null) {
                                ArrayAdapter<String> typeAdapters = new ArrayAdapter<>(requireContext(), R.layout.custom_spinner, typeState);
                                binding.spinnerstates.setAdapter(typeAdapters);
                            }

                            if (fuse.getOutput().getDisconnectedPhase() != null) {
                                if (fuse.getOutput().getDisconnectedPhase().equals("0") || fuse.getOutput().getDisconnectedPhase().equals("1")) {
                                    binding.connectedChk.setChecked(true);
                                }
                            } else {
                                binding.connectedChk.setChecked(false);
                            }

                            if (fuse.getOutput().getDemandType() != null && fuse.getOutput().getIsTotalDemand() != null) {
                                if (fuse.getOutput().getMeterIndex() != null && !fuse.getOutput().getMeterIndex().isEmpty()) {
                                    binding.meterIndex.setText(fuse.getOutput().getMeterIndex());
                                }

                                if (fuse.getOutput().getRefrenceTime() != null && !fuse.getOutput().getRefrenceTime().isEmpty()) {
                                    binding.referenceTime.setText(fuse.getOutput().getRefrenceTime());
                                }
                                if (fuse.getOutput().getDemandType().equals("0") && fuse.getOutput().getIsTotalDemand().equals("0")) {
                                    binding.kwKvarTv.setText("KVA-PF");
                                    if (!fuse.getOutput().getVal1A().equals("null")) {
                                        binding.aKwTv.setText(fuse.getOutput().getVal1A() + "KVA");
                                    }

                                    if (!fuse.getOutput().getVal2A().equals("null")) {
                                        binding.aKvarTv.setText(fuse.getOutput().getVal2A() + "PF%");
                                    }

                                    if (!fuse.getOutput().getVal1B().equals("null")) {
                                        binding.bKwTv.setText(fuse.getOutput().getVal1B() + "KVA");
                                    }

                                    if (!fuse.getOutput().getVal2B().equals("null")) {
                                        binding.bKvarTv.setText(fuse.getOutput().getVal2B() + "PF%");
                                    }

                                    if (!fuse.getOutput().getVal1C().equals("null")) {
                                        binding.cKwTv.setText(fuse.getOutput().getVal1C() + "KVA");
                                    }

                                    if (!fuse.getOutput().getVal2C().equals("null")) {
                                        binding.cKvarTv.setText(fuse.getOutput().getVal2C() + "PF%");
                                    }

                                } else if (fuse.getOutput().getDemandType().equals("2") && fuse.getOutput().getIsTotalDemand().equals("0")) {
                                    binding.kwKvarTv.setText("KW-PF");
                                    if (!fuse.getOutput().getVal1A().equals("null")) {
                                        binding.aKwTv.setText(fuse.getOutput().getVal1A() + "KW");
                                    }

                                    if (!fuse.getOutput().getVal2A().equals("null")) {
                                        binding.aKvarTv.setText(fuse.getOutput().getVal2A() + "PF%");
                                    }

                                    if (!fuse.getOutput().getVal1B().equals("null")) {
                                        binding.bKwTv.setText(fuse.getOutput().getVal1B() + "KW");
                                    }

                                    if (!fuse.getOutput().getVal2B().equals("null")) {
                                        binding.bKvarTv.setText(fuse.getOutput().getVal2B() + "PF%");
                                    }

                                    if (!fuse.getOutput().getVal1C().equals("null")) {
                                        binding.cKwTv.setText(fuse.getOutput().getVal1C() + "KW");
                                    }

                                    if (!fuse.getOutput().getVal2C().equals("null")) {
                                        binding.cKvarTv.setText(fuse.getOutput().getVal2C() + "PF%");
                                    }
                                } else if (fuse.getOutput().getDemandType().equals("3") && fuse.getOutput().getIsTotalDemand().equals("0")) {
                                    binding.kwKvarTv.setText("KW-Kvar");
                                    if (!fuse.getOutput().getVal1A().equals("null")) {
                                        binding.aKwTv.setText(fuse.getOutput().getVal1A() + "KW");
                                    }

                                    if (!fuse.getOutput().getVal2A().equals("null")) {
                                        binding.aKvarTv.setText(fuse.getOutput().getVal2A() + "Kvar");
                                    }

                                    if (!fuse.getOutput().getVal1B().equals("null")) {
                                        binding.bKwTv.setText(fuse.getOutput().getVal1B() + "KW");
                                    }

                                    if (!fuse.getOutput().getVal2B().equals("null")) {
                                        binding.bKvarTv.setText(fuse.getOutput().getVal2B() + "Kvar");
                                    }

                                    if (!fuse.getOutput().getVal1C().equals("null")) {
                                        binding.cKwTv.setText(fuse.getOutput().getVal1C() + "KW");
                                    }

                                    if (!fuse.getOutput().getVal2C().equals("null")) {
                                        binding.cKvarTv.setText(fuse.getOutput().getVal2C() + "Kvar");
                                    }
                                }
                            } else {
                                binding.meterLayout.setVisibility(View.GONE);
                                binding.kwKvarTv.setText("KW-Kvar");
                                if (fuse.getOutput().getVal1A() != null) {
                                    binding.aKwTv.setText(fuse.getOutput().getVal1A() + "KW");
                                }

                                if (fuse.getOutput().getVal2A() != null) {
                                    binding.aKvarTv.setText(fuse.getOutput().getVal2A() + "Kvar");
                                }

                                if (fuse.getOutput().getVal1B() != null) {
                                    binding.bKwTv.setText(fuse.getOutput().getVal1B() + "KW");
                                }

                                if (fuse.getOutput().getVal2B() != null) {
                                    binding.bKvarTv.setText(fuse.getOutput().getVal2B() + "Kvar");
                                }

                                if (fuse.getOutput().getVal1C() != null) {
                                    binding.cKwTv.setText(fuse.getOutput().getVal1C() + "KW");
                                }

                                if (fuse.getOutput().getVal2C() != null) {
                                    binding.cKvarTv.setText(fuse.getOutput().getVal2C() + "Kvar");
                                }
                            }

                            if (voltage != null) {
                                binding.voltageTv.setText(voltage);
                            }

                            SendData(sectionID, phase, fromNodeId, fromX, fromY, toNodeID, toNodeX, toNodeY, DeviceTypeLine, LineDeviceNumber);
                        }
                    } catch (Exception e) {
                        Log.d("exception", e.toString());
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
                    View layout = LayoutInflater.from(getActivity()).inflate(R.layout.toast_layout, null);
                    TextView Ok = layout.findViewById(R.id.okBtn);
                    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                    TextView header = layout.findViewById(R.id.headerTv);
                    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                    TextView description = layout.findViewById(R.id.descripTv);
                    header.setText(response.message() + " - " + response.code());
                    description.setText(requireContext().getString(R.string.error_msg));
                    Toast toast = new Toast(getActivity());
                    toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Fuse> call, @NonNull Throwable t) {
                binding.breakerInfoLayout.setVisibility(VISIBLE);
                binding.shimmerLayout.setVisibility(GONE);
                binding.shimmerView.stopShimmer();
                @SuppressLint("InflateParams")
                View layout = LayoutInflater.from(getActivity()).inflate(R.layout.toast_layout, null);
                TextView Ok = layout.findViewById(R.id.okBtn);
                @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                TextView header = layout.findViewById(R.id.headerTv);
                @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                TextView description = layout.findViewById(R.id.descripTv);
                header.setText(requireContext().getString(R.string.error));
                description.setText(requireContext().getString(R.string.error_msg));
                Toast toast = new Toast(getActivity());
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

    public void isClicked(boolean isCancel) {
        if (update != null) {
            update.isUpdate(binding.equipIdSpinnerBar.getSelectedItem().toString(), "14", deviceNumber, prefManager.getDBName(), String.valueOf(status));
        }
    }

}