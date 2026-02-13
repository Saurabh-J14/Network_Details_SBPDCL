package com.techLabs.nbpdcl.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.techLabs.nbpdcl.databinding.SelectedNetworkLayoutBinding;

import java.util.ArrayList;
import java.util.List;

public class SelectedNetworkAdapter extends RecyclerView.Adapter<SelectedNetworkAdapter.ViewHolder> {

    private List<String> selectedItems;
    private Context context;
    private OnItemRemovedListener onItemRemovedListener;
    private OnDataChangedListener onDataChangedListener;

    public interface OnItemRemovedListener {
        void onItemRemoved(String item);
    }

    public interface OnDataChangedListener {
        void onDataChanged(int itemCount);
    }

    public SelectedNetworkAdapter(Context context, List<String> selectedItems) {
        this.context = context;
        this.selectedItems = selectedItems;
    }

    public void setOnItemRemovedListener(OnItemRemovedListener listener) {
        this.onItemRemovedListener = listener;
    }

    public void setOnDataChangedListener(OnDataChangedListener listener) {
        this.onDataChangedListener = listener;
    }

    @NonNull
    @Override
    public SelectedNetworkAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        SelectedNetworkLayoutBinding binding = SelectedNetworkLayoutBinding.inflate(layoutInflater, parent, false);
        return new ViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull SelectedNetworkAdapter.ViewHolder holder, int position) {
        String item = selectedItems.get(position);
        holder.bindView(item);
    }

    @Override
    public int getItemCount() {
        return selectedItems.size();
    }

    public void addFeeder(String feederId) {
        if (!selectedItems.contains(feederId)) {
            selectedItems.add(feederId);
            notifyItemInserted(selectedItems.size() - 1);
            if (onDataChangedListener != null) {
                onDataChangedListener.onDataChanged(getItemCount());
            }
        }
    }

    public List<String> getSelectedItems() {
        return new ArrayList<>(selectedItems);
    }

    public void removeFeeder(String feederId) {
        int position = selectedItems.indexOf(feederId);
        if (position != -1) {
            selectedItems.remove(position);
            notifyItemRemoved(position);
            if (onDataChangedListener != null) {
                onDataChangedListener.onDataChanged(getItemCount());
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void clear() {
        selectedItems.clear();
        notifyDataSetChanged();
        if (onDataChangedListener != null) {
            onDataChangedListener.onDataChanged(getItemCount());
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private SelectedNetworkLayoutBinding binding;

        public ViewHolder(@NonNull SelectedNetworkLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        @SuppressLint("NotifyDataSetChanged")
        public void bindView(String item) {
            binding.networkId.setText(item);
            binding.checkBox.setChecked(true);

            binding.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (!isChecked) {
                    selectedItems.remove(item);
                    notifyDataSetChanged();
                    if (onItemRemovedListener != null) {
                        onItemRemovedListener.onItemRemoved(item);
                    }
                    if (onDataChangedListener != null) {
                        onDataChangedListener.onDataChanged(getItemCount());
                    }
                }
            });

        }
    }
}
