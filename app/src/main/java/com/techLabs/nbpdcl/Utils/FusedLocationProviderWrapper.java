package com.techLabs.nbpdcl.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import org.osmdroid.views.overlay.mylocation.IMyLocationConsumer;
import org.osmdroid.views.overlay.mylocation.IMyLocationProvider;

public class FusedLocationProviderWrapper implements IMyLocationProvider {

    private final Context context;
    private final FusedLocationProviderClient fusedClient;

    private LocationCallback locationCallback;
    private Location lastLocation;
    private IMyLocationConsumer locationConsumer;

    public FusedLocationProviderWrapper(Context context) {
        this.context = context.getApplicationContext();
        this.fusedClient = LocationServices.getFusedLocationProviderClient(context);
    }

    @SuppressLint("MissingPermission")
    @Override
    public boolean startLocationProvider(IMyLocationConsumer consumer) {

        this.locationConsumer = consumer;

        // Permission check (PREVENT CRASH)
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return false;
        }

        // Modern LocationRequest Builder
        LocationRequest request = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY,
                1000   // 1-second update
        )
                .setMinUpdateIntervalMillis(700)     // faster when moving
                .setMaxUpdateDelayMillis(2000)       // smooth batching
                .setMinUpdateDistanceMeters(1.0f)    // 1 meter movement
                .build();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult == null) return;

                lastLocation = locationResult.getLastLocation();

                if (lastLocation != null && locationConsumer != null) {
                    locationConsumer.onLocationChanged(
                            lastLocation,
                            FusedLocationProviderWrapper.this
                    );
                }
            }
        };

        fusedClient.requestLocationUpdates(
                request,
                locationCallback,
                Looper.getMainLooper()
        );

        return true;
    }

    @Override
    public void stopLocationProvider() {
        if (locationCallback != null) {
            fusedClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    public Location getLastKnownLocation() {
        return lastLocation;
    }

    @Override
    public void destroy() {
        stopLocationProvider();
    }
}
