package com.techLabs.nbpdcl.view.LayerInfo.DeviceInfo;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.techLabs.nbpdcl.R;
import com.techLabs.nbpdcl.Utils.PrefManager;
import com.techLabs.nbpdcl.Utils.callBack.IsCancel;
import com.techLabs.nbpdcl.Utils.callBack.IsClicked;
import com.techLabs.nbpdcl.Utils.callBack.SectionCallBack;
import com.techLabs.nbpdcl.Utils.callBack.Update;
import com.techLabs.nbpdcl.adapters.DynamicFragmentAdapter;
import com.techLabs.nbpdcl.databinding.DtinfoLayoutBinding;
import com.techLabs.nbpdcl.models.UpdateModel;
import com.techLabs.nbpdcl.retrofit.ApiInterface;
import com.techLabs.nbpdcl.retrofit.RetrofitClient;
import com.techLabs.nbpdcl.view.activity.LoginActivity;
import com.techLabs.nbpdcl.view.fragment.deviceFragment.CableFragment;
import com.techLabs.nbpdcl.view.fragment.deviceFragment.OverheadDetailsFragment;
import com.techLabs.nbpdcl.view.fragment.deviceFragment.TransformerFragment;
import com.techLabs.nbpdcl.view.fragment.deviceFragment.UnbalanceDetailFragment;
import com.techLabs.nbpdcl.view.fragment.deviceInfo.NodeDetailFragment;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TransformerDialog extends Dialog implements SectionCallBack, IsCancel, Update {

    private final Context mainContext;
    private DtinfoLayoutBinding binding;
    private final JsonObject jsonObject;
    private final String networkId;
    private final String voltage;
    private DynamicFragmentAdapter adapter;
    private final Map<String, Integer> deviceSize = new HashMap<>();
    private final IsCancel isCancels;
    private int tabPosition;
    private PrefManager prefManager;

    public TransformerDialog(@NonNull Context context, IsCancel isCancel, String networkID, String voltage, JsonObject jsonObject) {
        super(context);
        this.mainContext = context;
        this.isCancels = isCancel;
        this.networkId = networkID;
        this.jsonObject = jsonObject;
        this.voltage = voltage;
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DtinfoLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        View MainLayoutBackGround = getWindow().getDecorView().getRootView();
        MainLayoutBackGround.setBackground(getContext().getDrawable(R.drawable.pop_layout_background));

        prefManager = new PrefManager(mainContext);

        if (prefManager.getEditMode().equals("Survey")) {
            binding.addRemoveLayout.setVisibility(View.GONE);
            binding.editBreakerLayout.setVisibility(View.VISIBLE);
            binding.imgClose.setVisibility(View.INVISIBLE);
        } else {
            binding.addRemoveLayout.setVisibility(View.GONE);
            binding.editBreakerLayout.setVisibility(View.GONE);
            binding.imgClose.setVisibility(View.VISIBLE);
        }

        ViewPager2 viewPager = binding.viewPager;
        TabLayout transformerTab = binding.tablayout;
        adapter = new DynamicFragmentAdapter((FragmentActivity) mainContext);
        adapter.addFragment(new TransformerFragment(this, this, this, networkId, voltage, jsonObject), "Transformer");
        deviceSize.put("Transformer", 5);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(transformerTab, viewPager, (tab, position) -> {
            @SuppressLint("InflateParams")
            View customView = LayoutInflater.from(mainContext).inflate(R.layout.custom_tab, null);
            TextView tabTitle = customView.findViewById(R.id.tabText);
            tabTitle.setText(adapter.getPageTitle(position));
            tab.setCustomView(customView);

            transformerTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    binding.headerTitle.setText(adapter.getPageTitle(tab.getPosition()));
                    tabPosition = tab.getPosition();
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {
                }
            });
        }).attach();

        binding.headerTitle.setText(adapter.getPageTitle(0));

        binding.imgClose.setOnClickListener(v -> {
            dismiss();
            adapter.clearFragments();
        });

        binding.canclebtn.setOnClickListener(v -> {
            adapter.clearFragments();
            dismiss();
            if (isCancels != null) {
                isCancels.Event(true);
            }
        });

        binding.okbtn.setOnClickListener(v -> {
            Fragment currentFragment = adapter.getFragmentAt(tabPosition);

            if (currentFragment instanceof IsClicked) {
                ((IsClicked) currentFragment).isClicked(true);
            }
        });

    }

    @Override
    public void OnCableDataReceived(String sectionID, String phase, String fromNodeId, String fromX, String fromY, String toNodeID, String toNodeX, String toNodeY, String DeviceTypeLine, String DeviceLineNumber) {
        if (!phase.equals("None")) {
            switch (phase) {
                case "1":
                    binding.aChkBox.setChecked(true);
                    binding.bChkBox.setChecked(false);
                    binding.cChkBox.setChecked(false);
                    break;
                case "2":
                    binding.aChkBox.setChecked(false);
                    binding.bChkBox.setChecked(true);
                    binding.cChkBox.setChecked(false);
                    break;
                case "3":
                    binding.aChkBox.setChecked(false);
                    binding.bChkBox.setChecked(false);
                    binding.cChkBox.setChecked(true);
                    break;
                case "4":
                    binding.aChkBox.setChecked(true);
                    binding.bChkBox.setChecked(true);
                    binding.cChkBox.setChecked(false);
                    break;
                case "5":
                    binding.aChkBox.setChecked(true);
                    binding.bChkBox.setChecked(false);
                    binding.cChkBox.setChecked(true);
                    break;
                case "6":
                    binding.aChkBox.setChecked(false);
                    binding.bChkBox.setChecked(true);
                    binding.cChkBox.setChecked(true);
                    break;
                default:
                    binding.aChkBox.setChecked(true);
                    binding.bChkBox.setChecked(true);
                    binding.cChkBox.setChecked(true);
                    break;
            }
        }

        if (!sectionID.equals("None")) {
            binding.sectionTv.setText(sectionID);
        } else {
            binding.sectionTv.setText(R.string.undefined);
        }

        NodeDetailFragment nodeDetailFragment = new NodeDetailFragment();
        Bundle bundle0 = new Bundle();
        bundle0.putString("toNodeID", toNodeID);
        bundle0.putString("fromNodeID", fromNodeId);
        bundle0.putString("fromX", fromX);
        bundle0.putString("fromY", fromY);
        bundle0.putString("toX", toNodeX);
        bundle0.putString("toY", toNodeY);
        nodeDetailFragment.setArguments(bundle0);

        if (!deviceSize.containsKey("Node")) {
            adapter.addFragment(nodeDetailFragment, "Node");
            deviceSize.put("Node", 99);
        }

        if (!DeviceTypeLine.equals("None")) {
            JsonObject jsonObject1 = new JsonObject();
            switch (DeviceTypeLine) {
                case "1":
                    jsonObject1.addProperty("DeviceNumber", DeviceLineNumber);
                    jsonObject1.addProperty("DeviceType", DeviceTypeLine);
                    CableFragment cableFragment = new CableFragment(mainContext, this, this, networkId, voltage, jsonObject1);
                    if (!deviceSize.containsKey("Cable")) {
                        adapter.addFragment(cableFragment, "Cable");
                        deviceSize.put("Cable", 1);
                    }
                    break;
                case "2":
                    jsonObject1.addProperty("DeviceNumber", DeviceLineNumber);
                    jsonObject1.addProperty("DeviceType", DeviceTypeLine);
                    OverheadDetailsFragment overheadDetailsFragment = new OverheadDetailsFragment(this, this, jsonObject1, networkId);

                    if (!deviceSize.containsKey("OverHead")) {
                        adapter.addFragment(overheadDetailsFragment, "OverHead");
                        deviceSize.put("OverHead", 2);
                    }

                    break;
                case "23":
                    jsonObject1.addProperty("DeviceNumber", DeviceLineNumber);
                    jsonObject1.addProperty("DeviceType", DeviceTypeLine);
                    UnbalanceDetailFragment unbalanceDetailFragment = new UnbalanceDetailFragment(this, this, networkId, jsonObject1);

                    if (!deviceSize.containsKey("Cable")) {
                        adapter.addFragment(unbalanceDetailFragment, "UnBalanced");
                        deviceSize.put("UnBalanced", 23);
                    }
                    break;
            }
        }
    }

    @Override
    public void Event(boolean isCancel) {
        if (isCancel) {
            isCancels.Event(true);
            dismiss();
        }
    }

    @Override
    public void isUpdate(String eqp, String DeviceType, String DeviceNumber, String DbName, String status) {
        updateEqp(eqp, networkId, DeviceType, DeviceNumber, DbName, status);
    }

    private void updateEqp(String eqp, String networkId, String deviceType, String deviceNumber, String dbName, String status) {
        JsonObject jsonObject1 = new JsonObject();
        jsonObject1.addProperty("NetworkId", networkId);
        jsonObject1.addProperty("DeviceType", deviceType);
        jsonObject1.addProperty("Status", status);
        jsonObject1.addProperty("DeviceNumber", deviceNumber);
        jsonObject1.addProperty("ID", eqp);
        jsonObject1.addProperty("CYMDBNET", dbName);
        JsonArray jsonArray1 = new JsonArray();
        jsonArray1.add(jsonObject1);
        JsonObject jsonObject2 = new JsonObject();
        jsonObject2.add("Data", jsonArray1);
        Retrofit retrofit = RetrofitClient.getClient(mainContext);
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<UpdateModel> call = apiInterface.updateData(jsonObject2);
        call.enqueue(new Callback<UpdateModel>() {
            @Override
            public void onResponse(@NonNull Call<UpdateModel> call, @NonNull Response<UpdateModel> response) {
                if (response.code() == 200) {
                    UpdateModel updateModel = response.body();
                    assert updateModel != null;
                    if (updateModel.getMessage().equalsIgnoreCase("Device updated successfully")) {
                        Snackbar.make(binding.getRoot(), updateModel.getMessage(), Snackbar.LENGTH_LONG).show();
                        adapter.clearFragments();
                        dismiss();
                        if (isCancels != null) {
                            isCancels.Event(true);
                        }
                    }else {
                        Snackbar.make(binding.getRoot(), updateModel.getMessage(), Snackbar.LENGTH_LONG).show();
                    }
                } else if (response.code() == 401) {
                    prefManager.setIsUserLogin(false);
                    mainContext.startActivity(new Intent(mainContext, LoginActivity.class));
                    dismiss();
                } else {
                    Snackbar.make(binding.getRoot(), response.message() + " - " + response.code(), Snackbar.LENGTH_LONG).setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            updateEqp(eqp, networkId, deviceType, deviceNumber, dbName, status);
                        }
                    }).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UpdateModel> call, @NonNull Throwable t) {
                Snackbar.make(binding.getRoot(), mainContext.getString(R.string.error_msg), Snackbar.LENGTH_LONG).setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateEqp(eqp, networkId, deviceType, deviceNumber, dbName, status);
                    }
                }).show();
            }
        });

    }
}


