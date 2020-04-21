package com.example.nasaearthimagerydatabase;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class Activity1 extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "TestActivity";

    String latitude;
    String longitude;

    EditText latitudeEditText;
    EditText longitudeEditText;
    Button randomButton;
    Button searchButton;
    Button btnGetLocation;


    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_1);


        // Initialize components
        latitudeEditText =  findViewById(R.id.latitudeEditText);
        longitudeEditText =  findViewById(R.id.longitudeEditText);
        randomButton = findViewById(R.id.randomButton);
        searchButton = findViewById(R.id.searchButton);
        btnGetLocation = findViewById(R.id.btnGetLocation);


        // Load async task
        randomButton.setOnClickListener(click -> {
            RandomCoordinates req = new RandomCoordinates();
            req.execute("https://api.3geonames.org/?randomland=yes");
        });


        searchButton.setOnClickListener(click -> {
            if ((!latitudeEditText.getText().toString().equals("")) || (!longitudeEditText.getText().toString().equals(""))) {
                Intent intent = new Intent(getApplicationContext(), Activity2.class);
                intent.putExtra("LATITUDE", latitudeEditText.getText().toString());
                intent.putExtra("LONGITUDE", longitudeEditText.getText().toString());
                startActivity(intent);
            }
            else {
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.custom_toast_layout, findViewById(R.id.custom_toast_container));
                TextView tv = layout.findViewById(R.id.txtvw);
                tv.setText(R.string.activity1_toast);
                Toast toast = new Toast(getApplicationContext());
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
            }
        });

        // Load top toolbar
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


        // Get current location
        btnGetLocation.setOnClickListener(// Get current location
                this::onClick);

    }

    /**
     * Initialize top toolbar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_menu, menu);
        MenuItem itemZoomIn = menu.findItem(R.id.itemZoomIn);
        MenuItem itemZoomOut = menu.findItem(R.id.itemZoomOut);
        itemZoomIn.setVisible(false);
        itemZoomOut.setVisible(false);

        return true;
    }

    /**
     * Top toolbar items
     *
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.helpItem) {
            Dialog helpDialog = new Dialog(Activity1.this);
            helpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            helpDialog.setContentView(R.layout.activity_2_help_dialog);
            Button okButton = helpDialog.findViewById(R.id.okButton);
            TextView helpDescription = helpDialog.findViewById(R.id.helpDescription);
            helpDescription.setText(R.string.activity1Description);
            okButton.setOnClickListener(click ->  helpDialog.cancel());
            helpDialog.show();
        }
        return true;
    }

    /**
     * Navigation drawer items
     *
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mainActivity:
                Intent mainIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainIntent);
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

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    private void onClick(View click) {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        String locationManagerString = "";

        if (locationManager.isProviderEnabled(locationManager.NETWORK_PROVIDER)) {
            locationManagerString = locationManager.NETWORK_PROVIDER;
        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManagerString = locationManager.GPS_PROVIDER;
        }

        locationManager.requestLocationUpdates(locationManagerString, 0, 0, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                latitude = Double.toString(location.getLatitude());
                longitude = Double.toString(location.getLongitude());
                latitudeEditText.setText(latitude);
                longitudeEditText.setText(longitude);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }

            @Override
            public void onProviderEnabled(String s) {
            }

            @Override
            public void onProviderDisabled(String s) {
            }
        });
    }


    @SuppressLint("StaticFieldLeak")
    private class RandomCoordinates extends AsyncTask<String, Integer, String> {
        protected void onPreExecute() {
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
                int eventType = xpp.getEventType(); //The parser is currently at START_DOCUMENT
                while(eventType != XmlPullParser.END_DOCUMENT) {
                    if(eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("latt")) {
                            latitude = xpp.nextText();
                        }
                        else if (xpp.getName().equals("longt")) {
                            longitude = xpp.nextText();
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

        }

        public void onPostExecute(String fromDoInBackground) {
            latitudeEditText.setText(latitude);
            longitudeEditText.setText(longitude);
        }
    }


}