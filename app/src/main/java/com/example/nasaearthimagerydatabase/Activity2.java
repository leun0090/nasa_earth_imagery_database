package com.example.nasaearthimagerydatabase;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

public class Activity2 extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {

    String testUrl = "https://dev.virtualearth.net/REST/V1/Imagery/Metadata/Aerial/43.6532,-79.3832?zl=18&o=xml&ms=500,500&key=At7y4aOtMy4Uopf8cD8cu_um0-YGyp5nlzPLLDBxLmgDN4o6DUkvk0ZTs4QpYh1O";

    private static final String TAG = "Activity 2";
    String latitude;
    String longitude;
    String urlMap;
    String traceId;
    int zoom = 8;

    ProgressBar progressBar;
    ImageView mapView;
    Bitmap image = null;
    String imageUrl;
    TextView longitudeTextView;
    TextView latitudeTextView;

    Button zoomOutButton;
    Button zoomInButton;

    Button favoriteButton;
    Button saveButton;
    EditText titleEditText;

    // Shared preferences
    SharedPreferences sharedPreferences = null;
    public static final String DEFAULT="N/A";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);

        // Initialize layout items
        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        mapView = (ImageView) findViewById(R.id.mapView);
        latitudeTextView = (TextView) findViewById(R.id.latitudeTextView);
        longitudeTextView = (TextView) findViewById(R.id.longitudeTextView);
        titleEditText = (EditText) findViewById(R.id.titleEditText);
        favoriteButton = (Button) findViewById(R.id.favoriteButton);
        saveButton = (Button) findViewById(R.id.saveButton);
        zoomInButton = (Button) findViewById(R.id.zoomInButton);
        zoomOutButton = (Button) findViewById(R.id.zoomOutButton);

        // Load data from previous activity
        latitude = getIntent().getStringExtra("LATITUDE");
        longitude = getIntent().getStringExtra("LONGITUDE");

        Log.i(TAG, "latitude:" + latitude);
        // default
        if (latitude == null || longitude == null) {
            latitude = "43.6532";
            longitude = "-79.3832";
        }

        //urlMap = "https://dev.virtualearth.net/REST/V1/Imagery/Metadata/Aerial/"+ latitude +"," + longitude + "?zl=" + Integer.toString(zoom) + "&o=xml&ms=500,500&key=At7y4aOtMy4Uopf8cD8cu_um0-YGyp5nlzPLLDBxLmgDN4o6DUkvk0ZTs4QpYh1O";
        ApiUrl myUrl = new ApiUrl(latitude, longitude, Integer.toString(zoom));
        urlMap = myUrl.returnUrl();


        // Load data from sharedpreferences
        sharedPreferences = getSharedPreferences("ActivityTwo", Context.MODE_PRIVATE);
        String savedLatitude = sharedPreferences.getString("savedLatitude",DEFAULT);
        String savedLongitude = sharedPreferences.getString("savedLongitude",DEFAULT);
        String savedTitle = sharedPreferences.getString("savedTitle",DEFAULT);
        if (savedTitle.equals(DEFAULT)) {
            titleEditText.setText("");
        }
        else {
            titleEditText.setText(savedTitle);
        }


        // Load async task
        MapQuery req = new MapQuery();
        req.execute(urlMap);

        // Add click listener to favorite button
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(favoriteButton, R.string.activity2_favoriteSnack, Snackbar.LENGTH_LONG).show();

                // Load shared preferences data into title
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("savedTitle",titleEditText.getText().toString());
                editor.commit();
            }
        });

        // Saves Title into SharedPreferences
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Save longitude and latitude into shared preferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("savedLatitude",latitude);
                editor.putString("savedLongitude",latitude);
                editor.commit();
            }
        });


        // ZoomIn
        zoomInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapQuery req = new MapQuery();
                zoom++;
                myUrl.changeZoom(Integer.toString(zoom));
                req.execute(myUrl.returnUrl());
                //req.execute("https://dev.virtualearth.net/REST/V1/Imagery/Metadata/Aerial/"+ latitude +"," + longitude + "?zl=" + Integer.toString(zoom) + "&o=xml&ms=500,500&key=At7y4aOtMy4Uopf8cD8cu_um0-YGyp5nlzPLLDBxLmgDN4o6DUkvk0ZTs4QpYh1O");
            }
        });

        // ZoomOut
        zoomOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapQuery req = new MapQuery();
                zoom--;
                myUrl.changeZoom(Integer.toString(zoom));
                req.execute(myUrl.returnUrl());
                //req.execute("https://dev.virtualearth.net/REST/V1/Imagery/Metadata/Aerial/"+ latitude +"," + longitude + "?zl=" + Integer.toString(zoom) + "&o=xml&ms=500,500&key=At7y4aOtMy4Uopf8cD8cu_um0-YGyp5nlzPLLDBxLmgDN4o6DUkvk0ZTs4QpYh1O");
            }
        });


        // Load toolbar
        Toolbar tBar = findViewById(R.id.toolbar);
        setSupportActionBar(tBar);

        //Load NavigationDrawer:
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, tBar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    private class MapQuery extends AsyncTask<String, Integer, String> {
        protected void onPreExecute() {
            longitudeTextView.setVisibility(View.GONE);
            latitudeTextView.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String... args) {

            try {

                // Load url
                URL url = new URL(args[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                InputStream response = urlConnection.getInputStream();

                // Parse xml
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput( response  , "UTF-8");

                // Start parsing
                String parameter = null;
                int eventType = xpp.getEventType(); //The parser is currently at START_DOCUMENT
                while(eventType != XmlPullParser.END_DOCUMENT) {
                    if(eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("TraceId")) {
                            traceId = xpp.nextText().substring(0, 32);
                        }
                        else if(xpp.getName().equals("ImageUrl")) {
                            imageUrl = xpp.nextText();
                            publishProgress(50);

                            URL iconUrl = new URL(imageUrl);
                            HttpURLConnection iconConnection = (HttpURLConnection) iconUrl.openConnection();
                            iconConnection.connect();
                            int responseCode = iconConnection.getResponseCode();
                            if (responseCode == 200) {
                                image = BitmapFactory.decodeStream(iconConnection.getInputStream());
                            }
                        }
                    }
                    eventType = xpp.next(); //move to the next xml event and store it in a variable
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                Log.i(TAG, "Error");
            }

            return null;
        }

        public void onProgressUpdate(Integer ... args) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(args[0]);
        }

        public void onPostExecute(String fromDoInBackground) {
            progressBar.setVisibility(View.GONE);
            longitudeTextView.setVisibility(View.VISIBLE);
            latitudeTextView.setVisibility(View.VISIBLE);
            longitudeTextView.setText(longitude);
            latitudeTextView.setText(latitude);
            mapView.setImageBitmap(image);
        }
    }

    // Load top toolbar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_menu, menu);
        return true;
    }

    // Add actions to top toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String message = null;
        switch(item.getItemId()) {
            case R.id.item1:

                Intent activityOneIntent = new Intent(getApplicationContext(), Activity1.class);
                startActivity(activityOneIntent);
                break;
            case R.id.item2:
                message = "You clicked on Activity 2";
                Intent activityTwoIntent = new Intent(getApplicationContext(), Activity2.class);
                startActivity(activityTwoIntent);
                break;
            case R.id.item3:
                message = "You clicked on Activity 3";
                break;
            case R.id.item4:
                AlertDialog alertDialog = new AlertDialog.Builder(Activity2.this).create();
                alertDialog.setTitle("Help");
                alertDialog.setMessage("Help message to be shown");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
                message = "You clicked on Help";
                break;
        }
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        return true;
    }

    // Add actions to navigation drawer
    @Override
    public boolean onNavigationItemSelected( MenuItem item) {
        String message = null;
        switch(item.getItemId()) {
            case R.id.itemTest:
                message = "You clicked on Activity 1";
                Intent testIntent = new Intent(getApplicationContext(), TestActivity.class);
                startActivity(testIntent);
                break;
        }
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        Toast.makeText(this, "NavigationDrawer: " + message, Toast.LENGTH_LONG).show();
        return false;
    }

    private class ApiUrl {
        String start = "https://dev.virtualearth.net/REST/V1/Imagery/Metadata/Aerial/";
        String latitude = "";
        String longitude = "";
        String zoom = "";
        String end = "&o=xml&ms=500,500&key=At7y4aOtMy4Uopf8cD8cu_um0-YGyp5nlzPLLDBxLmgDN4o6DUkvk0ZTs4QpYh1O";

        public ApiUrl(String latitude, String longitude, String zoom) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.zoom = zoom;
        }

        public void changeZoom(String zoom) {
            this.zoom = zoom;
        }

        public String returnUrl() {
            return start + latitude + "," + longitude + "?zl=" + zoom + end;
        }
    }

}