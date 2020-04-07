package com.example.nasaearthimagerydatabase;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
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
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

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

    ApiUrl currentUrl;

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
        currentUrl = new ApiUrl(latitude, longitude, Integer.toString(zoom));
        urlMap = currentUrl.returnUrl();


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
                zoom++;
                zoom();
            }
        });

        // ZoomOut
        zoomOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zoom--;
                zoom();
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
        inflater.inflate(R.menu.top_menu2, menu);
        return true;
    }

    // Add actions to top toolbar
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.itemZoomIn:
                zoom++;
                zoom();
                break;
            case R.id.itemZoomOut:
                zoom--;
                zoom();
                break;
            case R.id.helpItem:
//                AlertDialog alertDialog = new AlertDialog.Builder(Activity2.this).create();
//                alertDialog.setTitle("Welcome to the Map View page!");
//                alertDialog.setMessage("You are currently viewing a map. Tap on the zoom in or out icons on the toolbar. Then enter a title to favorite this location.");
//                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int which) {
//                                dialog.dismiss();
//                            }
//                        });
//                alertDialog.show();

                Dialog helpDialog = new Dialog(Activity2.this);
                helpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                helpDialog.setContentView(R.layout.help_dialog2);
                Button okButton = helpDialog.findViewById(R.id.okButton);
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        helpDialog.cancel();
                    }
                });
                helpDialog.show();
                break;
        }
        return true;
    }

    // Add actions to navigation drawer
    @Override
    public boolean onNavigationItemSelected( MenuItem item) {
        switch(item.getItemId()) {
            case R.id.itemTest:
                Intent testIntent = new Intent(getApplicationContext(), TestActivity.class);
                startActivity(testIntent);
                break;

            case R.id.activityOne:
                Intent activityOneIntent = new Intent(getApplicationContext(), Activity1.class);
                startActivity(activityOneIntent);
                break;

            case R.id.activityTwo:
                Intent activityTwoIntent = new Intent(getApplicationContext(), Activity2.class);
                startActivity(activityTwoIntent);
                break;

            case R.id.activityThree:
                Intent activityThreeIntent = new Intent(getApplicationContext(), Activity3.class);
                startActivity(activityThreeIntent);
                break;
        }
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }



    // zoom
    public void zoom() {
        MapQuery req = new MapQuery();
        currentUrl.changeZoom(Integer.toString(zoom));
        req.execute(currentUrl.returnUrl());
    }

    // Class to store api url
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