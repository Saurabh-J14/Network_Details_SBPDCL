package com.techLabs.nbpdcl.view.fragment.deviceInfo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.techLabs.nbpdcl.databinding.FragmentNodeDetailBinding;

public class NodeDetailFragment extends Fragment {
    private FragmentNodeDetailBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentNodeDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.idFromNodesTv.setEnabled(false);
        binding.xFromNodesTv.setEnabled(false);
        binding.yFromNodesTv.setEnabled(false);
        binding.idToNodeTv.setEnabled(false);
        binding.xToNodeTv.setEnabled(false);
        binding.yToNodesTv.setEnabled(false);
        binding.corTypeChk.setEnabled(false);

        Bundle args = getArguments();
        if (args != null) {
            if (!args.getString("toX").equals("None")) {
                binding.xToNodeTv.setText(args.getString("toX"));
            }

            if (!args.getString("toY").equals("None")) {
                binding.yToNodesTv.setText(args.getString("toY"));
            }

            if (!args.getString("toNodeID").equals("None")) {
                binding.idToNodeTv.setText(args.getString("toNodeID"));
            }

            if (!args.getString("fromX").equals("None")) {
                binding.xFromNodesTv.setText(args.getString("fromX"));
            }

            if (!args.getString("fromY").equals("None")) {
                binding.yFromNodesTv.setText(args.getString("fromY"));
            }

            if (!args.getString("fromNodeID").equals("None")) {
                binding.idFromNodesTv.setText(args.getString("fromNodeID"));
            }

            if (!args.getString("toNodeID").equals("None") || !args.getString("fromNodeID").equals("None")) {
                binding.corTypeChk.setChecked(true);
            } else {
                binding.corTypeChk.setChecked(false);
            }
        }



    }

}
