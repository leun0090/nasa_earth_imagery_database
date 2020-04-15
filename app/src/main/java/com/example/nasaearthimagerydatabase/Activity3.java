package com.example.nasaearthimagerydatabase;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.material.navigation.NavigationView;

import java.lang.String;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Activity3 extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String ACTIVITY_NAME = "PROFILE_ACTIVITY";
    private ArrayList<MapElement> list_map_elements = new ArrayList<>();
    DbNasaEarthImagery dbHelper;
    ListView theList;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    ProgressBar progressBar;
    Handler handler = new Handler();
    int progress = 0;
    Thread thread;
    ForecastQuery networkThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_3);
        networkThread = new ForecastQuery();
        networkThread.execute();
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
/*
        thread = new Thread(() -> {
            while(progress<100) {
                handler.post((() -> progressBar.setProgress(progress)));
                try {
                    TimeUnit.MILLISECONDS.sleep(20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                progress+=5;
            }
            thread.interrupt();
        });
        thread.start();
        if(progress==100) {
            progressBar.setVisibility(View.INVISIBLE);
            progress=101;
        }*/

        MyListAdapter adpt = new MyListAdapter();
        theList = findViewById(R.id.aListView);
        theList.setAdapter(adpt);
        dbHelper = new DbNasaEarthImagery(this);
        networkThread.viewFavorites();
        Toolbar tBar = findViewById(R.id.toolbar);
        setSupportActionBar(tBar);

        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawerLayout, tBar, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        theList.setOnItemClickListener((parent, view, position, id) -> {
            MapElement e = list_map_elements.get(position);
            Intent intent = new Intent(getApplicationContext(), Activity2.class);
            intent.putExtra("LATITUDE", e.getLatitude());
            intent.putExtra("LONGITUDE", e.getLongitude());
            startActivity(intent);
        });


        theList.setOnItemLongClickListener((parent, view, pos, id) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.delete_this));
            builder.setMessage(getString(R.string.select_row) + (pos + 1) + getString(R.string.dataB_id) + id);
            builder.setPositiveButton(getString(R.string.yyes), (click, arg) -> {
                Log.e(ACTIVITY_NAME, "In function: onStart");
                dbHelper.deleteLocation(list_map_elements.get(pos).getId());
                list_map_elements.clear();
                networkThread.viewFavorites();
                if (list_map_elements.isEmpty()) list_map_elements.add(null);
                adpt.notifyDataSetChanged();
            });
            builder.setNegativeButton(getString(R.string.nnoo), (click, arg) -> {
            });
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

    private class ForecastQuery extends AsyncTask<String, Integer, String> {

        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... strings) {
            try {
                if (dbHelper.readAllLocationsToCursor().getColumnCount() != 9) dbHelper.deleteTable();
                publishProgress(10);
                list_map_elements = dbHelper.getListElements();
                // Cursor cursor = dbHelper.readAllLocationsToCursor();
                if (list_map_elements.size() == 0) {
                    MapElement loc[] = new MapElement[9];
                    loc[0] = new MapElement(0, "Ottawa", "45.424651", "-75.699520", "Parliament", 5, 12);               publishProgress(10);
                    loc[1] = new MapElement(0, "London", "51.51", "-0.1", "Millennium Bridge", 4, 12);                  publishProgress(20);
                    loc[2] = new MapElement(0, "Paris", "48.857", "2,294", "Tour Eiffel", 5, 12);                       publishProgress(30);
                    loc[3] = new MapElement(0, "Saudi Arabia", "21.422417", "39.826076", "Mecca", 3, 12);               publishProgress(40);
                    loc[4] = new MapElement(0, "Giza", "29.978556", "31.133885", "The Great Pyramid of Giza", 2, 12);   publishProgress(50);
                    loc[5] = new MapElement(0, "New York", "40.702739", "-74.016338", "Battery Park", 5, 12);           publishProgress(60);
                    loc[6] = new MapElement(0, "Beijing", "39.893255", "116.363785", "Xicheng District", 3, 12);        publishProgress(70);
                    loc[7] = new MapElement(0, "Moscow", "55.754039", "37.620280", "Red Square", 5, 12);                publishProgress(80);
                    loc[8] = new MapElement(0, "Baikonur", "45.919987", "63.342329", "Gagarin's Start", 4, 12);         publishProgress(90);
                    for (int i = 0; i < 9; i++)
                        dbHelper.insertLocation(loc[i]);
                    list_map_elements = dbHelper.getListElements();
                } else publishProgress(70);
                theList.setAdapter(new MyListAdapter());
                publishProgress(100);
            } catch (Exception ex) {
                Log.e("Crash!!", ex.getMessage());}
            return "Finished task";
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(values[0]);
            progress=values[0];
        }

        @Override
        protected void onPostExecute(String s) {
            //progressBar.setVisibility(View.INVISIBLE);
        }

        private void viewFavorites() {
            if (dbHelper.readAllLocationsToCursor().getColumnCount() != 9) dbHelper.deleteTable();
            publishProgress(10);
            list_map_elements = dbHelper.getListElements();
            // Cursor cursor = dbHelper.readAllLocationsToCursor();
            if (list_map_elements.size() == 0) {
                initElementsDemo();
                list_map_elements = dbHelper.getListElements();
            } else publishProgress(70);
            theList.setAdapter(new MyListAdapter());
            publishProgress(100);
        }

        public void initElementsDemo() {
            try {
                MapElement loc[] = new MapElement[9];
                loc[0] = new MapElement(0, "Ottawa", "45.424651", "-75.699520", "Parliament", 5, 12);               publishProgress(10);
                loc[1] = new MapElement(0, "London", "51.51", "-0.1", "Millennium Bridge", 4, 12);                  publishProgress(20);
                loc[2] = new MapElement(0, "Paris", "48.857", "2,294", "Tour Eiffel", 5, 12);                       publishProgress(30);
                loc[3] = new MapElement(0, "Saudi Arabia", "21.422417", "39.826076", "Mecca", 3, 12);               publishProgress(40);
                loc[4] = new MapElement(0, "Giza", "29.978556", "31.133885", "The Great Pyramid of Giza", 2, 12);   publishProgress(50);
                loc[5] = new MapElement(0, "New York", "40.702739", "-74.016338", "Battery Park", 5, 12);           publishProgress(60);
                loc[6] = new MapElement(0, "Beijing", "39.893255", "116.363785", "Xicheng District", 3, 12);        publishProgress(70);
                loc[7] = new MapElement(0, "Moscow", "55.754039", "37.620280", "Red Square", 5, 12);                publishProgress(80);
                loc[8] = new MapElement(0, "Baikonur", "45.919987", "63.342329", "Gagarin's Start", 4, 12);         publishProgress(90);
                for (int i = 0; i < 9; i++)
                    dbHelper.insertLocation(loc[i]);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
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
        //String message = null;
        //Look at your menu XML file. Put a case for every id in that file:
        switch (item.getItemId()) {
            //what to do when the menu item is selected:
            case R.id.item1:
                startActivity(new Intent(this, Activity1.class));
                //message = "You clicked on item 1";
                break;
            case R.id.item2:
                dbHelper.deleteTable();
                networkThread.doInBackground();
                //message = "You clicked on item 2";
                break;
            case R.id.item3:
                dbHelper.deleteTable();
                list_map_elements = dbHelper.getListElements();
                theList.setAdapter(new MyListAdapter());
                //message = "You clicked on item 3";
                break;
        }
        //Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        return true;
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
            TextView discr = null;
            TextView title = null;
            ImageView view = null;
            ImageView view2 = null, view3 = null, view4 = null, view5 = null;
            if (getItem(position).isFavorite()) {
                old = getLayoutInflater().inflate(R.layout.layout_favorite_view, parent, false);
                discr = old.findViewById(R.id.mapDescription);
                title = old.findViewById(R.id.mapTitle);
                view = old.findViewById(R.id.mapImage);
                view2 = old.findViewById(R.id.loc_star2);
                view3 = old.findViewById(R.id.loc_star3);
                view4 = old.findViewById(R.id.loc_star4);
                view5 = old.findViewById(R.id.loc_star5);

            }
            discr.setText(getItem(position).getDescription());
            title.setText(getItem(position).getTitle() + "  " + getItem(position).getLatitude() + ", " + getItem(position).getLongitude());
            int star = getItem(position).getFavorite();
            if (star < 2) view2.setImageResource(R.drawable.star2);
            if (star < 3) view3.setImageResource(R.drawable.star2);
            if (star < 4) view4.setImageResource(R.drawable.star2);
            if (star < 5) view5.setImageResource(R.drawable.star2);
            if (getItem(position).getImage() != null)
                getItem(position).bitMapToImageView(view, getItem(position).getImage());
            return old;
        }
    }
}
