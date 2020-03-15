package com.example.paymentapp;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class IndexFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_index, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loadBalance();
        loadTransactions();
        SwipeRefreshLayout swipeRefreshLayout = getActivity().findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setOnRefreshListener(() -> {
            getActivity().recreate();
        });
    }

    private void loadBalance() {
        new Thread(() -> {
            try {
                HttpURLConnection urlConnection = (HttpURLConnection) (
                        new URL(Constants.SERVER_URL + "/balance")).openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Authorization", "Bearer " + HelperUtil.getUserAuthToken(getContext()));
                if (urlConnection.getResponseCode() == 200) {
                    InputStream inputStream = urlConnection.getInputStream();
                    Gson gson = new Gson();
                    BalanceResponse response = gson.fromJson(new InputStreamReader(inputStream), BalanceResponse.class);
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> {
                        TextView textView = getActivity().findViewById(R.id.balanceText);
                        textView.setText(String.format("Rs. %s", response.balance));
                    });
                }
            } catch (IOException ignored) {

            }
        }).start();
    }

    private void loadTransactions() {
        new Thread(() -> {
            try {
                HttpURLConnection urlConnection = (HttpURLConnection) (
                        new URL(Constants.SERVER_URL + "/transactions")).openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.setRequestProperty("Authorization", "Bearer " + HelperUtil.getUserAuthToken(getContext()));
                if (urlConnection.getResponseCode() == 200) {
                    InputStream inputStream = urlConnection.getInputStream();
                    Gson gson = new Gson();
                    TransactionsResponse response = gson.fromJson(new InputStreamReader(inputStream), TransactionsResponse.class);
                    String[] strings = new String[response.transactions.length];
                    for (int i=0; i<response.transactions.length; i++) {
                        strings[i] = response.transactions[i].type == 0 ? "Credit" : "Debit";
                        strings[i] = strings[i] + "\n";
                        strings[i] = strings[i] + "Partner: " + response.transactions[i].partner ;
                        strings[i] = strings[i] + "\n";
                        strings[i] = strings[i] + "Amount: " + response.transactions[i].amount;
                        strings[i] = strings[i] + "\n";
                        strings[i] = strings[i] + "Status: " + (response.transactions[i].status == 0 ? "Success" : "Failure");
                    }
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> {
                        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.list_layout, strings);
                        ListView listView = getActivity().findViewById(R.id.transactions_list);
                        listView.setAdapter(arrayAdapter);
                    });
                }
            } catch (IOException ignored) {

            }
        }).start();
    }

    private static final class BalanceResponse {
        private float balance;
    }

    private static final class TransactionsResponse {
        private Transaction[] transactions;
    }

    private static final class Transaction {
        private String partner;
        private int type;
        private float amount;
        private int status;
    }

}
