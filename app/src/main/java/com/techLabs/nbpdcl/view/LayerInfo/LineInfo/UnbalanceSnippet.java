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

public class UnbalanceSnippet extends Dialog implements IsCancel {

    private final Context context;
    private SnipetLayoutBinding binding;
    private final String NetworkId;
    private final String SectionId;
    private final String CableId;
    private final String Length;
    private final JsonObject jsonObject;

    public UnbalanceSnippet(@NonNull Context context, String networkId, String sectionId, String cableId, String length, JsonObject jsonObject) {
        super(context);
        this.context = context;
        this.NetworkId = networkId;
        this.SectionId = sectionId;
        this.CableId = cableId;
        this.Length = length;
        this.jsonObject = jsonObject;
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

        binding.featuresTv.setText("Overhead Line Unbalanced");
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

        if (!CableId.isEmpty()) {
            binding.cableID.setText(CableId);
        } else {
            binding.cableID.setText("");
        }

        if (!Length.isEmpty()) {
            binding.lengthId.setText(Length);
        } else {
            binding.lengthId.setText("");
        }

        binding.imgClose.setOnClickListener(view -> {
            dismiss();
        });

        binding.moreBtn.setOnClickListener(view -> {
            UnbalanceMoreInfoDialog unbalanceMoreInfoDialog = new UnbalanceMoreInfoDialog(context, SectionId, this, NetworkId, jsonObject, CableId);
            unbalanceMoreInfoDialog.show();
        });
    }

    @Override
    public void Event(boolean isCancel) {
        if (isCancel){
            dismiss();
        }
    }
}
