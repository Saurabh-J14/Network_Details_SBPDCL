package com.techLabs.nbpdcl.view.fragment.deviceInfo;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.techLabs.nbpdcl.databinding.FragmentMeterInfoBinding;

public class MeterFragment extends Fragment {
    private FragmentMeterInfoBinding binding;
    
    public MeterFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMeterInfoBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if(args!= null){
            if(args.getString("meterIndex") != null && !args.getString("meterIndex").equals("None")){
                binding.meterIndex.setText(args.getString("meterIndex"));
            }
            if(args.getString("refTime") != null && !args.getString("refTime").equals("None")){
                binding.referenceTime.setText(args.getString("refTime"));
            }
            if(args.getString("demTp0Val1A") != null && !args.getString("demTp0Val1A").equals("0.0")){
                binding.aKwTv.setText(args.getString("demTp0Val1A"));
            }
            if(args.getString("demTp0Val2A") != null && !args.getString("demTp0Val2A").equals("0.0")){
                binding.aKvarTv.setText(args.getString("demTp0Val2A"));
            }
            if(args.getString("demTp0Val1B") != null && !args.getString("demTp0Val1B").equals("0.0")){
                binding.bKwTv.setText(args.getString("demTp0Val1B"));
            }
            if(args.getString("demTp0Val2B") != null && !args.getString("demTp0Val2B").equals("0.0")){
                binding.bKvarTv.setText(args.getString("demTp0Val2B"));
            }
            if(args.getString("demTp0Val1C") != null && !args.getString("demTp0Val1C").equals("0.0")){
                binding.cKwTv.setText(args.getString("demTp0Val1C"));
            }
            if(args.getString("demTp0Val2C") != null && !args.getString("demTp0Val2C").equals("0.0")){
                binding.cKvarTv.setText(args.getString("demTp0Val2C"));
            }

            if(args.getString("demTpVal1A") != null && !args.getString("demTpVal1A").equals("0.0")){
                binding.aKwTv.setText(args.getString("demTpVal1A"));
            }
            if(args.getString("demTpVal2A") != null && !args.getString("demTpVal2A").equals("0.0")){
                binding.aKvarTv.setText(args.getString("demTpVal2A"));
            }
            if(args.getString("demTpVal1B") != null && !args.getString("demTpVal1B").equals("0.0")){
                binding.bKwTv.setText(args.getString("demTpVal1B"));
            }
            if(args.getString("demTpVal2B") != null && !args.getString("demTpVal2B").equals("0.0")){
                binding.bKvarTv.setText(args.getString("demTpVal2B"));
            }
            if(args.getString("demTpVal1C") != null && !args.getString("demTpVal1C").equals("0.0")){
                binding.cKwTv.setText(args.getString("demTpVal1C"));
            }
            if(args.getString("demTpVal2C") != null && !args.getString("demTpVal2C").equals("0.0")){
                binding.cKvarTv.setText(args.getString("demTpVal2C"));
            }

            if(args.getString("demTp3Val1A") != null && !args.getString("demTp3Val1A").equals("0.0")){
                binding.aKwTv.setText(args.getString("demTp3Val1A"));
            }
            if(args.getString("demTp3Val2A") != null && !args.getString("demTp3Val2A").equals("0.0")){
                binding.aKvarTv.setText(args.getString("demTp3Val2A"));
            }
            if(args.getString("demTp3Val1B") != null && !args.getString("demTp3Val1B").equals("0.0")){
                binding.bKwTv.setText(args.getString("demTp3Val1B"));
            }
            if(args.getString("demTp3Val2B") != null && !args.getString("demTp3Val2B").equals("0.0")){
                binding.bKvarTv.setText(args.getString("demTp3Val2B"));
            }
            if(args.getString("demTp3Val1C") != null && !args.getString("demTp3Val1C").equals("0.0")){
                binding.cKwTv.setText(args.getString("demTp3Val1C"));
            }
            if(args.getString("demTp3Val2C") != null && !args.getString("demTp3Val2C").equals("0.0")){
                binding.cKvarTv.setText(args.getString("demTp3Val2C"));
            }

            if(args.getString("aKwTv") != null && !args.getString("aKwTv").equals("0.0")){
                binding.aKwTv.setText(args.getString("aKwTv"));
            }
            if(args.getString("aKvarTv") != null && !args.getString("aKvarTv").equals("0.0")){
                binding.aKvarTv.setText(args.getString("aKvarTv"));
            }
            if(args.getString("bKwTv") != null && !args.getString("bKwTv").equals("0.0")){
                binding.bKwTv.setText(args.getString("bKwTv"));
            }
            if(args.getString("bKvarTv") != null && !args.getString("bKvarTv").equals("0.0")){
                binding.bKvarTv.setText(args.getString("bKvarTv"));
            }
            if(args.getString("cKwTv") != null && !args.getString("cKwTv").equals("0.0")){
                binding.cKwTv.setText(args.getString("cKwTv"));
            }
            if(args.getString("cKvarTv") != null && !args.getString("cKvarTv").equals("0.0")){
                binding.cKvarTv.setText(args.getString("cKvarTv"));
            }
        }

    }
}