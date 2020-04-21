package com.example.nasaearthimagerydatabase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sharedPreferences = null;
    EditText emailText;
    public static final String DEFAULT="N/A";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button loginButton = findViewById(R.id.loginButton);
        Switch switch_button  = findViewById(R.id.simpleSwitch);
        TextView darkModeTextView = findViewById(R.id.darkModeTextView);
        emailText = findViewById(R.id.emailText);

        // SHARED PREFERENCES FOR DARK MODE
        sharedPreferences = getSharedPreferences("Bing", Context.MODE_PRIVATE);
        String darkMode = sharedPreferences.getString("DARKMODE",DEFAULT);
        if (darkMode.equals("DARK")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            saveDarkSharedPrefs("DARK");
            switch_button.setChecked(true);
            darkModeTextView.setText(R.string.dark_mode);
        }
        else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            saveDarkSharedPrefs("LIGHT");
            darkModeTextView.setText(R.string.light_mode);
        }


        // DARK MODE SWITCH

        switch_button.setOnCheckedChangeListener((view, isChecked) -> {
            if(isChecked){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                darkModeTextView.setText(R.string.dark_mode);
                saveDarkSharedPrefs("DARK");
            }
            else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                darkModeTextView.setText(R.string.light_mode);
                saveDarkSharedPrefs("LIGHT");
            }
        });

        // login email
        String savedEmail = sharedPreferences.getString("EMAIL",DEFAULT);
        if (savedEmail.equals(DEFAULT)) {
            emailText.setText("");
        }
        else {
            emailText.setText(savedEmail);
        }

        // Got to Activity 1
        loginButton.setOnClickListener(click -> {
            String email  = emailText.getText().toString();
            if ((email.equals("")) || (!isEmailValid(email))) {
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.custom_toast_layout, findViewById(R.id.custom_toast_container));
                TextView tv = layout.findViewById(R.id.txtvw);
                tv.setText(R.string.wrong_email);
                Toast toast = new Toast(getApplicationContext());
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
            }
            else {
                Intent intentActivity1 = new Intent(getApplicationContext(), Activity1.class);
                startActivity(intentActivity1);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("EMAIL", email);
                editor.apply();
            }
        });

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    public void saveDarkSharedPrefs(String mode) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("DARKMODE", mode);
        editor.apply();
    }


    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}