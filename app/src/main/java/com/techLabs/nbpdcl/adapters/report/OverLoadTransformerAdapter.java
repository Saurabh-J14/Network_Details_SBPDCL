package com.techLabs.nbpdcl.adapters.report;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.techLabs.nbpdcl.Utils.callBack.DeviceArgument;
import com.techLabs.nbpdcl.databinding.OverloadTransformerLayoutBinding;
import com.techLabs.nbpdcl.models.report.OverLoadTransformerReport;

import java.util.List;

public class OverLoadTransformerAdapter extends RecyclerView.Adapter<OverLoadTransformerAdapter.ViewHolder> {

    private Context mainContext;
    private List<OverLoadTransformerReport.Output.Datum> data;
    private DeviceArgument deviceArgument;
    private String reportName = "";

    public OverLoadTransformerAdapter(Context mainContext, List<OverLoadTransformerReport.Output.Datum> data, DeviceArgument deviceArgument, String reportName) {
        this.mainContext = mainContext;
        this.data = data;
        this.deviceArgument = deviceArgument;
        this.reportName = reportName;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        OverloadTransformerLayoutBinding binding = OverloadTransformerLayoutBinding.inflate(layoutInflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (data.size() == 0) {
            holder.bindViews();
        } else {
            OverLoadTransformerReport.Output.Datum item = data.get(position);
            holder.bindView(item);
        }
    }

    @Override
    public int getItemCount() {
        if (data.size() == 0) {
            return 1;
        } else {
            return data.size();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private OverloadTransformerLayoutBinding binding;

        public ViewHolder(@NonNull OverloadTransformerLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindView(OverLoadTransformerReport.Output.Datum item) {

            binding.constraintLayout.setVisibility(View.VISIBLE);
            binding.noDataTvLayout.setVisibility(View.GONE);

            if (item.getEqNo() != null && !item.getEqNo().isEmpty()) {
                binding.equipIdNoTv.setText(item.getEqNo());
                if (item.getEqNoColor() != null && !item.getEqNoColor().isEmpty() && !item.getEqNoColor().equals("NULL")) {
                    binding.equipIdNoTv.setBackgroundColor(Color.parseColor(item.getEqNoColor()));
                }
            }

            if (item.getFromNodeId() != null && !item.getFromNodeId().isEmpty()) {
                binding.fromNodeIdTv.setText(item.getFromNodeId());
                if (item.getFromNodeIdColor() != null && !item.getFromNodeIdColor().isEmpty() && !item.getFromNodeIdColor().equals("NULL")) {
                    binding.fromNodeIdTv.setBackgroundColor(Color.parseColor(item.getFromNodeIdColor()));
                }
            }

            if (item.getToNodeId() != null && !item.getToNodeId().isEmpty()) {
                binding.toNodeIdTv.setText(item.getToNodeId());
                if (item.getToNodeIdColor() != null && !item.getToNodeIdColor().isEmpty() && !item.getToNodeIdColor().equals("NULL")) {
                    binding.toNodeIdTv.setBackgroundColor(Color.parseColor(item.getToNodeIdColor()));
                }
            }

            if (item.getMvatot() != null && !item.getMvatot().isEmpty()) {
                binding.mvatotTv.setText(item.getMvatot());
                if (item.getMVATOTColor() != null && !item.getMVATOTColor().isEmpty() && !item.getMVATOTColor().equals("NULL")) {
                    binding.mvatotTv.setBackgroundColor(Color.parseColor(item.getMVATOTColor()));
                }
            }

            if (item.getLoading() != null && !item.getLoading().isEmpty()) {
                binding.loadingTv.setText(item.getLoading());
                if (item.getLOADINGColor() != null && !item.getLOADINGColor().isEmpty() && !item.getLOADINGColor().equals("NULL")) {
                    binding.loadingTv.setBackgroundColor(Color.parseColor(item.getLOADINGColor()));
                }
            }

            binding.viewEquipmentBtn.setOnClickListener(view -> {
                if (deviceArgument != null) {
                    if (reportName.equals("Load Flow - Overloaded transformers")) {
                        deviceArgument.onXYCordinateSend(item.getFromNodeId(), "41");
                    }
                }
            });

        }

        public void bindViews() {
            binding.mainLayout.setBackgroundColor(Color.TRANSPARENT);
            binding.constraintLayout.setVisibility(View.GONE);
            binding.noDataTvLayout.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(List<OverLoadTransformerReport.Output.Datum> list) {
        data = list;
        notifyDataSetChanged();
    }

}
