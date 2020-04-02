package com.example.nasaearthimagerydatabase;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

public class Activity1 extends AppCompatActivity {

    ProgressBar progressBar1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1);

        progressBar1 = findViewById(R.id.progressbar1);
        progressBar1.setVisibility(View.VISIBLE);


    }
}
