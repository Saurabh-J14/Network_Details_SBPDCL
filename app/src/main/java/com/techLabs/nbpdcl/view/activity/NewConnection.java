package com.techLabs.nbpdcl.view.activity;

import static com.techLabs.nbpdcl.Utils.Config.isTreeNode;

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
import com.techLabs.nbpdcl.adapters.NewConnectionAdapter;
import com.techLabs.nbpdcl.databinding.ActivityNewConnectionBinding;
import com.techLabs.nbpdcl.models.nsc.NewConnectionModel;
import com.techLabs.nbpdcl.retrofit.ApiInterface;
import com.techLabs.nbpdcl.retrofit.RetrofitClient;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class NewConnection extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private ActivityNewConnectionBinding binding;
    private boolean doubleBackToExitPressedOnce = false;
    private NewConnectionAdapter newConnectionAdapter;
    private PrefManager prefManager;

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNewConnectionBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        prefManager = new PrefManager(NewConnection.this);
        setSupportActionBar(binding.toolbar);
        binding.refreshLayout.setOnRefreshListener(this);
        binding.refreshLayout.setColorSchemeColors(ContextCompat.getColor(this, R.color.blue));

        if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(NewConnection.this)) {
            getNewConnection();
        } else {
            final Dialog dialog = new Dialog(NewConnection.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.no_internet_dialog);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(NewConnection.this.getDrawable(R.drawable.pop_background));
            LottieAnimationView lottieAnimationView = dialog.findViewById(R.id.animation_view);
            Button RetryBtn = dialog.findViewById(R.id.btnDialog);
            lottieAnimationView.playAnimation();
            RetryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(NewConnection.this)) {
                        getNewConnection();
                        dialog.dismiss();
                    }
                }
            });
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();
        }

        binding.connectionFilter.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                newConnectionAdapter.filter(newText);
                return true;
            }
        });

    }

    private void getNewConnection() {
        binding.shimmerViewContainer.startShimmer();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("CYMDBNET", prefManager.getDBName());
        jsonObject.addProperty("type", prefManager.getUserType());
        Retrofit retrofit = RetrofitClient.getClient(this);
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<NewConnectionModel> call = apiInterface.getNewConnectionData(jsonObject);
        call.enqueue(new Callback<NewConnectionModel>() {
            @Override
            public void onResponse(@NonNull Call<NewConnectionModel> call, @NonNull Response<NewConnectionModel> response) {
                if (response.code() == 200) {
                    binding.shimmerViewContainer.stopShimmer();
                    binding.shimmerViewContainer.setVisibility(View.GONE);
                    binding.recyclerView.setVisibility(View.VISIBLE);
                    binding.refreshLayout.setRefreshing(false);
                    NewConnectionModel newConnectionModel = response.body();
                    assert newConnectionModel != null;
                    if (newConnectionModel.getOutput() != null && !newConnectionModel.getOutput().isEmpty()) {
                        newConnectionAdapter = new NewConnectionAdapter(NewConnection.this, newConnectionModel.getOutput());
                        binding.recyclerView.setLayoutManager(new LinearLayoutManager(NewConnection.this));
                        binding.recyclerView.setHasFixedSize(true);
                        binding.recyclerView.setAdapter(newConnectionAdapter);
                    }
                } else if (response.code() == 401) {
                    prefManager.setIsUserLogin(false);
                    startActivity(new Intent(NewConnection.this, LoginActivity.class));
                    finish();
                } else {
                    binding.shimmerViewContainer.stopShimmer();
                    binding.shimmerViewContainer.setVisibility(View.GONE);
                    binding.recyclerView.setVisibility(View.VISIBLE);
                    Snackbar snack = Snackbar.make(findViewById(android.R.id.content), response.message() + " - " + response.code(), Snackbar.LENGTH_LONG);
                    snack.show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<NewConnectionModel> call, @NonNull Throwable t) {
                binding.shimmerViewContainer.stopShimmer();
                binding.shimmerViewContainer.setVisibility(View.GONE);
                binding.recyclerView.setVisibility(View.VISIBLE);
                binding.refreshLayout.setRefreshing(false);
                Snackbar snack = Snackbar.make(findViewById(android.R.id.content), getString(R.string.error_msg), Snackbar.LENGTH_LONG);
                snack.show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        isTreeNode = false;
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Snackbar snack = Snackbar.make(findViewById(android.R.id.content), "Press back again to exit!", Snackbar.LENGTH_LONG);
        snack.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.nav_toll_menu, menu);
        menu.getItem(0).setVisible(false);
        menu.getItem(1).setVisible(false);
        menu.getItem(2).setVisible(false);
        menu.getItem(3).setVisible(false);
        menu.getItem(4).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nav_logout) {
            prefManager.setIsUserLogin(false);
            startActivity(new Intent(NewConnection.this, LoginActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getNewConnection();
                Snackbar.make(binding.getRoot(), "List Update", Snackbar.LENGTH_LONG).show();
            }
        }, 3000);
    }
}