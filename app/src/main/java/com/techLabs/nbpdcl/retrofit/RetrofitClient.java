package com.techLabs.nbpdcl.retrofit;

import android.content.Context;

import androidx.annotation.NonNull;

import com.techLabs.nbpdcl.Utils.PrefManager;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
//    private static final String BASE_URL = "http://103.8.43.34:2931/";
//    private static final String BASE_URL = "http://125.16.220.8:2931/";
    private static final String BASE_URL = "http://103.8.43.35:2931/";

//    private static final String BASE_URL = "http://125.16.220.8:2941/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient(Context context) {
        if (retrofit == null) {
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder()
                    .connectTimeout(5, TimeUnit.MINUTES)
                    .readTimeout(5, TimeUnit.MINUTES)
                    .writeTimeout(5, TimeUnit.MINUTES);

            httpClient.addInterceptor(new Interceptor() {
                @NonNull
                @Override
                public Response intercept(@NonNull Chain chain) throws IOException {
                    Request originalRequest = chain.request();
                    Request.Builder builder = originalRequest.newBuilder();

                    String requestUrl = originalRequest.url().encodedPath();

                    // APIs where token should NOT be added
                    boolean isLoginApi = requestUrl.contains("AdminPanel/user/login/");
                    boolean isVerifyOtpApi = requestUrl.contains("AdminPanel/user/verify_otp/");

                    if (!isLoginApi && !isVerifyOtpApi) {
                        PrefManager pref = new PrefManager(context);
                        String accessToken = pref.getAccessToken();
                        if (accessToken != null && !accessToken.isEmpty()) {
                            builder.header("Authorization", "Bearer " + accessToken);
                        }
                    }

                    return chain.proceed(builder.build());
                }
            });

            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            httpClient.addInterceptor(loggingInterceptor);

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(httpClient.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }

}
