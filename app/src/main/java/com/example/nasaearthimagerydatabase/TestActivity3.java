package com.example.nasaearthimagerydatabase;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

public class TestActivity3 extends AppCompatActivity {

    private static final String TAG = "Test Activity 3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test3);

        String title = getIntent().getStringExtra("TITLE");
        String latitude = getIntent().getStringExtra("LATITUDE");
        String longitude = getIntent().getStringExtra("LONGITUDE");
        String description = getIntent().getStringExtra("DESCRIPTION");

        Log.i(TAG, "LATITUDE" + latitude);
    }
}
