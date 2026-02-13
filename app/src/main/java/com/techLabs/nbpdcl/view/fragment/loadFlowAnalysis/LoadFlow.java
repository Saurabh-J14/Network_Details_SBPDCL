package com.techLabs.nbpdcl.view.fragment.loadFlowAnalysis;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

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
import com.techLabs.nbpdcl.adapters.analysis.LoadFlowViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LoadFlow extends BottomSheetDialogFragment {

    private BottomSheetDialog dialog;
    private BottomSheetBehavior<View> bottomSheetBehavior;
    private View root;
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private LoadFlowViewPagerAdapter adapter;
    private Context context;
    private final String[] Configuration = {"DEFAULT"};

    public LoadFlow(Context context) {
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
        root = inflater.inflate(R.layout.load_flow_layout, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bottomSheetBehavior = BottomSheetBehavior.from((View) view.getParent());
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HALF_EXPANDED + 2);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        CoordinatorLayout layout = dialog.findViewById(R.id.bottomSheetLayout);
        assert layout != null;
        layout.setMinimumHeight(Resources.getSystem().getDisplayMetrics().heightPixels);

        Spinner spinner = dialog.findViewById(R.id.configuration);

        ArrayAdapter<String> calculationAdapters = new ArrayAdapter<>(requireActivity(), R.layout.custom_spinner, Configuration);
        assert spinner != null;
        spinner.setAdapter(calculationAdapters);

        Bundle bundle = this.getArguments();

        tabLayout = dialog.findViewById(R.id.tab_layout);
        viewPager2 = dialog.findViewById(R.id.view_pager);
        tabLayout.addTab(tabLayout.newTab().setText("Parameters"));
        tabLayout.addTab(tabLayout.newTab().setText("Networks"));
        tabLayout.addTab(tabLayout.newTab().setText("Controls"));
        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();

        if (bundle != null) {
            List<String> list = new ArrayList<String>((ArrayList<String>) Objects.requireNonNull(bundle.get("Network")));
            adapter = new LoadFlowViewPagerAdapter(fragmentManager, getLifecycle(), context, list);
            viewPager2.setAdapter(adapter);
        }

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

    }

}
