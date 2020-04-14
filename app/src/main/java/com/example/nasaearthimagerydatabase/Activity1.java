
package com.example.nasaearthimagerydatabase;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;

public class Activity1 extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    ProgressBar progressBar1;
    Toolbar toolbar;
    EditText LongEditText;
    EditText LatEditText;

    Button SearchBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1);

        progressBar1 = findViewById(R.id.progressbar1);
        progressBar1.setVisibility(View.VISIBLE);

        LatEditText = findViewById(R.id.LatEditText);
        LongEditText = findViewById(R.id.LongEditText);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout navdrawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle( this,
                navdrawer, toolbar, R.string.open, R.string.close);
        navdrawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = findViewById(R.id.nav_view);
        Menu navdrawer_menu = navigationView.getMenu();
        navdrawer_menu.findItem(R.id.help).setVisible(false);
        navigationView.setNavigationItemSelectedListener(this);

        SearchBtn.setOnClickListener(click ->{
            Intent activity2 = new Intent(getApplicationContext(), Activity2.class);
            activity2.putExtra("LATITUDE", LatEditText.getText().toString());
            activity2.putExtra("LONGITUDE", LongEditText.getText().toString());
            startActivity(activity2);
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_menu1, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected( MenuItem item) {
        switch(item.getItemId()) {
            case R.id.search:
                Intent activity2 = new Intent(getApplicationContext(), Activity2.class);
                startActivity(activity2);
                break;

            case R.id.favourite:
                Intent activity3 = new Intent(getApplicationContext(), Activity3.class);
                startActivity(activity3);
                break;

            case R.id.help:


                break;

        }
        return true;
    }

    @Override
    public boolean onNavigationItemSelected( MenuItem item) {


        switch(item.getItemId())
        {
            case R.id.search:
                Intent activity2 = new Intent(getApplicationContext(), Activity2.class);
                startActivity(activity2);
                break;

            case R.id.favourite:
                Intent activity3 = new Intent(getApplicationContext(), Activity3.class);
                startActivity(activity3);
                break;

            case R.id.help:

                break;
        }
        DrawerLayout navdrawer = findViewById(R.id.drawer_layout);
        navdrawer.closeDrawer(GravityCompat.START);
        return false;
    }


}