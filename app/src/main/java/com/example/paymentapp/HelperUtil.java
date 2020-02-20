package com.example.paymentapp;

import android.content.Context;
import android.content.SharedPreferences;

public final class HelperUtil {

    private static final String PREFERENCE_KEY = "easy_pay_app_preference";

    private static final String USER_ID_KEY = "user_id";

    static void setUseId(Context context, int userId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                PREFERENCE_KEY, Context.MODE_PRIVATE);
        sharedPreferences.edit().putInt(USER_ID_KEY, userId).apply();
    }

    static int getUseId(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                PREFERENCE_KEY, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(USER_ID_KEY, -1);
    }

}
