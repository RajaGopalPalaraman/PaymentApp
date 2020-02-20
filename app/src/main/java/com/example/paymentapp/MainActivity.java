package com.example.paymentapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.edot.network.HttpPOSTClient;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private static final int LOGIN_RESPONSE_WHAT = 1;

    private EditText username;
    private EditText password;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        handler = new Handler(getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == LOGIN_RESPONSE_WHAT) {
                    String data = (String) msg.obj;
                    if (data == null) {
                        Toast.makeText(MainActivity.this, "Unable to connect", Toast.LENGTH_SHORT).show();
                    } else if (data.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Invalid Login Credentials", Toast.LENGTH_SHORT).show();
                        password.setText("");
                    } else {
                        Toast.makeText(MainActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                        Gson gson = new Gson();
                        Models.User user = gson.fromJson(data, Models.User.class);
                        HelperUtil.setUseId(MainActivity.this, user.getId());
                        startActivity(new Intent(MainActivity.this, HomeActivity.class));
                        finish();
                    }
                }
            }
        };
        Button loginButton = findViewById(R.id.login);
        loginButton.setOnClickListener(v -> {
            if (username.getText().toString().trim().isEmpty()) {
                Toast.makeText(MainActivity.this, "Please Enter Username", Toast.LENGTH_SHORT).show();
            } else if (password.getText().toString().trim().isEmpty()) {
                Toast.makeText(MainActivity.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
            } else {
                login();
            }
        });

        Button signUpButton = findViewById(R.id.sign_up);
        signUpButton.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SignUpActivity.class));
            finish();
        });
    }

    private void login() {
        new Thread(() -> {
            HttpPOSTClient httpPOSTClient = new HttpPOSTClient();
            HashMap<String, String> paramsMap = new HashMap<>();
            paramsMap.put("username", username.getText().toString());
            paramsMap.put("password", password.getText().toString());
            if (httpPOSTClient.establishConnection("http://192.168.43.91:8080/payapp/login", paramsMap)) {
                InputStream inputStream = httpPOSTClient.getInputStream();
                byte[] bytes = new byte[50];
                int length;
                try {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    while ((length = inputStream.read(bytes)) != -1) {
                        byteArrayOutputStream.write(bytes, 0, length);
                    }
                    handler.obtainMessage(LOGIN_RESPONSE_WHAT, new String(byteArrayOutputStream.toByteArray(), StandardCharsets.UTF_8))
                            .sendToTarget();
                    return;
                } catch (IOException ignored) {}
            }
            handler.obtainMessage(LOGIN_RESPONSE_WHAT, null);
        }).start();
    }

}
