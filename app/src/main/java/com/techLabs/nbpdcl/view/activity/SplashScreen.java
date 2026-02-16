package com.techLabs.nbpdcl.view.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;

import androidx.appcompat.app.AppCompatActivity;

import com.techLabs.nbpdcl.Utils.PrefManager;
import com.techLabs.nbpdcl.databinding.ActivitySplashScreenBinding;

@SuppressLint("CustomSplashScreen")
public class SplashScreen extends AppCompatActivity {
    private ActivitySplashScreenBinding binding;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        PrefManager prefManager = new PrefManager(SplashScreen.this);
        boolean isFirstTimeUser = prefManager.getIsFirstTimeUser();

        if (!isFirstTimeUser) {
            startActivity(new Intent(SplashScreen.this, MainActivity.class));
            SplashScreen.this.finish();
            finish();
        } else {
            boolean hasLoggedIn = prefManager.getIsUserLogin();
            if (!hasLoggedIn) {
                startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                finish();
            } else {
                ScaleAnimation scaleAnimation = new ScaleAnimation(
                        1.0f, 1.2f,
                        1.0f, 1.2f,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF, 0.5f
                );
                scaleAnimation.setDuration(500);
                scaleAnimation.setRepeatMode(Animation.REVERSE);
                scaleAnimation.setRepeatCount(Animation.INFINITE);
                binding.imgMainLogo.startAnimation(scaleAnimation);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(SplashScreen.this, ExistNetworkActivity.class));
                        finish();
                    }
                }, 2000);

            }
        }
    }

}