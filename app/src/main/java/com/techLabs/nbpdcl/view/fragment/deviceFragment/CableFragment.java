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
import android.widget.AdapterView;
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
import com.techLabs.nbpdcl.Utils.callBack.IsClicked;
import com.techLabs.nbpdcl.Utils.callBack.SectionCallBack;
import com.techLabs.nbpdcl.Utils.callBack.Update;
import com.techLabs.nbpdcl.databinding.FragmentCableInfoDetailsBinding;
import com.techLabs.nbpdcl.models.EquipmentModel;
import com.techLabs.nbpdcl.models.Line.Cable;
import com.techLabs.nbpdcl.retrofit.ApiInterface;
import com.techLabs.nbpdcl.retrofit.RetrofitClient;
import com.techLabs.nbpdcl.view.activity.LoginActivity;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class CableFragment extends Fragment implements IsClicked {
    private final Context mainContext;
    private final SectionCallBack sectionCallBack;
    private final Update update;
    private final JsonObject jsonObject;
    private final String[] typeList = {"Cable", "Overhead", "UnbalanceOverhead"};
    private final String[] statusList = {"Connected", "Disconnected"};
    private final String networkId;
    private final String voltage;
    private FragmentCableInfoDetailsBinding binding;
    private PrefManager prefManager;
    private ArrayAdapter<String> cableIdAdapters;
    private ArrayList<String> items;
    private String cableId;
    private int status;
    private String phase;
    private String sectionID;
    private String deviceNumber;
    private String fromX;
    private String fromY;
    private String fromNodeId;
    private String toX;
    private String toY;
    private String toNodeId;

    public CableFragment(@NonNull Context context, SectionCallBack sectionCallBack, Update update, String networkId, String voltage, JsonObject jsonObject) {
        this.mainContext = context;
        this.sectionCallBack = sectionCallBack;
        this.update = update;
        this.jsonObject = jsonObject;
        this.networkId = networkId;
        this.voltage = voltage;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentCableInfoDetailsBinding.inflate(inflater, container, false);
        prefManager = new PrefManager(getContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (prefManager.getEditMode().equals("Edit")) {
            binding.editBtnLayout.setVisibility(View.GONE);
        } else if (prefManager.getEditMode().equals("Survey")) {
            binding.editBtnLayout.setVisibility(View.GONE);
            binding.cableIdSpinnerBar.setEnabled(true);
            binding.typeSpinnertv.setEnabled(false);
            binding.statusSpinnersBar.setEnabled(false);
            binding.deviceNumbersEdt.setEnabled(false);
            binding.lengthTv.setEnabled(false);
            binding.voltageTv.setEnabled(false);
            binding.nbCablePhaseTv.setEnabled(false);
            binding.condTempTv.setEnabled(false);
            binding.nominalRating.setEnabled(false);
            binding.conductorMaterialTv.setEnabled(false);
            binding.conductorSizeTv.setEnabled(false);
            binding.cableTypeTv.setEnabled(false);
        } else {
            binding.editBtnLayout.setVisibility(View.GONE);
            binding.cableIdSpinnerBar.setEnabled(false);
            binding.typeSpinnertv.setEnabled(false);
            binding.statusSpinnersBar.setEnabled(false);
            binding.deviceNumbersEdt.setEnabled(false);
            binding.lengthTv.setEnabled(false);
            binding.voltageTv.setEnabled(false);
            binding.nbCablePhaseTv.setEnabled(false);
            binding.condTempTv.setEnabled(false);
            binding.nominalRating.setEnabled(false);
            binding.conductorMaterialTv.setEnabled(false);
            binding.conductorSizeTv.setEnabled(false);
            binding.cableTypeTv.setEnabled(false);
        }

        ArrayAdapter<String> typeAdapters = new ArrayAdapter<>(mainContext, R.layout.custom_spinner, typeList);
        binding.typeSpinnertv.setAdapter(typeAdapters);

        ArrayAdapter<String> statusAdapters = new ArrayAdapter<>(mainContext, R.layout.custom_spinner, statusList);
        binding.statusSpinnersBar.setAdapter(statusAdapters);

        binding.typeSpinnertv.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(requireContext())) {
                    getEquipment(binding.typeSpinnertv.getSelectedItem().toString());
                } else {
                    Snackbar.make(view, R.string.no_internet_connection, Snackbar.LENGTH_INDEFINITE)
                            .setAction("Retry", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    getEquipment(binding.typeSpinnertv.getSelectedItem().toString());
                                }
                            }).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(requireContext())) {
            getCableData(jsonObject);
        } else {
            Snackbar.make(view, R.string.no_internet_connection, Snackbar.LENGTH_INDEFINITE)
                    .setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            getCableData(jsonObject);
                        }
                    }).show();
        }

    }

    private void getCableData(JsonObject jsonObject) {
        binding.cableInfoLayout.setVisibility(GONE);
        binding.shimmerLayout.setVisibility(VISIBLE);
        binding.shimmerView.startShimmer();
        jsonObject.addProperty("UserType", prefManager.getUserType());
        jsonObject.addProperty("CYMDBNET", prefManager.getDBName());
        Retrofit retrofit = RetrofitClient.getClient(mainContext);
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<Cable> call = apiInterface.getCableData(jsonObject);
        call.enqueue(new Callback<Cable>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<Cable> call, @NonNull Response<Cable> response) {
                if (response.code() == 200) {
                    try {
                        binding.cableInfoLayout.setVisibility(VISIBLE);
                        binding.shimmerLayout.setVisibility(GONE);
                        binding.shimmerView.stopShimmer();
                        Cable cable = response.body();
                        assert cable != null;
                        if (cable.getOutput() != null) {

                            if (cable.getOutput().getPhase() != null) {
                                phase = cable.getOutput().getPhase().toString();
                            } else {
                                phase = "None";
                            }

                            if (cable.getOutput().getSectionId() != null) {
                                sectionID = cable.getOutput().getSectionId();
                            } else {
                                sectionID = "None";
                            }

                            if (cable.getOutput().getDeviceNumber() != null) {
                                deviceNumber = cable.getOutput().getDeviceNumber();
                            } else {
                                deviceNumber = "None";
                            }

                            if (cable.getOutput().getFROMNodeIdX() != null) {
                                fromX = cable.getOutput().getFROMNodeIdX().toString();
                            } else {
                                fromX = "None";
                            }

                            if (cable.getOutput().getFROMNodeIdY() != null) {
                                fromY = cable.getOutput().getFROMNodeIdY().toString();
                            } else {
                                fromY = "None";
                            }

                            if (cable.getOutput().getFROMNodeId() != null) {
                                fromNodeId = cable.getOutput().getFROMNodeId();
                            } else {
                                fromNodeId = "None";
                            }

                            if (cable.getOutput().getTONodeIdX() != null) {
                                toX = cable.getOutput().getTONodeIdX().toString();
                            } else {
                                toX = "None";
                            }

                            if (cable.getOutput().getTONodeIdY() != null) {
                                toY = cable.getOutput().getTONodeIdY().toString();
                            } else {
                                toY = "None";
                            }

                            if (cable.getOutput().getTONodeId() != null) {
                                toNodeId = cable.getOutput().getTONodeId();
                            } else {
                                toNodeId = "None";
                            }

                            if (cable.getOutput().getCableId() != null) {
                                cableId = cable.getOutput().getCableId();
                            }

                            if (cable.getOutput().getStatus() != null) {
                                status = cable.getOutput().getStatus();
                            }

                            if (cable.getOutput().getDeviceNumber() != null && !cable.getOutput().getDeviceNumber().isEmpty() && !cable.getOutput().getDeviceNumber().equals("null")) {
                                binding.deviceNumbersEdt.setText(cable.getOutput().getDeviceNumber());
                            } else {
                                binding.deviceNumbersEdt.setText("UNDEFINED");
                            }

                            if (cable.getOutput().getLength() != null) {
                                binding.lengthTv.setText(cable.getOutput().getLength().toString() + " " + "M");
                            }
                            if (voltage != null) {
                                binding.voltageTv.setText(voltage);
                            }

                            if (cable.getOutput().getNumberOfCableInParallel() != null) {
                                binding.nbCablePhaseTv.setText(cable.getOutput().getNumberOfCableInParallel().toString() + " " + "runs");
                            }

                            if (cable.getOutput().getOperatingTemperature() != null) {
                                binding.condTempTv.setText(cable.getOutput().getOperatingTemperature().toString() + " " + "°C");
                            }

                            if (cable.getOutput().getNominalRating() != null && !cable.getOutput().getNominalRating().toString().isEmpty()) {
                                binding.nominalRating.setText(cable.getOutput().getNominalRating().toString() + " " + "A");
                            }

                            if (cable.getOutput().getCableType() != null) {
                                if (cable.getOutput().getCableType() == 0) {
                                    binding.cableTypeTv.setText("1C");
                                } else if (cable.getOutput().getCableType() == 1) {
                                    binding.cableTypeTv.setText("3C");
                                } else {
                                    binding.cableTypeTv.setText("3.5C");
                                }
                            }

                            if (cable.getOutput().getMaterialID() != null) {
                                binding.conductorMaterialTv.setText(cable.getOutput().getMaterialID().toString());
                            } else {
                                binding.conductorMaterialTv.setText("Default");
                            }

                            if (cable.getOutput().getSizeMm2() != null) {
                                binding.conductorSizeTv.setText(cable.getOutput().getSizeMm2().toString() + " " + "AWG");
                            }

                            sendData(phase, sectionID, fromX, fromY, fromNodeId, toX, toY, toNodeId);
                        }
                    } catch (Exception e) {
                        Log.d("Exception", Objects.requireNonNull(e.getLocalizedMessage()));
                    }
                } else if (response.code() == 401) {
                    binding.cableInfoLayout.setVisibility(VISIBLE);
                    binding.shimmerLayout.setVisibility(GONE);
                    binding.shimmerView.stopShimmer();
                    prefManager.setIsUserLogin(false);
                    startActivity(new Intent(requireContext(), LoginActivity.class));
                    requireActivity().finish();
                } else {
                    binding.cableInfoLayout.setVisibility(VISIBLE);
                    binding.shimmerLayout.setVisibility(GONE);
                    binding.shimmerView.stopShimmer();
                    @SuppressLint("InflateParams")
                    View layout = LayoutInflater.from(getContext()).inflate(R.layout.toast_layout, null);
                    TextView Ok = layout.findViewById(R.id.okBtn);
                    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                    TextView header = layout.findViewById(R.id.headerTv);
                    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                    TextView description = layout.findViewById(R.id.descripTv);
                    header.setText(response.message() + " - " + response.code());
                    description.setText(mainContext.getString(R.string.error_msg));
                    Toast toast = new Toast(getContext());
                    toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Cable> call, @NonNull Throwable t) {
                binding.cableInfoLayout.setVisibility(VISIBLE);
                binding.shimmerLayout.setVisibility(GONE);
                binding.shimmerView.stopShimmer();
                @SuppressLint("InflateParams")
                View layout = LayoutInflater.from(getContext()).inflate(R.layout.toast_layout, null);
                TextView Ok = layout.findViewById(R.id.okBtn);
                @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                TextView header = layout.findViewById(R.id.headerTv);
                @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                TextView description = layout.findViewById(R.id.descripTv);
                header.setText(mainContext.getString(R.string.error));
                description.setText(mainContext.getString(R.string.error_msg));
                Toast toast = new Toast(getContext());
                toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
            }
        });
    }

    private void sendData(String phase, String sectionID, String fromX, String fromY, String fromNodeId, String toX, String toY, String toNodeId) {
        sectionCallBack.OnCableDataReceived(sectionID, phase, fromNodeId, fromX, fromY, toNodeId, toX, toY, "None", "None");
    }

    private void getEquipment(String eqType) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("NetworkId", networkId);
        jsonObject.addProperty("Type", "Equipment");
        jsonObject.addProperty("Subtype", eqType);
        jsonObject.addProperty("CYMDBNET", prefManager.getDBName());
        Retrofit retrofit = RetrofitClient.getClient(mainContext);
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<EquipmentModel> call = apiInterface.getEquipmentData(jsonObject);
        call.enqueue(new Callback<EquipmentModel>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<EquipmentModel> call, @NonNull Response<EquipmentModel> response) {
                if (response.code() == 200) {
                    try {
                        EquipmentModel equipmentModel = response.body();
                        assert equipmentModel != null;
                        if (!equipmentModel.getAllEquipmentId().getEquipmentId().isEmpty()) {
                            items = new ArrayList<>(equipmentModel.getAllEquipmentId().getEquipmentId());
                            if (cableId != null) {
                                items.add(0, cableId);
                            }

                            if (status == 0) {
                                binding.statusSpinnersBar.setSelection(0);
                            } else {
                                binding.statusSpinnersBar.setSelection(1);
                            }

                            cableIdAdapters = new ArrayAdapter<>(mainContext, R.layout.custom_spinner, items);
                            binding.cableIdSpinnerBar.setAdapter(cableIdAdapters);
                        }

                    } catch (Exception e) {
                        Log.d("Exception", Objects.requireNonNull(e.getLocalizedMessage()));
                    }
                } else if (response.code() == 401){
                    prefManager.setIsUserLogin(false);
                    requireActivity().startActivity(new Intent(requireContext(), LoginActivity.class));
                    requireActivity().finish();
                }else {
                    @SuppressLint("InflateParams")
                    View layout = LayoutInflater.from(getContext()).inflate(R.layout.toast_layout, null);
                    TextView Ok = layout.findViewById(R.id.okBtn);
                    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                    TextView header = layout.findViewById(R.id.headerTv);
                    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                    TextView description = layout.findViewById(R.id.descripTv);
                    header.setText(response.message() + " - " + response.code());
                    description.setText(mainContext.getString(R.string.error_msg));
                    Ok.setOnClickListener(v -> {
                        getEquipment(eqType);
                    });
                    Toast toast = new Toast(getContext());
                    toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<EquipmentModel> call, @NonNull Throwable t) {
                @SuppressLint("InflateParams")
                View layout = LayoutInflater.from(getContext()).inflate(R.layout.toast_layout, null);
                TextView Ok = layout.findViewById(R.id.okBtn);
                @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                TextView header = layout.findViewById(R.id.headerTv);
                @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                TextView description = layout.findViewById(R.id.descripTv);
                header.setText(mainContext.getString(R.string.error));
                description.setText(mainContext.getString(R.string.error_msg));
                Ok.setOnClickListener(v -> {
                    getEquipment(eqType);
                });
                Toast toast = new Toast(getContext());
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
        requireView().requestFocus();
    }

    @Override
    public void isClicked(boolean isCancel) {
        if (update != null) {
            update.isUpdate(binding.cableIdSpinnerBar.getSelectedItem().toString(), "1", deviceNumber, prefManager.getDBName(), String.valueOf(status));
        }
    }

}