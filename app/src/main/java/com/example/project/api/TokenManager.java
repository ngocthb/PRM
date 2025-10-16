package com.example.project.api;

import android.content.Context;
import android.content.SharedPreferences;

public class TokenManager {
    private static final String PREFS_NAME = "prefs_auth";
    private static final String KEY_TOKEN = "key_token";
    private static final String KEY_USER_ID = "key_user_id";
    private static final String KEY_TOKEN_EXPIRY = "key_token_expiry";

    private static final long TOKEN_VALIDITY_MS = 3 * 60 * 60 * 1000L; // 3h

    private static TokenManager instance;
    private final SharedPreferences prefs;

    private TokenManager(Context context) {
        prefs = context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized TokenManager getInstance(Context context) {
        if (instance == null) {
            instance = new TokenManager(context);
        }
        return instance;
    }

    public void saveTokenAndUserId(String token, long userId) {
        long expiry = System.currentTimeMillis() + TOKEN_VALIDITY_MS;
        prefs.edit()
                .putString(KEY_TOKEN, token)
                .putLong(KEY_USER_ID, userId)
                .putLong(KEY_TOKEN_EXPIRY, expiry)
                .apply();
    }

    public String getToken() {
        long expiry = prefs.getLong(KEY_TOKEN_EXPIRY, 0);
        String token = prefs.getString(KEY_TOKEN, null);
        if (token != null && System.currentTimeMillis() < expiry) {
            return token;
        } else {
            // token hết hạn, xóa luôn
            clear();
            return null;
        }
    }

    public long getUserId() {
        long expiry = prefs.getLong(KEY_TOKEN_EXPIRY, 0);
        if (System.currentTimeMillis() < expiry) {
            long userId = prefs.getLong(KEY_USER_ID, -1L);
            return userId != -1L ? userId : -1L;
        } else {
            clear();
            return -1L;
        }
    }

    public boolean isTokenValid() {
        long expiry = prefs.getLong(KEY_TOKEN_EXPIRY, 0);
        return System.currentTimeMillis() < expiry;
    }

    public void clear() {
        prefs.edit()
                .remove(KEY_TOKEN)
                .remove(KEY_USER_ID)
                .remove(KEY_TOKEN_EXPIRY)
                .apply();
    }
}
