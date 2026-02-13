package com.techLabs.nbpdcl.view.fragment.deviceInfo;

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
import com.techLabs.nbpdcl.databinding.FragmentShuntCapacitorBinding;
import com.techLabs.nbpdcl.models.device.ShuntCapacitor;
import com.techLabs.nbpdcl.retrofit.ApiInterface;
import com.techLabs.nbpdcl.retrofit.RetrofitClient;
import com.techLabs.nbpdcl.view.activity.LoginActivity;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ShuntCapacitorFragment extends Fragment {

    private FragmentShuntCapacitorBinding binding;
    private Context mainContext;
    private PrefManager prefManager;
    private final JsonObject jsonObject;
    private final SectionCallBack sectionCallBack;
    private final String[] statusArray = {"Connected", "DisConnected"};
    private final String[] locationArray = {"At From Node", "At To Node", "At Middle Node"};
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

    public ShuntCapacitorFragment(SectionCallBack sectionCallBack, JsonObject jsonObject) {
        this.sectionCallBack = sectionCallBack;
        this.jsonObject = jsonObject;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentShuntCapacitorBinding.inflate(inflater, container, false);
        mainContext = getContext();
        return binding.getRoot();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainContext = getContext();
        prefManager = new PrefManager(mainContext);

        if (prefManager.getUserType().equals("Edit")) {
            binding.editShuntLayout.setVisibility(View.GONE);
        } else if (prefManager.getEditMode().equals("Survey")) {
            binding.editShuntLayout.setVisibility(View.GONE);
        } else {
            binding.editShuntLayout.setVisibility(View.GONE);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(mainContext, R.layout.custom_spinner, statusArray);
        binding.spinnerstatus.setAdapter(adapter);

        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(mainContext, R.layout.custom_spinner, locationArray);
        binding.locationSpinner.setAdapter(locationAdapter);

        if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(requireContext())) {
            getCapacitorInfo();
        } else {
            Snackbar.make(binding.getRoot(), R.string.no_internet_connection, Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getCapacitorInfo();
                }
            }).show();
        }

        binding.equipIdSpinnerBar.setEnabled(false);
        binding.shuntNumberTv.setEnabled(false);
        binding.spinnerstatus.setEnabled(false);
        binding.locationSpinner.setEnabled(false);
        binding.shuntTypesTv.setEnabled(false);
        binding.shuntRatedVoltageTv.setEnabled(false);
        binding.shuntInterruptingRatingTv.setEnabled(false);

    }

    private void getCapacitorInfo() {
        binding.shuntCapacitorInfoLayout.setVisibility(GONE);
        binding.shimmerLayout.setVisibility(VISIBLE);
        binding.shimmerView.startShimmer();
        jsonObject.addProperty("UserType", prefManager.getUserType());
        jsonObject.addProperty("CYMDBNET", prefManager.getDBName());
        Retrofit retrofit = RetrofitClient.getClient(mainContext);
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<ShuntCapacitor> call = apiInterface.getShuntCapacitorData(jsonObject);
        call.enqueue(new Callback<ShuntCapacitor>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<ShuntCapacitor> call, @NonNull Response<ShuntCapacitor> response) {
                if (response.code() == 200) {
                    try {
                        binding.shuntCapacitorInfoLayout.setVisibility(VISIBLE);
                        binding.shimmerLayout.setVisibility(GONE);
                        binding.shimmerView.stopShimmer();
                        ShuntCapacitor shuntCapacitor = response.body();
                        assert shuntCapacitor != null;
                        if (shuntCapacitor.getOutput() != null) {

                            if (shuntCapacitor.getOutput().getPhase() != null) {
                                phase = shuntCapacitor.getOutput().getPhase().toString();
                            } else {
                                phase = "None";
                            }

                            if (shuntCapacitor.getOutput().getSectionId() != null) {
                                sectionID = shuntCapacitor.getOutput().getSectionId();
                            } else {
                                sectionID = "None";
                            }

                            if (shuntCapacitor.getOutput().getFromx() != null) {
                                fromX = shuntCapacitor.getOutput().getFromx();
                            } else {
                                fromX = "None";
                            }

                            if (shuntCapacitor.getOutput().getFromy() != null) {
                                fromY = shuntCapacitor.getOutput().getFromy();
                            } else {
                                fromY = "None";
                            }

                            if (shuntCapacitor.getOutput().getFromNodeId() != null) {
                                fromNodeId = shuntCapacitor.getOutput().getFromNodeId();
                            } else {
                                fromNodeId = "None";
                            }

                            if (shuntCapacitor.getOutput().getTox() != null) {
                                toNodeX = shuntCapacitor.getOutput().getTox();
                            } else {
                                toNodeX = "None";
                            }

                            if (shuntCapacitor.getOutput().getToy() != null) {
                                toNodeY = shuntCapacitor.getOutput().getToy();
                            } else {
                                toNodeY = "None";
                            }

                            if (shuntCapacitor.getOutput().getToNodeId() != null) {
                                toNodeID = shuntCapacitor.getOutput().getToNodeId();
                            } else {
                                toNodeID = "None";
                            }

                            if (shuntCapacitor.getOutput().getDeviceTypeLine() != null) {
                                DeviceTypeLine = shuntCapacitor.getOutput().getDeviceTypeLine().toString();
                            } else {
                                DeviceTypeLine = "None";
                            }

                            if (shuntCapacitor.getOutput().getLineDeviceNumber() != null) {
                                LineDeviceNumber = shuntCapacitor.getOutput().getLineDeviceNumber();
                            } else {
                                LineDeviceNumber = "None";
                            }

                            if (shuntCapacitor.getOutput().getPhase() != null) {
                                if (shuntCapacitor.getOutput().getPhase() == 7) {
                                    binding.fxAPhase.setChecked(true);
                                    binding.fxBPhase.setChecked(true);
                                    binding.fxCPhase.setChecked(true);
                                    binding.switchedAPhase.setChecked(true);
                                    binding.switchedBPhase.setChecked(true);
                                    binding.switchedCPhase.setChecked(true);
                                } else if (shuntCapacitor.getOutput().getPhase() == 1) {
                                    binding.fxAPhase.setChecked(true);
                                    binding.fxBPhase.setChecked(false);
                                    binding.fxCPhase.setChecked(false);
                                    binding.switchedAPhase.setChecked(true);
                                    binding.switchedBPhase.setChecked(false);
                                    binding.switchedCPhase.setChecked(false);
                                } else if (shuntCapacitor.getOutput().getPhase() == 2) {
                                    binding.fxAPhase.setChecked(false);
                                    binding.fxBPhase.setChecked(true);
                                    binding.fxCPhase.setChecked(false);
                                    binding.switchedAPhase.setChecked(false);
                                    binding.switchedBPhase.setChecked(true);
                                    binding.switchedCPhase.setChecked(false);
                                } else if (shuntCapacitor.getOutput().getPhase() == 3) {
                                    binding.fxAPhase.setChecked(false);
                                    binding.fxBPhase.setChecked(false);
                                    binding.fxCPhase.setChecked(true);
                                    binding.switchedAPhase.setChecked(false);
                                    binding.switchedBPhase.setChecked(false);
                                    binding.switchedCPhase.setChecked(true);
                                }
                            }

                            if (shuntCapacitor.getOutput().getEquipmentId() != null && !shuntCapacitor.getOutput().getEquipmentId().isEmpty() && !shuntCapacitor.getOutput().getEquipmentId().equals("null")) {
                                String[] item = {shuntCapacitor.getOutput().getEquipmentId()};
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(mainContext, R.layout.custom_spinner, item);
                                binding.equipIdSpinnerBar.setAdapter(adapter);
                            }

                            if (!shuntCapacitor.getOutput().getDeviceNumber().isEmpty() && !shuntCapacitor.getOutput().getDeviceNumber().equals("null") && shuntCapacitor.getOutput().getDeviceNumber() != null) {
                                binding.shuntNumberTv.setText(shuntCapacitor.getOutput().getDeviceNumber());
                            }

                            if (shuntCapacitor.getOutput().getStatus() != null) {
                                if (shuntCapacitor.getOutput().getStatus() == 0) {
                                    binding.spinnerstatus.setSelection(0);
                                } else {
                                    binding.spinnerstatus.setSelection(1);
                                }
                            }

                            if (shuntCapacitor.getOutput().getLocation() != null) {
                                if (shuntCapacitor.getOutput().getLocation() == 1) {
                                    binding.locationSpinner.setSelection(0);
                                } else if (shuntCapacitor.getOutput().getLocation() == 2) {
                                    binding.locationSpinner.setSelection(1);
                                } else {
                                    binding.locationSpinner.setSelection(2);
                                }
                            }

                            if (shuntCapacitor.getOutput().getKvln() != null) {
                                binding.shuntRatedVoltageTv.setText(shuntCapacitor.getOutput().getKvln() + " " + "KVLN");
                            }

                            if (shuntCapacitor.getOutput().getInterruptingRating() != null) {
                                binding.shuntInterruptingRatingTv.setText(shuntCapacitor.getOutput().getInterruptingRating() + " " + "A");
                            }

                            if (shuntCapacitor.getOutput().getRatedKVAR() != null) {
                                binding.shuntRatedPowerTv.setText(shuntCapacitor.getOutput().getRatedKVAR() + " " + "Kvar");
                                binding.switchRatedPowerTv.setText(shuntCapacitor.getOutput().getRatedKVAR() + " " + "Kvar");
                            }

                            if (shuntCapacitor.getOutput().getLossesKW() != null) {
                                binding.shuntLossesTv.setText(shuntCapacitor.getOutput().getLossesKW() + " " + "Kw");
                                binding.switchLossesTv.setText(shuntCapacitor.getOutput().getLossesKW() + " " + "Kw");
                            }

                            SendData(sectionID, phase, fromNodeId, fromX, fromY, toNodeID, toNodeX, toNodeY, DeviceTypeLine, LineDeviceNumber);

                        }
                    } catch (Exception e) {
                        Log.d("Exception", Objects.requireNonNull(e.getLocalizedMessage()));
                    }
                } else if (response.code() == 401){
                    binding.shuntCapacitorInfoLayout.setVisibility(VISIBLE);
                    binding.shimmerLayout.setVisibility(GONE);
                    binding.shimmerView.stopShimmer();
                    prefManager.setIsUserLogin(false);
                    requireActivity().startActivity(new Intent(requireContext(), LoginActivity.class));
                    requireActivity().finish();
                }else {
                    binding.shuntCapacitorInfoLayout.setVisibility(VISIBLE);
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
            public void onFailure(@NonNull Call<ShuntCapacitor> call, @NonNull Throwable t) {
                binding.shuntConfigurationTv.setVisibility(VISIBLE);
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
                toast.
                        setView(layout);
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