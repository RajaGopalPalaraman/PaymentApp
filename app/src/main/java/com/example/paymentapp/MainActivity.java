package com.example.paymentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.login);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUserAvailable()) {
                    if (username.getText().toString().trim().isEmpty()) {
                        Toast.makeText(MainActivity.this, "Please Enter Username", Toast.LENGTH_SHORT).show();
                    } else if (password.getText().toString().trim().isEmpty()) {
                        Toast.makeText(MainActivity.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                    } else if (
                            username.getText().toString().equals(HelperUtil.getUsername(MainActivity.this)) &&
                                    password.getText().toString().equals(HelperUtil.getPassword(MainActivity.this))
                    ) {
                        Toast.makeText(MainActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, HomeActivity.class));
                        finish();
                    } else {
                        Toast.makeText(MainActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "User Not Registered. Kindly Sign Up", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Button signUpButton = findViewById(R.id.sign_up);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SignUpActivity.class));
                finish();
            }
        });
    }

    private boolean isUserAvailable() {
        return HelperUtil.getUsername(this) != null && HelperUtil.getPassword(this) != null;

    }

}
