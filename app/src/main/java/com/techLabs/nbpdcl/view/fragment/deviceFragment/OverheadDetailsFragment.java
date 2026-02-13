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
import com.techLabs.nbpdcl.databinding.FragmentOverheadDetailsBinding;
import com.techLabs.nbpdcl.models.EquipmentModel;
import com.techLabs.nbpdcl.models.Line.Overhead;
import com.techLabs.nbpdcl.retrofit.ApiInterface;
import com.techLabs.nbpdcl.retrofit.RetrofitClient;
import com.techLabs.nbpdcl.view.activity.LoginActivity;

import java.util.ArrayList;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class OverheadDetailsFragment extends Fragment implements IsClicked {
    private FragmentOverheadDetailsBinding binding;
    private PrefManager prefManager;
    private final JsonObject jsonObject;
    private SectionCallBack sectionCallBack;
    private Update update;
    private final String networkId;
    private final String[] typeList = {"Overhead", "Cable", "UnbalanceOverhead"};
    private ArrayList<String> items;
    private final ArrayList<String> statusItems = new ArrayList<>();
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

    public OverheadDetailsFragment(SectionCallBack sectionCallBack, Update update, JsonObject jsonObject, String networkId) {
        this.sectionCallBack = sectionCallBack;
        this.update = update;
        this.jsonObject = jsonObject;
        this.networkId = networkId;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOverheadDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        prefManager = new PrefManager(getActivity());

        if (prefManager.getUserType().equals("Edit")) {
            binding.editBtnLayout.setVisibility(View.GONE);
            binding.editBtnLayout.setVisibility(View.GONE);
            binding.typeSpinner.setEnabled(false);
            binding.overheadNumberTv.setEnabled(false);
            binding.spinnerstatus.setEnabled(false);
            binding.overheadLengthTv.setEnabled(false);
            binding.lineIdSpinnerbar.setEnabled(true);
            binding.ohNominalRating.setEnabled(false);
            binding.positiveSequenceFirstTv.setEnabled(false);
            binding.positiveSequenceSecondTv.setEnabled(false);
            binding.zeroSequenceFirstTv.setEnabled(false);
            binding.zeroSequenceSecondTv.setEnabled(false);
        } else if (prefManager.getEditMode().equals("Survey")) {
            binding.editBtnLayout.setVisibility(View.GONE);
            binding.typeSpinner.setEnabled(false);
            binding.overheadNumberTv.setEnabled(false);
            binding.spinnerstatus.setEnabled(false);
            binding.overheadLengthTv.setEnabled(false);
            binding.lineIdSpinnerbar.setEnabled(false);
            binding.ohNominalRating.setEnabled(false);
            binding.positiveSequenceFirstTv.setEnabled(false);
            binding.positiveSequenceSecondTv.setEnabled(false);
            binding.zeroSequenceFirstTv.setEnabled(false);
            binding.zeroSequenceSecondTv.setEnabled(false);
        } else {
            binding.editBtnLayout.setVisibility(View.GONE);
            binding.typeSpinner.setEnabled(false);
            binding.overheadNumberTv.setEnabled(false);
            binding.spinnerstatus.setEnabled(false);
            binding.overheadLengthTv.setEnabled(false);
            binding.lineIdSpinnerbar.setEnabled(false);
            binding.ohNominalRating.setEnabled(false);
            binding.positiveSequenceFirstTv.setEnabled(false);
            binding.positiveSequenceSecondTv.setEnabled(false);
            binding.zeroSequenceFirstTv.setEnabled(false);
            binding.zeroSequenceSecondTv.setEnabled(false);
        }


        if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(requireActivity())) {
            getOverheadInfo();
        } else {
            Snackbar.make(view, R.string.no_internet_connection, Snackbar.LENGTH_LONG)
                    .setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getOverheadInfo();
                        }
                    }).show();
        }

        statusItems.add("Connected");
        statusItems.add("Disconnected");
        ArrayAdapter<String> statusAdapters = new ArrayAdapter<>(requireActivity(), R.layout.custom_spinner, statusItems);
        binding.spinnerstatus.setAdapter(statusAdapters);

        ArrayAdapter<String> typeAdapters = new ArrayAdapter<>(requireActivity(), R.layout.custom_spinner, typeList);
        binding.typeSpinner.setAdapter(typeAdapters);

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

    private void getOverheadInfo() {
        binding.overheadInfoLayout.setVisibility(GONE);
        binding.shimmerLayout.setVisibility(VISIBLE);
        binding.shimmerView.startShimmer();
        jsonObject.addProperty("UserType", prefManager.getUserType());
        jsonObject.addProperty("CYMDBNET", prefManager.getDBName());
        Retrofit retrofit = RetrofitClient.getClient(requireContext());
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<Overhead> call = apiInterface.getOverheadData(jsonObject);
        call.enqueue(new Callback<Overhead>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<Overhead> call, @NonNull Response<Overhead> response) {
                if (response.code() == 200) {
                    try {
                        binding.overheadInfoLayout.setVisibility(VISIBLE);
                        binding.shimmerLayout.setVisibility(GONE);
                        binding.shimmerView.stopShimmer();
                        Overhead overhead = response.body();
                        assert overhead != null;
                        if (overhead.getOutput() != null) {

                            if (overhead.getOutput().getSectionId() != null) {
                                sectionID = overhead.getOutput().getSectionId();
                            } else {
                                sectionID = "None";
                            }

                            if (overhead.getOutput().getDeviceNumber() != null) {
                                deviceNumber = overhead.getOutput().getDeviceNumber();
                            } else {
                                deviceNumber = "None";
                            }

                            if (overhead.getOutput().getPhase() != null) {
                                phase = overhead.getOutput().getPhase().toString();
                            } else {
                                phase = "None";
                            }

                            if (overhead.getOutput().getFromNodeId() != null) {
                                fromNodeId = overhead.getOutput().getFromNodeId();
                            } else {
                                fromNodeId = "None";
                            }

                            if (overhead.getOutput().getFromNodeX() != null) {
                                fromX = overhead.getOutput().getFromNodeX();
                            } else {
                                fromX = "None";
                            }

                            if (overhead.getOutput().getFromNodeY() != null) {
                                fromY = overhead.getOutput().getFromNodeY();
                            } else {
                                fromY = "None";
                            }

                            if (overhead.getOutput().getToNodeId() != null) {
                                toNodeId = overhead.getOutput().getToNodeId();
                            } else {
                                toNodeId = "None";
                            }

                            if (overhead.getOutput().getToNodeX() != null) {
                                toX = overhead.getOutput().getToNodeX();
                            } else {
                                toY = "None";
                            }

                            if (overhead.getOutput().getToNodeY() != null) {
                                toY = overhead.getOutput().getToNodeY();
                            } else {
                                toY = "None";
                            }

                            if (overhead.getOutput().getDeviceNumber() != null && !overhead.getOutput().getDeviceNumber().isEmpty() && !overhead.getOutput().getDeviceNumber().equals("null")) {
                                binding.overheadNumberTv.setText(overhead.getOutput().getDeviceNumber());
                            } else {
                                binding.overheadNumberTv.setText("UNDEFINED");
                            }

                            if (overhead.getOutput().getLength() != null) {
                                binding.overheadLengthTv.setText(overhead.getOutput().getLength() + " " + "m");
                            } else {
                                binding.overheadLengthTv.setText("UNDEFINED");
                            }

                            if (overhead.getOutput().getLineId() != null && !overhead.getOutput().getLineId().isEmpty() && !overhead.getOutput().getLineId().equals("null")) {
                                lineId = overhead.getOutput().getLineId();
                            }

                            if (overhead.getOutput().getStatus() != null) {
                                status = overhead.getOutput().getStatus();
                            }

                            if (overhead.getOutput().getNominalRating() != null && !overhead.getOutput().getNominalRating().isEmpty() && !overhead.getOutput().getNominalRating().equals("null")) {
                                binding.ohNominalRating.setText(overhead.getOutput().getNominalRating() + " " + "A");
                            }

                            if (overhead.getOutput().getPositiveSequenceResistance() != null) {
                                binding.positiveSequenceFirstTv.setText(overhead.getOutput().getPositiveSequenceResistance() + " " + "R + jXΩ/km");
                            } else {
                                binding.positiveSequenceFirstTv.setText("");
                            }

                            if (overhead.getOutput().getPositiveSequenceReactance() != null) {
                                binding.positiveSequenceSecondTv.setText(overhead.getOutput().getPositiveSequenceReactance() + " " + "G + jBµS/km");
                            }

                            if (overhead.getOutput().getZeroSequenceResistance() != null) {
                                binding.zeroSequenceFirstTv.setText(overhead.getOutput().getZeroSequenceResistance() + " " + "R + jXΩ/km");
                            }

                            if (overhead.getOutput().getZeroSequenceReactance() != null) {
                                binding.zeroSequenceSecondTv.setText(overhead.getOutput().getZeroSequenceReactance() + " " + "G + jBµS/km");
                            }
                            sendData(phase, sectionID, fromX, fromY, fromNodeId, toX, toY, toNodeId);
                        }
                    } catch (Exception e) {
                        Log.d("Exception", Objects.requireNonNull(e.getLocalizedMessage()));
                    }
                } else if (response.code() == 401) {
                    binding.overheadInfoLayout.setVisibility(VISIBLE);
                    binding.shimmerLayout.setVisibility(GONE);
                    binding.shimmerView.stopShimmer();
                    prefManager.setIsUserLogin(false);
                    requireActivity().startActivity(new Intent(requireContext(), LoginActivity.class));
                    requireActivity().finish();
                } else {
                    binding.overheadInfoLayout.setVisibility(VISIBLE);
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
                    description.setText(requireActivity().getString(R.string.error_msg));
                    Toast toast = new Toast(getActivity());
                    toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Overhead> call, @NonNull Throwable t) {
                binding.overheadInfoLayout.setVisibility(VISIBLE);
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
                Toast toast = new Toast(requireContext());
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

    private void getEquipment(String eqType) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("NetworkId", networkId);
        jsonObject.addProperty("Type", "Equipment");
        jsonObject.addProperty("Subtype", eqType);
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

                            ArrayAdapter<String> cableIdAdapters = new ArrayAdapter<>(requireContext(), R.layout.custom_spinner, items);
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
                        getEquipment(eqType);
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
                    getEquipment(eqType);
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
    public void isClicked(boolean isCancel) {
        if (update != null) {
            update.isUpdate(binding.lineIdSpinnerbar.getSelectedItem().toString(), "2", deviceNumber, prefManager.getDBName(), String.valueOf(status));
        }
    }
}
