package com.example.paymentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class UserConfirmActivity extends AppCompatActivity {

    static final String EMAIL = "email";
    static final String USERNAME = "username";
    static final String PHONE = "phone";

    private TextView email;
    private TextView username;
    private TextView phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_confirm);
        TextView textView = findViewById(R.id.username);
        textView.setText(getIntent().getStringExtra(USERNAME));

        textView = findViewById(R.id.email);
        textView.setText(getIntent().getStringExtra(EMAIL));

        textView = findViewById(R.id.phone);
        textView.setText(getIntent().getStringExtra(PHONE));
    }

    public void onConfirm(View view) {
        setResult(RESULT_OK);
        finish();
    }

    public void onDeny(View view) {
        setResult(RESULT_CANCELED);
        finish();
    }

}
