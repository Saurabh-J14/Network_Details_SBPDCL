package com.techLabs.nbpdcl.adapters.Source;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.techLabs.nbpdcl.view.fragment.SourceFragment;
import com.techLabs.nbpdcl.view.fragment.SourceNetworkFragment;

public class SourceAdapter extends FragmentStateAdapter {
    private final Context context;
    private final String nodeId;
    private final String latitude;
    private final String longitude;
    private final String utX;
    private final String utY;

    public SourceAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle, Context context, String nodeId, String latitude, String longitude, String utX, String utY) {
        super(fragmentManager, lifecycle);
        this.context = context;
        this.nodeId = nodeId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.utX = utX;
        this.utY = utY;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                SourceNetworkFragment sourceNetworkFragment = new SourceNetworkFragment();
                Bundle bundle1 = new Bundle();
                bundle1.putString("NodeId", nodeId);
                bundle1.putString("DeviceType", "43");
                sourceNetworkFragment.setArguments(bundle1);
                return sourceNetworkFragment;

            case 1:
                SourceFragment sourceFragment = new SourceFragment();
                Bundle bundle = new Bundle();
                bundle.putString("NodeId", nodeId);
                bundle.putString("DeviceType", "43");
                bundle.putString("NodeIdX", utX);
                bundle.putString("NodeIdY", utY);
                bundle.putString("Latitude", latitude);
                bundle.putString("Longitude", longitude);
                sourceFragment.setArguments(bundle);
                return sourceFragment;
            default:
                return null;
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }

}