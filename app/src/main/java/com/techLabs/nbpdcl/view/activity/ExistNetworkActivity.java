package com.techLabs.nbpdcl.view.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.techLabs.nbpdcl.R;
import com.techLabs.nbpdcl.Utils.PrefManager;
import com.techLabs.nbpdcl.Utils.ResponseDataUtils;
import com.techLabs.nbpdcl.adapters.ExistNetworkAdapter;
import com.techLabs.nbpdcl.databinding.ActivitySurwayctivityBinding;
import com.techLabs.nbpdcl.models.ExistNetworkModel;
import com.techLabs.nbpdcl.retrofit.ApiInterface;
import com.techLabs.nbpdcl.retrofit.RetrofitClient;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ExistNetworkActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private ActivitySurwayctivityBinding binding;
    private ExistNetworkAdapter existNetworkAdapter;
    private PrefManager prefManager;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySurwayctivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        prefManager = new PrefManager(ExistNetworkActivity.this);
        prefManager.setUserType(prefManager.getType());
        setSupportActionBar(binding.toolbar);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(existNetworkAdapter);
        binding.swipeLayout.setOnRefreshListener(this);
        binding.swipeLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.blue));

        if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(ExistNetworkActivity.this)) {
            binding.shimmerViewContainer.startShimmer();
            binding.shimmerViewContainer.animate();
            getExistNetworkData();
        } else {
            final Dialog dialog = new Dialog(ExistNetworkActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.no_internet_dialog);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(ExistNetworkActivity.this.getDrawable(R.drawable.pop_background));
            LottieAnimationView lottieAnimationView = dialog.findViewById(R.id.animation_view);
            Button RetryBtn = dialog.findViewById(R.id.btnDialog);
            lottieAnimationView.playAnimation();
            RetryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(ExistNetworkActivity.this)) {
                        getExistNetworkData();
                        dialog.dismiss();
                    }
                }
            });
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();
        }

        binding.existNetworkFilter.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText != null && newText.length() >= 3 || !newText.isEmpty()) {
                    if (existNetworkAdapter != null) {
                        existNetworkAdapter.filter(newText);
                    }
                }
                return true;
            }
        });
    }

    private void getExistNetworkData() {
        binding.shimmerViewContainer.startShimmer();
        JsonObject jsonObject = new JsonObject();
        Retrofit retrofit = RetrofitClient.getClient(this);
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        jsonObject.addProperty("CYMDBNET", prefManager.getDatabaseSurvey());
        jsonObject.addProperty("UserType", prefManager.getUserType());
        jsonObject.addProperty("DatabaseType","Survey");
        Call<ExistNetworkModel> call = apiInterface.getExistNetworkData(jsonObject);
        call.enqueue(new Callback<ExistNetworkModel>() {
            @Override
            public void onResponse(@NonNull Call<ExistNetworkModel> call, @NonNull Response<ExistNetworkModel> response) {
                if (response.code() == 200) {
                    binding.shimmerViewContainer.stopShimmer();
                    binding.swipeLayout.setRefreshing(false);
                    binding.shimmerViewContainer.setVisibility(View.GONE);
                    binding.recyclerView.setVisibility(View.VISIBLE);
                    ExistNetworkModel existNetworkModel = response.body();
                    assert existNetworkModel != null;
                    if (existNetworkModel.getResult() != null && !existNetworkModel.getResult().isEmpty()) {
                        existNetworkAdapter = new ExistNetworkAdapter(ExistNetworkActivity.this, existNetworkModel.getResult());
                        binding.recyclerView.setLayoutManager(new LinearLayoutManager(ExistNetworkActivity.this));
                        binding.recyclerView.setHasFixedSize(true);
                        binding.recyclerView.setAdapter(existNetworkAdapter);
                        prefManager.setDBName(existNetworkModel.getDatabase());
                    }
                } else if (response.code() == 401) {
                    prefManager.setIsUserLogin(false);
                    binding.swipeLayout.setRefreshing(false);
                    startActivity(new Intent(ExistNetworkActivity.this, LoginActivity.class));
                    finish();
                } else {
                    binding.swipeLayout.setRefreshing(false);
                    binding.shimmerViewContainer.stopShimmer();
                    binding.shimmerViewContainer.setVisibility(View.GONE);
                    binding.recyclerView.setVisibility(View.VISIBLE);
                    Snackbar snack = Snackbar.make(findViewById(android.R.id.content), response.message() + " - " + response.code(), Snackbar.LENGTH_LONG);
                    snack.show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ExistNetworkModel> call, @NonNull Throwable t) {
                binding.shimmerViewContainer.stopShimmer();
                binding.shimmerViewContainer.setVisibility(View.GONE);
                binding.recyclerView.setVisibility(View.VISIBLE);
                binding.swipeLayout.setRefreshing(false);
                Snackbar snack = Snackbar.make(findViewById(android.R.id.content), getString(R.string.error_msg), Snackbar.LENGTH_LONG);
                snack.show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.nav_toll_menu, menu);
        menu.getItem(0).setVisible(false);
        menu.getItem(1).setVisible(false);
        menu.getItem(2).setVisible(true);
        menu.getItem(3).setVisible(true);
        menu.getItem(4).setVisible(false);
        menu.getItem(5).setVisible(true);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_logout:
                prefManager.setIsUserLogin(false);
                startActivity(new Intent(ExistNetworkActivity.this, LoginActivity.class));
                finish();
                break;

            case R.id.nav_new_source:
                Intent intent1 = new Intent(ExistNetworkActivity.this, MapActivity.class);
                intent1.putExtra("Type", "AddNetwork");
                prefManager.setEditMode("Survey");
                startActivity(intent1);
                break;

            case R.id.nav_profile:
                Intent intent = new Intent(ExistNetworkActivity.this, Profile.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getExistNetworkData();
                Snackbar.make(binding.getRoot(), "Update Network List", Snackbar.LENGTH_LONG).show();
            }
        }, 3000);
    }
}
