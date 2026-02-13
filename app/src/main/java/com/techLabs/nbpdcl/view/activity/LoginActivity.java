package com.techLabs.nbpdcl.view.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;

import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.techLabs.nbpdcl.R;
import com.techLabs.nbpdcl.Utils.PrefManager;
import com.techLabs.nbpdcl.Utils.ResponseDataUtils;
import com.techLabs.nbpdcl.databinding.ActivityLoginBinding;
import com.techLabs.nbpdcl.models.LoginModel;
import com.techLabs.nbpdcl.models.ProjectModel;
import com.techLabs.nbpdcl.retrofit.ApiInterface;
import com.techLabs.nbpdcl.retrofit.RetrofitClient;

import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {
    private ActivityLoginBinding binding;
    private boolean isPasswordVisible = false;
    private PrefManager prefManager;
    private boolean isBgAnimating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        prefManager = new PrefManager(this);
        Glide.with(LoginActivity.this).load(R.drawable.login_bg_sand).into(binding.bgImage);
        startAnimation();

        binding.loginBtn.setOnClickListener(view -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            View currentView = getCurrentFocus();
            if (currentView != null) {
                imm.hideSoftInputFromWindow(currentView.getWindowToken(), 0);
            }
            CheckDetails();
        });

        binding.imgVisible.setOnClickListener(v -> togglePasswordVisibility());

    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            binding.passwordEdtTxt.setTransformationMethod(PasswordTransformationMethod.getInstance());
            binding.imgVisible.setImageResource(R.drawable.ic_visibility_off);
        } else {
            binding.passwordEdtTxt.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            binding.imgVisible.setImageResource(R.drawable.ic_visibility);
        }
        isPasswordVisible = !isPasswordVisible;
        binding.passwordEdtTxt.setSelection(binding.passwordEdtTxt.getText().length());
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void CheckDetails() {
        binding.userNameEdtTxt.setError(null);
        binding.passwordEdtTxt.setError(null);
        boolean isCancel = false;
        View focusView = null;

        if (binding.passwordEdtTxt.getText().toString().trim().isEmpty()) {
            binding.passwordEdtTxt.setError("Please enter the password");
            focusView = binding.passwordEdtTxt;
            isCancel = true;
        }

        if (binding.userNameEdtTxt.getText().toString().trim().isEmpty()) {
            binding.userNameEdtTxt.setError("Please enter the username");
            focusView = binding.userNameEdtTxt;
            isCancel = true;
        } else if (binding.userNameEdtTxt.getText().toString().trim().length() < 3) {
            binding.userNameEdtTxt.setError("Please enter minimum three letter");
            focusView = binding.userNameEdtTxt;
            isCancel = true;
        }

        if (isCancel) {
            focusView.requestFocus();
        } else {
            if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(LoginActivity.this)) {
                binding.otpCard.setVisibility(View.GONE);
//                startBgAnimation();
                SendLoginData(binding.userNameEdtTxt.getText().toString().trim(), binding.passwordEdtTxt.getText().toString().trim());
            } else {
                Snackbar.make(binding.getRoot(), getString(R.string.no_internet_connection), Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(LoginActivity.this)) {
                            binding.otpCard.setVisibility(View.GONE);
//                            startBgAnimation();
                            SendLoginData(binding.userNameEdtTxt.getText().toString().trim(), binding.passwordEdtTxt.getText().toString().trim());
                        }
                    }
                }).show();
            }
        }
    }

    private void SendLoginData(String userName, String passWord) {
        binding.otpCard.setVisibility(View.GONE);
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", userName);
        jsonObject.addProperty("password", passWord);
        Retrofit retrofit = RetrofitClient.getClient(this);
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<LoginModel> call = apiInterface.getLogin(jsonObject);
        call.enqueue(new Callback<LoginModel>() {
            @Override
            public void onResponse(@NonNull Call<LoginModel> call, @NonNull Response<LoginModel> response) {
                if (response.code() == 200) {
                    try {
                        LoginModel loginModel = response.body();
                        assert loginModel != null;
                        if (loginModel.getMsg() != null && !loginModel.getMsg().equals("null") && loginModel.getMsg().equalsIgnoreCase("OTP sent successfully.")) {
                            prefManager.setIsUserLogin(true);
                            Intent intent = new Intent(LoginActivity.this, OtpVerificationActivity.class);
                            intent.putExtra("Number", loginModel.getMobileNumber());
                            intent.putExtra("userName", userName);
                            intent.putExtra("password", passWord);
                            startActivity(intent);
                            finish();
                        } else {
                            binding.otpCard.setVisibility(View.VISIBLE);
                            stopBgAnimation();
                            Snackbar.make(findViewById(android.R.id.content), "Incorrect username and password", Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    CheckDetails();
                                }
                            }).show();
                        }
                    } catch (Exception e) {
                        Log.d("Exception", Objects.requireNonNull(e.getLocalizedMessage()));
                    }
//                    getProjectList();
                } else {
                    stopBgAnimation();
                    binding.otpCard.setVisibility(View.VISIBLE);
                    Snackbar.make(binding.getRoot(), response.message() + " - " + response.code(), Snackbar.LENGTH_INDEFINITE).setAction("Retry", v -> {
                        CheckDetails();
                    }).setActionTextColor(getColor(R.color.blue)).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginModel> call, @NonNull Throwable t) {
                stopBgAnimation();
                binding.otpCard.setVisibility(View.VISIBLE);
                Snackbar.make(binding.getRoot(), getString(R.string.error_msg), Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckDetails();
                    }
                }).show();
            }
        });
    }

    private void getProjectList() {

        Retrofit retrofit = RetrofitClient.getClient(this);
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);

        Call<ProjectModel> call = apiInterface.getProject(
                "Bearer " + prefManager.getAccessToken(), prefManager.getUserName());

        call.enqueue(new Callback<ProjectModel>() {
            @Override
            public void onResponse(@NonNull Call<ProjectModel> call, @NonNull Response<ProjectModel> response) {

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
                    Snackbar.make(findViewById(android.R.id.content), "Session issue. Please continue OTP verification.", Snackbar.LENGTH_LONG).show();
                    finish();

                } else {
                    Snackbar.make(findViewById(android.R.id.content), response.message() + " - " + response.code(), Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ProjectModel> call, @NonNull Throwable t) {

                Snackbar.make(findViewById(android.R.id.content), getString(R.string.error_msg), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private void startBgAnimation() {
        if (isBgAnimating) return;
        isBgAnimating = true;
        startZoomIn();
    }

    private void startZoomIn() {
        if (!isBgAnimating) return;

        binding.bgImage.animate()
                .scaleX(1.1f)
                .scaleY(1.1f)
                .setDuration(15000)
                .setInterpolator(new FastOutSlowInInterpolator())
                .withEndAction(() -> {
                    if (isBgAnimating) startZoomOut();
                })
                .start();
    }

    private void startZoomOut() {
        if (!isBgAnimating) return;

        binding.bgImage.animate()
                .scaleX(1f)
                .scaleY(1f)
                .setDuration(15000)
                .setInterpolator(new FastOutSlowInInterpolator())
                .withEndAction(() -> {
                    if (isBgAnimating) startZoomIn();
                })
                .start();
    }

    private void stopBgAnimation() {
        isBgAnimating = false;               // stop future loops
        binding.bgImage.animate().cancel();  // stop current animation
        binding.bgImage.setScaleX(1f);       // reset
        binding.bgImage.setScaleY(1f);
    }

    private void startAnimation() {
        /*binding.bgImage.setScaleX(1.1f);
        binding.bgImage.setScaleY(1.1f);
        ObjectAnimator moveX = ObjectAnimator.ofFloat(
                binding.bgImage, "translationX", -40f, 40f);
        moveX.setDuration(6000);
        moveX.setRepeatCount(ValueAnimator.INFINITE);
        moveX.setRepeatMode(ValueAnimator.REVERSE);
        moveX.setInterpolator(new AccelerateDecelerateInterpolator());
        moveX.start();*/

        binding.bgImage.setScaleX(1.2f);
        binding.bgImage.setScaleY(1.2f);

        ObjectAnimator moveX = ObjectAnimator.ofFloat(
                binding.bgImage, "translationX", -40f, 40f);
        moveX.setDuration(6000);
        moveX.setRepeatCount(ValueAnimator.INFINITE);
        moveX.setRepeatMode(ValueAnimator.REVERSE);
        moveX.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator moveY = ObjectAnimator.ofFloat(
                binding.bgImage, "translationY", -30f, 30f);
        moveY.setDuration(6000);
        moveY.setRepeatCount(ValueAnimator.INFINITE);
        moveY.setRepeatMode(ValueAnimator.REVERSE);
        moveY.setInterpolator(new AccelerateDecelerateInterpolator());

        moveX.start();
        moveY.start();
    }

}
