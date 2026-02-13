package com.techLabs.nbpdcl.view.LayerInfo.LineInfo;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.viewpager2.widget.ViewPager2;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.techLabs.nbpdcl.R;
import com.techLabs.nbpdcl.Utils.Args;
import com.techLabs.nbpdcl.Utils.DefaultCustomer;
import com.techLabs.nbpdcl.Utils.ListDataManager;
import com.techLabs.nbpdcl.Utils.PrefManager;
import com.techLabs.nbpdcl.Utils.ResponseDataUtils;
import com.techLabs.nbpdcl.Utils.callBack.AddDevice;
import com.techLabs.nbpdcl.Utils.callBack.IsCancel;
import com.techLabs.nbpdcl.Utils.callBack.IsClicked;
import com.techLabs.nbpdcl.Utils.callBack.SectionCallBack;
import com.techLabs.nbpdcl.Utils.callBack.Update;
import com.techLabs.nbpdcl.adapters.DynamicFragmentAdapter;
import com.techLabs.nbpdcl.databinding.CableInfoDialogLayoutBinding;
import com.techLabs.nbpdcl.models.FindDeviceModel;
import com.techLabs.nbpdcl.models.UpdateModel;
import com.techLabs.nbpdcl.retrofit.ApiInterface;
import com.techLabs.nbpdcl.retrofit.RetrofitClient;
import com.techLabs.nbpdcl.view.activity.LoginActivity;
import com.techLabs.nbpdcl.view.fragment.deviceFragment.OverheadDetailsFragment;
import com.techLabs.nbpdcl.view.fragment.deviceFragment.SpotloadFragment;
import com.techLabs.nbpdcl.view.fragment.deviceFragment.SwitchFragment;
import com.techLabs.nbpdcl.view.fragment.deviceFragment.TransformerFragment;
import com.techLabs.nbpdcl.view.fragment.deviceInfo.BreakerFragment;
import com.techLabs.nbpdcl.view.fragment.deviceInfo.FuseFragment;
import com.techLabs.nbpdcl.view.fragment.deviceInfo.NodeDetailFragment;
import com.techLabs.nbpdcl.view.fragment.deviceInfo.ShuntCapacitorFragment;
import com.techLabs.nbpdcl.view.survey.device.BreakerDialogFragment;
import com.techLabs.nbpdcl.view.survey.device.CapacitorDialogFragment;
import com.techLabs.nbpdcl.view.survey.device.FuseDialogFragment;
import com.techLabs.nbpdcl.view.survey.device.SpotLoadDialogFragment;
import com.techLabs.nbpdcl.view.survey.device.SwitchDialogFragment;
import com.techLabs.nbpdcl.view.survey.device.TransformerDialogFragment;

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


public class OverHeadMoreInfoDialog extends Dialog implements SectionCallBack, IsCancel, Update {

    private CableInfoDialogLayoutBinding binding;
    private final Context mainContext;
    private final JsonObject jsonObject;
    private final String networkId;
    private final String sectionId;
    private final String voltage;
    private PrefManager prefManager;
    private final Map<String, Integer> deviceSize = new HashMap<>();
    private int tabPosition;
    private String phase;
    private DynamicFragmentAdapter adapter;
    private FindDeviceModel findDeviceModel;
    private final JsonArray deviceArray = new JsonArray();
    private final JsonObject breakerObject = new JsonObject();
    private final JsonObject switchObject = new JsonObject();
    private final JsonObject fuseObject = new JsonObject();
    private final JsonObject transformerObject = new JsonObject();
    private final JsonObject capacitorObject = new JsonObject();
    private final JsonObject spotLoadObject = new JsonObject();
    private final JsonObject sectionData = new JsonObject();
    private AddDevice addDevice;
    private final IsCancel isCancel;
    private final LinkedHashMap<String, Integer> insertedDevices = new LinkedHashMap<>();
    private final LinkedHashMap<String, Integer> insertDevices = new LinkedHashMap<>();
    private ViewPager2 viewPager;

