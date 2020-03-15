package com.example.paymentapp;

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

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AddMoneyFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_money, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button button = getActivity().findViewById(R.id.add_money_button);
        final Handler handler = new Handler(Looper.getMainLooper());
        button.setOnClickListener(v -> {
           EditText editText = getActivity().findViewById(R.id.amount);
           if (editText.getText().toString().isEmpty()) {
               Toast.makeText(getContext(), "Enter valid amount", Toast.LENGTH_SHORT).show();
           } else {
               float amount = Float.parseFloat(editText.getText().toString());
               if (amount <= 0) {
                   Toast.makeText(getContext(), "Enter valid amount", Toast.LENGTH_SHORT).show();
               } else {
                   Toast.makeText(getContext(), "Processing...", Toast.LENGTH_SHORT).show();
                   new Thread(() -> {
                       try {
                           if (addMoney(amount)) {
                               handler.post(() -> {
                                   Toast.makeText(getContext(), "Money added successfully", Toast.LENGTH_SHORT).show();
                                   editText.setText("");
                               });
                           } else {
                               handler.post(() -> {
                                   Toast.makeText(getContext(), "Unable to add money", Toast.LENGTH_SHORT).show();
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
        });
    }

    private boolean addMoney(float amount) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) (
                new URL(Constants.SERVER_URL + "/add-money")).openConnection();
        urlConnection.setDoOutput(true);
        urlConnection.setDoInput(true);
        urlConnection.setRequestProperty("Authorization", "Bearer " + HelperUtil.getUserAuthToken(getContext()));
        urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        OutputStream outputStream = urlConnection.getOutputStream();
        outputStream.write(("amount=" + amount).getBytes());
        outputStream.close();
        if (urlConnection.getResponseCode() == 200) {
            InputStream inputStream = urlConnection.getInputStream();
            Gson gson = new Gson();
            Response response = gson.fromJson(new InputStreamReader(inputStream), Response.class);
            return response.added;
        } else {
            return false;
        }
    }

    private static final class Response {
        private boolean added;
    }

}
