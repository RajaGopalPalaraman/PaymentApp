package com.example.paymentapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class TransferFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_transfer, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Handler handler = new Handler(Looper.getMainLooper());
        Button button = getActivity().findViewById(R.id.pay);
        button.setOnClickListener(v -> {
            EditText userKey = getActivity().findViewById(R.id.key);
            EditText amount = getActivity().findViewById(R.id.amount);

            if (userKey.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Enter valid User Key", Toast.LENGTH_SHORT).show();
            } else if (amount.getText().toString().isEmpty()) {
                Toast.makeText(getContext(), "Enter valid amount", Toast.LENGTH_SHORT).show();
            } else {
                float amountFloat = Float.parseFloat(amount.getText().toString());
                if (amountFloat <= 0) {
                    Toast.makeText(getContext(), "Enter valid amount", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Verifying User", Toast.LENGTH_SHORT).show();
                    new Thread(() -> {
                        try {
                            int userId = getUserId(userKey.getText().toString());
                            if (userId == 0) {
                                handler.post(() -> {
                                    Toast.makeText(getContext(), "User not registered", Toast.LENGTH_SHORT).show();
                                });
                            } else {
                                handler.post(() -> {
                                    Toast.makeText(getContext(), "User verified. Transferring...", Toast.LENGTH_SHORT).show();
                                });
                                if (transferMoney(userId, amountFloat)) {
                                    handler.post(() -> {
                                        userKey.setText("");
                                        amount.setText("");
                                        Toast.makeText(getContext(), "Money Transferred Successfully", Toast.LENGTH_SHORT).show();
                                    });
                                } else {
                                    handler.post(() -> {
                                        Toast.makeText(getContext(), "Unable to Transfer Money", Toast.LENGTH_SHORT).show();
                                    });
                                }
                            }
                        } catch (IOException ignored) {
                            handler.post(() -> {
                                Toast.makeText(getContext(), "Unable to connect", Toast.LENGTH_SHORT).show();
                            });
                        }
                    }).start();
                }
            }
        });
    }

    private boolean transferMoney(int toId, float amount) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) (
                new URL(Constants.SERVER_URL + "/transfer")).openConnection();
        urlConnection.setDoOutput(true);
        urlConnection.setDoInput(true);
        urlConnection.setRequestProperty("Authorization", "Bearer " + HelperUtil.getUserAuthToken(getContext()));
        urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        OutputStream outputStream = urlConnection.getOutputStream();
        outputStream.write(("amount=" + amount + "&").getBytes());
        outputStream.write(("to_id=" + toId).getBytes());
        outputStream.close();
        if (urlConnection.getResponseCode() == 200) {
            InputStream inputStream = urlConnection.getInputStream();
            Gson gson = new Gson();
            TransferResponse response = gson.fromJson(new InputStreamReader(inputStream), TransferResponse.class);
            return response.success;
        } else {
            return false;
        }
    }

    private int getUserId(String key) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) (
                new URL(Constants.SERVER_URL + "/get-user")).openConnection();
        urlConnection.setDoOutput(true);
        urlConnection.setDoInput(true);
        urlConnection.setRequestProperty("Authorization", "Bearer " + HelperUtil.getUserAuthToken(getContext()));
        urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        OutputStream outputStream = urlConnection.getOutputStream();
        outputStream.write("key=".getBytes());
        outputStream.write(key.getBytes());
        outputStream.close();
        if (urlConnection.getResponseCode() == 200) {
            InputStream inputStream = urlConnection.getInputStream();
            Gson gson = new Gson();
            UserResponse userResponse = gson.fromJson(new InputStreamReader(inputStream), UserResponse.class);
            return userResponse.id;
        } else {
            return 0;
        }
    }

    private static final class UserResponse {
        private int id;
    }

    private static final class TransferResponse {
        private boolean success;
    }

}
