package com.techLabs.nbpdcl.view.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.techLabs.nbpdcl.Utils.PrefManager;
import com.techLabs.nbpdcl.databinding.ActivityMainBinding;

import java.util.ArrayList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
public class MainActivity extends AppCompatActivity {

    private static final int MULTIPLE_PERMISSIONS = 10;
    private static final String[] PERMISSIONS_BELOW_13 = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private static final String[] PERMISSIONS_13_PLUS = {
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.ACCESS_FINE_LOCATION
    };
    private final ActivityResultLauncher<Intent> settingsLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> checkPermissions()
            );
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.getStart.setOnClickListener(v -> {
            if (checkPermissions()) {
                proceedNext();
            }
        });

    }

    private boolean checkPermissions() {
        String[] permissions = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                ? PERMISSIONS_13_PLUS
                : PERMISSIONS_BELOW_13;

        List<String> needed = new ArrayList<>();

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                needed.add(permission);
            }
        }

        if (!needed.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this,
                    needed.toArray(new String[0]),
                    MULTIPLE_PERMISSIONS
            );
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == MULTIPLE_PERMISSIONS) {
            if (allPermissionsGranted(grantResults)) {
                proceedNext();
            } else {
                openAppSettings();
            }
        }
    }

    private boolean allPermissionsGranted(int[] results) {
        for (int r : results) {
            if (r != PackageManager.PERMISSION_GRANTED) return false;
        }
        return true;
    }

    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", getPackageName(), null));
        settingsLauncher.launch(intent);
    }

    private void proceedNext() {
        new PrefManager(this).setIsFirstTimeUser(true);
        startActivity(new Intent(this, SplashScreen.class));
        finish();
    }

}