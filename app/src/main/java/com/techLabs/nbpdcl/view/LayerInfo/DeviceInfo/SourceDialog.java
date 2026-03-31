package com.techLabs.nbpdcl.view.LayerInfo.DeviceInfo;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.techLabs.nbpdcl.R;
import com.techLabs.nbpdcl.adapters.Source.SourceAdapter;
import com.techLabs.nbpdcl.databinding.SourceBinding;

import java.util.Objects;

public class SourceDialog extends Dialog {

    private SourceBinding binding;
    private final Context context;
    private final FragmentManager fragmentManager;
    private final Lifecycle lifecycle;
    private final String nodeId;
    private final String latitude;
    private final String longitude;
    private final String utX;
    private final String utY;

    public SourceDialog(@NonNull Context context, FragmentManager fragmentManager, Lifecycle lifecycle, String nodeId, String latitude, String longitude, String utX, String utY) {
        super(context);
        this.context = context;
        this.fragmentManager = fragmentManager;
        this.lifecycle = lifecycle;
        this.nodeId = nodeId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.utX = utX;
        this.utY = utY;
    }

    @SuppressLint({"MissingInflatedId", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SourceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        View MainLayoutBackGround = getWindow().getDecorView().getRootView();
        MainLayoutBackGround.setBackground(getContext().getDrawable(R.drawable.pop_layout_background));

        binding.imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        binding.tablayout.addTab(binding.tablayout.newTab().setText("Network"));
        binding.tablayout.addTab(binding.tablayout.newTab().setText("Source"));

        if (nodeId != null) {
            SourceAdapter adapter = new SourceAdapter(fragmentManager, lifecycle, context, nodeId, latitude, longitude, utX != null ? utX : latitude, utY != null ? utY : longitude);
            binding.viewPager.setAdapter(adapter);
        }

        binding.tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                binding.tablayout.selectTab(binding.tablayout.getTabAt(position));
            }
        });

    }
}

