package com.example.paymentapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class SignUpActivity extends AppCompatActivity {

    private static final int SIGN_UP_RESPONSE_WHAT = 1;
    private static final int FINGER_PRINT_REQUEST_CODE = 101;

    private static final String BOUNDARY="WebKitFormBoundary7MA4YWxkTrZu0gW";
    private static final String LINE_FEED="\r\n";

    private EditText username;
    private EditText password;
    private EditText phone;
    private EditText email;

    private Handler handler;

    private byte[] bytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_layout);
        handler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == SIGN_UP_RESPONSE_WHAT) {
                    String data = (String) msg.obj;
                    if (data == null) {
                        Toast.makeText(SignUpActivity.this, "Unable to connect", Toast.LENGTH_SHORT).show();
                    } else {
                        Gson gson = new Gson();
                        Response response = gson.fromJson(data, Response.class);
                        if (response.created) {
                            Toast.makeText(SignUpActivity.this, "Account Created Successfully!!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(SignUpActivity.this, response.error, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        };
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        Button register = findViewById(R.id.register);
        register.setOnClickListener(v -> {
            if (username.getText().toString().isEmpty()) {
                Toast.makeText(SignUpActivity.this, "Please Enter Username", Toast.LENGTH_SHORT).show();
            } else if (password.getText().toString().isEmpty()) {
                Toast.makeText(SignUpActivity.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
            } else if (phone.getText().toString().isEmpty()) {
                Toast.makeText(SignUpActivity.this, "Please Enter Phone Number", Toast.LENGTH_SHORT).show();
            } else if (email.getText().toString().isEmpty()) {
                Toast.makeText(SignUpActivity.this, "Please Enter Email Id", Toast.LENGTH_SHORT).show();
            } else {
                startActivityForResult(new Intent(SignUpActivity.this, FingerPrintScannerActivity.class),
                        FINGER_PRINT_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FINGER_PRINT_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            bytes = data.getByteArrayExtra(FingerPrintScannerActivity.ISO_TEMPLATE);
            if (bytes != null) {
                signUp();
            }
        }
    }

    private void signUp() {
        new Thread(() -> {
            try {
                HttpURLConnection urlConnection = (HttpURLConnection) (
                        new URL(Constants.SERVER_URL + "/sign_up")).openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setRequestProperty("Content-Type",
                        "multipart/form-data; boundary=\"" + BOUNDARY + "\"");
                OutputStream outputStream = urlConnection.getOutputStream();
                PrintWriter writer = new PrintWriter(outputStream);

                writer.append("--").append(BOUNDARY).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"username\"")
                        .append(LINE_FEED);
                writer.append(LINE_FEED);
                writer.append(username.getText().toString()).append(LINE_FEED);
                writer.flush();

                writer.append("--").append(BOUNDARY).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"password\"")
                        .append(LINE_FEED);
                writer.append(LINE_FEED);
                writer.append(password.getText().toString()).append(LINE_FEED);
                writer.flush();

                writer.append("--").append(BOUNDARY).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"email\"")
                        .append(LINE_FEED);
                writer.append(LINE_FEED);
                writer.append(email.getText().toString()).append(LINE_FEED);
                writer.flush();

                writer.append("--").append(BOUNDARY).append(LINE_FEED);
                writer.append("Content-Disposition: form-data; name=\"phone\"")
                        .append(LINE_FEED);
                writer.append(LINE_FEED);
                writer.append(phone.getText().toString()).append(LINE_FEED);
                writer.flush();

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
                writer.flush();

                writer.append(LINE_FEED).flush();
                writer.append("--").append(BOUNDARY).append("--").append(LINE_FEED);
                writer.close();

                if (urlConnection.getResponseCode() == 200) {
                    InputStream inputStream = urlConnection.getInputStream();
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    byte[] resBytes = new byte[50];
                    int length;
                    while ((length = inputStream.read(resBytes)) != -1) {
                        byteArrayOutputStream.write(resBytes, 0, length);
                    }
                    handler.obtainMessage(SIGN_UP_RESPONSE_WHAT, new String(byteArrayOutputStream.toByteArray()))
                            .sendToTarget();
                    return;
                }

            } catch (Exception e) {
                Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            handler.obtainMessage(SIGN_UP_RESPONSE_WHAT, null).sendToTarget();
        }).start();
    }

    private static class Response {
        private boolean created;
        private String error;
    }
}
