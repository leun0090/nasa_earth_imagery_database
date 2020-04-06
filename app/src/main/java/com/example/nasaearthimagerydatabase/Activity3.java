package com.example.nasaearthimagerydatabase;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.navigation.NavigationView;

import java.lang.String;
import java.sql.SQLException;
import java.util.ArrayList;

public class Activity3 extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private static final String ACTIVITY_NAME = "PROFILE_ACTIVITY";
    private ArrayList<Favorite> list_favorite= new ArrayList<>();
    DbNasaEarthImagery dbHelper;
    ListView theList;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_3);
        MyListAdapter adpt = new MyListAdapter();
        theList = findViewById(R.id.aListView);
        theList.setAdapter(adpt);
        dbHelper = new DbNasaEarthImagery(this);
        try {
            viewFavorites();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Toolbar tBar = findViewById(R.id.toolbar);
        setSupportActionBar(tBar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawerLayout, tBar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        theList.setOnItemLongClickListener((parent, view, pos, id) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.delete_this));
            builder.setMessage(getString(R.string.select_row) + (pos + 1) + getString(R.string.dataB_id) + id);
            builder.setPositiveButton(getString(R.string.yyes), (click, arg)  -> {
                Log.e(ACTIVITY_NAME, "In function: onStart");
                dbHelper.deleteLocation(list_favorite.get(pos).GetId());
                list_favorite.clear();
                try {
                    viewFavorites();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                if (list_favorite.isEmpty()) list_favorite.add(null);
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
            list_favorite.clear();
            //viewFavorites();
            adpt.notifyDataSetChanged();
            //textView.setText("");
        });
        /*Button receiveButton = findViewById(R.id.receiveButton);
        receiveButton.setOnClickListener(click -> {
            String msg=textView.getText().toString();
            dbHelper.insertFavorite(msg,false);
            list_Favorite.clear();
            viewFavorites();
            adpt.notifyDataSetChanged();
            textView.setText("");
        });*/

    }

    private void viewFavorites() throws SQLException {
        Cursor cursor = dbHelper.readAllLocationsToCursor();
        if (cursor.getCount() == 0) initElements();
        /*
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()){
                Favorite fvr = new Favorite(cursor.getString(1), cursor.getInt(2)==1?true:false, cursor.getLong(0));
                list_favorite.add(fvr);
                theList.setAdapter(new MyListAdapter());
            }
        }*/
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

    public void initElements() throws SQLException {
        MapElement loc[] = new MapElement[5];
        loc[0] =new MapElement("Title 1","45.335421","-75.783714","Description 1","http://ecn.t1.tiles.virtualearth.net/tiles/a030230322201101.jpeg?g=8293",true);
        loc[1] =new MapElement("Title 2","45.345421","-75.793714","Description 2","http://ecn.t1.tiles.virtualearth.net/tiles/a030230322023321.jpeg?g=8293",true);
        loc[2] =new MapElement("Title 3","45.355421","-75.803714","Description 3","http://ecn.t2.tiles.virtualearth.net/tiles/a030230322023302.jpeg?g=8293",true);
        loc[3] =new MapElement("Title 4","45.365421","-75.813714","Description 4","http://ecn.t1.tiles.virtualearth.net/tiles/a030230322023211.jpeg?g=8293",true);
        loc[4] =new MapElement("Title 5","45.375421","-75.823714","Description 5","http://ecn.t0.tiles.virtualearth.net/tiles/a030230322023030.jpeg?g=8293",true);
        for (int i=0; i<5;i++) dbHelper.insertLocation(loc[i].getTitle(),loc[i].getLatitude(),loc[i].getLongitude(),loc[i].getDescription(),loc[i].getImage(),loc[i].isFavorite());
    }

    public class Favorite {
        private String Favorite;
        private boolean isFavorite;
        private long id;

        public Favorite(String Favorite, boolean isFavorite, long id ){
            this.Favorite = Favorite;
            this.isFavorite = isFavorite;
            this.id=id;
        }

        public boolean isFavorite() {
            return isFavorite;
        }
        public long GetId() {return  id;}
    }


    private class MyListAdapter extends BaseAdapter {

        public MyListAdapter() {
        }

        @Override
        public int getCount() {
            return list_favorite.size();
        }

        @Override
        public Favorite getItem(int position) {
            return list_favorite.get(position);
        }

        @Override
        public long getItemId(int position) {
            return list_favorite.get(position).GetId();
        }

        @Override
        public View getView(int position, View old, ViewGroup parent) {
            TextView FavoriteText=null;
            if (getItem(position).isFavorite()) {
                old = getLayoutInflater().inflate(R.layout.layout_favorite_view, parent, false);
                FavoriteText = old.findViewById(R.id.mapDescription);
            }
            FavoriteText.setText(getItem(position).Favorite);
            return old;
        }
    }
}