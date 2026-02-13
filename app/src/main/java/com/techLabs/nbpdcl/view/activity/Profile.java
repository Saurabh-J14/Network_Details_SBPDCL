package com.techLabs.nbpdcl.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.techLabs.nbpdcl.R;
import com.techLabs.nbpdcl.Utils.PrefManager;
import com.techLabs.nbpdcl.Utils.ResponseDataUtils;
import com.techLabs.nbpdcl.databinding.ActivityProfileBinding;
import com.techLabs.nbpdcl.models.Logout;
import com.techLabs.nbpdcl.retrofit.ApiInterface;
import com.techLabs.nbpdcl.retrofit.RetrofitClient;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class Profile extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private PrefManager prefManager;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        prefManager = new PrefManager(this);

        try {
            if (prefManager.getName() != null && !prefManager.getName().isBlank()) {
                binding.profileName.setText(prefManager.getName());
            } else {
                binding.profileName.setText("");
            }

            if (prefManager.getDesignation() != null && !prefManager.getDesignation().isBlank()) {
                binding.empProfileIdTv.setText(prefManager.getDesignation() + " | ID: " + prefManager.getUserName());
            } else {
                binding.empProfileIdTv.setText("");
            }

            if (prefManager.getEmail() != null && !prefManager.getEmail().isBlank()) {
                binding.emailTv.setText(prefManager.getEmail());
            } else {
                binding.emailTv.setText("");
            }

            if (prefManager.getMobile() != null && !prefManager.getMobile().isBlank()) {
                binding.mobileNoTv.setText(prefManager.getMobile());
            } else {
                binding.mobileNoTv.setText("");
            }

            if (prefManager.getPlace_Of_Work() != null && !prefManager.getPlace_Of_Work().isBlank()) {
                binding.placeWorkTv.setText(prefManager.getPlace_Of_Work());
            } else {
                binding.placeWorkTv.setText("");
            }

            if (prefManager.getRegion() != null && prefManager.getZone() != null && prefManager.getCircle() != null && !prefManager.getRegion().isBlank()) {
                binding.regionDivisionCircleTv.setText(prefManager.getRegion() + " , " + prefManager.getZone() + " , " + prefManager.getCircle());
            } else {
                binding.regionDivisionCircleTv.setText("");
            }

            if (prefManager.getLastLogin() != null && !prefManager.getLastLogin().isBlank()) {
                binding.tvLastLogin.setText(prefManager.getLastLogin());
            } else {
                binding.tvLastLogin.setText("");
            }

            if (prefManager.getLastLogout() != null && !prefManager.getLastLogout().isBlank()) {
                binding.tvLastLogout.setText(prefManager.getLastLogout());
            } else {
                binding.tvLastLogout.setText("");
            }

            if (prefManager.getLastLogin() != null && !prefManager.getLastLogin().isBlank()) {
                binding.tvLastLogin.setText(ResponseDataUtils.formatDateTime(prefManager.getLastLogin()));
            } else {
                binding.tvLastLogin.setText("");
            }

            if (prefManager.getLastLogout() != null && !prefManager.getLastLogout().isBlank()) {
                binding.tvLastLogout.setText(ResponseDataUtils.formatDateTime(prefManager.getLastLogout()));
            } else {
                binding.tvLastLogout.setText("");
            }
        } catch (Exception e) {
            Log.d("Exception", Objects.requireNonNull(e.getLocalizedMessage()));
        }

        binding.btnLogout.setOnClickListener(v -> {
            if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(this)) {
                logout();
            } else {
                Snackbar.make(binding.getRoot(), getString(R.string.no_internet_connection), Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(Profile.this)) {
                            logout();
                        }
                    }
                }).show();
            }
        });

    }

    private void logout() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("token", prefManager.getAccessToken());
        Retrofit retrofit = RetrofitClient.getClient(this);
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<Logout> call = apiInterface.logout(jsonObject);
        call.enqueue(new Callback<Logout>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<Logout> call, @NonNull Response<Logout> response) {
                if (response.code() == 200) {
                    Logout logout = response.body();
                    assert logout != null;
                    if (logout.getMessage().equalsIgnoreCase("Logout successful")) {
                        prefManager.setIsUserLogin(false);
                        prefManager.setUserType(null);
                        prefManager.setAccessToken(null);
                        prefManager.setUserName(null);
                        prefManager.setType(null);
                        prefManager.setName(null);
                        prefManager.setDesignation(null);
                        prefManager.setEmail(null);
                        prefManager.setMobile(null);
                        prefManager.setDate_Joined(null);
                        prefManager.setPlace_Of_Work(null);
                        prefManager.setRegion(null);
                        prefManager.setZone(null);
                        prefManager.setCircle(null);
                        startActivity(new Intent(Profile.this, LoginActivity.class));
                        finish();
                    }
                } else if (response.code() == 401){
                    prefManager.setIsUserLogin(false);
                    startActivity(new Intent(Profile.this, LoginActivity.class));
                    finish();
                }else {
                    @SuppressLint("InflateParams")
                    View layout = LayoutInflater.from(Profile.this).inflate(R.layout.toast_layout, null);
                    TextView Ok = layout.findViewById(R.id.okBtn);
                    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                    TextView header = layout.findViewById(R.id.headerTv);
                    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                    TextView description = layout.findViewById(R.id.descripTv);
                    header.setText(response.message() + " - " + response.code());
                    description.setText(getString(R.string.error_msg));
                    Ok.setOnClickListener(v -> {
                        logout();
                    });
                    Toast toast = new Toast(Profile.this);
                    toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Logout> call, @NonNull Throwable t) {
                @SuppressLint("InflateParams")
                View layout = LayoutInflater.from(Profile.this).inflate(R.layout.toast_layout, null);
                TextView Ok = layout.findViewById(R.id.okBtn);
                @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                TextView header = layout.findViewById(R.id.headerTv);
                @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                TextView description = layout.findViewById(R.id.descripTv);
                header.setText(getString(R.string.error));
                description.setText(getString(R.string.error_msg));
                Ok.setOnClickListener(v -> {
                    logout();
                });
                Toast toast = new Toast(Profile.this);
                toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
            }
        });
    }
}
