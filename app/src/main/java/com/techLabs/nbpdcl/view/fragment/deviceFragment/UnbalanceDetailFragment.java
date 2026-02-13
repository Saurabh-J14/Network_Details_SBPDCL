package com.techLabs.nbpdcl.view.fragment.deviceFragment;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.techLabs.nbpdcl.R;
import com.techLabs.nbpdcl.Utils.PrefManager;
import com.techLabs.nbpdcl.Utils.ResponseDataUtils;
import com.techLabs.nbpdcl.Utils.callBack.IsClicked;
import com.techLabs.nbpdcl.Utils.callBack.SectionCallBack;
import com.techLabs.nbpdcl.Utils.callBack.Update;
import com.techLabs.nbpdcl.databinding.FragmentUnbalanceDetailBinding;
import com.techLabs.nbpdcl.models.EquipmentModel;
import com.techLabs.nbpdcl.models.Line.Unbalanced;
import com.techLabs.nbpdcl.retrofit.ApiInterface;
import com.techLabs.nbpdcl.retrofit.RetrofitClient;
import com.techLabs.nbpdcl.view.activity.LoginActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UnbalanceDetailFragment extends Fragment implements IsClicked {
    private FragmentUnbalanceDetailBinding binding;
    private PrefManager prefManager;
    private final JsonObject jsonObject;
    private String networkId;
    private SectionCallBack sectionCallBack;
    private Update update;
    private final String[] typeList = {"UnbalanceOverhead", "Overhead", "Cable"};
    private final ArrayList<String> statusItems = new ArrayList<>();
    private List<String> items;
    private String lineId;
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

    public UnbalanceDetailFragment(SectionCallBack sectionCallBack, Update update, String networkId, JsonObject jsonObject) {
        this.sectionCallBack = sectionCallBack;
        this.update = update;
        this.networkId = networkId;
        this.jsonObject = jsonObject;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentUnbalanceDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefManager = new PrefManager(requireContext());

        ArrayAdapter<String> typeAdapters = new ArrayAdapter<>(requireActivity(), R.layout.custom_spinner, typeList);
        binding.typeSpinner.setAdapter(typeAdapters);

        statusItems.add("Connected");
        statusItems.add("Disconnected");
        ArrayAdapter<String> statusAdapters = new ArrayAdapter<>(requireActivity(), R.layout.custom_spinner, statusItems);
        binding.spinnerstatus.setAdapter(statusAdapters);

        if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(requireActivity())) {
            getUnbalancedInfo();
        } else {
            Snackbar.make(view, R.string.no_internet_connection, Snackbar.LENGTH_LONG).setAction("Retry", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getUnbalancedInfo();
                }
            }).show();
        }

        binding.typeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(requireActivity())) {
                    getEquipment(binding.typeSpinner.getSelectedItem().toString());
                } else {
                    final Dialog dialog = new Dialog(requireActivity());
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.no_internet_dialog);
                    Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(requireContext().getDrawable(R.drawable.pop_background));
                    LottieAnimationView lottieAnimationView = dialog.findViewById(R.id.animation_view);
                    Button RetryBtn = dialog.findViewById(R.id.btnDialog);
                    lottieAnimationView.playAnimation();
                    RetryBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(requireActivity())) {
                                getEquipment(binding.typeSpinner.getSelectedItem().toString());
                                dialog.dismiss();
                            }
                        }
                    });
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setCancelable(false);
                    dialog.show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void getEquipment(String string) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("NetworkId", networkId);
        jsonObject.addProperty("Type", "Equipment");
        jsonObject.addProperty("Subtype", string);
        jsonObject.addProperty("CYMDBNET", prefManager.getDBName());
        Retrofit retrofit = RetrofitClient.getClient(requireContext());
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

                            if (lineId != null) {
                                items.add(0, lineId);
                            }

                            if (status == 0) {
                                binding.spinnerstatus.setSelection(0);
                            } else {
                                binding.spinnerstatus.setSelection(0);
                            }

                            ArrayAdapter<String> cableIdAdapters = new ArrayAdapter<>(requireActivity(), R.layout.custom_spinner, items);
                            binding.lineIdSpinnerbar.setAdapter(cableIdAdapters);
                        }

                    } catch (Exception e) {
                        Log.d("Exception", Objects.requireNonNull(e.getLocalizedMessage()));
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
                    description.setText(requireContext().getString(R.string.error_msg));
                    Ok.setOnClickListener(v -> {
                        getEquipment(string);
                    });
                    Toast toast = new Toast(getActivity());
                    toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<EquipmentModel> call, @NonNull Throwable t) {
                @SuppressLint("InflateParams")
                View layout = LayoutInflater.from(getActivity()).inflate(R.layout.toast_layout, null);
                TextView Ok = layout.findViewById(R.id.okBtn);
                @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                TextView header = layout.findViewById(R.id.headerTv);
                @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                TextView description = layout.findViewById(R.id.descripTv);
                header.setText(requireContext().getString(R.string.error));
                description.setText(requireContext().getString(R.string.error_msg));
                Ok.setOnClickListener(v -> {
                    getEquipment(string);
                });
                Toast toast = new Toast(getActivity());
                toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
            }
        });
    }

    private void getUnbalancedInfo() {
        binding.unbalanceInfoLayout.setVisibility(GONE);
        binding.shimmerLayout.setVisibility(VISIBLE);
        binding.shimmerView.startShimmer();
        jsonObject.addProperty("UserType", prefManager.getUserType());
        jsonObject.addProperty("CYMDBNET", prefManager.getDBName());
        Retrofit retrofit = RetrofitClient.getClient(requireContext());
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<Unbalanced> call = apiInterface.getUnbalancedData(jsonObject);
        call.enqueue(new Callback<Unbalanced>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<Unbalanced> call, @NonNull Response<Unbalanced> response) {
                if (response.code() == 200) {
                    try {
                        binding.unbalanceInfoLayout.setVisibility(VISIBLE);
                        binding.shimmerLayout.setVisibility(GONE);
                        binding.shimmerView.stopShimmer();
                        Unbalanced unbalanced = response.body();
                        assert unbalanced != null;
                        if (unbalanced.getOutput() != null) {

                            if (unbalanced.getOutput().getSectionId() != null) {
                                sectionID = unbalanced.getOutput().getSectionId();
                            } else {
                                sectionID = "None";
                            }

                            if (unbalanced.getOutput().getDeviceNumber() != null) {
                                deviceNumber = unbalanced.getOutput().getDeviceNumber();
                            } else {
                                deviceNumber = "None";
                            }

                            if (unbalanced.getOutput().getPhase() != null) {
                                phase = unbalanced.getOutput().getPhase().toString();
                            } else {
                                phase = "None";
                            }

                            if (unbalanced.getOutput().getFromNodeId() != null) {
                                fromNodeId = unbalanced.getOutput().getFromNodeId();
                            } else {
                                fromNodeId = "None";
                            }

                            if (unbalanced.getOutput().getFromNodeX() != null) {
                                fromX = unbalanced.getOutput().getFromNodeX();
                            } else {
                                fromX = "None";
                            }

                            if (unbalanced.getOutput().getFromNodeY() != null) {
                                fromY = unbalanced.getOutput().getFromNodeY();
                            } else {
                                fromY = "None";
                            }

                            if (unbalanced.getOutput().getToNodeId() != null) {
                                toNodeId = unbalanced.getOutput().getToNodeId();
                            } else {
                                toNodeId = "None";
                            }

                            if (unbalanced.getOutput().getToNodeX() != null) {
                                toX = unbalanced.getOutput().getToNodeX();
                            } else {
                                toX = "None";
                            }

                            if (unbalanced.getOutput().getToNodeY() != null) {
                                toY = unbalanced.getOutput().getToNodeY();
                            } else {
                                toY = "None";
                            }

                            if (unbalanced.getOutput().getStatus() != null) {
                                status = unbalanced.getOutput().getStatus();
                            }

                            if (unbalanced.getOutput().getLineId() != null) {
                                lineId = unbalanced.getOutput().getLineId();
                            }

                            if (unbalanced.getOutput().getDeviceNumber() != null && !unbalanced.getOutput().getDeviceNumber().isEmpty() && !unbalanced.getOutput().getDeviceNumber().equals("null")) {
                                binding.deviceNumberTv.setText(unbalanced.getOutput().getDeviceNumber());
                            }

                            if (unbalanced.getOutput().getLength() != null && !unbalanced.getOutput().getLength().isEmpty() && !unbalanced.getOutput().getLength().equals("null")) {
                                binding.lengthTv.setText(unbalanced.getOutput().getLength() + " " + "m");
                            }

                            sendData(phase, sectionID, fromX, fromY, fromNodeId, toX, toY, toNodeId);

                        }
                    } catch (Exception e) {
                        Log.d("Exception", Objects.requireNonNull(e.getLocalizedMessage()));
                    }
                } else if (response.code() == 401) {
                    binding.unbalanceInfoLayout.setVisibility(VISIBLE);
                    binding.shimmerLayout.setVisibility(GONE);
                    binding.shimmerView.stopShimmer();
                    prefManager.setIsUserLogin(false);
                    requireActivity().startActivity(new Intent(requireContext(), LoginActivity.class));
                    requireActivity().finish();
                } else {
                    binding.unbalanceInfoLayout.setVisibility(VISIBLE);
                    binding.shimmerLayout.setVisibility(GONE);
                    binding.shimmerView.stopShimmer();
                    @SuppressLint("InflateParams")
                    View layout = LayoutInflater.from(getActivity()).inflate(R.layout.toast_layout, null);
                    TextView Ok = layout.findViewById(R.id.okBtn);
                    @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView header = layout.findViewById(R.id.headerTv);
                    @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView description = layout.findViewById(R.id.descripTv);
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
            public void onFailure(@NonNull Call<Unbalanced> call, @NonNull Throwable t) {
                binding.unbalanceInfoLayout.setVisibility(VISIBLE);
                binding.shimmerLayout.setVisibility(GONE);
                binding.shimmerView.stopShimmer();
                @SuppressLint("InflateParams")
                View layout = LayoutInflater.from(getActivity()).inflate(R.layout.toast_layout, null);
                TextView Ok = layout.findViewById(R.id.okBtn);
                @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView header = layout.findViewById(R.id.headerTv);
                @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView description = layout.findViewById(R.id.descripTv);
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

    private void sendData(String phase, String sectionID, String fromX, String fromY, String fromNodeId, String toX, String toY, String toNodeId) {
        if (sectionCallBack != null) {
            sectionCallBack.OnCableDataReceived(sectionID, phase, fromNodeId, fromX, fromY, toNodeId, toX, toY, "None", "None");
        }
    }

    @Override
    public void isClicked(boolean isCancel) {
        if (update != null) {
            update.isUpdate(binding.lineIdSpinnerbar.getSelectedItem().toString(), "23", deviceNumber, prefManager.getDBName(), String.valueOf(status));
        }
    }

}