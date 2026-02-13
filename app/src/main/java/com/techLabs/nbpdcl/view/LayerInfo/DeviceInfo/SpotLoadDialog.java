package com.techLabs.nbpdcl.view.LayerInfo.DeviceInfo;

import static com.techLabs.nbpdcl.R.drawable;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.gson.JsonObject;
import com.techLabs.nbpdcl.R;
import com.techLabs.nbpdcl.Utils.callBack.IsClicked;
import com.techLabs.nbpdcl.Utils.callBack.SectionCallBack;
import com.techLabs.nbpdcl.Utils.callBack.Update;
import com.techLabs.nbpdcl.adapters.DynamicFragmentAdapter;
import com.techLabs.nbpdcl.databinding.SpotloadinfoLayoutBinding;
import com.techLabs.nbpdcl.view.fragment.deviceFragment.CableFragment;
import com.techLabs.nbpdcl.view.fragment.deviceFragment.OverheadDetailsFragment;
import com.techLabs.nbpdcl.view.fragment.deviceFragment.SpotloadFragment;
import com.techLabs.nbpdcl.view.fragment.deviceFragment.UnbalanceDetailFragment;
import com.techLabs.nbpdcl.view.fragment.deviceInfo.NodeDetailFragment;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SpotLoadDialog extends Dialog implements SectionCallBack, Update {

    private SpotloadinfoLayoutBinding binding;
    private final Context mainContext;
    private final JsonObject jsonObject;
    private final String networkID;
    private final Map<String, Integer> deviceSize = new HashMap<>();
    private DynamicFragmentAdapter adapter;

    public SpotLoadDialog(@NonNull Context context, String networkID, JsonObject jsonObject) {
        super(context);
        this.mainContext = context;
        this.networkID = networkID;
        this.jsonObject = jsonObject;
    }

    @SuppressLint({"ResourceType", "SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SpotloadinfoLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        View MainLayoutBackGround = getWindow().getDecorView().getRootView();
        MainLayoutBackGround.setBackground(getContext().getDrawable(drawable.pop_layout_background));

        binding.addRemoveL.setVisibility(View.GONE);

        ViewPager2 viewPager = binding.viewPager;
        TabLayout transformerTab = binding.tablayout;
        adapter = new DynamicFragmentAdapter((FragmentActivity) mainContext);
        adapter.addFragment(new SpotloadFragment(this, jsonObject), "SpotLoad");
        deviceSize.put("SpotLoad", 20);
        viewPager.setAdapter(adapter);
        adjustViewPagerHeight(viewPager, 0);

        new TabLayoutMediator(transformerTab, viewPager, (tab, position) -> {
            @SuppressLint("InflateParams") View customView = LayoutInflater.from(mainContext).inflate(R.layout.custom_tab, null);
            TextView tabTitle = customView.findViewById(R.id.tabText);
            tabTitle.setText(adapter.getPageTitle(position));
            tab.setCustomView(customView);
            transformerTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    binding.headerTitle.setText(adapter.getPageTitle(tab.getPosition()));
                    adjustViewPagerHeight(viewPager, tab.getPosition());
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
        binding.editSpotloadLayout.setVisibility(View.GONE);

        binding.imgClose.setOnClickListener(view -> {
            dismiss();
            adapter.clearFragments();
        });

    }

    private void adjustViewPagerHeight(ViewPager2 viewPager, int position) {
        View view = adapter.createFragment(position).getView();
        if (view != null) {
            view.post(() -> {
                int wMeasureSpec = View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY);
                @SuppressLint("Range") int hMeasureSpec = View.MeasureSpec.makeMeasureSpec(ViewGroup.LayoutParams.WRAP_CONTENT, View.MeasureSpec.UNSPECIFIED);
                view.measure(wMeasureSpec, hMeasureSpec);
                ViewGroup.LayoutParams params = viewPager.getLayoutParams();
                params.height = view.getMeasuredHeight();
                viewPager.setLayoutParams(params);
            });
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
                    break;
                default:
                    binding.aChkBox.setChecked(true);
                    binding.bChkBox.setChecked(true);
                    binding.cChkBox.setChecked(true);
                    break;
            }
        }

        if (!sectionID.equals("None")) {
            binding.sectionIdTv.setText(sectionID);
        } else {
            binding.sectionIdTv.setText(R.string.undefined);
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
                    CableFragment cableFragment = new CableFragment(mainContext, this, this, networkID, "", jsonObject1);
                    if (!deviceSize.containsKey("Cable")) {
                        adapter.addFragment(cableFragment, "Cable");
                        deviceSize.put("Cable", 1);
                    }
                    break;
                case "2":
                    jsonObject1.addProperty("DeviceNumber", DeviceLineNumber);
                    jsonObject1.addProperty("DeviceType", DeviceTypeLine);
                    OverheadDetailsFragment overheadDetailsFragment = new OverheadDetailsFragment(this, this, jsonObject1, networkID);

                    if (!deviceSize.containsKey("OverHead")) {
                        adapter.addFragment(overheadDetailsFragment, "OverHead");
                        deviceSize.put("OverHead", 2);
                    }

                    break;
                case "23":
                    jsonObject1.addProperty("DeviceNumber", DeviceLineNumber);
                    jsonObject1.addProperty("DeviceType", DeviceTypeLine);
                    UnbalanceDetailFragment unbalanceDetailFragment = new UnbalanceDetailFragment(this, this, networkID, jsonObject1);

                    if (!deviceSize.containsKey("Cable")) {
                        adapter.addFragment(unbalanceDetailFragment, "UnBalanced");
                        deviceSize.put("UnBalanced", 23);
                    }
                    break;
            }
        }
    }

    @Override
    public void isUpdate(String eqp, String DeviceType, String DeviceNumber, String DbName, String status) {

    }
}
