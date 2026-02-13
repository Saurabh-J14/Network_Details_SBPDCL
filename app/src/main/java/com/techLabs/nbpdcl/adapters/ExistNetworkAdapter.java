package com.techLabs.nbpdcl.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.techLabs.nbpdcl.databinding.ExistNetworkListItemBinding;
import com.techLabs.nbpdcl.models.ExistNetworkModel;
import com.techLabs.nbpdcl.view.activity.MapActivity;

import java.util.ArrayList;
import java.util.List;

public class ExistNetworkAdapter extends RecyclerView.Adapter<ExistNetworkAdapter.ViewHolder> {

    private List<ExistNetworkModel.Result> list;

    private List<ExistNetworkModel.Result> updateList;
    private Context mainContext;


    public ExistNetworkAdapter(Context mainContext, List<ExistNetworkModel.Result> list) {
        this.mainContext = mainContext;
        this.list = new ArrayList<>(list);
        this.updateList = new ArrayList<>(list);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ExistNetworkListItemBinding binding = ExistNetworkListItemBinding.inflate(inflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExistNetworkModel.Result result = list.get(position);
        holder.bindView(result);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filter(String text) {
        list.clear();
        if (text.isEmpty()) {
            list.addAll(updateList);
        } else {
            text = text.toLowerCase().replaceAll("\\s+", "");
            for (ExistNetworkModel.Result result : updateList) {
                if (result.getNetworkId() != null && result.getNetworkId().toLowerCase().replaceAll("\\s+", "").contains(text)) {
                    list.add(result);
                }
            }
        }
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final ExistNetworkListItemBinding binding;

        public ViewHolder(ExistNetworkListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindView(ExistNetworkModel.Result result) {
            try {
                if (result.getNetworkId() != null && !result.getNetworkId().isEmpty()) {
                    binding.feederIdTv.setText(result.getNetworkId());
                } else {
                    binding.feederIdTv.setText("");
                }

                if (result.getGroup1() != null && !result.getGroup1().isEmpty()) {
                    binding.group1.setText(result.getGroup1());
                } else {
                    binding.group1.setText("");
                }

                if (result.getGroup2() != null && !result.getGroup2().isEmpty()) {
                    binding.group2.setText(result.getGroup2());
                } else {
                    binding.group2.setText("");
                }

                if (result.getGroup3() != null && !result.getGroup3().isEmpty()) {
                    binding.group3.setText(result.getGroup3());
                } else {
                    binding.group3.setText("");
                }

                if (result.getGroup4() != null && !result.getGroup4().isEmpty()) {
                    binding.group4.setText(result.getGroup4());
                } else {
                    binding.group4.setText("");
                }

                if (result.getGroup5() != null && !result.getGroup5().isEmpty()) {
                    binding.group5.setText(result.getGroup5());
                } else {
                    binding.group5.setText("");
                }

                if (result.getGroup6() != null && !result.getGroup6().isEmpty()) {
                    binding.group6.setText(result.getGroup6());
                } else {
                    binding.group6.setText("");
                }

                binding.loadNetworkBtn.setOnClickListener(v -> {
                    Intent intent = new Intent(mainContext, MapActivity.class);
                    ArrayList<String> list1 = new ArrayList<>();
                    list1.add(result.getNetworkId());
                    intent.putStringArrayListExtra("NetworkId", list1);
                    intent.putExtra("Type", "ExistNetwork");
                    mainContext.startActivity(intent);
                });

            } catch (Exception e) {
                e.getLocalizedMessage();
            }
        }
    }
}
