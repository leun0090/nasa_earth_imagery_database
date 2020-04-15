package com.example.nasaearthimagerydatabase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // hi again!!
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button activityButton1 = findViewById(R.id.activityButton1);
        Button activityButton2 = findViewById(R.id.activityButton2);
        Button activityButton3 = findViewById(R.id.activityButton3);

        Intent intent1 = new Intent(getApplicationContext(), Activity1.class);
        activityButton1.setOnClickListener(click -> startActivity(intent1));


        activityButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(getApplicationContext(), Activity2.class);
                startActivity(intent2);
            }
        });

        activityButton3.setOnClickListener(c -> {
            Intent intent3 = new Intent(MainActivity.this, Activity3.class);
            MainActivity.this.startActivity(intent3);
        });


        // Backup Activities 1
        activityButton1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Intent intentTest1 = new Intent(getApplicationContext(), TestActivity1.class);
                startActivity(intentTest1);
                return true;
            }
        });


    }
}
