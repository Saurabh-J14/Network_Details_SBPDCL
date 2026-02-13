package com.techLabs.nbpdcl.view.LayerInfo.DeviceInfo;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.techLabs.nbpdcl.R;
import com.techLabs.nbpdcl.adapters.CustAdapter.CustomersAdapter;
import com.techLabs.nbpdcl.databinding.CustmbtnLayoutBinding;
import com.techLabs.nbpdcl.models.device.SpotLoad;

import java.util.List;

public class CustomerDetailsDialog extends Dialog {
    private CustmbtnLayoutBinding binding;
    private final Context mainContext;
    private CustomersAdapter customersAdapter;
    private final String[] formatList = {"kVA & PF", "kW & kvar", "kW & PF"};
    private final String[] loadList = {"DEFAULT"};
    private List<SpotLoad.Output.CustomerData> customerData;

    public CustomerDetailsDialog(@NonNull Context context, List<SpotLoad.Output.CustomerData> customerData) {
        super(context);
        this.mainContext = context;
        this.customerData = customerData;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = CustmbtnLayoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ArrayAdapter<String> loadAdapter = new ArrayAdapter<>(mainContext, R.layout.custom_spinner, loadList);
        binding.loadModelSpinner.setAdapter(loadAdapter);

        ArrayAdapter<String> formatAdapter = new ArrayAdapter<>(mainContext, R.layout.custom_spinner, formatList);
        binding.formatSpinner.setAdapter(formatAdapter);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(mainContext));
        if (customerData != null && !customerData.isEmpty()) {
            customersAdapter = new CustomersAdapter(mainContext, customerData);
            binding.recyclerView.setAdapter(customersAdapter);
        } else {
            Toast.makeText(mainContext, "No customer data to display", Toast.LENGTH_SHORT).show();
        }


        binding.imgClose.setOnClickListener(v ->
                dismiss()
        );

     /*   if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(mainContext)) {
            getCustomerDetails();
        } else {
            final Dialog dialog = new Dialog(mainContext);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.no_internet_dialog);
            Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(mainContext.getDrawable(R.drawable.pop_background));
            LottieAnimationView lottieAnimationView = dialog.findViewById(R.id.animation_view);
            Button RetryBtn = dialog.findViewById(R.id.btnDialog);
            lottieAnimationView.playAnimation();
            RetryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (ResponseDataUtils.checkInternetConnectionAndInternetAccess(mainContext)) {
                        getCustomerDetails();
                        dialog.dismiss();
                    }
                }
            });
            dialog.setCanceledOnTouchOutside(false);
            dialog.setCancelable(false);
            dialog.show();
        }*/

    }

   /* private void getCustomerDetails() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("DeviceNumber", deviceNumber);
        jsonObject.addProperty("DeviceType", "20");
        jsonObject.addProperty("UserType", prefManager.getUserType());
        jsonObject.addProperty("CYMDBNET", prefManager.getDBName());
        Retrofit retrofit = RetrofitClient.getClient(mainContext);
        ApiInterface apiInterface = retrofit.create(ApiInterface.class);
        Call<SpotLoad> call = apiInterface.getSpotLoadData(jsonObject);
        call.enqueue(new Callback<SpotLoad>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call<SpotLoad> call, @NonNull Response<SpotLoad> response) {
                if (response.code() == 200) {
                    SpotLoad spotLoad = response.body();
                    assert spotLoad != null;
                    binding.recyclerView.setLayoutManager(new LinearLayoutManager(mainContext));
                    customersAdapter = new CustomersAdapter(mainContext, spotLoad.getOutput().get(0).getCustomerData(), spotLoad.getOutput().get(0).getPhase());
                    binding.recyclerView.setAdapter(customersAdapter);
                } else {
                    @SuppressLint("InflateParams")
                    View layout = LayoutInflater.from(mainContext).inflate(R.layout.toast_layout, null);
                    TextView Ok = layout.findViewById(R.id.okBtn);
                    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                    TextView header = layout.findViewById(R.id.headerTv);
                    @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                    TextView description = layout.findViewById(R.id.descripTv);
                    header.setText(response.message() + " - " + response.code());
                    description.setText(mainContext.getString(R.string.error_msg));
                    Toast toast = new Toast(mainContext);
                    toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                    toast.setDuration(Toast.LENGTH_LONG);
                    toast.setView(layout);
                    toast.show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<SpotLoad> call, @NonNull Throwable t) {
                @SuppressLint("InflateParams")
                View layout = LayoutInflater.from(mainContext).inflate(R.layout.toast_layout, null);
                TextView Ok = layout.findViewById(R.id.okBtn);
                @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                TextView header = layout.findViewById(R.id.headerTv);
                @SuppressLint({"MissingInflatedId", "LocalSuppress"})
                TextView description = layout.findViewById(R.id.descripTv);
                header.setText(mainContext.getString(R.string.error));
                description.setText(mainContext.getString(R.string.error_msg));
                Toast toast = new Toast(mainContext);
                toast.setGravity(Gravity.FILL_HORIZONTAL | Gravity.BOTTOM, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
            }

        });
    }*/

}


