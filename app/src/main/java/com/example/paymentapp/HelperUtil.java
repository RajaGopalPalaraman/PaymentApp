package com.example.paymentapp;

import android.content.Context;
import android.content.SharedPreferences;

public final class HelperUtil {

    private static final String PREFERENCE_KEY = "easy_pay_app_preference";

    private static final String USER_AUTH_TOKEN_KEY = "user_ayth_token";

    static void setUserAuthToken(Context context, String token) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                PREFERENCE_KEY, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(USER_AUTH_TOKEN_KEY, token).apply();
    }

    static String getUserAuthToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                PREFERENCE_KEY, Context.MODE_PRIVATE);
        return sharedPreferences.getString(USER_AUTH_TOKEN_KEY, null);
    }

}
