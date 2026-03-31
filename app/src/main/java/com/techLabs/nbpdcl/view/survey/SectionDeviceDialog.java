package com.techLabs.nbpdcl.view.survey;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
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
import com.techLabs.nbpdcl.Utils.DefaultCustomer;
import com.techLabs.nbpdcl.Utils.ListDataManager;
import com.techLabs.nbpdcl.Utils.PrefManager;
import com.techLabs.nbpdcl.Utils.UTMConverter;
import com.techLabs.nbpdcl.Utils.callBack.AddDevice;
import com.techLabs.nbpdcl.adapters.FragmentAdapter;
import com.techLabs.nbpdcl.databinding.EditDeviceLayoutBinding;
import com.techLabs.nbpdcl.view.survey.device.BreakerDialogFragment;
import com.techLabs.nbpdcl.view.survey.device.CapacitorDialogFragment;
import com.techLabs.nbpdcl.view.survey.device.FuseDialogFragment;
import com.techLabs.nbpdcl.view.survey.device.SectionFragment;
import com.techLabs.nbpdcl.view.survey.device.SpotLoadDialogFragment;
import com.techLabs.nbpdcl.view.survey.device.SwitchDialogFragment;
import com.techLabs.nbpdcl.view.survey.device.TransformerDialogFragment;

