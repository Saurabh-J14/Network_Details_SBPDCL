package com.techLabs.nbpdcl.view.fragment;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.techLabs.nbpdcl.R;
import com.techLabs.nbpdcl.Utils.PrefManager;
import com.techLabs.nbpdcl.Utils.ResponseDataUtils;
import com.techLabs.nbpdcl.Utils.VoltageDonutView;
import com.techLabs.nbpdcl.databinding.FragmentDashboardBinding;
import com.techLabs.nbpdcl.models.FeederModel;
import com.techLabs.nbpdcl.models.MainFeeder;
import com.techLabs.nbpdcl.models.ProjectModel;
import com.techLabs.nbpdcl.retrofit.ApiInterface;
import com.techLabs.nbpdcl.retrofit.RetrofitClient;
import com.techLabs.nbpdcl.view.activity.ExistNetworkActivity;
import com.techLabs.nbpdcl.view.activity.LoginActivity;
import com.techLabs.nbpdcl.view.activity.Profile;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


public class DashboardFragment extends Fragment {
    private FragmentDashboardBinding binding;
    private PrefManager prefManager;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        prefManager = new PrefManager(requireContext());
        binding.toolbar.setTitle("Dashboard");
        binding.toolbar.inflateMenu(R.menu.dashboard_nav_menu);

        MenuItem profileItem = binding.toolbar.getMenu().findItem(R.id.menu_profile);
        MenuItem featuresItem = binding.toolbar.getMenu().findItem(R.id.menu_features);
        MenuItem surveyItem = binding.toolbar.getMenu().findItem(R.id.menu_survey);

        if (featuresItem != null && featuresItem.getIcon() != null) {
            featuresItem.getIcon().setTint(getResources().getColor(R.color.white));
        }

        if (surveyItem != null && surveyItem.getIcon() != null) {
            surveyItem.getIcon().setTint(getResources().getColor(R.color.white));
        }

        if (profileItem != null) {
            profileItem.setActionView(R.layout.profile_icon_layout);
            View profileView = profileItem.getActionView();
            assert profileView != null;
            TextView userLetter = profileView.findViewById(R.id.userProfile);

            String name = prefManager.getName();
            userLetter.setText(name != null && !name.isEmpty()
                    ? name.substring(0, 1).toUpperCase() : "R");

            profileView.setOnClickListener(v -> {
                Intent intent = new Intent(requireContext(), Profile.class);
                startActivity(intent);
            });

        } else {
            Log.e("DashboardFragment", "menu_profile item NOT FOUND");
        }

        binding.toolbar.setOnMenuItemClickListener(item -> {

            int id = item.getItemId();

            if (item.getItemId() == R.id.menu_features) {

                Fragment fragment = requireActivity()
                        .getSupportFragmentManager()
                        .findFragmentById(R.id.main);

                if (fragment != null) {
                    requireActivity()
                            .getSupportFragmentManager()
                            .beginTransaction()
                            .hide(fragment)
                            .commit();
                }

                requireActivity().findViewById(R.id.loadNetworkBtn)
                        .setVisibility(View.VISIBLE);

                return true;
            }

            if (id == R.id.menu_survey) {
                Intent intent = new Intent(requireContext(), ExistNetworkActivity.class);
                intent.putExtra("Type", "ExistNetwork");
                prefManager.setEditMode("Survey");
                startActivity(intent);
                return true;
            }
            return false;
        });

        binding.swipeRefresh.setColorSchemeResources(R.color.blue, R.color.fuse_color);
        binding.swipeRefresh.setOnRefreshListener(this::getMainData);

