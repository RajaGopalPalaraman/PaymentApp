package com.example.paymentapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    public void moneyTransfer(View view) {
        startActivity(new Intent(this, PaymentActivity.class));
    }

    public void recharge(View view) {
    }

}
