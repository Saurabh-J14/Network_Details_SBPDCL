package com.techLabs.nbpdcl.Utils;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.JsonObject;
import com.techLabs.nbpdcl.retrofit.ApiInterface;
import com.techLabs.nbpdcl.retrofit.RetrofitClient;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;

public class ExcelDownloadManager {
    private ApiInterface apiService;
    private Context mainContext;

    public ExcelDownloadManager(@NonNull Context context) {
        this.mainContext = context;
        Retrofit retrofit = RetrofitClient.getClient(mainContext);
        apiService = retrofit.create(ApiInterface.class);
    }

    public void downloadExcel(JsonObject jsonObject, Callback<ResponseBody> callback) {
        Call<ResponseBody> call = apiService.downloadExcel(jsonObject);
        call.enqueue(callback);
    }
}
