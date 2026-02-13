package com.techLabs.nbpdcl.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.techLabs.nbpdcl.R;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends ArrayAdapter<String> {
    private List<String> allItems;
    private List<String> selectedItems;
    private List<String> unselectedItems;
    private Context context;
    private OnItemSelectedListener listener;

    public interface OnItemSelectedListener {
        void onItemSelected(String item, boolean isSelected);
    }

    public CustomAdapter(Context context, List<String> items) {
        super(context, 0, items);
        this.context = context;
        this.allItems = new ArrayList<>(items);
        this.selectedItems = new ArrayList<>();
        this.unselectedItems = new ArrayList<>(items);
    }

    public void setOnItemSelectedListener(OnItemSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return selectedItems.size() + unselectedItems.size();
    }

    @Override
    public String getItem(int position) {
        if (position < selectedItems.size()) {
            return selectedItems.get(position);
        } else {
            return unselectedItems.get(position - selectedItems.size());
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.custom_spinner_checkbox, parent, false);
        }

        CheckBox checkBox = convertView.findViewById(R.id.cBox);
        TextView textView = convertView.findViewById(R.id.netId);

        String item = getItem(position);
        textView.setText(item);

        checkBox.setOnCheckedChangeListener(null);
        checkBox.setChecked(selectedItems.contains(item));

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!selectedItems.contains(item)) {
                    selectedItems.add(item);
                    unselectedItems.remove(item);
                }
            } else {
                selectedItems.remove(item);
                unselectedItems.add(item);
            }

            notifyDataSetChanged(); // Refresh list with new order
            if (listener != null) {
                listener.onItemSelected(item, isChecked);
            }
        });

        convertView.setOnClickListener(v -> checkBox.performClick());
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    public void clearSelection() {
        selectedItems.clear();
        unselectedItems = new ArrayList<>(allItems);
        notifyDataSetChanged();
    }
}