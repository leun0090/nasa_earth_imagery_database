package com.example.nasaearthimagerydatabase;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.navigation.NavigationView;

import java.lang.String;
import java.sql.SQLException;
import java.util.ArrayList;

public class Activity3 extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private static final String ACTIVITY_NAME = "PROFILE_ACTIVITY";
    private ArrayList<MapElement> list_map_elements= new ArrayList<>();
    DbNasaEarthImagery dbHelper;
    ListView theList;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_3);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        MyListAdapter adpt = new MyListAdapter();
        theList = findViewById(R.id.aListView);
        theList.setAdapter(adpt);
        dbHelper = new DbNasaEarthImagery(this);
        viewFavorites();
        Toolbar tBar = findViewById(R.id.toolbar);
        setSupportActionBar(tBar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawerLayout, tBar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Add item to db
        String title = getIntent().getStringExtra("TITLE");
        String latitude = getIntent().getStringExtra("LATITUDE");
        String longitude = getIntent().getStringExtra("LONGITUDE");
        String description = getIntent().getStringExtra("DESCRIPTION");
        try {
            MapElement newMapElement = new MapElement(0,title, latitude, longitude, description, true);
            dbHelper.insertLocation(newMapElement);
            list_map_elements.add(newMapElement);
            adpt.notifyDataSetChanged();
        }
        catch ( SQLException e) {
            e.printStackTrace();
        }


        theList.setOnItemLongClickListener((parent, view, pos, id) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.delete_this));
            builder.setMessage(getString(R.string.select_row) + (pos + 1) + getString(R.string.dataB_id) + id);
            builder.setPositiveButton(getString(R.string.yyes), (click, arg)  -> {
                Log.e(ACTIVITY_NAME, "In function: onStart");
                dbHelper.deleteLocation(list_map_elements.get(pos).getId());
                list_map_elements.clear();
                viewFavorites();
                if (list_map_elements.isEmpty()) list_map_elements.add(null);
                adpt.notifyDataSetChanged();
            });
            builder.setNegativeButton(getString(R.string.nnoo), (click, arg)  -> { });
            builder.create().show();
            return true;
        });

        //TextView textView = findViewById(R.id.textField);
        Button sendButton = findViewById(R.id.favoriteButton);

        sendButton.setOnClickListener(click -> {
            //String msg=textView.getText().toString();
            //dbHelper.insertFavorite(msg,true);
            list_map_elements.clear();
            //viewFavorites();
            adpt.notifyDataSetChanged();
            //textView.setText("");
        });

    }

    private void viewFavorites(){
        //dbHelper.deleteTable();
        list_map_elements = dbHelper.getListElements();
       // Cursor cursor = dbHelper.readAllLocationsToCursor();
        if (list_map_elements.size() == 0) {initElementsDemo();
            list_map_elements = dbHelper.getListElements();}
       // else dbHelper.deleteAllLocation();
        theList.setAdapter(new MyListAdapter());
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item0:
                startActivity(new Intent(this, MainActivity.class));
                break;
            case R.id.item1:
                startActivity(new Intent(this, Activity1.class));
                break;
            case R.id.item2:
                startActivity(new Intent(this, Activity2.class));
                break;

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_menu3, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String message = null;
        //Look at your menu XML file. Put a case for every id in that file:
        switch (item.getItemId()) {
            //what to do when the menu item is selected:
            case R.id.item1:
                message = "You clicked on item 1";
                break;
            case R.id.item2:
                message = "You clicked on item 2";
                break;
            case R.id.item3:
                message = "You clicked on item 3";
                break;
        }
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        return true;
    }

    public void initElementsDemo() {
        try {
            MapElement loc[] = new MapElement[9];
            loc[0] = new MapElement(0,"Title 1", "45.335421", "-75.783714", "Description 1", true);
            loc[1] = new MapElement(0,"Title 2", "45.345421", "-75.793714", "Description 2", true);
            loc[2] = new MapElement(0,"Title 3", "45.355421", "-75.803714", "Description 3", true);
            loc[3] = new MapElement(0,"Title 4", "45.365421", "-75.813714", "Description 4", true);
            loc[4] = new MapElement(0,"Title 5", "45.375421", "-75.823714", "Description 5", true);
            loc[5] = new MapElement(0,"Title 6", "37.802297", "-122.405844", "Description 6", true);
            loc[6] = new MapElement(0,"Title 7", "37.792297", "-122.405844", "Description 7", true);
            loc[7] = new MapElement(0,"Title 8", "37.782297", "-122.405844", "Description 8", true);
            loc[8] = new MapElement(0,"Title 9", "37.802297", "-122.415844", "Description 9", true);
            for (int i = 0; i < 9; i++)
                dbHelper.insertLocation(loc[i]);
        }
        catch ( SQLException e) {
            e.printStackTrace();}
    }

    private class MyListAdapter extends BaseAdapter {

        public MyListAdapter() {
        }

        @Override
        public int getCount() {
            return list_map_elements.size();
        }

        @Override
        public MapElement getItem(int position) {
            return list_map_elements.get(position);
        }

        @Override
        public long getItemId(int position) {
            return list_map_elements.get(position).getId();
        }

        @Override
        public View getView(int position, View old, ViewGroup parent) {
            TextView discr=null;
            TextView title=null;
            ImageView view=null;
            if (getItem(position).isFavorite()) {
                old = getLayoutInflater().inflate(R.layout.layout_favorite_view, parent, false);
                discr = old.findViewById(R.id.mapDescription);
                title = old.findViewById(R.id.mapTitle);
                view = old.findViewById(R.id.mapImage);
            }
            discr.setText(getItem(position).getDescription());
            title.setText(getItem(position).getTitle()+"  "+getItem(position).getLatitude()+", "+getItem(position).getLongitude());
            if (getItem(position).getImage()!=null)
                getItem(position).bitMapToImageView(view,getItem(position).getImage());
            return old;
        }
    }
}