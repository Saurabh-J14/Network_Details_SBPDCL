package com.techLabs.nbpdcl.view.survey;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.techLabs.nbpdcl.R;
import com.techLabs.nbpdcl.Utils.PrefManager;
import com.techLabs.nbpdcl.Utils.callBack.AddDevice;
import com.techLabs.nbpdcl.adapters.DynamicFragmentAdapter;
import com.techLabs.nbpdcl.databinding.SourceEditDialogBinding;
import com.techLabs.nbpdcl.view.survey.device.SourceEditFragment;
import com.techLabs.nbpdcl.view.survey.device.SourceNetworkEditFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class SourceEditDialog extends Dialog {

    private SourceEditDialogBinding binding;
    private final Context mainContext;
    private final FragmentManager fragmentManager;
    private final Lifecycle lifeCycle;
    private DynamicFragmentAdapter adapter;
    private PrefManager prefManager;
    private final Double lat;
    private final Double lon;
    private AddDevice addDevice;
    private SourceNetworkEditFragment networkFragment;
    private SourceEditFragment sourceFragment;

    public SourceEditDialog(@NonNull Context context, FragmentManager fragmentManager, Lifecycle lifecycle, Double lat, Double lon) {
        super(context);
        this.mainContext = context;
        this.fragmentManager = fragmentManager;
        this.lifeCycle = lifecycle;
        this.lat = lat;
        this.lon = lon;
    }

    @SuppressLint({"MissingInflatedId", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SourceEditDialogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        View MainLayoutBackGround = getWindow().getDecorView().getRootView();
        MainLayoutBackGround.setBackground(getContext().getDrawable(R.drawable.pop_layout_background));
        prefManager = new PrefManager(mainContext);

        addDevice = (AddDevice) mainContext;
        binding.imgClose.setOnClickListener(view -> dismiss());

        ViewPager2 viewPager = binding.viewPager;
        TabLayout tabLayout = binding.tablayout;
        adapter = new DynamicFragmentAdapter((FragmentActivity) mainContext);

        /*adapter.addFragment(new SourceNetworkEditFragment(), "Network");
        adapter.addFragment(new SourceEditFragment(lat, lon), "Source");*/
        networkFragment = new SourceNetworkEditFragment();
        sourceFragment = new SourceEditFragment(lat, lon);

        adapter.addFragment(networkFragment, "Network");
        adapter.addFragment(sourceFragment, "Source");

        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(adapter.getPageTitle(position));
        }).attach();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        binding.btnOk.setOnClickListener(v -> {
            try {
                checkDetails();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        });

        binding.btnCancel.setOnClickListener(v -> {
            dismiss();
        });

    }

    private void checkDetails() throws JSONException {
        boolean isSourceEditFragmentValid = false;
        boolean isSourceNetworkEditFragmentValid = false;

        JSONObject sourceEditData = null;
        JSONObject sourceNetworkEditData = null;

        /*Fragment sourceEditFragment = fragmentManager.findFragmentByTag("f1");

        if (sourceEditFragment instanceof SourceEditFragment) {
            if (sourceEditFragment.getView() != null) {
                if (((SourceEditFragment) sourceEditFragment).checkAllFields()) {
                    sourceEditData = ((SourceEditFragment) sourceEditFragment).getData();
                    isSourceEditFragmentValid = true;
                } else {
                    Toast.makeText(mainContext, "Please fill all required fields in the Source tab.", Toast.LENGTH_SHORT).show();
                }
            }
        }*/
        if (sourceFragment != null && sourceFragment.getView() != null) {
            if (sourceFragment.checkAllFields()) {
                sourceEditData = sourceFragment.getData();
                isSourceEditFragmentValid = true;
            }
        }

        /*Fragment sourceNetworkEditFragment = fragmentManager.findFragmentByTag("f0");

        if (sourceNetworkEditFragment instanceof SourceNetworkEditFragment) {
            if (sourceNetworkEditFragment.getView() != null) {
                if (((SourceNetworkEditFragment) sourceNetworkEditFragment).checkAllFields()) {
                    sourceNetworkEditData = ((SourceNetworkEditFragment) sourceNetworkEditFragment).getData();
                    isSourceNetworkEditFragmentValid = true;
                } else {
                    Snackbar.make(binding.getRoot(), "All field required in Both Tab", Snackbar.LENGTH_LONG).show();
                }
            }
        }*/
        if (networkFragment != null && networkFragment.getView() != null) {
            if (networkFragment.checkAllFields()) {
                sourceNetworkEditData = networkFragment.getData();
                isSourceNetworkEditFragmentValid = true;
            }
        }


        if (isSourceEditFragmentValid && isSourceNetworkEditFragmentValid) {
            try {
                JsonObject sectionObject = new JsonObject();
                sectionObject.addProperty("Username", prefManager.getUserName());
                sectionObject.addProperty("NetworkId", sourceNetworkEditData.getString("networkName"));
                sectionObject.addProperty("Mode", "Mobile");
                sectionObject.addProperty("CYMDBNET", prefManager.getDBName());
                JsonArray x = new JsonArray();
                JsonArray y = new JsonArray();
                x.add(sourceEditData.getString("Latitude"));
                y.add(sourceEditData.getString("Longitude"));
                sectionObject.add("X", x);
                sectionObject.add("Y", y);

                JsonObject deviceObject = new JsonObject();
                deviceObject.addProperty("DeviceType", "0");
                deviceObject.addProperty("NetworkType", "0");
                deviceObject.addProperty("Group1", sourceNetworkEditData.getString("group1"));
                deviceObject.addProperty("Group2", sourceNetworkEditData.getString("group2"));
                deviceObject.addProperty("Group3", sourceNetworkEditData.getString("group3"));
                deviceObject.addProperty("Group4", sourceNetworkEditData.getString("groupFour"));
                deviceObject.addProperty("Group5", sourceNetworkEditData.getString("groupFive"));
                deviceObject.addProperty("Group6", sourceNetworkEditData.getString("groupSix"));
                deviceObject.addProperty("AmbientTemperature", sourceNetworkEditData.getString("ambientTemperature"));
                deviceObject.addProperty("EquipmentID", sourceEditData.getString("eqpID"));

                JsonArray deviceArray = new JsonArray();
                deviceArray.add(deviceObject);
                JsonArray jsonArray = new JsonArray();
                JsonObject jsonObject = new JsonObject();
                jsonObject.add("Section", sectionObject);
                jsonObject.add("Device", deviceArray);
                jsonArray.add(jsonObject);

                if (addDevice != null) {
                    JsonObject object = new JsonObject();
                    object.add("Data", jsonArray);
                    addDevice.addDevice("0", object);
                    dismiss();
                }
            }catch (Exception e){
                Log.d("Exception", Objects.requireNonNull(e.getLocalizedMessage()));
            }
        }
    }

}
