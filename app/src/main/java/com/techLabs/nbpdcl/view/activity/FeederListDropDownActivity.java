package com.techLabs.nbpdcl.view.activity;

import static com.techLabs.nbpdcl.Utils.Config.isTreeNode;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.techLabs.nbpdcl.R;
import com.techLabs.nbpdcl.Utils.PrefManager;
import com.techLabs.nbpdcl.Utils.ResponseDataUtils;
import com.techLabs.nbpdcl.adapters.CustomAdapter;
import com.techLabs.nbpdcl.adapters.SelectedNetworkAdapter;
import com.techLabs.nbpdcl.databinding.ActivityFeederListDropDownActvityBinding;
import com.techLabs.nbpdcl.models.FeederModel;
import com.techLabs.nbpdcl.models.MainFeeder;
import com.techLabs.nbpdcl.retrofit.ApiInterface;
import com.techLabs.nbpdcl.retrofit.RetrofitClient;
import com.techLabs.nbpdcl.view.fragment.DashboardFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FeederListDropDownActivity extends AppCompatActivity {

    private final List<String> spinDb = new ArrayList<>();
    private final List<String> region = new ArrayList<>();
    private final List<String> zone = new ArrayList<>();
    private final List<String> circle = new ArrayList<>();
    private final List<String> division = new ArrayList<>();
    private final List<String> sub = new ArrayList<>();
    private ActivityFeederListDropDownActvityBinding binding;
    private PrefManager prefManager;
    private boolean doubleBackToExitPressedOnce = false;
    private String DbName = null;
    private ArrayAdapter<String> adapter;
    private SelectedNetworkAdapter selectedNetworkAdapter;

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFeederListDropDownActvityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        prefManager = new PrefManager(this);
        binding.toolbar.setTitle("Network List");
        setSupportActionBar(binding.toolbar);
        binding.shimmerViewContainer.startShimmer();
        binding.loadNetworkBtn.setVisibility(View.GONE);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main, new DashboardFragment())
                    .commit();
        }

        if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(this)) {
            binding.itemLayout.setVisibility(View.GONE);
            binding.shimmerViewContainer.setVisibility(View.VISIBLE);
            binding.shimmerViewContainer.startShimmer();
            getDbName();
        } else {
            final Dialog dialog = new Dialog(FeederListDropDownActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.no_internet_dialog);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(FeederListDropDownActivity.this.getDrawable(R.drawable.pop_background));
            LottieAnimationView lottieAnimationView = dialog.findViewById(R.id.animation_view);
            Button RetryBtn = dialog.findViewById(R.id.btnDialog);
            lottieAnimationView.playAnimation();
            RetryBtn.setOnClickListener(view -> {
                if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(FeederListDropDownActivity.this)) {
                    binding.itemLayout.setVisibility(View.GONE);
                    binding.shimmerViewContainer.setVisibility(View.VISIBLE);
                    binding.shimmerViewContainer.startShimmer();
                    getDbName();
                    dialog.dismiss();
                }
            });
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();
        }

        binding.swipeRefreshLayout.setColorSchemeResources(R.color.blue, R.color.fuse_color);
        binding.swipeRefreshLayout.setOnRefreshListener(() -> {
            getRefresh();
            getReset();
            if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(FeederListDropDownActivity.this)) {
                getDbName();
            } else {
                binding.swipeRefreshLayout.setRefreshing(false);
                Snackbar.make(binding.getRoot(), "No Internet Connection", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Retry", v -> getDbName()).show();
            }
        });

        adapter = new ArrayAdapter<>(this, R.layout.custom_spinner, spinDb);
        binding.databaseSpin.setAdapter(adapter);
        binding.databaseSpin.setDropDownBackgroundResource(android.R.color.white);

        selectedNetworkAdapter = new SelectedNetworkAdapter(this, new ArrayList<>());
        binding.rvLoaded.setLayoutManager(new LinearLayoutManager(this));
        binding.rvLoaded.setAdapter(selectedNetworkAdapter);

        selectedNetworkAdapter.setOnItemRemovedListener(this::isClear);

        binding.databaseSpin.setOnItemClickListener((parent, view, position, id) -> {
            DbName = spinDb.get(position);
            prefManager.setDBName(DbName);
            binding.databaseSpin.setText(DbName, false);
            if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(FeederListDropDownActivity.this)) {
                getFeederListData(DbName);
            } else {
                final Dialog dialog = new Dialog(FeederListDropDownActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.no_internet_dialog);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(FeederListDropDownActivity.this.getDrawable(R.drawable.pop_background));
                LottieAnimationView lottieAnimationView = dialog.findViewById(R.id.animation_view);
                Button RetryBtn = dialog.findViewById(R.id.btnDialog);
                lottieAnimationView.playAnimation();
                RetryBtn.setOnClickListener(view1 -> {
                    if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(FeederListDropDownActivity.this)) {
                        getFeederListData(DbName);
                        dialog.dismiss();
                    }
                });
                dialog.setCanceledOnTouchOutside(false);
                dialog.setCancelable(false);
                dialog.show();
            }

            binding.regionSpin.setText("", false);
            binding.zoneSpin.setText("", false);
            binding.circleSpin.setText("", false);
            binding.divisionSpin.setText("", false);
            binding.subStnSpin.setText("", false);
            binding.feederIdSpin.setText("", false);
        });

        binding.databaseSpin.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && binding.databaseSpin.getAdapter() != null && binding.databaseSpin.getAdapter().getCount() > 0) {
                binding.databaseSpin.showDropDown();
            }
        });

        binding.databaseSpin.setOnClickListener(v -> {
            if (binding.databaseSpin.getAdapter() != null && binding.databaseSpin.getAdapter().getCount() > 0) {
                binding.databaseSpin.showDropDown();
            }
        });

        binding.loadNetworkBtn.setOnClickListener(v -> {
            if (!ResponseDataUtils.NetworkList.isEmpty()) {
                if (prefManager.getType().contains("Edit")) {
                    prefManager.setUserType("Analysis");
                } else {
                    prefManager.setUserType(prefManager.getType());
                }
                Intent intent = new Intent(FeederListDropDownActivity.this, MapActivity.class);
                intent.putExtra("NetworkId", ResponseDataUtils.NetworkList);
                intent.putExtra("Type", "Normal");
                prefManager.setDBName(DbName);
                prefManager.setEditMode("Normal");
                startActivity(intent);
            } else {
                Snackbar snack = Snackbar.make(findViewById(android.R.id.content), "Please select any feeder!", Snackbar.LENGTH_LONG);
                snack.show();
            }
        });

        getOnBackPressedDispatcher().addCallback(this,
                new OnBackPressedCallback(true) {

                    @Override
                    public void handleOnBackPressed() {

                        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.main);

                        if (fragment != null && !fragment.isVisible()) {
                            getSupportFragmentManager()
                                    .beginTransaction()
                                    .show(fragment)
                                    .commit();

                            binding.loadNetworkBtn.setVisibility(View.GONE);
                            return;
                        }


                        isTreeNode = false;

                        if (doubleBackToExitPressedOnce) {
                            finish();
                            return;
                        }

                        doubleBackToExitPressedOnce = true;

                        showExitSnackbar();

                        new Handler(Looper.getMainLooper())
                                .postDelayed(() ->
                                                doubleBackToExitPressedOnce = false,
                                        2000
                                );
                    }
                });

    }

    private void getDbName() {
        JsonObject jsonObject = new JsonObject();
        JsonArray networkID = new JsonArray();
        JsonArray array5 = new JsonArray();
        JsonArray array4 = new JsonArray();
        JsonArray array3 = new JsonArray();
        JsonArray array2 = new JsonArray();
        JsonArray array1 = new JsonArray();
        jsonObject.add("NetworkId", networkID);
        jsonObject.add("Group5", array5);
        jsonObject.add("Group4", array4);
        jsonObject.add("Group3", array3);
        jsonObject.add("Group2", array2);
        jsonObject.add("Group1", array1);
        jsonObject.addProperty("UserType", prefManager.getUserType());
        jsonObject.addProperty("CYMDBNET", "");
        jsonObject.addProperty("Project", prefManager.getProjectName());
        jsonObject.addProperty("DashBoardType", "Main");
        jsonObject.addProperty("Mode", "Mobile");
        Retrofit retrofit = RetrofitClient.getClient(this);
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<MainFeeder> call = apiInterface.getMainFeederData(jsonObject);
        call.enqueue(new Callback<MainFeeder>() {
            @Override
            public void onResponse(@NonNull Call<MainFeeder> call, @NonNull Response<MainFeeder> response) {
                if (response.code() == 200) {
                    binding.swipeRefreshLayout.setRefreshing(false);
                    binding.itemLayout.setVisibility(View.VISIBLE);
                    binding.shimmerViewContainer.setVisibility(View.GONE);
                    binding.shimmerViewContainer.stopShimmer();
                    assert response.body() != null;
                    List<String> databaseNames = response.body().getOutput().getDatabaseName();
                    spinDb.clear();
                    spinDb.addAll(databaseNames);
                    adapter.notifyDataSetChanged();
                } else if (response.code() == 401) {
                    binding.swipeRefreshLayout.setRefreshing(false);
                    binding.itemLayout.setVisibility(View.VISIBLE);
                    binding.shimmerViewContainer.setVisibility(View.GONE);
                    binding.shimmerViewContainer.stopShimmer();
                    prefManager.setIsUserLogin(false);
                    startActivity(new Intent(FeederListDropDownActivity.this, LoginActivity.class));
                    finish();
                } else {
                    binding.swipeRefreshLayout.setRefreshing(false);
                    binding.itemLayout.setVisibility(View.VISIBLE);
                    binding.shimmerViewContainer.setVisibility(View.GONE);
                    binding.shimmerViewContainer.stopShimmer();
                    Snackbar snack = Snackbar.make(binding.getRoot(), response.message() + " - " + response.code(), Snackbar.LENGTH_INDEFINITE);
                    snack.show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MainFeeder> call, @NonNull Throwable t) {
                binding.swipeRefreshLayout.setRefreshing(false);
                binding.itemLayout.setVisibility(View.VISIBLE);
                binding.shimmerViewContainer.setVisibility(View.GONE);
                binding.shimmerViewContainer.stopShimmer();
                Snackbar snack = Snackbar.make(findViewById(android.R.id.content), getString(R.string.error_msg), Snackbar.LENGTH_LONG);
                snack.show();
            }
        });
    }

    private void getFeederListData(String Item) {
        DbName = Item;
        JsonObject jsonObject = new JsonObject();
        JsonArray networkID = new JsonArray();
        JsonArray array5 = new JsonArray();
        JsonArray array4 = new JsonArray();
        JsonArray array3 = new JsonArray();
        JsonArray array2 = new JsonArray();
        JsonArray array1 = new JsonArray();
        jsonObject.add("NetworkId", networkID);
        jsonObject.add("Group5", array5);
        jsonObject.add("Group4", array4);
        jsonObject.add("Group3", array3);
        jsonObject.add("Group2", array2);
        jsonObject.add("Group1", array1);
        jsonObject.addProperty("UserType", prefManager.getUserType());
        jsonObject.addProperty("CYMDBNET", Item);
        jsonObject.addProperty("Project", prefManager.getProjectName());
        jsonObject.addProperty("DashBoardType", "Database");
        jsonObject.addProperty("Mode", "Mobile");
        Retrofit retrofit = RetrofitClient.getClient(this);
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<FeederModel> call = apiInterface.getFeederData(jsonObject);
        call.enqueue(new Callback<FeederModel>() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onResponse(@NonNull Call<FeederModel> call, @NonNull Response<FeederModel> response) {
                if (response.code() == 200) {
                    binding.itemLayout.setVisibility(View.VISIBLE);
                    binding.shimmerViewContainer.stopShimmer();
                    binding.shimmerViewContainer.setVisibility(View.GONE);
                    assert response.body() != null;
                    if (response.body().getOutput().getGroup5().getGroup5() != null && !response.body().getOutput().getGroup5().getGroup5().isEmpty()) {
                        region.clear();
                        region.addAll(response.body().getOutput().getGroup5().getGroup5());
                        setSpinner(binding.regionSpin, region, false);
                    } else {
                        binding.regionSpin.setFocusable(false);
                    }
                    binding.textReg.setVisibility(region.isEmpty() ? View.GONE : View.VISIBLE);
                    binding.regionSpin.setVisibility(region.isEmpty() ? View.GONE : View.VISIBLE);

                    if (response.body().getOutput().getGroup4().getGroup4() != null && !response.body().getOutput().getGroup4().getGroup4().isEmpty()) {
                        zone.clear();
                        zone.addAll(response.body().getOutput().getGroup4().getGroup4());
                        setSpinner(binding.zoneSpin, zone, false);
                    } else {
                        binding.zoneSpin.setFocusable(false);
                    }
                    binding.textZone.setVisibility(zone.isEmpty() ? View.GONE : View.VISIBLE);
                    binding.zoneSpin.setVisibility(zone.isEmpty() ? View.GONE : View.VISIBLE);

                    if (response.body().getOutput().getGroup3().getGroup3() != null && !response.body().getOutput().getGroup3().getGroup3().isEmpty()) {
                        circle.clear();
                        circle.addAll(response.body().getOutput().getGroup3().getGroup3());
                        setSpinner(binding.circleSpin, circle, false);
                    } else {
                        binding.circleSpin.setFocusable(false);
                    }
                    binding.textCircle.setVisibility(circle.isEmpty() ? View.GONE : View.VISIBLE);
                    binding.circleSpin.setVisibility(circle.isEmpty() ? View.GONE : View.VISIBLE);

                    if (response.body().getOutput().getGroup2().getGroup2() != null && !response.body().getOutput().getGroup2().getGroup2().isEmpty()) {
                        division.clear();
                        division.addAll(response.body().getOutput().getGroup2().getGroup2());
                        setSpinner(binding.divisionSpin, division, false);
                    } else {
                        binding.divisionSpin.setFocusable(false);
                    }
                    binding.textDev.setVisibility(division.isEmpty() ? View.GONE : View.VISIBLE);
                    binding.divisionSpin.setVisibility(division.isEmpty() ? View.GONE : View.VISIBLE);

                    if (response.body().getOutput().getGroup1().getGroup1() != null && !response.body().getOutput().getGroup1().getGroup1().isEmpty()) {
                        sub.clear();
                        sub.addAll(response.body().getOutput().getGroup1().getGroup1());
                        setSpinner(binding.subStnSpin, sub, false);
                    } else {
                        binding.subStnSpin.setFocusable(false);
                    }
                    binding.textStn.setVisibility(sub.isEmpty() ? View.GONE : View.VISIBLE);
                    binding.subStnSpin.setVisibility(sub.isEmpty() ? View.GONE : View.VISIBLE);

                    if (response.body().getOutput().getNetworkName().getNetworkId() != null && !response.body().getOutput().getNetworkName().getNetworkId().isEmpty()) {
                        setSpinner(binding.feederIdSpin, response.body().getOutput().getNetworkName().getNetworkId(), true);
                    } else {
                        binding.feederIdSpin.setFocusable(false);
                    }

                } else if (response.code() == 401) {
                    binding.itemLayout.setVisibility(View.VISIBLE);
                    binding.shimmerViewContainer.setVisibility(View.GONE);
                    binding.shimmerViewContainer.stopShimmer();
                    prefManager.setIsUserLogin(false);
                    startActivity(new Intent(FeederListDropDownActivity.this, LoginActivity.class));
                    finish();
                } else {
                    binding.itemLayout.setVisibility(View.VISIBLE);
                    binding.shimmerViewContainer.setVisibility(View.GONE);
                    binding.shimmerViewContainer.stopShimmer();
                    Snackbar snack = Snackbar.make(findViewById(android.R.id.content), response.code(), Snackbar.LENGTH_INDEFINITE);
                    snack.show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<FeederModel> call, @NonNull Throwable t) {
                binding.itemLayout.setVisibility(View.VISIBLE);
                binding.shimmerViewContainer.setVisibility(View.GONE);
                binding.shimmerViewContainer.stopShimmer();
                Snackbar snack = Snackbar.make(findViewById(android.R.id.content), getString(R.string.error_msg), Snackbar.LENGTH_LONG);
                snack.show();
            }
        });
    }

    private void getGroup(String groupType, String groupValue) {
        binding.itemLayout.setVisibility(View.GONE);
        binding.shimmerViewContainer.setVisibility(View.VISIBLE);
        binding.shimmerViewContainer.startShimmer();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("CYMDBNET", prefManager.getDBName());
        jsonObject.addProperty("DashBoardType", "Group");
        jsonObject.addProperty("Group1", groupType.equals("Group1") ? groupValue : "");
        jsonObject.addProperty("Group2", groupType.equals("Group2") ? groupValue : "");
        jsonObject.addProperty("Group3", groupType.equals("Group3") ? groupValue : "");
        jsonObject.addProperty("Group4", groupType.equals("Group4") ? groupValue : "");
        jsonObject.addProperty("Group5", groupType.equals("Group5") ? groupValue : "");
        jsonObject.add("NetworkId", new JsonArray());
        jsonObject.addProperty("Project", prefManager.getProjectName());
        jsonObject.addProperty("Mode", "Mobile");
        jsonObject.addProperty("UserType", prefManager.getUserType());
        Retrofit retrofit = RetrofitClient.getClient(this);
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<FeederModel> call = apiInterface.getFeederData(jsonObject);
        call.enqueue(new Callback<FeederModel>() {
            @RequiresApi(api = Build.VERSION_CODES.Q)
            @Override
            public void onResponse(@NonNull Call<FeederModel> call, @NonNull Response<FeederModel> response) {
                if (response.code() == 200) {
                    binding.itemLayout.setVisibility(View.VISIBLE);
                    binding.shimmerViewContainer.stopShimmer();
                    binding.shimmerViewContainer.setVisibility(View.GONE);
                    assert response.body() != null;
                    if (response.body().getOutput().getGroup5().getGroup5() != null && !response.body().getOutput().getGroup5().getGroup5().isEmpty()) {
                        setSpinner(binding.regionSpin, region, false);
                    } else {
                        binding.regionSpin.setFocusable(false);
                    }
                    if (groupType.equals("Group5")) {
                        setSpinner(binding.zoneSpin, response.body().getOutput().getGroup4().getGroup4(), false);
                        setSpinner(binding.circleSpin, response.body().getOutput().getGroup3().getGroup3(), false);
                        setSpinner(binding.divisionSpin, response.body().getOutput().getGroup2().getGroup2(), false);
                        setSpinner(binding.subStnSpin, response.body().getOutput().getGroup1().getGroup1(), false);
                        setSpinner(binding.feederIdSpin, response.body().getOutput().getNetworkName().getNetworkId(), true);
                    }
                    if (groupType.equals("Group4")) {
                        setSpinner(binding.circleSpin, response.body().getOutput().getGroup3().getGroup3(), false);
                        setSpinner(binding.divisionSpin, response.body().getOutput().getGroup2().getGroup2(), false);
                        setSpinner(binding.subStnSpin, response.body().getOutput().getGroup1().getGroup1(), false);
                        setSpinner(binding.feederIdSpin, response.body().getOutput().getNetworkName().getNetworkId(), true);
                    }
                    if (groupType.equals("Group3")) {
                        setSpinner(binding.divisionSpin, response.body().getOutput().getGroup2().getGroup2(), false);
                        setSpinner(binding.subStnSpin, response.body().getOutput().getGroup1().getGroup1(), false);
                        setSpinner(binding.feederIdSpin, response.body().getOutput().getNetworkName().getNetworkId(), true);
                    }
                    if (groupType.equals("Group2")) {
                        setSpinner(binding.subStnSpin, response.body().getOutput().getGroup1().getGroup1(), false);
                        setSpinner(binding.feederIdSpin, response.body().getOutput().getNetworkName().getNetworkId(), true);
                    }
                    if (groupType.equals("Group1")) {
                        setSpinner(binding.feederIdSpin, response.body().getOutput().getNetworkName().getNetworkId(), true);
                    }
                } else if (response.code() == 401) {
                    binding.itemLayout.setVisibility(View.VISIBLE);
                    binding.shimmerViewContainer.stopShimmer();
                    binding.shimmerViewContainer.setVisibility(View.GONE);
                    prefManager.setIsUserLogin(false);
                    startActivity(new Intent(FeederListDropDownActivity.this, LoginActivity.class));
                    finish();
                } else {
                    binding.itemLayout.setVisibility(View.VISIBLE);
                    binding.shimmerViewContainer.stopShimmer();
                    binding.shimmerViewContainer.setVisibility(View.GONE);
                    Snackbar snack = Snackbar.make(binding.getRoot(), response.message() + " - " + response.code(), Snackbar.LENGTH_INDEFINITE);
                    snack.show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<FeederModel> call, @NonNull Throwable t) {
                binding.itemLayout.setVisibility(View.VISIBLE);
                binding.shimmerViewContainer.stopShimmer();
                binding.shimmerViewContainer.setVisibility(View.GONE);
                Snackbar.make(binding.getRoot(), getString(R.string.error_msg), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void isClear(String removedItem) {
        ResponseDataUtils.NetworkList.remove(removedItem);
        List<String> currentItems = selectedNetworkAdapter.getSelectedItems();
        CustomAdapter adapter = (CustomAdapter) binding.feederIdSpin.getAdapter();
        adapter.clearSelection();

        SpannableStringBuilder builder = new SpannableStringBuilder();
        Drawable cancelIcon = ContextCompat.getDrawable(binding.feederIdSpin.getContext(), R.drawable.close);
        if (cancelIcon != null) {
            int iconSize = (int) (16 * binding.feederIdSpin.getContext().getResources().getDisplayMetrics().density);
            cancelIcon.setBounds(0, 0, iconSize, iconSize);

            for (int i = 0; i < currentItems.size(); i++) {
                String text = currentItems.get(i) + " ";
                builder.append(text);
                int start = builder.length();
                builder.append(" ");
                int end = builder.length();
                builder.setSpan(new ImageSpan(cancelIcon, ImageSpan.ALIGN_CENTER), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                String currentItem = currentItems.get(i);
                builder.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        selectedNetworkAdapter.removeFeeder(currentItem);
                        ResponseDataUtils.NetworkList.remove(currentItem);
                        isClear(currentItem);
                    }

                    @Override
                    public void updateDrawState(@NonNull TextPaint ds) {
                        ds.setUnderlineText(false);
                    }
                }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                if (i < currentItems.size() - 1) {
                    builder.append(", ");
                }
            }
        }

        binding.feederIdSpin.setText(builder, false);
        binding.feederIdSpin.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.nav_toll_menu, menu);
        menu.getItem(0).setVisible(false);
        menu.getItem(1).setVisible(false);
        menu.getItem(2).setVisible(false);
        menu.getItem(3).setVisible(true);
        menu.getItem(4).setVisible(true);
        menu.getItem(5).setVisible(false);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_logout:
                prefManager.setIsUserLogin(false);
                startActivity(new Intent(FeederListDropDownActivity.this, LoginActivity.class));
                finish();
                break;

            case R.id.nav_nsc:
                if (prefManager.getType().contains("Admin")) {
                    prefManager.setUserType("Admin");
                    Intent intent = new Intent(FeederListDropDownActivity.this, NewConnection.class);
                    prefManager.setEditMode("Normal");
                    startActivity(intent);
                    finish();
                }
                break;

            case R.id.nav_survey:
                Intent intent = new Intent(FeederListDropDownActivity.this, ExistNetworkActivity.class);
                intent.putExtra("Type", "ExistNetwork");
                prefManager.setEditMode("Survey");
                startActivity(intent);
                break;

            case R.id.nav_profile:
                Intent intent1 = new Intent(FeederListDropDownActivity.this, Profile.class);
                startActivity(intent1);
                break;


        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @SuppressLint("UseCompatLoadingForDrawables")
    private void setSpinner(AutoCompleteTextView item, List<String> data, boolean isClicked) {
        if (isClicked) {
            CustomAdapter adapter = new CustomAdapter(this, data);
            item.setAdapter(adapter);
            List<String> selectedItems = new ArrayList<>(ResponseDataUtils.NetworkList);
            item.setText("", false);

            adapter.setOnItemSelectedListener((selectedItem, isSelected) -> {
                if (isSelected) {
                    if (!selectedItems.contains(selectedItem)) {
                        selectedItems.add(selectedItem);
                        selectedNetworkAdapter.addFeeder(selectedItem);
                        ResponseDataUtils.NetworkList.add(selectedItem);
                    }
                } else {
                    selectedItems.remove(selectedItem);
                    selectedNetworkAdapter.removeFeeder(selectedItem);
                    ResponseDataUtils.NetworkList.remove(selectedItem);
                }
                iconCancel(item, selectedItems, adapter);
                hideKeyboard();
            });


            item.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (s.toString().isEmpty()) {
                        selectedItems.clear();
                        ResponseDataUtils.NetworkList.clear();
                    }
                }
            });

        } else {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.custom_spinner, data);
            item.setAdapter(adapter);
            item.setDropDownBackgroundResource(android.R.color.white);

            item.setOnItemClickListener((parent, view, position, id) -> {
                String group = adapter.getItem(position);
                item.setText(group, false);
                if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(FeederListDropDownActivity.this)) {
                    if (item == binding.regionSpin) {
                        getGroup("Group5", group);
                    } else if (item == binding.zoneSpin) {
                        getGroup("Group4", group);
                    } else if (item == binding.circleSpin) {
                        getGroup("Group3", group);
                    } else if (item == binding.divisionSpin) {
                        getGroup("Group2", group);
                    } else if (item == binding.subStnSpin) {
                        getGroup("Group1", group);
                    }
                } else {
                    final Dialog dialog = new Dialog(FeederListDropDownActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.no_internet_dialog);
                    Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(FeederListDropDownActivity.this.getDrawable(R.drawable.pop_background));
                    LottieAnimationView lottieAnimationView = dialog.findViewById(R.id.animation_view);
                    Button RetryBtn = dialog.findViewById(R.id.btnDialog);
                    lottieAnimationView.playAnimation();
                    RetryBtn.setOnClickListener(view1 -> {
                        if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(FeederListDropDownActivity.this)) {
                            if (item == binding.regionSpin) {
                                getGroup("Group5", group);
                            } else if (item == binding.zoneSpin) {
                                getGroup("Group4", group);
                            } else if (item == binding.circleSpin) {
                                getGroup("Group3", group);
                            } else if (item == binding.divisionSpin) {
                                getGroup("Group2", group);
                            } else if (item == binding.subStnSpin) {
                                getGroup("Group1", group);
                            }
                            dialog.dismiss();
                        }
                    });
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setCancelable(false);
                    dialog.show();
                }

            });
        }

        item.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                item.setBackgroundResource(R.drawable.auto_bg);
                item.post(() -> {
                    if (item.getAdapter() != null && item.getAdapter().getCount() > 0) {
                        item.showDropDown();
                    }
                });
            } else {
                item.setBackgroundResource(R.drawable.tag_spinner_bg);
            }
        });
        item.setOnClickListener(v -> {
            if (item.getAdapter() != null && item.getAdapter().getCount() > 0) {
                item.showDropDown();
            }
        });
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        View view = this.getCurrentFocus();
        if (view != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        ResponseDataUtils.NetworkList.clear();
        selectedNetworkAdapter.clear();
        if (binding.feederIdSpin.getAdapter() instanceof CustomAdapter) {
            CustomAdapter adapter = (CustomAdapter) binding.feederIdSpin.getAdapter();
            adapter.clearSelection();
            binding.feederIdSpin.setText("", false);
            binding.feederIdSpin.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    private void iconCancel(AutoCompleteTextView item, List<String> selectedItems, CustomAdapter adapter) {
        SpannableStringBuilder builder = new SpannableStringBuilder();
        Drawable cancelIcon = ContextCompat.getDrawable(item.getContext(), R.drawable.close);
        if (cancelIcon != null) {
            int iconSize = (int) (16 * item.getContext().getResources().getDisplayMetrics().density);
            cancelIcon.setBounds(0, 0, iconSize, iconSize);

            for (int i = 0; i < selectedItems.size(); i++) {
                String text = selectedItems.get(i) + " ";
                builder.append(text);
                ImageSpan imageSpan = new ImageSpan(cancelIcon, ImageSpan.ALIGN_CENTER);
                int start = builder.length();
                builder.append(" ");
                int end = builder.length();
                builder.setSpan(imageSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                String currentItem = selectedItems.get(i);
                builder.setSpan(new ClickableSpan() {
                    @Override
                    public void onClick(@NonNull View widget) {
                        if (selectedItems.contains(currentItem)) {
                            selectedItems.remove(currentItem);
                            selectedNetworkAdapter.removeFeeder(currentItem);
                            ResponseDataUtils.NetworkList.remove(currentItem);
                            iconCancel(item, selectedItems, adapter);
                            adapter.clearSelection();
                        }
                    }

                    @Override
                    public void updateDrawState(@NonNull TextPaint ds) {
                        super.updateDrawState(ds);
                        ds.setUnderlineText(false);
                    }
                }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                if (i < selectedItems.size() - 1) {
                    builder.append(", ");
                }
            }
        }

        item.setText(builder, false);
        item.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void getRefresh() {
        spinDb.clear();
        region.clear();
        zone.clear();
        circle.clear();
        division.clear();
        sub.clear();
        ResponseDataUtils.NetworkList.clear();

        binding.databaseSpin.setText("", false);
        binding.regionSpin.setText("", false);
        binding.zoneSpin.setText("", false);
        binding.circleSpin.setText("", false);
        binding.divisionSpin.setText("", false);
        binding.subStnSpin.setText("", false);
        binding.feederIdSpin.setText("", false);

        selectedNetworkAdapter.clear();

        DbName = null;
        prefManager.setDBName(null);

        adapter.notifyDataSetChanged();
        if (binding.feederIdSpin.getAdapter() instanceof CustomAdapter) {
            ((CustomAdapter) binding.feederIdSpin.getAdapter()).clearSelection();
        }
    }

    private void getReset() {
        binding.textReg.setVisibility(View.VISIBLE);
        binding.regionSpin.setVisibility(View.VISIBLE);
        binding.textZone.setVisibility(View.VISIBLE);
        binding.zoneSpin.setVisibility(View.VISIBLE);
        binding.textCircle.setVisibility(View.VISIBLE);
        binding.circleSpin.setVisibility(View.VISIBLE);
        binding.textDev.setVisibility(View.VISIBLE);
        binding.divisionSpin.setVisibility(View.VISIBLE);
        binding.textStn.setVisibility(View.VISIBLE);
        binding.subStnSpin.setVisibility(View.VISIBLE);
    }

    private void showExitSnackbar() {
        Snackbar.make(
                binding.getRoot(),
                "Press back again to exit!",
                Snackbar.LENGTH_LONG
        ).show();
    }

}