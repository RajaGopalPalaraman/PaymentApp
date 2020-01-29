package com.example.paymentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up_layout);
        final EditText username = findViewById(R.id.username);
        final EditText password = findViewById(R.id.password);
        final EditText phone = findViewById(R.id.phone);
        final EditText email = findViewById(R.id.email);
        final EditText aadhar = findViewById(R.id.aadhar);
        Button register = findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username.getText().toString().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please Enter Username", Toast.LENGTH_SHORT).show();
                } else if (password.getText().toString().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please Enter Password", Toast.LENGTH_SHORT).show();
                } else if (phone.getText().toString().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please Enter Phone Number", Toast.LENGTH_SHORT).show();
                } else if (email.getText().toString().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please Enter Email Id", Toast.LENGTH_SHORT).show();
                } else if (aadhar.getText().toString().isEmpty()) {
                    Toast.makeText(SignUpActivity.this, "Please Enter Aadhar Number", Toast.LENGTH_SHORT).show();
                } else {
                    HelperUtil.setUsername(SignUpActivity.this, username.getText().toString());
                    HelperUtil.setPassword(SignUpActivity.this, password.getText().toString());
                    HelperUtil.setPhone(SignUpActivity.this, phone.getText().toString());
                    HelperUtil.setEmail(SignUpActivity.this, email.getText().toString());
                    HelperUtil.setAadhar(SignUpActivity.this, aadhar.getText().toString());
                    Toast.makeText(SignUpActivity.this, "Account Created Successfully!!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(SignUpActivity.this, MainActivity.class));
                    finish();
                }
            }
        });
    }
}