import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SectionDeviceDialog extends Dialog {

    private EditDeviceLayoutBinding binding;
    private final Context mainContext1;
    private List<GeoPoint> newSectionGeoPointList = new ArrayList<>();
    private final String networkId;
    private AddDevice addDevice;
    private final String nodeId;
    private FragmentAdapter adapter;
    private String phase;
    private int tabPosition;
    private PrefManager prefManager;
    private final JsonObject sectionData = new JsonObject();
    private final JsonObject sectionObject1 = new JsonObject();
    private final JsonObject breakerObject = new JsonObject();
    private final JsonObject transformerObject = new JsonObject();
    private final JsonObject fuseObject = new JsonObject();
    private final JsonObject switchObject = new JsonObject();
    private final JsonObject capacitorObject = new JsonObject();
    private final JsonObject spotLoadObject = new JsonObject();
    private String sectionID;
    private final JsonArray deviceArray = new JsonArray();

    public SectionDeviceDialog(@NonNull Context context1, List<GeoPoint> newSectionGeoPointList, String networkId, String nodeId) {
        super(context1);
        this.mainContext1 = context1;
        this.newSectionGeoPointList = newSectionGeoPointList;
        this.networkId = networkId;
        this.nodeId = nodeId;
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "NotifyDataSetChanged"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = EditDeviceLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        View MainLayoutBackGround = getWindow().getDecorView().getRootView();
        MainLayoutBackGround.setBackground(getContext().getDrawable(R.drawable.pop_background));

        addDevice = (AddDevice) mainContext1;
        prefManager = new PrefManager(mainContext1);

        binding.phaseA.setOnCheckedChangeListener((buttonView, chk) -> {
            if (binding.phaseB.isChecked() || binding.phaseC.isChecked() && !chk) {
                binding.phaseA.setChecked(chk);
                CheckPhase();
            } else if (!binding.phaseB.isChecked() && !binding.phaseC.isChecked() && !chk) {
                binding.phaseA.setChecked(true);
                CheckPhase();
            }
        });

        binding.phaseB.setOnCheckedChangeListener((buttonView, chk) -> {
            if (binding.phaseA.isChecked() || binding.phaseC.isChecked() && !chk) {
                binding.phaseB.setChecked(chk);
                CheckPhase();
            } else if (!binding.phaseA.isChecked() && !binding.phaseC.isChecked() && !chk) {
                binding.phaseB.setChecked(true);
                CheckPhase();
            }
        });

        binding.phaseC.setOnCheckedChangeListener((buttonView, chk) -> {
            if (binding.phaseA.isChecked() || binding.phaseB.isChecked() && !chk) {
                binding.phaseC.setChecked(chk);
                CheckPhase();
            } else if (!binding.phaseA.isChecked() && !binding.phaseB.isChecked() && !chk) {
                binding.phaseC.setChecked(true);
                CheckPhase();
            }
        });

        binding.okbtn.setOnClickListener(v -> {
            for (int i = 0; i < adapter.getItemCount(); i++) {
                if (Objects.equals(adapter.getPageTitle(i), "Section")) {
                    Args.setSectionValidate(true);
                } else if (Objects.equals(adapter.getPageTitle(i), "Transformer")) {
                    Args.setIsTransformerValidate(true);
                } else if (Objects.equals(adapter.getPageTitle(i), "Fuse")) {
                    Args.setIsFuseValidate(true);
                } else if (Objects.equals(adapter.getPageTitle(i), "Breaker")) {
                    Args.setISBreakerValidate(true);
                } else if (Objects.equals(adapter.getPageTitle(i), "Switch")) {
                    Args.setIsSwitchValidate(true);
                } else if (Objects.equals(adapter.getPageTitle(i), "SpotLoad By Phase")) {
                    Args.setIsSpotloadValidate(true);
                } else if (Objects.equals(adapter.getPageTitle(i), "SpotLoad Three Phase")) {
                    Args.setIsSpotloadValidate(true);
                }
            }
            if (binding != null) {
                checkDetails();
            }
        });

        binding.canclebtn.setOnClickListener(v -> {
            if (addDevice != null) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("isCancel", true);
                addDevice.addDevice("98", jsonObject);
                dismiss();
            }
            dismiss();
        });

        binding.viewPager.setVisibility(View.VISIBLE);
        binding.tablayout.setVisibility(View.VISIBLE);

        ViewPager2 viewPager = binding.viewPager;
        TabLayout tabLayout = binding.tablayout;

        SectionFragment sectionFragment = new SectionFragment();
        Bundle bundle = new Bundle();
        bundle.putString("Network", networkId);
        bundle.putParcelableArrayList("Coordinates", new ArrayList<>(newSectionGeoPointList));
        sectionFragment.setArguments(bundle);
        adapter = new FragmentAdapter((FragmentActivity) mainContext1);
        adapter.addFragment(sectionFragment, "Section");
        viewPager.requestLayout();
        adapter.notifyDataSetChanged();

        binding.viewPager.setAdapter(adapter);
        Args.setSectionValidate(true);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(adapter.getPageTitle(position));
        }).attach();

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(adapter.getPageTitle(position));
            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
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

        binding.addDevice.setOnClickListener(v -> {
            if (binding.phaseA.isChecked() && binding.phaseB.isChecked() && binding.phaseC.isChecked()) {
                phase = "7";
            } else if (binding.phaseB.isChecked() && binding.phaseC.isChecked()) {
                phase = "6";
            } else if (binding.phaseA.isChecked() && binding.phaseC.isChecked()) {
                phase = "5";
            } else if (binding.phaseA.isChecked() && binding.phaseB.isChecked()) {
                phase = "4";
            } else if (binding.phaseC.isChecked()) {
                phase = "3";
            } else if (binding.phaseB.isChecked()) {
                phase = "2";
            } else {
                phase = "1";
            }
            PopupMenu popupMenu = new PopupMenu(mainContext1, v);
            popupMenu.getMenuInflater().inflate(R.menu.add_device_menu, popupMenu.getMenu());
            popupMenu.setForceShowIcon(true);

            if (!adapter.getTitleList().contains("Section") && adapter.getItemCount() == 1) {
                popupMenu.getMenu().getItem(0).setEnabled(false);
                popupMenu.getMenu().getItem(1).setEnabled(false);
                popupMenu.getMenu().getItem(2).setEnabled(false);
                popupMenu.getMenu().getItem(3).setEnabled(false);
            } else {
                popupMenu.getMenu().getItem(0).setEnabled(true);
                popupMenu.getMenu().getItem(1).setEnabled(true);
                popupMenu.getMenu().getItem(2).setEnabled(true);
                popupMenu.getMenu().getItem(3).setEnabled(true);
            }

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @SuppressLint("NonConstantResourceId")
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()) {
                        case R.id.transformer_menu:
                            if (adapter.getTitleList().contains("Section")) {
                                if (adapter.getItemCount() < 3 && !Objects.equals(adapter.getPageTitle(tabPosition), "Transformer")) {
                                    menuItem.setChecked(!menuItem.isChecked());

                                    TransformerDialogFragment transformerDialogFragment = new TransformerDialogFragment(networkId, 0);
                                    adapter.addFragment(transformerDialogFragment, "Transformer");
                                    binding.viewPager.setAdapter(adapter);
                                    viewPager.requestLayout();
                                    adapter.notifyDataSetChanged();
                                    Args.setIsTransformerValidate(true);
                                }
                            } else {
                                if (adapter.getItemCount() < 1) {
                                    menuItem.setChecked(!menuItem.isChecked());

                                    TransformerDialogFragment transformerDialogFragment = new TransformerDialogFragment(networkId, 0);
                                    adapter.addFragment(transformerDialogFragment, "Transformer");
                                    binding.viewPager.setAdapter(adapter);
                                    viewPager.requestLayout();
                                    adapter.notifyDataSetChanged();
                                    Args.setIsTransformerValidate(true);
                                }
                            }
                            return true;

                        case R.id.fuse_menu:
                            if (adapter.getTitleList().contains("Section")) {
                                if (adapter.getItemCount() < 3 && !Objects.equals(adapter.getPageTitle(tabPosition), "Fuse")) {
                                    menuItem.setChecked(!menuItem.isChecked());
                                    adapter.addFragment(new FuseDialogFragment(networkId, 0), "Fuse");
                                    binding.viewPager.setAdapter(adapter);
                                    viewPager.requestLayout();
                                    adapter.notifyDataSetChanged();
                                    Args.setIsFuseValidate(true);
                                }
                            } else {
                                if (adapter.getItemCount() < 1) {
                                    menuItem.setChecked(!menuItem.isChecked());
                                    adapter.addFragment(new FuseDialogFragment(networkId, 0), "Fuse");
                                    binding.viewPager.setAdapter(adapter);
                                    viewPager.requestLayout();
                                    adapter.notifyDataSetChanged();
                                    Args.setIsFuseValidate(true);
                                }
                            }

                            return true;

                        case R.id.breaker_menu:
                            if (adapter.getTitleList().contains("Section")) {
                                if (adapter.getItemCount() < 3 && !Objects.equals(adapter.getPageTitle(tabPosition), "Breaker")) {
                                    menuItem.setChecked(!menuItem.isChecked());
                                    adapter.addFragment(new BreakerDialogFragment(networkId, 0), "Breaker");
                                    binding.viewPager.setAdapter(adapter);
                                    viewPager.requestLayout();
                                    adapter.notifyDataSetChanged();
                                    Args.setISBreakerValidate(true);

                                }
                            } else {
                                if (adapter.getItemCount() < 1) {
                                    menuItem.setChecked(!menuItem.isChecked());
                                    adapter.addFragment(new BreakerDialogFragment(networkId, 0), "Breaker");
                                    binding.viewPager.setAdapter(adapter);
                                    viewPager.requestLayout();
                                    adapter.notifyDataSetChanged();
                                    Args.setISBreakerValidate(true);

                                }
                            }

                            return true;

                        case R.id.switched_menu:
                            if (adapter.getTitleList().contains("Section")) {
                                if (adapter.getItemCount() < 3 && !Objects.equals(adapter.getPageTitle(tabPosition), "Switch")) {
                                    menuItem.setChecked(!menuItem.isChecked());
                                    adapter.addFragment(new SwitchDialogFragment(networkId, 0), "Switch");
                                    binding.viewPager.setAdapter(adapter);
                                    viewPager.requestLayout();
                                    adapter.notifyDataSetChanged();
                                    Args.setIsSwitchValidate(true);

                                }
                            } else {
                                if (adapter.getItemCount() < 1) {
                                    menuItem.setChecked(!menuItem.isChecked());
                                    adapter.addFragment(new SwitchDialogFragment(networkId, 0), "Switch");
                                    binding.viewPager.setAdapter(adapter);
                                    viewPager.requestLayout();
                                    adapter.notifyDataSetChanged();
                                    Args.setIsSwitchValidate(true);

                                }
                            }
                            return true;

                        case R.id.byPhase:
                            if (adapter.getTitleList().contains("Section")) {
                                if (adapter.getItemCount() < 3 && !Objects.equals(adapter.getPageTitle(tabPosition), "SpotLoad By Phase")) {
                                    menuItem.setChecked(!menuItem.isChecked());
                                    adapter.addFragment(new SpotLoadDialogFragment("byPhase", phase, 0), "SpotLoad By Phase");
                                    binding.viewPager.setAdapter(adapter);
                                    viewPager.requestLayout();
                                    adapter.notifyDataSetChanged();
                                    Args.setIsSpotloadValidate(true);
                                    Args.setPhaseValidate(phase);

                                    if (!adapter.getTitleList().contains("SpotLoad Three Phase")) {
                                        binding.phaseA.setEnabled(true);
                                        binding.phaseA.setFocusable(true);
                                        binding.phaseA.setClickable(true);

                                        binding.phaseB.setEnabled(true);
                                        binding.phaseB.setFocusable(true);
                                        binding.phaseB.setClickable(true);

                                        binding.phaseC.setEnabled(true);
                                        binding.phaseC.setFocusable(true);
                                        binding.phaseC.setClickable(true);
                                    }

                                }
                            } else {
                                if (adapter.getItemCount() < 1) {
                                    menuItem.setChecked(!menuItem.isChecked());
                                    adapter.addFragment(new SpotLoadDialogFragment("byPhase", phase, 0), "SpotLoad By Phase");
                                    binding.viewPager.setAdapter(adapter);
                                    viewPager.requestLayout();
                                    adapter.notifyDataSetChanged();
                                    Args.setIsSpotloadValidate(true);
                                    Args.setPhaseValidate(phase);

                                    if (!adapter.getTitleList().contains("SpotLoad Three Phase")) {
                                        binding.phaseA.setEnabled(true);
                                        binding.phaseA.setFocusable(true);
                                        binding.phaseA.setClickable(true);

                                        binding.phaseB.setEnabled(true);
                                        binding.phaseB.setFocusable(true);
                                        binding.phaseB.setClickable(true);

                                        binding.phaseC.setEnabled(true);
                                        binding.phaseC.setFocusable(true);
                                        binding.phaseC.setClickable(true);
                                    }

                                }
                            }
                            return true;

                        case R.id.threePhase:
                            if (adapter.getTitleList().contains("Section")) {
                                if (adapter.getItemCount() < 3 && !Objects.equals(adapter.getPageTitle(tabPosition), "SpotLoad")) {
                                    menuItem.setChecked(!menuItem.isChecked());
                                    adapter.addFragment(new SpotLoadDialogFragment("ThreePhase", phase, 0), "SpotLoad Three Phase");
                                    binding.viewPager.setAdapter(adapter);
                                    viewPager.requestLayout();
                                    adapter.notifyDataSetChanged();
                                    Args.setIsSpotloadValidate(true);
                                    Args.setPhaseValidate(phase);

                                    binding.phaseA.setChecked(true);
                                    binding.phaseA.setEnabled(false);
                                    binding.phaseA.setFocusable(false);
                                    binding.phaseA.setClickable(false);

                                    binding.phaseB.setChecked(true);
                                    binding.phaseB.setEnabled(false);
                                    binding.phaseB.setFocusable(false);
                                    binding.phaseB.setClickable(false);

                                    binding.phaseC.setChecked(true);
                                    binding.phaseC.setEnabled(false);
                                    binding.phaseC.setFocusable(false);
                                    binding.phaseC.setClickable(false);

                                }
                            } else {
                                if (adapter.getItemCount() < 1) {
                                    menuItem.setChecked(!menuItem.isChecked());
                                    adapter.addFragment(new SpotLoadDialogFragment("ThreePhase", phase, 0), "SpotLoad Three Phase");
                                    binding.viewPager.setAdapter(adapter);
                                    viewPager.requestLayout();
                                    adapter.notifyDataSetChanged();
                                    Args.setIsSpotloadValidate(true);
                                    Args.setPhaseValidate(phase);

                                    binding.phaseA.setChecked(true);
                                    binding.phaseA.setEnabled(false);
                                    binding.phaseA.setFocusable(false);
                                    binding.phaseA.setClickable(false);

                                    binding.phaseB.setChecked(true);
                                    binding.phaseB.setEnabled(false);
                                    binding.phaseB.setFocusable(false);
                                    binding.phaseB.setClickable(false);

                                    binding.phaseC.setChecked(true);
                                    binding.phaseC.setEnabled(false);
                                    binding.phaseC.setFocusable(false);
                                    binding.phaseC.setClickable(false);

                                }
                            }
                            return true;

                        case R.id.shunt_capacitor_menu:
                            if (adapter.getTitleList().contains("Section")) {
                                if (adapter.getItemCount() < 3 && !Objects.equals(adapter.getPageTitle(tabPosition), "Capacitor")) {
                                    menuItem.setChecked(!menuItem.isChecked());
                                    adapter.addFragment(new CapacitorDialogFragment(networkId, 0), "Capacitor");
                                    binding.viewPager.setAdapter(adapter);
                                    viewPager.requestLayout();
                                    adapter.notifyDataSetChanged();
                                    Args.setIsCapacitorValidate(true);

                                }
                            } else {
                                if (adapter.getItemCount() < 1) {
                                    menuItem.setChecked(!menuItem.isChecked());
                                    adapter.addFragment(new CapacitorDialogFragment(networkId, 0), "Capacitor");
                                    binding.viewPager.setAdapter(adapter);
                                    viewPager.requestLayout();
                                    adapter.notifyDataSetChanged();
                                    Args.setIsCapacitorValidate(true);

                                }
                            }

                            return true;

                        default:
                            return false;
                    }
                }
            });
            popupMenu.show();
        });

        binding.removeDevice.setOnClickListener(v -> {
            if (adapter.getItemCount() == 0) {
                binding.removeDevice.setEnabled(false);
                Snackbar.make(binding.getRoot(), "No Devices Added !", Snackbar.LENGTH_SHORT).show();
                return;
            } else {
                binding.removeDevice.setEnabled(true);
            }

            int position = tabPosition;
            boolean moreThanOneTab = adapter.getItemCount() > 1;
            boolean hasSectionTab = adapter.hasFragmentWithTitle("Section");
            boolean currentIsSection =
                    "Section".contentEquals(Objects.requireNonNull(adapter.getPageTitle(position)));

            if (hasSectionTab && moreThanOneTab && currentIsSection) {
                for (int i = 0; i < adapter.getItemCount(); i++) {
                    if (!"Section".contentEquals(Objects.requireNonNull(adapter.getPageTitle(i)))) {
                        position = i;
                        break;
                    }
                }
            }

            CharSequence csTitle = adapter.getPageTitle(position);
            String title = csTitle != null ? csTitle.toString() : "";

            adapter.removeFragment(position);

            if (adapter.getItemCount() == 0) {
                binding.viewPager.setAdapter(null);
            } else {
                binding.viewPager.setCurrentItem(0, false);
            }
            resetValidationFlagsForTitle(title);

            /*  if (adapter.getItemCount() > 1 && !adapter.getPageTitle(tabPosition).equals("Section")) {
                adapter.removeFragment(tabPosition);

                if (adapter.getPageTitle(tabPosition).equals("Transformer")) {
                    Args.setIsTransformerValidate(false);
                }

                if (adapter.getPageTitle(tabPosition).equals("Fuse")) {
                    Args.setIsFuseValidate(false);
                }

                if (adapter.getPageTitle(tabPosition).equals("Breaker")) {
                    Args.setISBreakerValidate(false);
                }

                if (adapter.getPageTitle(tabPosition).equals("Switch")) {
                    Args.setIsSwitchValidate(false);
                }

                if (adapter.getPageTitle(tabPosition).equals("SpotLoad By Phase")) {
                    Args.setIsSpotloadValidate(false);
                    binding.phaseA.setEnabled(true);
                    binding.phaseA.setFocusable(true);
                    binding.phaseA.setClickable(true);

                    binding.phaseB.setEnabled(true);
                    binding.phaseB.setFocusable(true);
                    binding.phaseB.setClickable(true);

                    binding.phaseC.setEnabled(true);
                    binding.phaseC.setFocusable(true);
                    binding.phaseC.setClickable(true);
                }

                if (adapter.getPageTitle(tabPosition).equals("SpotLoad Three Phase")) {
                    Args.setIsSpotloadValidate(false);
                    binding.phaseA.setEnabled(true);
                    binding.phaseA.setFocusable(true);
                    binding.phaseA.setClickable(true);

                    binding.phaseB.setEnabled(true);
                    binding.phaseB.setFocusable(true);
                    binding.phaseB.setClickable(true);

                    binding.phaseC.setEnabled(true);
                    binding.phaseC.setFocusable(true);
                    binding.phaseC.setClickable(true);
                }

                if (adapter.getPageTitle(tabPosition).equals("Capacitor")) {
                    Args.setIsCapacitorValidate(false);
                }

            } else {
                Snackbar.make(binding.getRoot(), "Section is required", Snackbar.LENGTH_LONG).show();
            }*/
        });

    }

    @Override
    public void dismiss() {
        super.dismiss();
        binding = null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (addDevice != null) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("isCancel", true);
            addDevice.addDevice("98", jsonObject);
            dismiss();
        }
        dismiss();
    }

    private void checkDetails() {
        binding.sectionIdEdt.setError(null);
        boolean isCancel = false;
        View focusView = null;


        if (TextUtils.isEmpty(binding.sectionIdEdt.getText().toString().trim())) {
            binding.sectionIdEdt.setError(Html.fromHtml("<font color='red'>Please enter number</font>"));
            focusView = binding.sectionIdEdt;
            isCancel = true;
        }

        if (isCancel) {
            focusView.requestFocus();
        } else {
            getPhase();
        }
    }

    private void getPhase() {
        try {
            if (binding.phaseA.isChecked() && binding.phaseB.isChecked() && binding.phaseC.isChecked()) {
                phase = "7";
            } else if (binding.phaseB.isChecked() && binding.phaseC.isChecked()) {
                phase = "6";
            } else if (binding.phaseA.isChecked() && binding.phaseC.isChecked()) {
                phase = "5";
            } else if (binding.phaseA.isChecked() && binding.phaseB.isChecked()) {
                phase = "4";
            } else if (binding.phaseC.isChecked()) {
                phase = "3";
            } else if (binding.phaseB.isChecked()) {
                phase = "2";
            } else {
                phase = "1";
            }

            sectionID = binding.sectionIdEdt.getText().toString();

            for (int i = 0; i < adapter.getItemCount(); i++) {
                if (Objects.equals(adapter.getPageTitle(i), "Section")) {
                    getSectionData();

                } else if (Objects.equals(adapter.getPageTitle(i), "Transformer")) {
                    getTransformerData();

                } else if (Objects.equals(adapter.getPageTitle(i), "Fuse")) {
                    getFuseData();

                } else if (Objects.equals(adapter.getPageTitle(i), "Breaker")) {
                    getBreakerData();

                } else if (Objects.equals(adapter.getPageTitle(i), "Switch")) {
                    getSwitchData();

                } else if (Objects.equals(adapter.getPageTitle(i), "SpotLoad By Phase")) {
                    getSpotLoadData();

                } else if (Objects.equals(adapter.getPageTitle(i), "SpotLoad Three Phase")) {
                    getSpotLoadData();
                } else {
                    getCapacitorData();

                }
            }

        } catch (Exception e) {
            Log.d("Exception", Objects.requireNonNull(e.getLocalizedMessage()));
        }
    }

    private void getSectionData() {
        Args.getSectionParameter().observe((LifecycleOwner) mainContext1, bundle -> {
            if (bundle != null && bundle.getString("DeviceNumber") != null && bundle.getString("DeviceType") != null && bundle.getString("CableID") != null && sectionObject1.isEmpty()) {
                try {
                    if (!sectionData.isEmpty()) {
                        sectionObject1.addProperty("DeviceNumber", "0");
                        sectionObject1.addProperty("DeviceType", bundle.getString("DeviceType"));
                        sectionObject1.addProperty("Location", "0");
                        sectionObject1.addProperty("Status", bundle.getString("Status"));
                        sectionObject1.addProperty("EquipmentID", bundle.getString("CableID"));
                        deviceArray.add(sectionObject1);
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

    private void getBreakerData() {
        Args.getBreakerParameter().observe((LifecycleOwner) mainContext1, bundle -> {
            if (bundle != null && bundle.getString("DeviceNumber") != null && bundle.getString("DeviceType") != null && bundle.getString("Location") != null && bundle.getString("Status") != null && bundle.getString("EquipmentID") != null && breakerObject.isEmpty()) {
                try {
                    if (!sectionData.isEmpty()) {
                        breakerObject.addProperty("DeviceNumber", "0");
                        breakerObject.addProperty("DeviceType", bundle.getString("DeviceType"));

                        if (sectionObject1.isEmpty()) {
                            breakerObject.addProperty("Location", "0");
                        } else {
                            breakerObject.addProperty("Location", bundle.getString("Location"));
                        }

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
        Args.getSwitchParameter().observe((LifecycleOwner) mainContext1, bundle -> {
            if (bundle != null && bundle.getString("DeviceNumber") != null && bundle.getString("DeviceType") != null && bundle.getString("Location") != null && bundle.getString("Status") != null && bundle.getString("EquipmentID") != null && switchObject.isEmpty()) {
                try {
                    if (!sectionData.isEmpty()) {
                        switchObject.addProperty("DeviceNumber", "0");
                        switchObject.addProperty("DeviceType", bundle.getString("DeviceType"));

                        if (sectionObject1.isEmpty()) {
                            switchObject.addProperty("Location", "0");
                        } else {
                            switchObject.addProperty("Location", bundle.getString("Location"));
                        }

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
        Args.getFuseParameter().observe((LifecycleOwner) mainContext1, bundle -> {
            if (bundle != null && bundle.getString("DeviceNumber") != null && bundle.getString("DeviceType") != null && bundle.getString("Location") != null && bundle.getString("Status") != null && bundle.getString("EquipmentID") != null && fuseObject.isEmpty()) {
                try {
                    if (!sectionData.isEmpty()) {
                        fuseObject.addProperty("DeviceNumber", "0");
                        fuseObject.addProperty("DeviceType", bundle.getString("DeviceType"));

                        if (sectionObject1.isEmpty()) {
                            fuseObject.addProperty("Location", "0");
                        } else {
                            fuseObject.addProperty("Location", bundle.getString("Location"));
                        }

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
        Args.getTransformerParameter().observe((LifecycleOwner) mainContext1, bundle -> {
            if (bundle != null && bundle.getString("DeviceNumber") != null && bundle.getString("DeviceType") != null && bundle.getString("Location") != null && bundle.getString("Status") != null && bundle.getString("EquipmentID") != null && transformerObject.isEmpty()) {
                try {
                    if (!sectionData.isEmpty()) {
                        transformerObject.addProperty("DeviceNumber", "0");
                        transformerObject.addProperty("DeviceType", bundle.getString("DeviceType"));
                        if (sectionObject1.isEmpty()) {
                            transformerObject.addProperty("Location", "0");
                        } else {
                            transformerObject.addProperty("Location", bundle.getString("Location"));
                        }
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
        Args.getCapacitorParameter().observe((LifecycleOwner) mainContext1, bundle -> {
            if (bundle != null && bundle.getString("DeviceNumber") != null && bundle.getString("DeviceType") != null && bundle.getString("Location") != null && bundle.getString("Status") != null && bundle.getString("EquipmentID") != null && capacitorObject.isEmpty()) {
                try {
                    if (!sectionData.isEmpty()) {
                        capacitorObject.addProperty("DeviceNumber", "0");
                        capacitorObject.addProperty("DeviceType", bundle.getString("DeviceType"));
                        if (sectionObject1.isEmpty()) {
                            capacitorObject.addProperty("Location", "0");
                        } else {
                            capacitorObject.addProperty("Location", bundle.getString("Location"));
                        }
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
        Args.getSpotloadParameter().observe((LifecycleOwner) mainContext1, bundle -> {
            if (bundle != null && bundle.getString("DeviceNumber") != null && bundle.getString("DeviceType") != null && bundle.getString("Location") != null && bundle.getString("PhaseType") != null && spotLoadObject.isEmpty()) {
                try {
                    if (!sectionData.isEmpty()) {
                        spotLoadObject.addProperty("DeviceNumber", "0");
                        spotLoadObject.addProperty("DeviceType", bundle.getString("DeviceType"));

                        if (sectionObject1.isEmpty()) {
                            spotLoadObject.addProperty("Location", "0");
                        } else {
                            spotLoadObject.addProperty("Location", bundle.getString("Location"));
                        }

                        spotLoadObject.addProperty("PhaseType", bundle.getString("PhaseType"));
                        JsonArray jsonArray1 = new JsonArray();
                        if (!ListDataManager.getData().isEmpty()) {
                            for (int i = 0; i < ListDataManager.getData().size(); i++) {
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
            } else {
                if (binding != null) {
                    Snackbar.make(binding.getRoot(), "All filed are required!", Snackbar.LENGTH_LONG).show();
                }
            }
        });

    }

    private void getSectionDeviceData() {
        if (phase != null && networkId != null && nodeId != null) {
            try {
                sectionData.addProperty("Username", prefManager.getUserName());
                sectionData.addProperty("SectionId", "0");
                sectionData.addProperty("Phase", phase);
                sectionData.addProperty("NetworkId", networkId);
                sectionData.addProperty("FromNodeID", nodeId);
//                sectionData.addProperty("Mode", "Web");
                sectionData.addProperty("CYMDBNET", prefManager.getDBName());
                JsonArray latArray = new JsonArray();
                JsonArray lonArray = new JsonArray();
                if (newSectionGeoPointList != null) {
                    for (GeoPoint point : newSectionGeoPointList) {
                        double lat = point.getLatitude();
                        double lon = point.getLongitude();
                        UTMConverter.Result utm = UTMConverter.fromLatLon(lat, lon);
                        latArray.add(utm.easting);
                        lonArray.add(utm.northing);
                    }
                }
                sectionData.add("X", latArray);
                sectionData.add("Y", lonArray);

                for (int i = 0; i < adapter.getItemCount(); i++) {
                    String title = (String) adapter.getPageTitle(i);

                    if (title == null) continue;

                    switch (title) {
                        case "Section":
                            getSectionData();
                            break;

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
        if (adapter.getItemCount() == deviceArray.size()) {
            try {
                if (sectionData.has("X") && sectionData.has("Y")) {
                    JsonArray xArray = sectionData.getAsJsonArray("X");
                    JsonArray yArray = sectionData.getAsJsonArray("Y");
                    if (xArray != null && yArray != null && xArray.size() == yArray.size()) {
                        JsonArray newX = new JsonArray();
                        JsonArray newY = new JsonArray();
                        for (int i = 0; i < xArray.size(); i++) {
                            double xVal = xArray.get(i).getAsDouble();
                            double yVal = yArray.get(i).getAsDouble();
                            if (xVal >= -90.0 && xVal <= 90.0 && yVal >= -180.0 && yVal <= 180.0) {
                                UTMConverter.Result utm = UTMConverter.fromLatLon(xVal, yVal);
                                newX.add(utm.easting);
                                newY.add(utm.northing);
                            } else {
                                newX.add(xArray.get(i));
                                newY.add(yArray.get(i));
                            }
                        }
                        sectionData.add("X", newX);
                        sectionData.add("Y", newY);
                    }
                }
            } catch (Exception e) {
                Log.d("UTM", "Failed to enforce UTM in sendData: " + e.getLocalizedMessage());
            }
            JsonObject jsonObject = new JsonObject();
            JsonObject jsonObject1 = new JsonObject();
            jsonObject1.add("Section", sectionData);
            jsonObject1.add("Device", deviceArray);
            JsonArray jsonArray = new JsonArray();
            jsonArray.add(jsonObject1);
            jsonObject.addProperty("Type", "Survey");
            jsonObject.add("Data", jsonArray);
            if (addDevice != null) {
                try {
                    addDevice.addDevice("98", jsonObject);
                } finally {
                    dismiss();
                }
            }
        } else {
            if (binding != null) {
                Snackbar.make(binding.getRoot(), "All filed are required!", Snackbar.LENGTH_LONG).show();
            }
            getPhase();
        }
    }

    private void CheckPhase() {
        if (binding.phaseA.isChecked() && binding.phaseB.isChecked() && binding.phaseC.isChecked()) {
            phase = "7";
        } else if (binding.phaseB.isChecked() && binding.phaseC.isChecked()) {
            phase = "6";
        } else if (binding.phaseA.isChecked() && binding.phaseC.isChecked()) {
            phase = "5";
        } else if (binding.phaseA.isChecked() && binding.phaseB.isChecked()) {
            phase = "4";
        } else if (binding.phaseC.isChecked()) {
            phase = "3";
        } else if (binding.phaseB.isChecked()) {
            phase = "2";
        } else {
            phase = "1";
        }
        Args.setPhaseValidate(phase);
    }

    private void resetValidationFlagsForTitle(String title) {
        switch (title) {
            case "Transformer":
                Args.setIsTransformerValidate(false);
                break;

            case "Fuse":
                Args.setIsFuseValidate(false);
                break;

            case "Breaker":
                Args.setISBreakerValidate(false);
                break;

            case "Switch":
                Args.setIsSwitchValidate(false);
                break;

            case "Capacitor":
                Args.setIsCapacitorValidate(false);
                break;

            case "SpotLoad By Phase":
                Args.setIsSpotloadValidate(false);
                binding.phaseA.setEnabled(true);
                binding.phaseA.setFocusable(true);
                binding.phaseA.setClickable(true);

                binding.phaseB.setEnabled(true);
                binding.phaseB.setFocusable(true);
                binding.phaseB.setClickable(true);

                binding.phaseC.setEnabled(true);
                binding.phaseC.setFocusable(true);
                binding.phaseC.setClickable(true);
                break;

            case "SpotLoad Three Phase":
                Args.setIsSpotloadValidate(false);
                binding.phaseA.setEnabled(true);
                binding.phaseA.setFocusable(true);
                binding.phaseA.setClickable(true);

                binding.phaseB.setEnabled(true);
                binding.phaseB.setFocusable(true);
                binding.phaseB.setClickable(true);

                binding.phaseC.setEnabled(true);
                binding.phaseC.setFocusable(true);
                binding.phaseC.setClickable(true);
                break;

            case "Section":
                Args.setSectionValidate(false);
                break;
        }
    }

}
