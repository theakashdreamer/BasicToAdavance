package com.skysoftsolution.basictoadavance.apiClasses;
import static com.skysoftsolution.basictoadavance.constants.AppConstant.BASE_URL;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitRequest {
    private static Retrofit retrofit;
    public static String URLLocal = "https://spassess.technosysservicesdemos.com/api/";

    public static Retrofit getRetrofitInstance() {
        if (retrofit == null) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(interceptor)
                    .readTimeout(4, TimeUnit.MINUTES)
                    .connectTimeout(4, TimeUnit.MINUTES)
                    .build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(URLLocal)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
    public static Retrofit getRetrofitInstancenew() {
        if (retrofit == null || !retrofit.baseUrl().url().toString().equalsIgnoreCase(URLLocal)) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .readTimeout(4, TimeUnit.MINUTES)
                    .connectTimeout(4, TimeUnit.MINUTES)
                    .build();

            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl(URLLocal)   // Use your base URL here
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }


    public static Retrofit getRetrofitInstance1() {
        if (retrofit == null || !retrofit.baseUrl().url().toString().equalsIgnoreCase(BASE_URL)) {
            // Set up the logging interceptor to log request and response data
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);  // Logs request and response bodies

            // Configure OkHttpClient and attach the logging interceptor
            OkHttpClient okHttpClient = new OkHttpClient.Builder().readTimeout(4, TimeUnit.MINUTES)
                    .connectTimeout(4, TimeUnit.MINUTES).addInterceptor(loggingInterceptor)  // Add the logging interceptor here
                    .build();

            // Build Retrofit instance
            retrofit = new retrofit2.Retrofit.Builder().baseUrl(BASE_URL).client(okHttpClient).addConverterFactory(GsonConverterFactory.create()).build();
        }

        return retrofit;
    }

    public static Retrofit getRetrofitInstancenewFile() {
        if (retrofit == null || !retrofit.baseUrl().url().toString().equalsIgnoreCase(URLLocal)) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .readTimeout(4, TimeUnit.MINUTES)
                    .connectTimeout(4, TimeUnit.MINUTES)
                    .build();

            retrofit = new retrofit2.Retrofit.Builder()
                    .baseUrl("https://spassess.technosysservicesdemos.com/api/")// Use your base URL here
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return retrofit;
    }


}