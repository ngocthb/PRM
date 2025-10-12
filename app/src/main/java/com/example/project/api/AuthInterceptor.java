package com.example.project.api;

import android.content.Context;
import java.io.IOException;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {
    private final Context appContext;

    public AuthInterceptor(Context context) {
        this.appContext = context.getApplicationContext();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        String token = TokenManager.getInstance(appContext).getToken();

        Request.Builder builder = original.newBuilder();
        if (token != null && !token.isEmpty()) {
            // Thay "Bearer " nếu backend yêu cầu khác
            builder.addHeader("Authorization", "Bearer " + token);
        }
        Request request = builder.build();
        return chain.proceed(request);
    }
}