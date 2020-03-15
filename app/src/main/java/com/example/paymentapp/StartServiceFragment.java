package com.example.paymentapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class StartServiceFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_start_service, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Handler handler = new Handler(Looper.getMainLooper());
        Button button = getActivity().findViewById(R.id.start_service);
        EditText amount = getActivity().findViewById(R.id.amount);
        button.setOnClickListener(v -> {
            float amountFloat = Float.parseFloat(amount.getText().toString());
            if (amountFloat <= 0) {
                Toast.makeText(getContext(), "Enter valid amount", Toast.LENGTH_SHORT).show();
                return;
            }
            if (HelperUtil.getUserServiceRegistered((getContext()))) {
                new Thread(() -> {
                    try {
                        persistServiceAuthToken(getContext());
                        handler.post(() -> {
                            Toast.makeText(getContext(), "Service started", Toast.LENGTH_SHORT).show();
                        });
                        startActivity(new Intent(getContext(), ServiceActivity.class).putExtra(ServiceActivity.AMOUNT, amountFloat));
                    } catch (IOException ignored) {
                        handler.post(() -> {
                            Toast.makeText(getContext(), "Unable to start service", Toast.LENGTH_SHORT).show();
                        });
                    }
                }).start();
            } else {
                Toast.makeText(getContext(), "Service not registered", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void persistServiceAuthToken(Context context) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) (
                new URL(Constants.SERVER_URL + "/service/auth-token")).openConnection();
        urlConnection.setRequestMethod("POST");
        urlConnection.setRequestProperty("Authorization", "Bearer " + HelperUtil.getUserAuthToken(getContext()));
        if (urlConnection.getResponseCode() == 200) {
            InputStream inputStream = urlConnection.getInputStream();

            byte[] bytes = new byte[100];
            int length;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            while ((length = inputStream.read(bytes)) != -1) {
                byteArrayOutputStream.write(bytes, 0, length);
            }
            HelperUtil.setUserServiceAuthToken(getContext(), new String(byteArrayOutputStream.toByteArray()));
            Log.d("LogTag", HelperUtil.getUserServiceAuthToken(getContext()));
        }

    }

}
