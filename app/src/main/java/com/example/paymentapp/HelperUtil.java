package com.example.paymentapp;

import android.content.Context;
import android.content.SharedPreferences;

public final class HelperUtil {

    private static final String PREFERENCE_KEY = "easy_pay_app_preference";

    private static final String USER_AUTH_TOKEN_KEY = "user_auth_token";
    private static final String USER_SERVICE_AUTH_TOKEN_KEY = "user_service_auth_token";
    private static final String USER_SERVICE_REGISTERED = "user_service_registered";

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

    static void setUserServiceAuthToken(Context context, String token) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                PREFERENCE_KEY, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(USER_SERVICE_AUTH_TOKEN_KEY, token).apply();
    }

    static String getUserServiceAuthToken(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                PREFERENCE_KEY, Context.MODE_PRIVATE);
        return sharedPreferences.getString(USER_SERVICE_AUTH_TOKEN_KEY, null);
    }

    static void setUserServiceRegistered(Context context, boolean registered) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                PREFERENCE_KEY, Context.MODE_PRIVATE);
        sharedPreferences.edit().putBoolean(USER_SERVICE_REGISTERED, registered).apply();
    }

    static boolean getUserServiceRegistered(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                PREFERENCE_KEY, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(USER_SERVICE_REGISTERED, false);
    }

    static void clearAll(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                PREFERENCE_KEY, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(USER_AUTH_TOKEN_KEY, null).apply();
        sharedPreferences.edit().putString(USER_SERVICE_AUTH_TOKEN_KEY, null).apply();
        sharedPreferences.edit().putBoolean(USER_SERVICE_REGISTERED, false).apply();
    }

}
