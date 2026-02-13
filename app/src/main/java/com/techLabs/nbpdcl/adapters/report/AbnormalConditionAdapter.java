package com.techLabs.nbpdcl.adapters.report;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.techLabs.nbpdcl.Utils.callBack.DeviceArgument;
import com.techLabs.nbpdcl.databinding.AbnormalConditionReportLayoutBinding;
import com.techLabs.nbpdcl.models.report.AbnormalReport;

import java.util.List;

public class AbnormalConditionAdapter extends RecyclerView.Adapter<AbnormalConditionAdapter.ViewHolder> {

    private Context mainContext;
    private List<AbnormalReport.Output.Datum> data;
    private DeviceArgument deviceArgument;
    private String reportName = "";

    public AbnormalConditionAdapter(Context mainContext, List<AbnormalReport.Output.Datum> data, DeviceArgument deviceArgument, String reportName) {
        this.mainContext = mainContext;
        this.data = data;
        this.deviceArgument = (DeviceArgument) deviceArgument;
        this.reportName = reportName;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        AbnormalConditionReportLayoutBinding binding = AbnormalConditionReportLayoutBinding.inflate(layoutInflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AbnormalReport.Output.Datum item = data.get(position);
        holder.bindView(item);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private AbnormalConditionReportLayoutBinding binding;

        public ViewHolder(@NonNull AbnormalConditionReportLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindView(AbnormalReport.Output.Datum item) {

            String overLoadColors = null;
            String overVoltageColors = null;
            String underVoltageColors = null;

            if (!item.getFeederIdColor().equals("NULL") && !item.getFeederIdColor().isEmpty()) {
                if (item.getFeederIdColor().equals("#FF0000") && overLoadColors == null) {
                    overLoadColors = item.getFeederIdColor();
                } else if (item.getFeederIdColor().equals("#00FF00") && overVoltageColors == null) {
                    overVoltageColors = item.getFeederIdColor();
                } else if (item.getFeederIdColor().equals("#FFFF00") && underVoltageColors == null) {
                    underVoltageColors = item.getFeederIdColor();
                }
            }

            if (!item.getSectionIdColor().equals("NULL") && !item.getSectionIdColor().isEmpty()) {
                if (item.getSectionIdColor().equals("#FF0000") && overLoadColors == null) {
                    overLoadColors = item.getSectionIdColor();
                } else if (item.getSectionIdColor().equals("#00FF00") && overVoltageColors == null) {
                    overVoltageColors = item.getSectionIdColor();
                } else if (item.getSectionIdColor().equals("#FFFF00") && underVoltageColors == null) {
                    underVoltageColors = item.getSectionIdColor();
                }
            }

            if (!item.getEqIdColor().equals("NULL") && !item.getEqIdColor().isEmpty()) {
                if (item.getEqIdColor().equals("#FF0000") && overLoadColors == null) {
                    overLoadColors = item.getEqIdColor();
                } else if (item.getEqIdColor().equals("#00FF00") && overVoltageColors == null) {
                    overVoltageColors = item.getEqIdColor();
                } else if (item.getEqIdColor().equals("#FFFF00") && underVoltageColors == null) {
                    underVoltageColors = item.getEqIdColor();
                }
            }

            if (!item.getEqCodeColor().equals("NULL") && !item.getEqCodeColor().isEmpty()) {
                if (item.getEqCodeColor().equals("#FF0000") && overLoadColors == null && overLoadColors == null) {
                    overLoadColors = item.getEqCodeColor();
                } else if (item.getEqCodeColor().equals("#00FF00") && overVoltageColors == null) {
                    overVoltageColors = item.getEqCodeColor();
                } else if (item.getEqCodeColor().equals("#FFFF00") && underVoltageColors == null) {
                    underVoltageColors = item.getEqCodeColor();
                }
            }

            if (!item.getLOADINGAColor().equals("NULL") && !item.getLOADINGAColor().isEmpty()) {
                if (item.getLOADINGAColor().equals("#FF0000") && overLoadColors == null) {
                    overLoadColors = item.getLOADINGAColor();
                } else if (item.getLOADINGAColor().equals("#00FF00") && overVoltageColors == null) {
                    overVoltageColors = item.getLOADINGAColor();
                } else if (item.getLOADINGAColor().equals("#FFFF00") && underVoltageColors == null) {
                    underVoltageColors = item.getLOADINGAColor();
                }
            }

            if (!item.getLOADINGBColor().equals("NULL") && !item.getLOADINGBColor().isEmpty()) {
                if (item.getLOADINGBColor().equals("#FF0000") && overLoadColors == null) {
                    overLoadColors = item.getLOADINGBColor();
                } else if (item.getLOADINGBColor().equals("#00FF00") && overVoltageColors == null) {
                    overVoltageColors = item.getLOADINGBColor();
                } else if (item.getLOADINGBColor().equals("#FFFF00") && underVoltageColors == null) {
                    underVoltageColors = item.getLOADINGBColor();
                }
            }

            if (!item.getLOADINGCColor().equals("NULL") && !item.getLOADINGCColor().isEmpty()) {
                if (item.getLOADINGCColor().equals("#FF0000") && overLoadColors == null) {
                    overLoadColors = item.getLOADINGCColor();
                } else if (item.getLOADINGCColor().equals("#00FF00") && overVoltageColors == null) {
                    overVoltageColors = item.getLOADINGCColor();
                } else if (item.getLOADINGCColor().equals("#FFFF00") && underVoltageColors == null) {
                    underVoltageColors = item.getLOADINGCColor();
                }
            }

            if (!item.getVAColor().equals("NULL") && !item.getVAColor().isEmpty()) {
                if (item.getVAColor().equals("#FF0000") && overLoadColors == null) {
                    overLoadColors = item.getVAColor();
                } else if (item.getVAColor().equals("#00FF00") && overVoltageColors == null) {
                    overVoltageColors = item.getVAColor();
                } else if (item.getVAColor().equals("#FFFF00") && underVoltageColors == null) {
                    underVoltageColors = item.getVAColor();
                }
            }

            if (!item.getVBColor().equals("NULL") && !item.getVBColor().isEmpty()) {
                if (item.getVBColor().equals("#FF0000") && overLoadColors == null) {
                    overLoadColors = item.getVBColor();
                } else if (item.getVBColor().equals("#00FF00") && overVoltageColors == null) {
                    overVoltageColors = item.getVBColor();
                } else if (item.getVBColor().equals("#FFFF00") && underVoltageColors == null) {
                    underVoltageColors = item.getVBColor();
                }
            }

            if (!item.getVCColor().equals("NULL") && !item.getVCColor().isEmpty()) {
                if (item.getVCColor().equals("#FF0000") && overLoadColors == null) {
                    overLoadColors = item.getVCColor();
                } else if (item.getVCColor().equals("#00FF00") && overVoltageColors == null) {
                    overVoltageColors = item.getVCColor();
                } else if (item.getVCColor().equals("#FFFF00") && underVoltageColors == null) {
                    underVoltageColors = item.getVCColor();
                }
            }

            if (item.getEqCode() != null && !item.getEqCode().isEmpty()) {
                binding.abnormalContentLayout.codeTvc.setText(item.getEqCode());
                if (item.getEqCodeColor() != null && !item.getEqCodeColor().isEmpty() && !item.getEqCodeColor().equals("NULL")) {
                    binding.abnormalContentLayout.codeTvc.setBackgroundColor(Color.parseColor(item.getEqCodeColor()));
                }
            }

            if (item.getFeederId() != null && !item.getFeederId().isEmpty()) {
                binding.abnormalContentLayout.feederIdTv.setText(item.getFeederId());
                if (item.getFeederIdColor() != null && !item.getFeederIdColor().isEmpty() && !item.getFeederIdColor().equals("NULL")) {
                    binding.abnormalContentLayout.feederIdTv.setBackgroundColor(Color.parseColor(item.getFeederIdColor()));
                }
            }

            if (item.getSectionId() != null && !item.getSectionId().isEmpty()) {
                binding.abnormalContentLayout.sectionIdTv.setText(item.getSectionId());
                if (item.getSectionIdColor() != null && !item.getSectionIdColor().isEmpty() && !item.getSectionIdColor().equals("NULL")) {
                    binding.abnormalContentLayout.sectionIdTv.setBackgroundColor(Color.parseColor(item.getSectionIdColor()));
                }
            }

            if (item.getEqId() != null && !item.getEqId().isEmpty()) {
                binding.abnormalContentLayout.equipmentIdTv.setText(item.getEqId());
                if (item.getEqIdColor() != null && !item.getEqIdColor().isEmpty() && !item.getEqIdColor().equals("NULL")) {
                    binding.abnormalContentLayout.equipmentIdTv.setBackgroundColor(Color.parseColor(item.getEqIdColor()));
                }
            }

            if (item.getEqCode() != null && !item.getEqCode().isEmpty()) {
                binding.abnormalContentLayout.codeTv.setText(item.getEqCode());
                if (item.getEqCodeColor() != null && !item.getEqCodeColor().isEmpty() && !item.getEqCodeColor().equals("NULL")) {
                    binding.abnormalContentLayout.codeTv.setBackgroundColor(Color.parseColor(item.getEqCodeColor()));
                }
            }

            if (item.getLoadinga() != null && !item.getLoadinga().isEmpty()) {
                binding.abnormalContentLayout.loadingATv.setText(item.getLoadinga());
                if (item.getLOADINGAColor() != null && !item.getLOADINGAColor().isEmpty() && !item.getLOADINGAColor().equals("NULL")) {
                    binding.abnormalContentLayout.loadingATv.setBackgroundColor(Color.parseColor(item.getLOADINGAColor()));
                }
            }

            if (item.getLoadingb() != null && !item.getLoadingb().isEmpty()) {
                binding.abnormalContentLayout.loadingBTv.setText(item.getLoadingb());
                if (item.getLOADINGBColor() != null && !item.getLOADINGBColor().isEmpty() && !item.getLOADINGBColor().equals("NULL")) {
                    binding.abnormalContentLayout.loadingBTv.setBackgroundColor(Color.parseColor(item.getLOADINGBColor()));
                }
            }

            if (item.getLoadingc() != null && !item.getLoadingc().isEmpty()) {
                binding.abnormalContentLayout.loadingCTv.setText(item.getLoadingc());
                if (item.getLOADINGCColor() != null && !item.getLOADINGCColor().isEmpty() && !item.getLOADINGCColor().equals("NULL")) {
                    binding.abnormalContentLayout.loadingCTv.setBackgroundColor(Color.parseColor(item.getLOADINGCColor()));
                }
            }

            if (item.getVa() != null && !item.getVa().isEmpty()) {
                binding.abnormalContentLayout.vaTv.setText(item.getVa());
                if (item.getVAColor() != null && !item.getVAColor().isEmpty() && !item.getVAColor().equals("NULL")) {
                    binding.abnormalContentLayout.vaTv.setBackgroundColor(Color.parseColor(item.getVAColor()));
                }
            }

            if (item.getVb() != null && !item.getVb().isEmpty()) {
                binding.abnormalContentLayout.vBTv.setText(item.getVb());
                if (item.getVBColor() != null && !item.getVBColor().isEmpty() && !item.getVBColor().equals("NULL")) {
                    binding.abnormalContentLayout.vBTv.setBackgroundColor(Color.parseColor(item.getVBColor()));
                }
            }

            if (item.getVc() != null && !item.getVc().isEmpty()) {
                binding.abnormalContentLayout.vCTv.setText(item.getVc());
                if (item.getVCColor() != null && !item.getVCColor().isEmpty() && !item.getVCColor().equals("NULL")) {
                    binding.abnormalContentLayout.vCTv.setBackgroundColor(Color.parseColor(item.getVCColor()));
                }
            }

            binding.abnormalContentLayout.viewEquipmentBtn.setOnClickListener(view -> {
                if (deviceArgument != null) {
                    if (reportName.equals("Load Flow - Abnormal conditions")) {
                        if (item.getEqCode().contains("Two-Winding Transformer")) {
                            deviceArgument.onXYCordinateSend(item.getSectionId(), "5");
                        } else if (item.getEqCode().contains("Spot Load")) {
                            deviceArgument.onXYCordinateSend(item.getSectionId(), "20");
                        } else if (item.getEqCode().contains("Breaker")) {
                            deviceArgument.onXYCordinateSend(item.getSectionId(), "8");
                        } else if (item.getEqCode().contains("Cable")) {
                            deviceArgument.onXYCordinateSend(item.getSectionId(), "1");
                        } else if (item.getEqCode().contains("Switch")) {
                            deviceArgument.onXYCordinateSend(item.getSectionId(), "13");
                        } else if (item.getEqCode().contains("Fuse")) {
                            deviceArgument.onXYCordinateSend(item.getSectionId(), "14");
                        } else if (item.getEqCode().contains("OverHead")) {
                            deviceArgument.onXYCordinateSend(item.getSectionId(), "2");
                        } else if (item.getEqCode().contains("ShuntCapacitor")) {
                            deviceArgument.onXYCordinateSend(item.getSectionId(), "61");
                        } else {
                            deviceArgument.onXYCordinateSend(item.getSectionId(), "23");
                        }
                    }
                }
            });

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(List<AbnormalReport.Output.Datum> list) {
        data = list;
        notifyDataSetChanged();
    }

}
