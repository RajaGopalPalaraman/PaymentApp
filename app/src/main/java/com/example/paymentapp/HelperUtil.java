package com.example.paymentapp;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

public final class HelperUtil {

    private static final String PREFERENCE_KEY = "easy_pay_app_preference";

    private static final String USERNAME_KEY = "username";
    private static final String PASSWORD_KEY = "password";
    private static final String PHONE_KEY = "phone";
    private static final String EMAIL_KEY = "email";
    private static final String AADHAR_KEY = "aadhar";

    static void setUsername(Context context, @NonNull String username) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                PREFERENCE_KEY, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(USERNAME_KEY, username).apply();
    }

    static void setPassword(Context context, @NonNull String password) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                PREFERENCE_KEY, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(PASSWORD_KEY, password).apply();
    }

    static void setPhone(Context context, @NonNull String phone) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                PREFERENCE_KEY, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(PHONE_KEY, phone).apply();
    }

    static void setEmail(Context context, @NonNull String email) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                PREFERENCE_KEY, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(EMAIL_KEY, email).apply();
    }

    static void setAadhar(Context context, @NonNull String aadhar) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                PREFERENCE_KEY, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(AADHAR_KEY, aadhar).apply();
    }

    static String getUsername(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                PREFERENCE_KEY, Context.MODE_PRIVATE);
        return sharedPreferences.getString(USERNAME_KEY, null);
    }

    static String getPassword(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                PREFERENCE_KEY, Context.MODE_PRIVATE);
        return sharedPreferences.getString(PASSWORD_KEY, null);
    }

}
