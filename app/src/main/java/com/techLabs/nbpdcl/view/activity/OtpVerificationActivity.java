package com.techLabs.nbpdcl.view.activity;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.JsonObject;
import com.techLabs.nbpdcl.R;
import com.techLabs.nbpdcl.Utils.AppSignatureHelper;
import com.techLabs.nbpdcl.Utils.OtpViewHandler;
import com.techLabs.nbpdcl.Utils.PrefManager;
import com.techLabs.nbpdcl.Utils.ResponseDataUtils;
import com.techLabs.nbpdcl.Utils.SmsBroadcastReceiver;
import com.techLabs.nbpdcl.databinding.ActivityOtpVerificationBinding;
import com.techLabs.nbpdcl.models.LoginModel;
import com.techLabs.nbpdcl.models.VerifyOTP;
import com.techLabs.nbpdcl.retrofit.ApiInterface;
import com.techLabs.nbpdcl.retrofit.RetrofitClient;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class OtpVerificationActivity extends AppCompatActivity {

    private static final int SMS_CONSENT_REQUEST = 200;
    private ActivityOtpVerificationBinding binding;
    private Intent intent;
    private PrefManager prefManager;
    private SmsBroadcastReceiver smsReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOtpVerificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        intent = getIntent();
        prefManager = new PrefManager(this);
        Glide.with(OtpVerificationActivity.this).load(R.drawable.otp_verify_bg).into(binding.otpBgImage);
        AppSignatureHelper helper = new AppSignatureHelper(this);
        List<String> appHashList = helper.getAppSignatures();
        if (!appHashList.isEmpty()) {
            String appHash = appHashList.get(0);
            Log.d("APP_HASH", appHash);
        }

        OtpViewHandler[] otpFields = new OtpViewHandler[]{
                binding.et1, binding.et2, binding.et3,
                binding.et4, binding.et5, binding.et6
        };

        for (int i = 0; i < otpFields.length; i++) {

            final int index = i;

            otpFields[i].addTextChangedListener(new TextWatcher() {
                @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() == 1 && index < otpFields.length - 1) {
                        otpFields[index + 1].requestFocus();
                    }
                }

                @Override public void afterTextChanged(Editable s) {}
            });

            otpFields[i].setOnOtpBackspaceListener(() -> {
                if (index > 0) {
                    otpFields[index - 1].requestFocus();
                    otpFields[index - 1].setSelection(
                            otpFields[index - 1].getText().length()
                    );
                }
            });
        }

        binding.tvResend.setOnClickListener(v -> {
            binding.tvResend.setEnabled(false);
            binding.tvResend.setTextColor(getResources().getColor(R.color.gray));
            ResendOTP(intent.getStringExtra("userName"), intent.getStringExtra("password"));
        });

        String mobile = intent.getStringExtra("Number");
        String maskedNumber = "";
        if (mobile != null && mobile.length() == 10) {
            maskedNumber = "*******" + mobile.substring(7);
        }
        String message = "🔐 OTP has been sent to: " + maskedNumber;

        binding.rootLayout.post(() -> {

            View customView = LayoutInflater.from(this)
                    .inflate(R.layout.custom_snackbar, binding.rootLayout, false);

            TextView textView = customView.findViewById(R.id.snackbar_text);
            textView.setText(message);

            customView.setId(View.generateViewId());

            ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);

            params.topToBottom = binding.otpCard.getId();
            params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
            params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;

            params.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.89f);
            params.topMargin = dpToPx(12);

            customView.setLayoutParams(params);

            binding.rootLayout.addView(customView);

            customView.setAlpha(0f);
            customView.animate().alpha(1f).setDuration(250).start();

        });

        binding.btnVerify.setOnClickListener(v -> {
            checkData();
        });

        startSmsListener();
        startTimer();

        binding.tvResend.setEnabled(false);
        binding.tvResend.setTextColor(getResources().getColor(R.color.gray));
    }

    private void ResendOTP(String userName, String password) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", userName);
        jsonObject.addProperty("password", password);
        Retrofit retrofit = RetrofitClient.getClient(this);
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<LoginModel> call = apiInterface.getLogin(jsonObject);
        call.enqueue(new Callback<LoginModel>() {
            @Override
            public void onResponse(@NonNull Call<LoginModel> call, @NonNull Response<LoginModel> response) {
                if (response.code() == 200) {
                    LoginModel loginModel = response.body();
                    assert loginModel != null;
                    if (loginModel.getMsg().equalsIgnoreCase("OTP sent successfully.")) {
                        startTimer();
                        binding.et1.setText("");
                        binding.et2.setText("");
                        binding.et3.setText("");
                        binding.et4.setText("");
                        binding.et5.setText("");
                        binding.et6.setText("");
                    }
                } else {
                    binding.tvResend.setEnabled(true);
                    binding.tvResend.setTextColor(getResources().getColor(R.color.blue));
                    Snackbar.make(binding.getRoot(), response.message() + " - " + response.code(), Snackbar.LENGTH_INDEFINITE).setAction("Retry", v -> {
                        binding.tvResend.setEnabled(false);
                        binding.tvResend.setTextColor(getResources().getColor(R.color.gray));
                        ResendOTP(userName, password);
                    }).setActionTextColor(getColor(R.color.blue)).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<LoginModel> call, @NonNull Throwable t) {
                binding.tvResend.setEnabled(true);
                binding.tvResend.setTextColor(getResources().getColor(R.color.blue));
                Snackbar.make(binding.getRoot(), getString(R.string.error_msg), Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        binding.tvResend.setEnabled(false);
                        binding.tvResend.setTextColor(getResources().getColor(R.color.gray));
                        ResendOTP(userName, password);
                    }
                }).show();
            }
        });

    }

    private void startSmsListener() {
        SmsRetrieverClient client = SmsRetriever.getClient(this);
        client.startSmsUserConsent(null)
                .addOnSuccessListener(aVoid ->
                        Log.d("Waiting for OTP...", "Waiting for OTP...")
                )
                .addOnFailureListener(e ->
                        Snackbar.make(binding.getRoot(), "There is Technical issues please try again later!", Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                binding.tvResend.setEnabled(false);
                                binding.tvResend.setTextColor(getResources().getColor(R.color.gray));
                                ResendOTP(intent.getStringExtra("userName"), intent.getStringExtra("password"));
                            }
                        }).show());
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onStart() {
        super.onStart();
        registerReceiverForOtp();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (smsReceiver != null)
            unregisterReceiver(smsReceiver);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SMS_CONSENT_REQUEST && resultCode == RESULT_OK) {
            assert data != null;
            String message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE);
            String otp = extractOtp(message);
            if (otp != null && otp.length() == 6) {
                fillOtpBoxes(otp);
            }
        }
    }

    private String extractOtp(String message) {
        if (message == null) return null;
        Pattern pattern = Pattern.compile("\\b\\d{6}\\b");
        Matcher matcher = pattern.matcher(message);
        if (matcher.find()) {
            return matcher.group(0);
        }
        return null;
    }

    private void fillOtpBoxes(String otp) {
        if (otp == null || otp.length() != 6) return;
        binding.et1.setText(String.valueOf(otp.charAt(0)));
        binding.et2.setText(String.valueOf(otp.charAt(1)));
        binding.et3.setText(String.valueOf(otp.charAt(2)));
        binding.et4.setText(String.valueOf(otp.charAt(3)));
        binding.et5.setText(String.valueOf(otp.charAt(4)));
        binding.et6.setText(String.valueOf(otp.charAt(5)));
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    private void registerReceiverForOtp() {
        smsReceiver = new SmsBroadcastReceiver(new SmsBroadcastReceiver.OtpReceiveListener() {
            @Override
            public void onOtpReceived(Intent consentIntent) {
                try {
                    startActivityForResult(consentIntent, SMS_CONSENT_REQUEST);
                } catch (Exception e) {
                    Log.e("OTP", "Error starting consent intent: " + e.getMessage());
                }
            }

            @Override
            public void onFailure() {
                Log.e("OTP", "Timeout or failure");
            }
        });

        registerReceiver(
                smsReceiver,
                SmsBroadcastReceiver.getIntentFilter(),
                Context.RECEIVER_EXPORTED
        );
    }

    private void checkData() {
        binding.et1.setError(null);
        binding.et2.setError(null);
        binding.et3.setError(null);
        binding.et4.setError(null);
        binding.et5.setError(null);
        binding.et6.setError(null);
        boolean isCancel = false;
        View focusView = null;

        if (binding.et6.getText().toString().trim().isEmpty()) {
            focusView = binding.et6;
            isCancel = true;
        }

        if (binding.et5.getText().toString().trim().isEmpty()) {
            focusView = binding.et5;
            isCancel = true;
        }

        if (binding.et4.getText().toString().trim().isEmpty()) {
            focusView = binding.et4;
            isCancel = true;
        }

        if (binding.et3.getText().toString().trim().isEmpty()) {
            focusView = binding.et3;
            isCancel = true;
        }

        if (binding.et2.getText().toString().trim().isEmpty()) {
            focusView = binding.et2;
            isCancel = true;
        }

        if (binding.et1.getText().toString().trim().isEmpty()) {
            focusView = binding.et1;
            isCancel = true;
        }
        if (isCancel) {
            focusView.requestFocus();
        } else {
            if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(this)) {
                OTPVerify(binding.et1.getText().toString().trim(), binding.et2.getText().toString().trim(), binding.et3.getText().toString().trim(), binding.et4.getText().toString().trim(), binding.et5.getText().toString().trim(), binding.et6.getText().toString().trim());
            } else {
                Snackbar.make(binding.getRoot(), getString(R.string.no_internet_connection), Snackbar.LENGTH_INDEFINITE).setAction("Retry", v -> {
                    if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(OtpVerificationActivity.this)) {
                        OTPVerify(binding.et1.getText().toString().trim(), binding.et2.getText().toString().trim(), binding.et3.getText().toString().trim(), binding.et4.getText().toString().trim(), binding.et5.getText().toString().trim(), binding.et6.getText().toString().trim());
                    }
                }).show();
            }
        }
    }

    private void OTPVerify(String text1, String text2, String text3, String text4, String text5, String text6) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("user_id", intent.getStringExtra("userName"));
        jsonObject.addProperty("otp", text1 + text2 + text3 + text4 + text5 + text6);
        Retrofit retrofit = RetrofitClient.getClient(this);
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<VerifyOTP> call = apiInterface.otpVerify(jsonObject);
        call.enqueue(new Callback<VerifyOTP>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<VerifyOTP> call, @NonNull Response<VerifyOTP> response) {
                if (response.code() == 200) {
                    try {
                        VerifyOTP verifyOTP = response.body();
                        assert verifyOTP != null;
                        if (verifyOTP.getMsg() != null && !verifyOTP.getMsg().equalsIgnoreCase("null") && !verifyOTP.getMsg().isBlank()) {
                            if (verifyOTP.getMsg().equalsIgnoreCase("OTP Verified successfully")) {
                                prefManager.setIsUserLogin(true);
                                prefManager.setProjectName("SBPDCL");
                                if (verifyOTP.getUsertype() != null && !verifyOTP.getUsertype().equals("null") && !verifyOTP.getLoginTime().isBlank()) {
                                    prefManager.setUserType(verifyOTP.getUsertype());
                                }

                                if (verifyOTP.getAccess() != null && !verifyOTP.getAccess().equals("null") && !verifyOTP.getAccess().isBlank()) {
                                    prefManager.setAccessToken(verifyOTP.getAccess());
                                }

                                if (verifyOTP.getUsername() != null && !verifyOTP.getUsername().equals("null") && !verifyOTP.getUsername().isBlank()) {
                                    prefManager.setUserName(verifyOTP.getUsername());
                                }

                                if (verifyOTP.getUsertype() != null && !verifyOTP.getUsertype().equals("null") && !verifyOTP.getUsertype().isBlank()) {
                                    prefManager.setType(verifyOTP.getUsertype());
                                }

                                if (verifyOTP.getName() != null && !verifyOTP.getName().equals("null") && !verifyOTP.getName().isBlank()) {
                                    prefManager.setName(verifyOTP.getName());
                                }

                                if (verifyOTP.getDesignation() != null && !verifyOTP.getDesignation().equals("null") && !verifyOTP.getDesignation().isBlank()) {
                                    prefManager.setDesignation(verifyOTP.getDesignation());
                                }

                                if (verifyOTP.getEmail() != null && !verifyOTP.getEmail().equals("null") && !verifyOTP.getEmail().isBlank()) {
                                    prefManager.setEmail(verifyOTP.getEmail());
                                }

                                prefManager.setMobile(intent.getStringExtra("Number"));

                                if (verifyOTP.getDateJoined() != null && !verifyOTP.getDateJoined().equals("null") && !verifyOTP.getDateJoined().isBlank()) {
                                    prefManager.setDate_Joined(verifyOTP.getDateJoined());
                                }

                                if (verifyOTP.getPlaceOfWork() != null && !verifyOTP.getPlaceOfWork().equals("null") && !verifyOTP.getPlaceOfWork().isBlank()) {
                                    prefManager.setPlace_Of_Work(verifyOTP.getPlaceOfWork());
                                }

                                if (verifyOTP.getRegion() != null && !verifyOTP.getRegion().equals("null") && !verifyOTP.getRegion().isBlank()) {
                                    prefManager.setRegion(verifyOTP.getRegion());
                                }

                                if (verifyOTP.getZone() != null && !verifyOTP.getZone().equals("null") && !verifyOTP.getZone().isBlank()) {
                                    prefManager.setZone(verifyOTP.getZone());
                                }

                                if (verifyOTP.getCircle() != null && !verifyOTP.getCircle().equals("null") && !verifyOTP.getCircle().isBlank()) {
                                    prefManager.setCircle(verifyOTP.getCircle());
                                }

                                if (verifyOTP.getLoginTime() != null && !verifyOTP.getLoginTime().equals("null") && !verifyOTP.getLoginTime().isBlank()) {
                                    prefManager.setLastLogin(verifyOTP.getLoginTime());
                                }

                                if (verifyOTP.getLogoutTime() != null && !verifyOTP.getLogoutTime().equals("null") && !verifyOTP.getLogoutTime().isBlank()) {
                                    prefManager.setLastLogout(verifyOTP.getLogoutTime());
                                }

                                startActivity(new Intent(OtpVerificationActivity.this, SplashScreen.class));
                                finish();
                            } else if (verifyOTP.getMsg().equalsIgnoreCase("Invalid OTP")) {
                                Snackbar.make(binding.getRoot(), "OTP do not match.", Snackbar.LENGTH_LONG).show();
                            } else {
                                Snackbar.make(binding.getRoot(), "OTP Verification Failed, try again later!", Snackbar.LENGTH_INDEFINITE).setAction("Retry", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        checkData();
                                    }
                                }).show();
                            }
                        }

                    } catch (Exception e) {
                        Log.d("Exception", Objects.requireNonNull(e.getLocalizedMessage()));
                    }
                } else {
                    @SuppressLint("InflateParams")
                    View layout = LayoutInflater.from(OtpVerificationActivity.this).inflate(R.layout.toast_layout, null);
                    TextView Ok = layout.findViewById(R.id.okBtn);
                    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                    TextView header = layout.findViewById(R.id.headerTv);
                    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                    TextView description = layout.findViewById(R.id.descripTv);
                    header.setText(response.message() + " - " + response.code());
                    description.setText(getString(R.string.error_msg));
                    Ok.setOnClickListener(v -> {
                        OTPVerify(text1, text2, text3, text4, text5, text6);
                    });
                    Toast toast = new Toast(OtpVerificationActivity.this);
                    toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<VerifyOTP> call, @NonNull Throwable t) {
                @SuppressLint("InflateParams")
                View layout = LayoutInflater.from(OtpVerificationActivity.this).inflate(R.layout.toast_layout, null);
                TextView Ok = layout.findViewById(R.id.okBtn);
                @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                TextView header = layout.findViewById(R.id.headerTv);
                @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                TextView description = layout.findViewById(R.id.descripTv);
                header.setText(getString(R.string.error));
                description.setText(getString(R.string.error_msg));
                Ok.setOnClickListener(v -> {
                    OTPVerify(text1, text2, text3, text4, text5, text6);
                });
                Toast toast = new Toast(OtpVerificationActivity.this);
                toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
            }
        });
    }

    private void startTimer() {
        new CountDownTimer(60000, 1000) {
            @SuppressLint("DefaultLocale")
            @Override
            public void onTick(long millisUntilFinished) {
                int totalSeconds = (int) (millisUntilFinished / 1000);
                int minutes = totalSeconds / 60;
                int seconds = totalSeconds % 60;
                binding.tvTimer.setText(String.format("(%02d:%02d)", minutes, seconds));
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onFinish() {
                binding.tvTimer.setText("(00:00)");
                binding.tvResend.setEnabled(true);
                binding.tvResend.setTextColor(getResources().getColor(R.color.blue));
            }
        }.start();
    }

    private int dpToPx(int dp) {
        return Math.round(dp * getResources().getDisplayMetrics().density);
    }

    private void startAnimation() {

        binding.otpBgImage.setScaleX(1.2f);
        binding.otpBgImage.setScaleY(1.2f);

        ObjectAnimator moveX = ObjectAnimator.ofFloat(
                binding.otpBgImage, "translationX", -40f, 40f);
        moveX.setDuration(6000);
        moveX.setRepeatCount(ValueAnimator.INFINITE);
        moveX.setRepeatMode(ValueAnimator.REVERSE);
        moveX.setInterpolator(new AccelerateDecelerateInterpolator());

        ObjectAnimator moveY = ObjectAnimator.ofFloat(
                binding.otpBgImage, "translationY", -30f, 30f);
        moveY.setDuration(6000);
        moveY.setRepeatCount(ValueAnimator.INFINITE);
        moveY.setRepeatMode(ValueAnimator.REVERSE);
        moveY.setInterpolator(new AccelerateDecelerateInterpolator());

        moveX.start();
        moveY.start();
    }


}
