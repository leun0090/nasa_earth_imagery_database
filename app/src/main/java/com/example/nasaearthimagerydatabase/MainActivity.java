package com.example.nasaearthimagerydatabase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.nasaearthimagerydatabase.Activity2.DEFAULT;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button activityButton1 = findViewById(R.id.activityButton1);
        Button activityButton2 = findViewById(R.id.activityButton2);
        Button activityButton3 = findViewById(R.id.activityButton3);
        Button loginButton = findViewById(R.id.loginButton);
        Switch switch_button  = (Switch) findViewById(R.id.simpleSwitch);
        TextView darkModeTextView = findViewById(R.id.darkModeTextView);

        Intent intent1 = new Intent(getApplicationContext(), Activity1.class);
        activityButton1.setOnClickListener(click -> startActivity(intent1));

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Activity1.class));
            }
        });

        activityButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Activity2.class));
            }
        });

        activityButton3.setOnClickListener(c -> {
                startActivity(new Intent(MainActivity.this, Activity3.class));
        });


        // Backup Activities 1
        activityButton1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                startActivity(new Intent(getApplicationContext(), TestActivity1.class));
                return true;
            }
        });

        // SHARED PREFERENCES FOR DARK MODE
        sharedPreferences = getSharedPreferences("DarkMode", Context.MODE_PRIVATE);
        String darkMode = sharedPreferences.getString("DARKMODE",DEFAULT);
        if (darkMode.equals("DARK")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            saveDarkSharedPrefs("DARK");
            switch_button.setChecked(true);
            darkModeTextView.setText("Dark Mode");
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            saveDarkSharedPrefs("LIGHT");
            darkModeTextView.setText("Light Mode");
        }


        // DARK MODE SWITCH
        switch_button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    darkModeTextView.setText("Dark Mode");
                    saveDarkSharedPrefs("DARK");
                }
                else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    darkModeTextView.setText("Light Mode");
                    saveDarkSharedPrefs("LIGHT");
                }
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    public void saveDarkSharedPrefs(String mode) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("DARKMODE", mode);
        editor.commit();
    }


    /**
     * Close the virtual keyboard
     */
    private void closeKeyboard() {
        // current edittext
        View view = this.getCurrentFocus();
        // if there is a view that has focus
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
