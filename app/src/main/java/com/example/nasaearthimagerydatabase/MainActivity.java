package com.example.nasaearthimagerydatabase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
        Switch switch_button  = (Switch) findViewById(R.id.simpleSwitch);
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
        switch_button.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
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
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email  = emailText.getText().toString();
                if ((email.equals("")) || (!isEmailValid(email))) {
                    //Toast.makeText(getApplicationContext(), R.string.email_error2, Toast.LENGTH_LONG).show();

                    LayoutInflater inflater = getLayoutInflater();
                    View layout = inflater.inflate(R.layout.custom_toast_layout, (ViewGroup) findViewById(R.id.custom_toast_container));
                    TextView tv = (TextView) layout.findViewById(R.id.txtvw);
                    tv.setText("Wrong Email");
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
                    editor.commit();
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

    boolean isEmailValid(CharSequence email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}