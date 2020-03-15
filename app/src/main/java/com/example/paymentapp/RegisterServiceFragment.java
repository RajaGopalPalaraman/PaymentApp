package com.example.paymentapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class RegisterServiceFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register_service, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (HelperUtil.getUserServiceRegistered(getContext())) {
            Toast.makeText(getContext(), "Service Already Registered", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), "Registering Service", Toast.LENGTH_SHORT).show();
            register();
        }
    }

    private void register() {
        Handler handler = new Handler(Looper.getMainLooper());
        new Thread(() -> {
            try {
                HttpURLConnection urlConnection = (HttpURLConnection) (
                        new URL(Constants.SERVER_URL + "/service/register")).openConnection();
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("Authorization", "Bearer " + HelperUtil.getUserAuthToken(getContext()));
                int status = urlConnection.getResponseCode();
                if (status == 201 || status == 422) {
                    HelperUtil.setUserServiceRegistered(getContext(), true);
                    handler.post(() -> {
                        Toast.makeText(getContext(), "Service registered successfully", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    handler.post(() -> {
                        Toast.makeText(getContext(), "Service not registered", Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (IOException ignored) {
                handler.post(() -> {
                    Toast.makeText(getContext(), "Unable to connect", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

}
