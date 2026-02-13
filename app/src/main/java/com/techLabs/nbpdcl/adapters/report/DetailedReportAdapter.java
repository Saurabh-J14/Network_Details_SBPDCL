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
import com.techLabs.nbpdcl.databinding.DetailedReportLayoutBinding;
import com.techLabs.nbpdcl.models.report.DetailedReport;

import java.util.List;

public class DetailedReportAdapter extends RecyclerView.Adapter<DetailedReportAdapter.ViewHolder> {

    private Context mainContext;
    private List<DetailedReport.Output.Datum> data;
    private DeviceArgument deviceArgument;
    private String reportName = "";

    public DetailedReportAdapter(Context mainContext, List<DetailedReport.Output.Datum> detailReportList, DeviceArgument deviceArgument, String reportName) {
        this.mainContext = mainContext;
        this.data = detailReportList;
        this.deviceArgument = (DeviceArgument) deviceArgument;
        this.reportName = reportName;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        DetailedReportLayoutBinding binding = DetailedReportLayoutBinding.inflate(layoutInflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (data.size() == 0) {
            holder.bindViews();
        } else {
            DetailedReport.Output.Datum item = data.get(position);
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
        private DetailedReportLayoutBinding binding;

        public ViewHolder(@NonNull DetailedReportLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bindView(DetailedReport.Output.Datum item) {

            binding.constraintLayout.setVisibility(View.VISIBLE);
            binding.noDataTvLayout.setVisibility(View.GONE);

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
                if (item.getEqCodeColor().equals("#FF0000") && overLoadColors == null) {
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

            if (!item.getKWAColor().equals("NULL") && !item.getKWAColor().isEmpty()) {
                if (item.getKWAColor().equals("#FF0000") && overLoadColors == null) {
                    overLoadColors = item.getKWAColor();
                } else if (item.getKWAColor().equals("#00FF00") && overVoltageColors == null) {
                    overVoltageColors = item.getKWAColor();
                } else if (item.getKWAColor().equals("#FFFF00") && underVoltageColors == null) {
                    underVoltageColors = item.getKWAColor();
                }
            }

            if (!item.getKVARAColor().equals("NULL") && !item.getKVARAColor().isEmpty()) {
                if (item.getKVARAColor().equals("#FF0000") && overLoadColors == null) {
                    overLoadColors = item.getKVARAColor();
                } else if (item.getKVARAColor().equals("#00FF00") && overVoltageColors == null) {
                    overVoltageColors = item.getKVARAColor();
                } else if (item.getKVARAColor().equals("#FFFF00") && underVoltageColors == null) {
                    underVoltageColors = item.getKVARAColor();
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

            if (overLoadColors != null) {
                binding.overloadColorTv.setBackgroundColor(Color.parseColor(overLoadColors));
            }

            if (overVoltageColors != null) {
                binding.overVoltageColorTv.setBackgroundColor(Color.parseColor(overVoltageColors));
            }

            if (underVoltageColors != null) {
                binding.underVoltageColorTv.setBackgroundColor(Color.parseColor(underVoltageColors));
            }

            if (item.getEqCode() != null && !item.getEqCode().isEmpty()) {
                binding.codeTv.setText(item.getEqCode());
            }

            if (item.getFeederId() != null && !item.getFeederId().isEmpty()) {
                binding.feederIdTv.setText(item.getFeederId());
                if (item.getFeederIdColor() != null && !item.getFeederIdColor().isEmpty() && !item.getFeederIdColor().equals("NULL")) {
                    binding.feederIdTv.setBackgroundColor(Color.parseColor(item.getFeederIdColor()));
                }
            }

            if (item.getSectionId() != null && !item.getSectionId().isEmpty()) {
                binding.sectionIdTv.setText(item.getSectionId());
                if (item.getSectionIdColor() != null && !item.getSectionIdColor().isEmpty() && !item.getSectionIdColor().equals("NULL")) {
                    binding.sectionIdTv.setBackgroundColor(Color.parseColor(item.getSectionIdColor()));
                }
            }

            if (item.getEqId() != null && !item.getEqId().isEmpty()) {
                binding.equipmentIdTv.setText(item.getEqId());
                if (item.getEqIdColor() != null && !item.getEqIdColor().isEmpty() && !item.getEqIdColor().equals("NULL")) {
                    binding.equipmentIdTv.setBackgroundColor(Color.parseColor(item.getEqIdColor()));
                }
            }

            if (item.getLoadinga() != null && !item.getLoadinga().isEmpty()) {
                binding.loadingATv.setText(item.getLoadinga());
                if (item.getLOADINGAColor() != null && !item.getLOADINGAColor().isEmpty() && !item.getLOADINGAColor().equals("NULL")) {
                    binding.loadingATv.setBackgroundColor(Color.parseColor(item.getLOADINGAColor()));
                }
            }

            if (item.getKwa() != null && !item.getKwa().isEmpty()) {
                binding.kwaTv.setText(item.getEqCode());
                if (item.getKWAColor() != null && !item.getKWAColor().isEmpty() && !item.getKWAColor().equals("NULL")) {
                    binding.kwaTv.setBackgroundColor(Color.parseColor(item.getKWAColor()));
                }
            }

            if (item.getKvara() != null && !item.getKvara().isEmpty()) {
                binding.kvaraTv.setText(item.getKvara());
                if (item.getKVARAColor() != null && !item.getKVARAColor().isEmpty() && !item.getKVARAColor().equals("NULL")) {
                    binding.kvaraTv.setBackgroundColor(Color.parseColor(item.getKVARAColor()));
                }
            }

            if (item.getVa() != null && !item.getVa().isEmpty()) {
                binding.vaTv.setText(item.getVa());
                if (item.getVAColor() != null && !item.getVAColor().isEmpty() && !item.getVAColor().equals("NULL")) {
                    binding.vaTv.setBackgroundColor(Color.parseColor(item.getVAColor()));
                }
            }

            binding.viewEquipmentBtn.setOnClickListener(view -> {
                if (deviceArgument != null) {
                    if (reportName.equals("Load Flow - Detailed")) {
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

        public void bindViews() {
            binding.mainLayout.setBackgroundColor(Color.TRANSPARENT);
            binding.constraintLayout.setVisibility(View.GONE);
            binding.noDataTvLayout.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(List<DetailedReport.Output.Datum> list) {
        data = list;
        notifyDataSetChanged();
    }

}
