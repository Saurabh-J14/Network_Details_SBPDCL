package com.techLabs.nbpdcl.view.LayerInfo.LineInfo;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;

import com.techLabs.nbpdcl.R;
import com.techLabs.nbpdcl.Utils.callBack.IsCancel;
import com.techLabs.nbpdcl.databinding.SnipetLayoutBinding;
import com.google.gson.JsonObject;

public class  OverheadSnippet extends Dialog implements IsCancel {

    private SnipetLayoutBinding binding;
    private final Context context;
    private final String NetworkId;
    private final String SectionId;
    private final String lineId;
    private final String Length;
    private final JsonObject jsonObject;
    private final String voltage;

    public OverheadSnippet(@NonNull Context context, String networkId, String sectionId, String lineId, String length, String voltage, JsonObject jsonObject) {
        super(context);
        this.context = context;
        this.NetworkId = networkId;
        this.SectionId = sectionId;
        this.lineId = lineId;
        this.Length = length;
        this.jsonObject = jsonObject;
        this.voltage = voltage;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SnipetLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        View MainLayoutBackGround = getWindow().getDecorView().getRootView();
        MainLayoutBackGround.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        MainLayoutBackGround.setBackgroundResource(R.drawable.pop_background);

        binding.featuresTv.setText("OverHead Line Balanced");
        binding.cableTV.setText("Line ID");

        if (!NetworkId.isEmpty()) {
            binding.networkId.setText(NetworkId);
        } else {
            binding.networkId.setText("");
        }

        if (!SectionId.isEmpty()) {
            binding.sectionId.setText(SectionId);
        } else {
            binding.sectionId.setText("");
        }

        if (!lineId.isEmpty()) {
            binding.cableID.setText(lineId);
        } else {
            binding.cableID.setText(lineId);
        }

        if (!Length.isEmpty()) {
            binding.lengthId.setText(Length + " " + "m");
        } else {
            binding.lengthId.setText("");
        }

        binding.imgClose.setOnClickListener(view -> {
            dismiss();
        });

        binding.moreBtn.setOnClickListener(view -> {
            OverHeadMoreInfoDialog overHeadMoreInfoDialog = new OverHeadMoreInfoDialog(context, this, jsonObject, NetworkId, SectionId, voltage);
            overHeadMoreInfoDialog.show();
        });
    }

    @Override
    public void Event(boolean isCancel) {
        if (isCancel){
            dismiss();
        }
    }
}
