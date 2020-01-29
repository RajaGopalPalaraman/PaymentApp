package com.example.paymentapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class PaymentActivity extends AppCompatActivity {

    private static final int PICK_CONTACT = 121;

    private EditText phone;
    private EditText amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        phone = findViewById(R.id.phone);
        amount = findViewById(R.id.amount);
    }

    public void pay(View view) {
    }

    public void contacts(View view) {
        Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(i, PICK_CONTACT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (PICK_CONTACT == requestCode && resultCode == RESULT_OK && data != null) {
            Uri contactUri = data.getData();
            if (contactUri != null) {
                Cursor cursor = getContentResolver().query(contactUri, null, null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    phone.setText(cursor.getString(column));
                    cursor.close();
                }
            }
        }
    }

}