        if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(requireActivity())) {
            getMainData();
            getProjectList();
        } else {
            Snackbar.make(requireView(), getString(R.string.no_internet_connection), Snackbar.LENGTH_INDEFINITE)
                    .setAction("Retry", v -> {
                        getMainData();
                        getProjectList();
                    })
                    .show();
        }

        binding.spinnerDatabase.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(requireContext())) {
                    if (!selectedItem.equalsIgnoreCase("All DataBase")) {
                        getDashboardData(selectedItem);
                    }
                } else {
                    Snackbar.make(requireView(), getString(R.string.no_internet_connection), Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(requireContext())) {
                                if (!selectedItem.equalsIgnoreCase("All DataBase")) {
                                    getDashboardData(selectedItem);
                                }
                            }
                        }
                    }).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void getMainData() {
        binding.dashboardLayout.setVisibility(View.GONE);
        binding.shimmerView.setVisibility(View.VISIBLE);
        binding.shimmerView.startShimmer();
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("NetworkId", new JsonArray());
        jsonObject.addProperty("UserType", prefManager.getUserType());
        jsonObject.addProperty("Project", prefManager.getProjectName());
        jsonObject.addProperty("Group5", "");
        jsonObject.addProperty("Group4", "");
        jsonObject.addProperty("Group3", "");
        jsonObject.addProperty("Group2", "");
        jsonObject.addProperty("Group1", "");
        jsonObject.addProperty("DashBoardType", "Main");
        jsonObject.addProperty("CYMDBNET", "");
        jsonObject.addProperty("Mode", "Mobile");
        Retrofit retrofit = RetrofitClient.getClient(getActivity());
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<MainFeeder> call = apiInterface.getMainFeederData(jsonObject);
        call.enqueue(new Callback<MainFeeder>() {
            @Override
            public void onResponse(@NonNull Call<MainFeeder> call, @NonNull Response<MainFeeder> response) {
                if (response.code() == 200) {
                    binding.swipeRefresh.setRefreshing(false);
                    binding.dashboardLayout.setVisibility(View.VISIBLE);
                    binding.shimmerView.stopShimmer();
                    binding.shimmerView.setVisibility(View.GONE);
                    MainFeeder mainFeeder = response.body();
                    assert mainFeeder != null;
                    if (mainFeeder.getOutput().getDatabaseName() != null && !mainFeeder.getOutput().getDatabaseName().isEmpty()) {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.custom_spinner, mainFeeder.getOutput().getDatabaseName());
                        adapter.insert("All DataBase", 0);
                        binding.spinnerDatabase.setAdapter(adapter);
                    }
                    valueAnimator(binding.txtActualKva, mainFeeder.getOutput().getCustomeraLoad() != null ? mainFeeder.getOutput().getCustomeraLoad().getActualKVA() : 0.0);
                    valueAnimator(binding.txtConnectedKva, mainFeeder.getOutput().getCustomeraLoad() != null ? mainFeeder.getOutput().getCustomeraLoad().getConnectedKVA() : 0.0);
                    valueAnimator(binding.txtCustomer, mainFeeder.getOutput().getCustomerCountall() != null ? mainFeeder.getOutput().getCustomerCountall().getConsumerCount() : 0);
/*                    valueAnimator(binding.txtSubstation, mainFeeder.getOutput().getNetworkinfo() != null ? mainFeeder.getOutput().getNetworkinfo().getGroup1Count() : 0);
                    valueAnimator(binding.txtFeeder, mainFeeder.getOutput().getNetworkinfo() != null ? mainFeeder.getOutput().getNetworkinfo().getNetworkIdCount() : 0);*/

                    valueAnimator(binding.powerDtCount, mainFeeder.getOutput().getNetworkinfo() != null ? mainFeeder.getOutput().getNetworkinfo().getPowerTotal() : 0);
                    valueAnimator(binding.distributionTransformer, mainFeeder.getOutput().getNetworkinfo() != null ? mainFeeder.getOutput().getNetworkinfo().getDistributionTotal() : 0);
                    valueAnimator(binding.breakerCount, mainFeeder.getOutput().getNetworkinfo() != null ? mainFeeder.getOutput().getCbData2().getCBCount() : 0);
                    valueAnimator(binding.switchCount, mainFeeder.getOutput().getNetworkinfo() != null ? mainFeeder.getOutput().getSwitchData().getSwitchCount() : 0);
                    valueAnimator(binding.fuseCount, mainFeeder.getOutput().getNetworkinfo() != null ? mainFeeder.getOutput().getFuseData().getFuseCount() : 0);

                    double cableLength = mainFeeder.getOutput().getCableLen() != null ? mainFeeder.getOutput().getCableLen().getLength() : 0.0;
                    double overheadLength = mainFeeder.getOutput().getOverheadlen() != null ? mainFeeder.getOutput().getOverheadlen().getLength() : 0.0;
                    double totalLength = cableLength + overheadLength;

                    valueAnimator(binding.txtCableLength, cableLength);
                    valueAnimator(binding.txtOverHeadLength, overheadLength);
                    valueAnimator(binding.txtTotalLength, totalLength);

                    updatePoleChart(mainFeeder.getOutput().getNetworkinfo().getVoltageWise());

                } else if (response.code() == 401) {
                    binding.swipeRefresh.setRefreshing(false);
                    binding.dashboardLayout.setVisibility(View.VISIBLE);
                    binding.shimmerView.stopShimmer();
                    binding.shimmerView.setVisibility(View.GONE);
                    binding.swipeRefresh.setRefreshing(false);
                    prefManager.setIsUserLogin(false);
                    requireActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
                    requireActivity().finish();
                } else {
                    binding.swipeRefresh.setRefreshing(false);
                    binding.shimmerView.stopShimmer();
                    binding.shimmerView.setVisibility(View.GONE);
                    binding.dashboardLayout.setVisibility(View.VISIBLE);
                    binding.swipeRefresh.setRefreshing(false);
                    Snackbar.make(requireView(), "Server Error: " + response.code() + "Please try again!", Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            getMainData();
                        }
                    }).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MainFeeder> call, @NonNull Throwable t) {
                binding.swipeRefresh.setRefreshing(false);
                binding.dashboardLayout.setVisibility(View.VISIBLE);
                binding.shimmerView.stopShimmer();
                binding.shimmerView.setVisibility(View.GONE);
                binding.swipeRefresh.setRefreshing(false);
                Snackbar.make(requireView(), getString(R.string.error_msg), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void getDashboardData(String itemAtPosition) {
        binding.dashboardLayout.setVisibility(View.GONE);
        binding.shimmerView.setVisibility(View.VISIBLE);
        binding.shimmerView.startShimmer();
        JsonObject jsonObject = new JsonObject();
        jsonObject.add("NetworkId", new JsonArray());
        jsonObject.addProperty("UserType", prefManager.getUserType());
        jsonObject.addProperty("Project", prefManager.getProjectName());
        jsonObject.addProperty("Group5", "");
        jsonObject.addProperty("Group4", "");
        jsonObject.addProperty("Group3", "");
        jsonObject.addProperty("Group2", "");
        jsonObject.addProperty("Group1", "");
        jsonObject.addProperty("DashBoardType", "Database");
        jsonObject.addProperty("CYMDBNET", itemAtPosition);
        jsonObject.addProperty("Mode", "Mobile");
        Retrofit retrofit = RetrofitClient.getClient(getActivity());
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<FeederModel> call = apiInterface.getFeederData(jsonObject);
        call.enqueue(new Callback<FeederModel>() {
            @Override
            public void onResponse(@NonNull Call<FeederModel> call, @NonNull Response<FeederModel> response) {
                if (response.code() == 200) {
                    binding.swipeRefresh.setRefreshing(false);
                    binding.dashboardLayout.setVisibility(View.VISIBLE);
                    binding.shimmerView.stopShimmer();
                    binding.shimmerView.setVisibility(View.GONE);
                    FeederModel mainFeeder = response.body();
                    assert mainFeeder != null;
                    valueAnimator(binding.txtActualKva, mainFeeder.getOutput().getCustomeraLoad() != null ? mainFeeder.getOutput().getCustomeraLoad().getActualKVA() : 0.0);
                    valueAnimator(binding.txtConnectedKva, mainFeeder.getOutput().getCustomeraLoad() != null ? mainFeeder.getOutput().getCustomeraLoad().getConnectedKVA() : 0.0);
                    valueAnimator(binding.txtCustomer, mainFeeder.getOutput().getCustomerCountall() != null ? mainFeeder.getOutput().getCustomerCountall().getConsumerCount() : 0);
/*                    valueAnimator(binding.txtSubstation, mainFeeder.getOutput().getNetworkinfo() != null ? mainFeeder.getOutput().getNetworkinfo().getGroup1Count() : 0);
                    valueAnimator(binding.txtFeeder, mainFeeder.getOutput().getNetworkinfo() != null ? mainFeeder.getOutput().getNetworkinfo().getNetworkIdCount() : 0);*/

                    valueAnimator(binding.powerDtCount, mainFeeder.getOutput().getNetworkinfo() != null ? mainFeeder.getOutput().getNetworkinfo().getPowerTotal() : 0);
                    valueAnimator(binding.distributionTransformer, mainFeeder.getOutput().getNetworkinfo() != null ? mainFeeder.getOutput().getNetworkinfo().getDistributionTotal() : 0);
                    valueAnimator(binding.breakerCount, mainFeeder.getOutput().getNetworkinfo() != null ? mainFeeder.getOutput().getCbData2().getCBCount() : 0);
                    valueAnimator(binding.switchCount, mainFeeder.getOutput().getNetworkinfo() != null ? mainFeeder.getOutput().getSwitchData().getSwitchCount() : 0);

                    valueAnimator(binding.fuseCount, mainFeeder != null && mainFeeder.getOutput() != null && mainFeeder.getOutput().getFuseData() != null
                            && mainFeeder.getOutput().getFuseData().getFuseCount() != null
                            ? mainFeeder.getOutput().getFuseData().getFuseCount()
                            : 0
                    );

                    double cableLength = mainFeeder.getOutput().getCableLen() != null ? mainFeeder.getOutput().getCableLen().getLength() : 0.0;
                    double overheadLength = mainFeeder.getOutput().getOverheadlen() != null ? mainFeeder.getOutput().getOverheadlen().getLength() : 0.0;
                    double totalLength = cableLength + overheadLength;

                    valueAnimator(binding.txtCableLength, cableLength);
                    valueAnimator(binding.txtOverHeadLength, overheadLength);
                    valueAnimator(binding.txtTotalLength, totalLength);
                    updatePoleChart(mainFeeder.getOutput().getNetworkinfo().getVoltageWise());
                } else if (response.code() == 401) {
                    binding.swipeRefresh.setRefreshing(false);
                    prefManager.setIsUserLogin(false);
                    requireActivity().startActivity(new Intent(getActivity(), LoginActivity.class));
                    requireActivity().finish();

                } else {
                    binding.swipeRefresh.setRefreshing(false);
                    Snackbar.make(requireView(), "Server Error: " + response.code(), Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<FeederModel> call, @NonNull Throwable t) {
                binding.swipeRefresh.setRefreshing(false);
                binding.dashboardLayout.setVisibility(View.VISIBLE);
                binding.shimmerView.stopShimmer();
                binding.shimmerView.setVisibility(View.GONE);
                Snackbar.make(requireView(), getString(R.string.error_msg), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @SuppressLint("DefaultLocale")
    private void valueAnimator(final TextView textView, double targetValue) {
        ValueAnimator animator = ValueAnimator.ofFloat(0f, (float) targetValue);
        animator.setDuration(1000);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(animation -> {
            float animatedValue = (float) animation.getAnimatedValue();
            if (textView.getId() == R.id.txtCableLength || textView.getId() == R.id.txtOverHeadLength
                    || textView.getId() == R.id.txtTotalLength) {
                textView.setText(String.format("%.2f km", animatedValue));
            } else {
                textView.setText(String.valueOf(Math.round(animatedValue)));
            }
        });
        animator.start();
    }
    private void updatePoleChart(List<MainFeeder.VoltageWise> voltageWise) {

        binding.donutContainer.removeAllViews();
        if (voltageWise == null || voltageWise.isEmpty()) return;
        MainFeeder.VoltageWise v132_33 = null;
        MainFeeder.VoltageWise v33 = null;
        MainFeeder.VoltageWise v33_11 = null;
        MainFeeder.VoltageWise v11 = null;

        for (MainFeeder.VoltageWise item : voltageWise) {

            if (item == null || item.getGroup2() == null) continue;

            String group = item.getGroup2().trim();

            if (group.contains("132")) {
                v132_33 = item;
            } else if (group.equals("33")) {
                v33 = item;
            } else if (group.contains("33/11")) {
                v33_11 = item;
            } else if (group.contains("11")) {
                v11 = item;
            }
        }

        MainFeeder.VoltageWise[] ordered = {v132_33, v33, v33_11, v11};

        for (MainFeeder.VoltageWise item : ordered) {

            if (item == null || item.getGroup2() == null) continue;

            String group = item.getGroup2().trim();

            int feeder = item.getNetworkType0Count() != null
                    ? item.getNetworkType0Count() : 0;

            int pss = item.getNetworkType1Count() != null
                    ? item.getNetworkType1Count() : 0;

            int feederColor = 0;
            int pssColor = 0;

            if (group.contains("11")) {
                feederColor = R.color.feeder_default;
                pssColor = R.color.pss;
            }

            if (group.contains("33")) {
                feederColor = R.color.feeder_default;
                pssColor = R.color.pss;
            }

            if (group.contains("132")) {
                feederColor = R.color.feeder_default;
                pssColor = R.color.pss;
            }

            addDonut(group + " kV", feeder, pss,
                    feederColor == 0 ? R.color.feeder_11 : feederColor,
                    pssColor == 0 ? R.color.pss_default : pssColor);
        }

    }

    @SuppressLint("SetTextI18n")
    private void addDonut(String title, int feeder, int pss, int feederColor, int pssColor) {

        View view = LayoutInflater.from(getContext())
                .inflate(R.layout.voltage_donut_item, binding.donutContainer, false);

        TextView txtVoltage = view.findViewById(R.id.txtVoltage);
        VoltageDonutView donut = view.findViewById(R.id.donut);
        LinearLayout legendContainer = view.findViewById(R.id.legendContainer);

        donut.setData(feeder, pss, feederColor, pssColor);

        int bgColorRes;
        if (title.startsWith("132/33")) {
            bgColorRes = R.color.pss_132;

        } else if (title.startsWith("33/11")) {
            bgColorRes = R.color.pss_33;

        } else if (title.startsWith("33")) {
            bgColorRes = R.color.feeder_33;

        } else {
            bgColorRes = R.color.feeder_default;
        }
        int resolvedBgColor = getResources().getColor(bgColorRes);

        txtVoltage.setText(title);
        txtVoltage.setTypeface(null, android.graphics.Typeface.BOLD);
        txtVoltage.setTextColor(Color.BLACK);
        txtVoltage.setPadding(18, 8, 18, 8);

        txtVoltage.setBackgroundResource(R.drawable.bg_voltage_label);

        android.graphics.drawable.GradientDrawable drawable =
                    (android.graphics.drawable.GradientDrawable) txtVoltage.getBackground().mutate();

        drawable.setColor(Color.argb(40,
                        Color.red(resolvedBgColor),
                        Color.green(resolvedBgColor),
                        Color.blue(resolvedBgColor)
                ));

        drawable.setStroke((int) (getResources().getDisplayMetrics().density), Color.argb(180,
                        Color.red(resolvedBgColor),
                        Color.green(resolvedBgColor),
                        Color.blue(resolvedBgColor)
                ));

        legendContainer.removeAllViews();

        if (feeder > 0) {
            TextView tv = new TextView(getContext());
            tv.setText("● Feeder");
            tv.setTextSize(12f);
            tv.setTypeface(null, android.graphics.Typeface.BOLD);
            tv.setTextColor(getResources().getColor(feederColor));
            tv.setPadding(0, 4, 16, 4);
            legendContainer.addView(tv);
        }

        if (pss > 0) {
            TextView tv = new TextView(getContext());
            tv.setText("● PSS");
            tv.setTextSize(12f);
            tv.setTypeface(null, android.graphics.Typeface.BOLD);
            tv.setTextColor(getResources().getColor(pssColor));
            tv.setPadding(0, 4, 16, 4);
            legendContainer.addView(tv);
        }

        binding.donutContainer.addView(view);
    }

    private void getProjectList() {

        Retrofit retrofit = RetrofitClient.getClient(requireActivity());
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        Call<ProjectModel> call = apiInterface.getProject("Bearer " + prefManager.getAccessToken(), prefManager.getUserName());

        call.enqueue(new Callback<ProjectModel>() {
            @Override
            public void onResponse(@NonNull Call<ProjectModel> call, @NonNull Response<ProjectModel> response) {

                if (!isAdded()) return;
                if (response.code() == 200 && response.body() != null) {

                    List<ProjectModel.ProjectName> projectList = response.body().getProjectName();
                    for (ProjectModel.ProjectName project : projectList) {
                        if ("Survey".equalsIgnoreCase(project.getServerType())) {
                            prefManager.setDatabaseSurvey(project.getDatabase());
                            break;
                        }
                    }

                } else if (response.code() == 401) {
                    prefManager.setIsUserLogin(false);
                    if (isAdded()) {
                        Intent intent = new Intent(requireActivity(), LoginActivity.class);
                        startActivity(intent);
                        requireActivity().finish();
                    }

                } else {
                    Snackbar.make(binding.getRoot(), response.message() + " - " + response.code(), Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProjectModel> call, @NonNull Throwable t) {
                if (!isAdded()) return;
                Snackbar.make(binding.getRoot(), getString(R.string.error_msg), Snackbar.LENGTH_LONG).show();
            }
        });
    }

}