    public OverHeadMoreInfoDialog(@NonNull Context context, IsCancel isCancel, JsonObject jsonObject, String networkId, String sectionId, String voltage) {
        super(context);
        this.mainContext = context;
        this.isCancel = isCancel;
        this.jsonObject = jsonObject;
        this.networkId = networkId;
        this.sectionId = sectionId;
        this.voltage = voltage;
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = CableInfoDialogLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        View MainLayoutBackGround = getWindow().getDecorView().getRootView();
        MainLayoutBackGround.setBackground(getContext().getDrawable(R.drawable.pop_layout_background));
        prefManager = new PrefManager(mainContext);
        addDevice = (AddDevice) mainContext;

        viewPager = binding.viewPager;

        if (prefManager.getEditMode().equals("Survey")) {
            binding.addRemoveLayout.setVisibility(View.VISIBLE);
            binding.editBreakerLayout.setVisibility(View.VISIBLE);
            binding.imgClose.setVisibility(View.INVISIBLE);
            binding.minusImg.setVisibility(View.INVISIBLE);
            binding.RemoveDevice.setVisibility(View.INVISIBLE);
        } else {
            binding.addRemoveLayout.setVisibility(View.GONE);
            binding.editBreakerLayout.setVisibility(View.GONE);
            binding.imgClose.setVisibility(View.VISIBLE);
        }

        binding.sectionIdEdt.setEnabled(false);

        ViewPager2 viewPager = binding.viewPager;
        TabLayout tabLayout = binding.tablayout;

        adapter = new DynamicFragmentAdapter((FragmentActivity) mainContext);
        adapter.addFragment(new OverheadDetailsFragment(this, this, jsonObject, networkId), "Overhead");
        deviceSize.put("OverHead", 2);
        insertedDevices.put("Overhead", 2);
        viewPager.setAdapter(adapter);

        if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(mainContext)) {
            if (sectionId != null) {
                getDevices(sectionId);
            }
        } else {
            final Dialog dialog = new Dialog(mainContext);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.no_internet_dialog);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(mainContext.getDrawable(R.drawable.pop_background));
            LottieAnimationView lottieAnimationView = dialog.findViewById(R.id.animation_view);
            Button RetryBtn = dialog.findViewById(R.id.btnDialog);
            lottieAnimationView.playAnimation();
            RetryBtn.setOnClickListener(view -> {
                if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(mainContext)) {
                    getDevices(sectionId);
                    dialog.dismiss();
                }
            });
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();
        }

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

        binding.imgClose.setOnClickListener(view -> dismiss());
        binding.headerTitle.setText(adapter.getPageTitle(0));
        viewPager.setCurrentItem(1);
        viewPager.setCurrentItem(0);

        binding.addDevice.setOnClickListener(v -> {
            if (binding.aChkBox.isChecked() && binding.bChkBox.isChecked() && binding.cChkBox.isChecked()) {
                phase = "7";
            } else if (binding.bChkBox.isChecked() && binding.cChkBox.isChecked()) {
                phase = "6";
            } else if (binding.aChkBox.isChecked() && binding.cChkBox.isChecked()) {
                phase = "5";
            } else if (binding.aChkBox.isChecked() && binding.bChkBox.isChecked()) {
                phase = "4";
            } else if (binding.cChkBox.isChecked()) {
                phase = "3";
            } else if (binding.bChkBox.isChecked()) {
                phase = "2";
            } else {
                phase = "1";
            }
            if (binding.aChkBox.isChecked() && binding.bChkBox.isChecked() && binding.cChkBox.isChecked()) {
                phase = "7";
            } else if (binding.bChkBox.isChecked() && binding.cChkBox.isChecked()) {
                phase = "6";
            } else if (binding.aChkBox.isChecked() && binding.cChkBox.isChecked()) {
                phase = "5";
            } else if (binding.aChkBox.isChecked() && binding.bChkBox.isChecked()) {
                phase = "4";
            } else if (binding.cChkBox.isChecked()) {
                phase = "3";
            } else if (binding.bChkBox.isChecked()) {
                phase = "2";
            } else {
                phase = "1";
            }

            PopupMenu popupMenu = new PopupMenu(mainContext, v);
            popupMenu.getMenuInflater().inflate(R.menu.add_device_menu, popupMenu.getMenu());
            popupMenu.setForceShowIcon(true);
            if (findDeviceModel.getOutput().size() == 3) {
                popupMenu.getMenu().getItem(0).setEnabled(false);
                popupMenu.getMenu().getItem(1).setEnabled(false);
            }
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @SuppressLint({"NonConstantResourceId", "NotifyDataSetChanged"})
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.transformer_menu:
                            if (adapter.getItemCount() < 5 && !adapter.getPageTitle(tabPosition).equals("Transformer")) {
                                if (adapter.getItemCount() > 2) {
                                    String searchKey = "Node";
                                    int index = 0;
                                    for (String key : insertedDevices.keySet()) {
                                        if (key.equals(searchKey)) {
                                            System.out.println("Index of '" + searchKey + "' is: " + index);
                                            adapter.removeFragment(index);
                                            insertedDevices.remove("Node");
                                            break;
                                        }
                                        index++;
                                    }
                                }

                                menuItem.setChecked(!menuItem.isChecked());
                                TransformerDialogFragment transformerDialogFragment = new TransformerDialogFragment(networkId, findDeviceModel.getOutput().get(findDeviceModel.getOutput().size() - 1).getDeviceType());
                                adapter.addFragment(transformerDialogFragment, "Transformer");
                                insertDevices.put("Transformer", 5);
                                insertedDevices.put("Transformer", 5);
                                binding.viewPager.setAdapter(adapter);
                                viewPager.requestLayout();
                                adapter.notifyDataSetChanged();
                                Args.setIsTransformerValidate(true);
                            }
                            return true;

                        case R.id.fuse_menu:
                            if (adapter.getItemCount() < 5 && !adapter.getPageTitle(tabPosition).equals("Fuse")) {
                                if (adapter.getItemCount() > 2) {
                                    String searchKey = "Node";
                                    int index = 0;
                                    for (String key : insertedDevices.keySet()) {
                                        if (key.equals(searchKey)) {
                                            System.out.println("Index of '" + searchKey + "' is: " + index);
                                            adapter.removeFragment(index);
                                            insertedDevices.remove("Node");
                                            break;
                                        }
                                        index++;
                                    }
                                }

                                menuItem.setChecked(!menuItem.isChecked());
                                adapter.addFragment(new FuseDialogFragment(networkId, findDeviceModel.getOutput().get(findDeviceModel.getOutput().size() - 1).getDeviceType()), "Fuse");
                                insertDevices.put("Fuse", 14);
                                insertedDevices.put("Fuse", 14);
                                binding.viewPager.setAdapter(adapter);
                                viewPager.requestLayout();
                                adapter.notifyDataSetChanged();
                                Args.setIsFuseValidate(true);
                            }
                            return true;

                        case R.id.breaker_menu:
                            if (adapter.getItemCount() < 5 && !adapter.getPageTitle(tabPosition).equals("Breaker")) {
                                if (adapter.getItemCount() > 2) {
                                    String searchKey = "Node";
                                    int index = 0;
                                    for (String key : insertedDevices.keySet()) {
                                        if (key.equals(searchKey)) {
                                            System.out.println("Index of '" + searchKey + "' is: " + index);
                                            adapter.removeFragment(index);
                                            insertedDevices.remove("Node");
                                            break;
                                        }
                                        index++;
                                    }
                                }

                                menuItem.setChecked(!menuItem.isChecked());
                                adapter.addFragment(new BreakerDialogFragment(networkId, findDeviceModel.getOutput().get(findDeviceModel.getOutput().size() - 1).getDeviceType()), "Breaker");
                                insertDevices.put("Breaker", 8);
                                insertedDevices.put("Breaker", 8);
                                binding.viewPager.setAdapter(adapter);
                                viewPager.requestLayout();
                                adapter.notifyDataSetChanged();
                                Args.setISBreakerValidate(true);
                            }
                            return true;

                        case R.id.switched_menu:
                            if (adapter.getItemCount() < 5 && !adapter.getPageTitle(tabPosition).equals("Switch")) {
                                if (adapter.getItemCount() > 2) {
                                    String searchKey = "Node";
                                    int index = 0;
                                    for (String key : insertedDevices.keySet()) {
                                        if (key.equals(searchKey)) {
                                            System.out.println("Index of '" + searchKey + "' is: " + index);
                                            adapter.removeFragment(index);
                                            insertedDevices.remove("Node");
                                            break;
                                        }
                                        index++;
                                    }
                                }

                                menuItem.setChecked(!menuItem.isChecked());
                                adapter.addFragment(new SwitchDialogFragment(networkId, findDeviceModel.getOutput().get(findDeviceModel.getOutput().size() - 1).getDeviceType()), "Switch");
                                insertDevices.put("Switch", 13);
                                insertedDevices.put("Switch", 13);
                                binding.viewPager.setAdapter(adapter);
                                viewPager.requestLayout();
                                adapter.notifyDataSetChanged();
                                Args.setIsSwitchValidate(true);
                            }
                            return true;

                        case R.id.byPhase:
                            if (adapter.getItemCount() < 5 && !adapter.getPageTitle(tabPosition).equals("SpotLoad By Phase")) {
                                if (adapter.getItemCount() > 2) {
                                    String searchKey = "Node";
                                    int index = 0;
                                    for (String key : insertedDevices.keySet()) {
                                        if (key.equals(searchKey)) {
                                            System.out.println("Index of '" + searchKey + "' is: " + index);
                                            adapter.removeFragment(index);
                                            insertedDevices.remove("Node");
                                            break;
                                        }
                                        index++;
                                    }
                                }

                                menuItem.setChecked(!menuItem.isChecked());
                                adapter.addFragment(new SpotLoadDialogFragment("byPhase", phase, findDeviceModel.getOutput().get(findDeviceModel.getOutput().size() - 1).getDeviceType()), "SpotLoad By Phase");
                                insertDevices.put("SpotLoad By Phase", 20);
                                insertedDevices.put("SpotLoad By Phase", 20);
                                binding.viewPager.setAdapter(adapter);
                                viewPager.requestLayout();
                                adapter.notifyDataSetChanged();
                                Args.setIsSpotloadValidate(true);
                            }
                            return true;

                        case R.id.threePhase:
                            if (adapter.getItemCount() < 5 && phase.equals("7") && !adapter.getPageTitle(tabPosition).equals("SpotLoad Three Phase")) {
                                if (adapter.getItemCount() > 2) {
                                    String searchKey = "Node";
                                    int index = 0;
                                    for (String key : insertedDevices.keySet()) {
                                        if (key.equals(searchKey)) {
                                            System.out.println("Index of '" + searchKey + "' is: " + index);
                                            adapter.removeFragment(index);
                                            insertedDevices.remove("Node");
                                            break;
                                        }
                                        index++;
                                    }
                                }

                                menuItem.setChecked(!menuItem.isChecked());
                                adapter.addFragment(new SpotLoadDialogFragment("threePhase", phase, findDeviceModel.getOutput().get(findDeviceModel.getOutput().size() - 1).getDeviceType()), "SpotLoad Three Phase");
                                insertDevices.put("SpotLoad", 20);
                                insertedDevices.put("SpotLoad", 20);
                                binding.viewPager.setAdapter(adapter);
                                viewPager.requestLayout();
                                adapter.notifyDataSetChanged();
                                Args.setIsSpotloadValidate(true);
                            } else {
                                Snackbar.make(binding.getRoot(), "Installed Device By Phase", Snackbar.LENGTH_LONG).show();
                            }
                            return true;

                        case R.id.shunt_capacitor_menu:
                            if (adapter.getItemCount() < 5 && !adapter.getPageTitle(tabPosition).equals("Capacitor")) {
                                if (adapter.getItemCount() > 2) {
                                    String searchKey = "Node";
                                    int index = 0;
                                    for (String key : insertedDevices.keySet()) {
                                        if (key.equals(searchKey)) {
                                            System.out.println("Index of '" + searchKey + "' is: " + index);
                                            adapter.removeFragment(index);
                                            insertedDevices.remove("Node");
                                            break;
                                        }
                                        index++;
                                    }
                                }

                                menuItem.setChecked(!menuItem.isChecked());
                                adapter.addFragment(new CapacitorDialogFragment(networkId, findDeviceModel.getOutput().get(findDeviceModel.getOutput().size() - 1).getDeviceType()), "ShuntCapacitor");
                                insertDevices.put("ShuntCapacitor", 17);
                                insertedDevices.put("ShuntCapacitor", 17);
                                binding.viewPager.setAdapter(adapter);
                                viewPager.requestLayout();
                                adapter.notifyDataSetChanged();
                                Args.setIsCapacitorValidate(true);

                            }
                            return true;

                        default:
                            return false;
                    }
                }
            });
            popupMenu.show();
        });

        binding.RemoveDevice.setOnClickListener(v -> {
            if (!adapter.getPageTitle(tabPosition).equals("Overhead") && !adapter.getPageTitle(tabPosition).equals("Node")) {
                adapter.removeFragment(tabPosition);
            } else {
                Snackbar.make(binding.getRoot(), "You must select a device !", Snackbar.LENGTH_LONG).show();
            }
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

                            if (adapter.getPageTitle(index).equals("ShuntCapacitor")) {
                                Args.setIsCapacitorValidate(true);
                                getCapacitorData();
                            }

                            if (adapter.getPageTitle(index).equals("Fuse")) {
                                Args.setIsFuseValidate(true);
                                getFuseData();
                            }

                            if (adapter.getPageTitle(index).equals("SpotLoad Three Phase")) {
                                Args.setIsSpotloadValidate(true);
                                getSpotLoadData();
                            }
                            if (adapter.getPageTitle(index).equals("SpotLoad By Phase")) {
                                Args.setIsSpotloadValidate(true);
                                getSpotLoadData();
                            }

                            if (adapter.getPageTitle(index).equals("Switch")) {
                                Args.setIsSwitchValidate(true);
                                getSwitchData();
                            }

                            if (adapter.getPageTitle(index).equals("Transformer")) {
                                Args.setIsTransformerValidate(true);
                                getTransformerData();
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

    private void getDevices(String sectionId) {
        JsonObject jsonObject1 = new JsonObject();
        jsonObject1.addProperty("DeviceNumber", sectionId);
        jsonObject1.addProperty("DeviceType", "40");
        jsonObject1.addProperty("UserType", prefManager.getUserType());
        jsonObject1.addProperty("CYMDBNET", prefManager.getDBName());
        Retrofit retrofit = RetrofitClient.getClient(mainContext);
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<FindDeviceModel> call = apiInterface.FindDevice(jsonObject1);
        call.enqueue(new Callback<FindDeviceModel>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<FindDeviceModel> call, @NonNull Response<FindDeviceModel> response) {
                if (response.code() == 200) {
                    try {
                        findDeviceModel = response.body();
                        assert findDeviceModel != null;
                        if (findDeviceModel.getOutput() != null && !findDeviceModel.getOutput().isEmpty()) {
                            for (int i = 0; i < findDeviceModel.getOutput().size(); i++) {
                                if (findDeviceModel.getOutput().get(i).getDeviceType() == 8) {
                                    JsonObject jsonObject2 = new JsonObject();
                                    jsonObject2.addProperty("DeviceNumber", findDeviceModel.getOutput().get(i).getDeviceNumber());
                                    jsonObject2.addProperty("DeviceType", findDeviceModel.getOutput().get(i).getDeviceType().toString());
                                    adapter.addFragment(new BreakerFragment(OverHeadMoreInfoDialog.this,OverHeadMoreInfoDialog.this, voltage, networkId,jsonObject2), "Breaker");
                                    deviceSize.put("Breaker", 8);
                                    insertedDevices.put("Breaker", 8);
                                    viewPager.setAdapter(adapter);
                                } else if (findDeviceModel.getOutput().get(i).getDeviceType() == 16) {
                                    JsonObject jsonObject2 = new JsonObject();
                                    jsonObject2.addProperty("DeviceNumber", findDeviceModel.getOutput().get(i).getDeviceNumber());
                                    jsonObject2.addProperty("DeviceType", findDeviceModel.getOutput().get(i).getDeviceType().toString());
                                    adapter.addFragment(new ShuntCapacitorFragment(OverHeadMoreInfoDialog.this, jsonObject2), "ShuntCapacitor");
                                    deviceSize.put("ShuntCapacitor", 17);
                                    insertedDevices.put("ShuntCapacitor", 17);
                                    viewPager.setAdapter(adapter);
                                } else if (findDeviceModel.getOutput().get(i).getDeviceType() == 14) {
                                    JsonObject jsonObject2 = new JsonObject();
                                    jsonObject2.addProperty("DeviceNumber", findDeviceModel.getOutput().get(i).getDeviceNumber());
                                    jsonObject2.addProperty("DeviceType", findDeviceModel.getOutput().get(i).getDeviceType().toString());
                                    adapter.addFragment(new FuseFragment(OverHeadMoreInfoDialog.this,OverHeadMoreInfoDialog.this, voltage, jsonObject2), "Fuse");
                                    deviceSize.put("Fuse", 14);
                                    insertedDevices.put("Fuse", 14);
                                    viewPager.setAdapter(adapter);
                                } else if (findDeviceModel.getOutput().get(i).getDeviceType() == 20) {
                                    JsonObject jsonObject2 = new JsonObject();
                                    jsonObject2.addProperty("DeviceNumber", findDeviceModel.getOutput().get(i).getDeviceNumber());
                                    jsonObject2.addProperty("DeviceType", findDeviceModel.getOutput().get(i).getDeviceType().toString());
                                    adapter.addFragment(new SpotloadFragment(OverHeadMoreInfoDialog.this, jsonObject2), "SpotLoad");
                                    deviceSize.put("SpotLoad", 20);
                                    insertedDevices.put("SpotLoad", 20);
                                    viewPager.setAdapter(adapter);
                                } else if (findDeviceModel.getOutput().get(i).getDeviceType() == 13) {
                                    JsonObject jsonObject2 = new JsonObject();
                                    jsonObject2.addProperty("DeviceNumber", findDeviceModel.getOutput().get(i).getDeviceNumber());
                                    jsonObject2.addProperty("DeviceType", findDeviceModel.getOutput().get(i).getDeviceType().toString());
                                    adapter.addFragment(new SwitchFragment(OverHeadMoreInfoDialog.this,OverHeadMoreInfoDialog.this,networkId, voltage, jsonObject2), "Switch");
                                    deviceSize.put("Switch", 13);
                                    insertedDevices.put("Switch", 13);
                                    viewPager.setAdapter(adapter);
                                } else if (findDeviceModel.getOutput().get(i).getDeviceType() == 5) {
                                    JsonObject jsonObject2 = new JsonObject();
                                    jsonObject2.addProperty("DeviceNumber", findDeviceModel.getOutput().get(i).getDeviceNumber());
                                    jsonObject2.addProperty("DeviceType", findDeviceModel.getOutput().get(i).getDeviceType().toString());
                                    adapter.addFragment(new TransformerFragment(OverHeadMoreInfoDialog.this, OverHeadMoreInfoDialog.this, OverHeadMoreInfoDialog.this, networkId, voltage, jsonObject2), "Transformer");
                                    insertedDevices.put("Transformer", 5);
                                    deviceSize.put("Transformer", 5);
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.d("Exception", Objects.requireNonNull(e.getLocalizedMessage()));
                    }
                } else {
                    @SuppressLint("InflateParams")
                    View layout = LayoutInflater.from(mainContext).inflate(R.layout.toast_layout, null);
                    TextView Ok = layout.findViewById(R.id.okBtn);
                    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                    TextView header = layout.findViewById(R.id.headerTv);
                    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                    TextView description = layout.findViewById(R.id.descripTv);
                    header.setText(response.message() + " - " + response.code());
                    description.setText(mainContext.getString(R.string.error_msg));
                    Ok.setOnClickListener(v -> getDevices(sectionId));
                    Toast toast = new Toast(mainContext);
                    toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<FindDeviceModel> call, @NonNull Throwable t) {
                @SuppressLint("InflateParams")
                View layout = LayoutInflater.from(mainContext).inflate(R.layout.toast_layout, null);
                TextView Ok = layout.findViewById(R.id.okBtn);
                @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                TextView header = layout.findViewById(R.id.headerTv);
                @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                TextView description = layout.findViewById(R.id.descripTv);
                header.setText(mainContext.getString(R.string.error));
                description.setText(mainContext.getString(R.string.error_msg));
                Ok.setOnClickListener(v -> getDevices(sectionId));
                Toast toast = new Toast(mainContext);
                toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
            }
        });
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
                    } else {
                        getSectionDeviceData();
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

    private void getSwitchData() {
        Args.getSwitchParameter().observe((LifecycleOwner) mainContext, bundle -> {
            if (bundle != null && bundle.getString("DeviceNumber") != null && bundle.getString("DeviceType") != null && sectionId != null && bundle.getString("Location") != null && bundle.getString("Status") != null && bundle.getString("EquipmentID") != null) {
                try {
                    if (!sectionData.isEmpty()) {
                        switchObject.addProperty("DeviceNumber", "0");
                        switchObject.addProperty("DeviceType", bundle.getString("DeviceType"));
                        switchObject.addProperty("Location", bundle.getString("Location"));
                        switchObject.addProperty("Status", bundle.getString("Status"));
                        switchObject.addProperty("EquipmentID", bundle.getString("EquipmentID"));
                        deviceArray.add(switchObject);
                        sendData();
                    } else {
                        getSectionDeviceData();
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

    private void getFuseData() {
        Args.getFuseParameter().observe((LifecycleOwner) mainContext, bundle -> {
            if (bundle != null && bundle.getString("DeviceNumber") != null && bundle.getString("DeviceType") != null && sectionId != null && bundle.getString("Location") != null && bundle.getString("Status") != null && bundle.getString("EquipmentID") != null) {
                try {
                    if (!sectionData.isEmpty()) {
                        fuseObject.addProperty("DeviceNumber", "0");
                        fuseObject.addProperty("DeviceType", bundle.getString("DeviceType"));
                        fuseObject.addProperty("Location", bundle.getString("Location"));
                        fuseObject.addProperty("Status", bundle.getString("Status"));
                        fuseObject.addProperty("EquipmentID", bundle.getString("EquipmentID"));
                        deviceArray.add(fuseObject);
                        sendData();
                    } else {
                        getSectionDeviceData();
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

    private void getTransformerData() {
        Args.getTransformerParameter().observe((LifecycleOwner) mainContext, bundle -> {
            if (bundle != null && sectionId != null && bundle.getString("DeviceNumber") != null && bundle.getString("DeviceType") != null && bundle.getString("Location") != null && bundle.getString("Status") != null && bundle.getString("EquipmentID") != null) {
                try {
                    if (!sectionData.isEmpty()) {
                        transformerObject.addProperty("DeviceNumber", "0");
                        transformerObject.addProperty("DeviceType", bundle.getString("DeviceType"));
                        transformerObject.addProperty("Location", bundle.getString("Location"));
                        transformerObject.addProperty("Status", bundle.getString("Status"));
                        transformerObject.addProperty("EquipmentID", bundle.getString("EquipmentID"));
                        transformerObject.addProperty("DTCName", bundle.getString("dtName"));
                        deviceArray.add(transformerObject);
                        sendData();
                    } else {
                        getSectionDeviceData();
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

    private void getCapacitorData() {
        Args.getCapacitorParameter().observe((LifecycleOwner) mainContext, bundle -> {
            if (bundle != null && bundle.getString("DeviceNumber") != null && bundle.getString("DeviceType") != null && sectionId != null && bundle.getString("Location") != null && bundle.getString("Status") != null && bundle.getString("EquipmentID") != null) {
                try {
                    if (!sectionData.isEmpty()) {
                        capacitorObject.addProperty("DeviceNumber", "0");
                        capacitorObject.addProperty("DeviceType", bundle.getString("DeviceType"));
                        capacitorObject.addProperty("Location", bundle.getString("Location"));
                        capacitorObject.addProperty("Status", bundle.getString("Status"));
                        capacitorObject.addProperty("EquipmentID", bundle.getString("EquipmentID"));
                        deviceArray.add(capacitorObject);
                        sendData();
                    } else {
                        getSectionDeviceData();
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

    private void getSpotLoadData() {
        Args.getSpotloadParameter().observe((LifecycleOwner) mainContext, bundle -> {
            if (bundle != null && bundle.getString("DeviceNumber") != null && bundle.getString("DeviceType") != null && sectionId != null && bundle.getString("Location") != null && bundle.getString("PhaseType") != null) {
                try {
                    if (!sectionData.isEmpty()) {
                        spotLoadObject.addProperty("DeviceNumber", "0");
                        spotLoadObject.addProperty("DeviceType", bundle.getString("DeviceType"));
                        spotLoadObject.addProperty("Location", bundle.getString("Location"));
                        spotLoadObject.addProperty("PhaseType", bundle.getString("PhaseType"));
                        JsonArray jsonArray1 = new JsonArray();
                        if (!ListDataManager.getData().isEmpty()) {
                            for (int i = 1; i < ListDataManager.getData().size(); i++) {
                                jsonArray1.add(ListDataManager.getData().get(i));
                            }
                        } else {
                            DefaultCustomer defaultCustomer = new DefaultCustomer(phase, bundle.getString("PhaseType"));
                            defaultCustomer.OneCustomer();
                            jsonArray1.add(ListDataManager.getDefaultData().get(0));
                        }
                        spotLoadObject.add("CustomerData", jsonArray1);
                        deviceArray.add(spotLoadObject);
                        sendData();
                        ListDataManager.clearData();
                        ListDataManager.clearDefaultData();
                    } else {
                        getSectionDeviceData();
                    }
                } catch (Exception e) {
                    Log.d("Exception", Objects.requireNonNull(e.getLocalizedMessage()));
                }
            }
        });
    }

    private void getSectionDeviceData() {
        if (phase != null && networkId != null && sectionId != null) {
            try {
                sectionData.addProperty("Username", prefManager.getUserName());
                sectionData.addProperty("SectionId", sectionId);
                sectionData.addProperty("Phase", phase);
                sectionData.addProperty("NetworkId", networkId);
                sectionData.addProperty("FromNodeID", "");
                sectionData.addProperty("Mode", "Mobile");
                sectionData.addProperty("CYMDBNET", prefManager.getDBName());
                JsonArray latArray = new JsonArray();
                JsonArray lonArray = new JsonArray();
                latArray.add(0);
                lonArray.add(0);
                sectionData.add("X", latArray);
                sectionData.add("Y", lonArray);

                for (int i = 0; i < adapter.getItemCount(); i++) {
                    String title = (String) adapter.getPageTitle(i);

                    if (title == null) continue;

                    switch (title) {
                        case "Transformer":
                            getTransformerData();
                            break;

                        case "Fuse":
                            getFuseData();
                            break;

                        case "Breaker":
                            getBreakerData();
                            break;

                        case "Switch":
                            getSwitchData();
                            break;

                        case "SpotLoad By Phase":
                            getSpotLoadData();
                            break;

                        case "SpotLoad Three Phase":
                            getSpotLoadData();
                            break;

                        case "Capacitor":
                            getCapacitorData();
                            break;

                        default:
                            Log.w("TabHandler", "Unhandled tab title: " + title);
                            break;
                    }
                }
            } catch (Exception e) {
                Log.d("Exception", Objects.requireNonNull(e.getLocalizedMessage()));
            }
        } else {
            Snackbar.make(binding.getRoot(), "Some Required Field Empty!", Snackbar.LENGTH_SHORT).show();
        }
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
                case "7":
                    binding.aChkBox.setChecked(true);
                    binding.bChkBox.setChecked(true);
                    binding.cChkBox.setChecked(true);
                    break;
            }
        }

        if (!sectionID.equals("None")) {
            binding.sectionIdEdt.setText(sectionID);
        }

        if (adapter.getItemCount() < 3) {
            NodeDetailFragment nodeDetailFragment = new NodeDetailFragment();
            Bundle bundle0 = new Bundle();
            bundle0.putString("fromX", fromX);
            bundle0.putString("fromY", fromY);
            bundle0.putString("fromNodeID", fromNodeId);
            bundle0.putString("toX", toNodeX);
            bundle0.putString("toY", toNodeY);
            bundle0.putString("toNodeID", toNodeID);
            nodeDetailFragment.setArguments(bundle0);
            if (!deviceSize.containsKey("Node")) {
                adapter.addFragment(nodeDetailFragment, "Node");
                insertedDevices.put("Node", 99);
                deviceSize.put("Node", 99);
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
        updateEqp(eqp, networkId, DeviceType, DeviceNumber, DbName);
    }

    private void updateEqp(String eqp, String networkId, String deviceType, String deviceNumber, String dbName) {
        JsonObject jsonObject1 = new JsonObject();
        jsonObject1.addProperty("NetworkId", networkId);
        jsonObject1.addProperty("DeviceType", deviceType);
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
                    try {
                        UpdateModel updateModel = response.body();
                        assert updateModel != null;
                        if (updateModel.getMessage().equalsIgnoreCase("Device Updated successfully")) {
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
                } else if (response.code() == 401) {
                    prefManager.setIsUserLogin(false);
                    mainContext.startActivity(new Intent(mainContext, LoginActivity.class));
                    dismiss();
                } else {
                    Snackbar.make(binding.getRoot(), response.message() + " - " + response.code(), Snackbar.LENGTH_LONG).setAction("Retry", v -> updateEqp(eqp, networkId, deviceType, deviceNumber, dbName)).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<UpdateModel> call, @NonNull Throwable t) {
                Snackbar.make(binding.getRoot(), mainContext.getString(R.string.error_msg), Snackbar.LENGTH_LONG).setAction("Retry", v -> updateEqp(eqp, networkId, deviceType, deviceNumber, dbName)).show();
            }
        });
    }

}
