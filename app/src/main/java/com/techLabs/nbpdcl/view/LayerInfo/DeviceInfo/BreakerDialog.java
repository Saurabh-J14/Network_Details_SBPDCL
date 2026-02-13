package com.techLabs.nbpdcl.view.LayerInfo.DeviceInfo;

import static com.techLabs.nbpdcl.Utils.ListDataManager.sendData;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.techLabs.nbpdcl.R;
import com.techLabs.nbpdcl.Utils.Args;
import com.techLabs.nbpdcl.Utils.callBack.AddDevice;
import com.techLabs.nbpdcl.Utils.callBack.IsCancel;
import com.techLabs.nbpdcl.Utils.callBack.IsClicked;
import com.techLabs.nbpdcl.Utils.callBack.SectionCallBack;
import com.techLabs.nbpdcl.Utils.callBack.Update;
import com.techLabs.nbpdcl.adapters.DynamicFragmentAdapter;
import com.techLabs.nbpdcl.databinding.BreakerMoreinfoLayoutBinding;
import com.techLabs.nbpdcl.models.UpdateModel;
import com.techLabs.nbpdcl.retrofit.ApiInterface;
import com.techLabs.nbpdcl.retrofit.RetrofitClient;
import com.techLabs.nbpdcl.view.fragment.deviceFragment.CableFragment;
import com.techLabs.nbpdcl.view.fragment.deviceFragment.OverheadDetailsFragment;
import com.techLabs.nbpdcl.view.fragment.deviceFragment.UnbalanceDetailFragment;
import com.techLabs.nbpdcl.view.fragment.deviceInfo.BreakerFragment;
import com.techLabs.nbpdcl.view.fragment.deviceInfo.NodeDetailFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class BreakerDialog extends Dialog implements SectionCallBack, IsCancel, Update  {

    private BreakerMoreinfoLayoutBinding binding;
    private final Context mainContext;
    private final JsonObject jsonObject;
    private final String voltage;
    private final String networkId;
    private final Map<String, Integer> deviceSize = new HashMap<>();
    private DynamicFragmentAdapter adapter;
    private final IsCancel isCancel;
    private int tabPosition;
    private AddDevice addDevice;
    private final JsonObject breakerObject = new JsonObject();
    private final JsonObject sectionData = new JsonObject();
    private final JsonArray deviceArray = new JsonArray();
    private final LinkedHashMap<String, Integer> insertDevices = new LinkedHashMap<>();
    private final LinkedHashMap<String, Integer> insertedDevices = new LinkedHashMap<>();

    public BreakerDialog(@NonNull Context context, IsCancel isCancel, String networkId, String voltage, JsonObject jsonObject) {
        super(context);
        this.mainContext = context;
        this.isCancel = isCancel;
        this.jsonObject = jsonObject;
        this.voltage = voltage;
        this.networkId = networkId;
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = BreakerMoreinfoLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        View MainLayoutBackGround = getWindow().getDecorView().getRootView();
        MainLayoutBackGround.setBackground(getContext().getDrawable(R.drawable.pop_layout_background));
        addDevice = (AddDevice) mainContext;
        binding.addRemoveLayout1.setVisibility(View.GONE);

        ViewPager2 viewPager = binding.viewPager;
        TabLayout tabLayout = binding.tablayout;
        adapter = new DynamicFragmentAdapter((FragmentActivity) mainContext);
        adapter.addFragment(new BreakerFragment(this,this, networkId,voltage, jsonObject), "Breaker");
        deviceSize.put("Breaker", 8);
        viewPager.setAdapter(adapter);
        adjustViewPagerHeight(viewPager, 0);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            @SuppressLint("InflateParams")
            View customView = LayoutInflater.from(mainContext).inflate(R.layout.custom_tab, null);
            TextView tabTitle = customView.findViewById(R.id.tabText);
            tabTitle.setText(adapter.getPageTitle(position));
            tab.setCustomView(customView);
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    binding.headerTitle.setText(adapter.getPageTitle(tab.getPosition()));
                    adjustViewPagerHeight(viewPager, tab.getPosition());
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
            if (isCancel != null) {
                isCancel.Event(true);
            }

        });

        binding.okbtn.setOnClickListener(v -> {
            if (!insertDevices.isEmpty()) {
                CheckDetails();
            } else {
                Fragment currentFragment = adapter.getFragmentAt(tabPosition);

                if (currentFragment instanceof IsClicked) {
                    ((IsClicked) currentFragment).isClicked(true);
                }
            }
        });

    }
    private void CheckDetails() {
        try {
            insertDevices.keySet();
            if (!insertDevices.isEmpty()) {
                List<String> data = new ArrayList<>(insertDevices.keySet());
                for (int i = 0; i < data.size(); i++) {
                    int index = 0;
                    for (String key : insertedDevices.keySet()) {
                        if (key.equals(data.get(i))) {
                            System.out.println("Index of '" + data.get(i) + "' is: " + index);

                            if (adapter.getPageTitle(index).equals("Breaker")) {
                                Args.setISBreakerValidate(true);
                                getBreakerData();
                            }

                            break;
                        }
                        index++;
                    }
                }
            }
        } catch (Exception e) {
            Log.d("Exception", Objects.requireNonNull(e.getLocalizedMessage()));
        }
    }

    private void getBreakerData() {
        Args.getBreakerParameter().observe((LifecycleOwner) mainContext, bundle -> {
            if (bundle != null && bundle.getString("DeviceNumber") != null && bundle.getString("DeviceType") != null && bundle.getString("Location") != null && bundle.getString("Status") != null && bundle.getString("EquipmentID") != null) {
                try {
                    if (!sectionData.isEmpty()) {
                        breakerObject.addProperty("DeviceNumber", "0");
                        breakerObject.addProperty("DeviceType", bundle.getString("DeviceType"));
                        breakerObject.addProperty("Location", bundle.getString("Location"));
                        breakerObject.addProperty("Status", bundle.getString("Status"));
                        breakerObject.addProperty("EquipmentID", bundle.getString("EquipmentID"));
                        deviceArray.add(breakerObject);
                        sendData();
                    }
                } catch (Exception e) {
                    Log.d("Exception", Objects.requireNonNull(e.getLocalizedMessage()));
                }
            } else {
                if (binding != null) {
                    Snackbar.make(binding.getRoot(), "All filed are required!", Snackbar.LENGTH_LONG).show();
                }

            }
        });
    }

    private void sendData() {
        if (insertDevices.size() == deviceArray.size()) {
            if (addDevice != null) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.add("Section", sectionData);
                jsonObject.add("Device", deviceArray);
                JsonArray jsonArray = new JsonArray();
                jsonArray.add(jsonObject);
                JsonObject jsonObject1 = new JsonObject();
                jsonObject1.add("Data", jsonArray);
                addDevice.addDevice("", jsonObject1);
                dismiss();
                if (isCancel != null) {
                    isCancel.Event(true);
                }
            }
        }
    }

    private void adjustViewPagerHeight(ViewPager2 viewPager, int position) {
        Fragment fragment = adapter.getFragmentAt(position);
        if (fragment == null || fragment.getView() == null) return;
        View view = fragment.getView();
        view.post(() -> {
            int widthSpec = View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY);
            int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            view.measure(widthSpec, heightSpec);
            ViewGroup.LayoutParams params = viewPager.getLayoutParams();
            params.height = view.getMeasuredHeight();
            viewPager.setLayoutParams(params);
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

        if (!DeviceTypeLine.equals("None") && !DeviceLineNumber.equals("None")) {
            JsonObject jsonObject1 = new JsonObject();
            if (DeviceTypeLine.equals("1")) {
                jsonObject1.addProperty("DeviceNumber", DeviceLineNumber);
                jsonObject1.addProperty("DeviceType", DeviceTypeLine);
                CableFragment cableFragment = new CableFragment(mainContext, this, this, networkId, voltage, jsonObject1);
                if (!deviceSize.containsKey("Cable")) {
                    adapter.addFragment(cableFragment, "Cable");
                    deviceSize.put("Cable", 1);
                }
            } else if (DeviceTypeLine.equals("2")) {
                jsonObject1.addProperty("DeviceNumber", DeviceLineNumber);
                jsonObject1.addProperty("DeviceType", DeviceTypeLine);
                OverheadDetailsFragment overheadDetailsFragment = new OverheadDetailsFragment(this, this, jsonObject1, networkId);

                if (!deviceSize.containsKey("OverHead")) {
                    adapter.addFragment(overheadDetailsFragment, "OverHead");
                    deviceSize.put("OverHead", 2);
                }

            } else if (DeviceTypeLine.equals("23")) {
                jsonObject1.addProperty("DeviceNumber", DeviceLineNumber);
                jsonObject1.addProperty("DeviceType", DeviceTypeLine);
                UnbalanceDetailFragment unbalanceDetailFragment = new UnbalanceDetailFragment(this, this, networkId, jsonObject1);

                if (!deviceSize.containsKey("Cable")) {
                    adapter.addFragment(unbalanceDetailFragment, "UnBalanced");
                    deviceSize.put("UnBalanced", 23);
                }
            }
        }
    }

    @Override
    public void Event(boolean isCancel) {
        if (isCancel) {
            dismiss();
        }
    }

    @Override
    public void isUpdate(String eqp, String DeviceType, String DeviceNumber, String DbName, String status) {
        updateEqp(eqp, networkId, DeviceType, DeviceNumber, DbName, status);
    }

    private void updateEqp(String eqp, String networkId, String deviceType, String deviceNumber, String dbName, String status ){
        JsonObject jsonObject1=new JsonObject();
        jsonObject1.addProperty("NetworkId", networkId);
        jsonObject1.addProperty("DeviceType", deviceType);
        jsonObject1.addProperty("DeviceNumber", deviceNumber);
        jsonObject1.addProperty("Status", status);
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
                    try {
                        UpdateModel updateModel = response.body();
                        assert updateModel != null;
                        if (updateModel.getMessage().equalsIgnoreCase("Device updated successfully")) {
                            Snackbar.make(binding.getRoot(), updateModel.getMessage(), Snackbar.LENGTH_LONG).show();
                            adapter.clearFragments();
                            dismiss();
                            if (isCancel != null) {
                                isCancel.Event(true);
                            }
                        } else {
                            Snackbar.make(binding.getRoot(), updateModel.getMessage(), Snackbar.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        Log.d("Exception", Objects.requireNonNull(e.getLocalizedMessage()));
                    }
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
            public void onFailure(Call<UpdateModel> call, Throwable t) {
                Snackbar.make(binding.getRoot(), mainContext.getString(R.string.error_msg), Snackbar.LENGTH_LONG).setAction("Retry", v -> updateEqp(eqp, networkId, deviceType, deviceNumber, dbName,status)).show();
            }
        });
    }
}

