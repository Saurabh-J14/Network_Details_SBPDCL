package com.techLabs.nbpdcl.view.fragment.shortCircuitAnalysis;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.tabs.TabLayout;
import com.techLabs.nbpdcl.R;
import com.techLabs.nbpdcl.adapters.analysis.ShortCircuitViewPagerAdapter;
import com.techLabs.nbpdcl.databinding.ShortCircuitLayoutBinding;

import java.util.ArrayList;
import java.util.List;

public class ShortCircuit extends BottomSheetDialogFragment {
    private Context context;
    private ShortCircuitLayoutBinding binding;
    private BottomSheetDialog dialog;
    private BottomSheetBehavior<View> bottomSheetBehavior;

    public ShortCircuit(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = ShortCircuitLayoutBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        CoordinatorLayout layout = dialog.findViewById(R.id.bottomSheetLayout);
        assert layout != null;
        layout.setMinimumHeight(Resources.getSystem().getDisplayMetrics().heightPixels);
        Bundle bundle = this.getArguments();

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HALF_EXPANDED + 5) {

                } else if (newState == BottomSheetBehavior.PEEK_HEIGHT_AUTO) {

                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

//        binding.tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        binding.tabLayout.addTab(binding.tabLayout.newTab().setCustomView(createTabView("Calculation")));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setCustomView(createTabView("Parameters")));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setCustomView(createTabView("Network")));
        binding.tabLayout.addTab(binding.tabLayout.newTab().setCustomView(createTabView("ShortCircuitRating")));
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

        if (bundle != null) {
            List<String> list = new ArrayList<String>((ArrayList<String>) bundle.get("Network"));
            ShortCircuitViewPagerAdapter adapter = new ShortCircuitViewPagerAdapter(fragmentManager, getLifecycle(), context, list, bundle.getString("Index"), bundle.getString("NodeId"));
            binding.viewPager.setAdapter(adapter);
        }


        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
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
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position));
            }
        });

    }

    private View createTabView(String tabText) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.custom_tab, null);
        TextView textView = view.findViewById(R.id.tabText);
        textView.setText(tabText);
        return view;
    }
}
