package com.example.paymentapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class ServiceActivity extends AppCompatActivity {

    private static final int FINGER_PRINT_REQUEST_CODE = 102;
    private static final int USER_CONFIRMATION_REQUEST_CODE = 103;

    private static final String BOUNDARY="WebKitFormBoundary7MA4YWxkTrZu0gW";
    private static final String LINE_FEED="\r\n";

    static final String AMOUNT = "amount";

    private float amount;

    private byte[] bytes;

    private UserResponse userResponse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service);
        amount = getIntent().getFloatExtra(AMOUNT, 0);

        Button button = findViewById(R.id.pay);
        button.setText(String.format("Pay Rs.%s", amount));
    }

    public void onPay(View view) {
        startActivityForResult(new Intent(this, FingerPrintScannerActivity.class),
                FINGER_PRINT_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Handler handler = new Handler(Looper.getMainLooper());
        if (requestCode == FINGER_PRINT_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            bytes = data.getByteArrayExtra(FingerPrintScannerActivity.ISO_TEMPLATE);
            if (bytes != null) {
                new Thread(() -> {
                    try {
                        userResponse = getUser();
                        if (userResponse == null) {
                            handler.post(() -> {
                                Toast.makeText(ServiceActivity.this, "Fingerprint not registered", Toast.LENGTH_SHORT).show();
                            });
                        } else {
                            handler.post(() -> {
                                startActivityForResult(
                                        new Intent(ServiceActivity.this, UserConfirmActivity.class)
                                                .putExtra(UserConfirmActivity.EMAIL, userResponse.email)
                                                .putExtra(UserConfirmActivity.USERNAME, userResponse.username)
                                                .putExtra(UserConfirmActivity.PHONE, userResponse.phone),
                                        USER_CONFIRMATION_REQUEST_CODE);
                            });
                        }
                    } catch (IOException e) {
                        handler.post(() -> {
                            Toast.makeText(ServiceActivity.this, "Unable to connect", Toast.LENGTH_SHORT).show();
                        });
                    }
                }).start();
            }
        }
        if (requestCode == USER_CONFIRMATION_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Processing", Toast.LENGTH_SHORT).show();
                transfer();
            } else {
                Toast.makeText(this, "Transaction Cancelled", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private UserResponse getUser() throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) (
                new URL(Constants.SERVER_URL + "/service/info")).openConnection();
        urlConnection.setDoOutput(true);
        urlConnection.setDoInput(true);
        urlConnection.setRequestProperty("Authorization", "Bearer " + HelperUtil.getUserServiceAuthToken(this));
        urlConnection.setRequestProperty("Content-Type",
                "multipart/form-data; boundary=\"" + BOUNDARY + "\"");
        OutputStream outputStream = urlConnection.getOutputStream();
        PrintWriter writer = new PrintWriter(outputStream);

        writer.append("--").append(BOUNDARY).append(LINE_FEED);

        writer.append("Content-Disposition: form-data; name=\"fingerprint\"; filename=\"finger.png\"")
                .append(LINE_FEED);
        writer.append("Content-Type: ").append(URLConnection.guessContentTypeFromName("finger.png"))
                .append(LINE_FEED);
        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
        writer.append(LINE_FEED);
        writer.flush();

        outputStream.write(bytes);
        outputStream.flush();
        writer.append(LINE_FEED);

        writer.append(LINE_FEED).flush();
        writer.append("--").append(BOUNDARY).append("--").append(LINE_FEED);
        writer.close();

        UserResponse response = null;
        if (urlConnection.getResponseCode() == 200) {
            Gson gson = new Gson();
            response = gson.fromJson(new InputStreamReader(urlConnection.getInputStream()), UserResponse.class);
            if (response.email == null) {
                response = null;
            }
        }
        return response;
    }

    private void transfer() {
        Handler handler = new Handler(Looper.getMainLooper());
        new Thread(() -> {
            try {
                HttpURLConnection urlConnection = (HttpURLConnection) (
                        new URL(Constants.SERVER_URL + "/service/transfer")).openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestProperty("Authorization", "Bearer " + HelperUtil.getUserServiceAuthToken(this));
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                OutputStream outputStream = urlConnection.getOutputStream();
                outputStream.write(("amount=" + amount + "&").getBytes());
                outputStream.write(("from_id=" + userResponse.id).getBytes());
                outputStream.close();

                if (urlConnection.getResponseCode() == 200) {
                    Gson gson = new Gson();
                    TransferResponse response = gson.fromJson(new InputStreamReader(urlConnection.getInputStream()), TransferResponse.class);
                    if (response.success) {
                        handler.post(() -> {
                            Toast.makeText(ServiceActivity.this, "Transaction successful", Toast.LENGTH_SHORT).show();
                        });
                        return;
                    }
                }
                handler.post(() -> {
                    Toast.makeText(ServiceActivity.this, "Unable to transfer money", Toast.LENGTH_SHORT).show();
                });
            } catch (IOException ignored) {
                handler.post(() -> {
                    Toast.makeText(ServiceActivity.this, "Unable to connect", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private static final class UserResponse {
        private int id;
        private String email;
        private String phone;
        private String username;
    }

    private static final class TransferResponse {
        private boolean success;
    }

}
