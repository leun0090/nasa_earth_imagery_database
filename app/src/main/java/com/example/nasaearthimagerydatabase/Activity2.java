package com.example.nasaearthimagerydatabase;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

public class Activity2 extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    String testUrl = "https://dev.virtualearth.net/REST/V1/Imagery/Metadata/Aerial/43.6532,-79.3832?zl=18&o=xml&ms=500,500&key=At7y4aOtMy4Uopf8cD8cu_um0-YGyp5nlzPLLDBxLmgDN4o6DUkvk0ZTs4QpYh1O";

    // https://docs.microsoft.com/en-us/bingmaps/rest-services/traffic/get-traffic-incidents
    //http://dev.virtualearth.net/REST/v1/Traffic/Incidents/45.4215,-75.6972,46.4215,-74.6972?key=At7y4aOtMy4Uopf8cD8cu_um0-YGyp5nlzPLLDBxLmgDN4o6DUkvk0ZTs4QpYh1O

    private static final String TAG = "Activity 2";
    String latitude;
    String longitude;
    String urlMap;
    String traceId;
    int zoom = 12;

    ProgressBar progressBar;
    ImageView mapView;
    Bitmap image = null;
    String imageUrl;
    TextView longitudeTextView;
    TextView latitudeTextView;

    ImageButton leftButton;
    ImageButton rightButton;
    ImageButton upButton;
    ImageButton downButton;

    ImageButton favoriteButton;
    ImageButton coffeeshopButton;
    EditText titleEditText;
    RatingBar simpleRatingBar;

    ApiUrl currentUrl;

    Boolean isTablet;
    DetailsFragment2 dFragment;

    Double move_lat_long = 0.05;

    // Shared preferences
    SharedPreferences sharedPreferences = null;
    public static final String DEFAULT = "N/A";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_2);

        // Initialize layout items
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        mapView = (ImageView) findViewById(R.id.mapView);
        latitudeTextView = (TextView) findViewById(R.id.latitudeTextView);
        longitudeTextView = (TextView) findViewById(R.id.longitudeTextView);
        titleEditText = (EditText) findViewById(R.id.titleEditText);
        favoriteButton = (ImageButton) findViewById(R.id.favoriteButton);
        coffeeshopButton = (ImageButton) findViewById(R.id.coffeeshopButton);
        leftButton = (ImageButton) findViewById(R.id.leftButton);
        rightButton = (ImageButton) findViewById(R.id.rightButton);
        upButton = (ImageButton) findViewById(R.id.upButton);
        downButton = (ImageButton) findViewById(R.id.downButton);

        simpleRatingBar = (RatingBar) findViewById(R.id.simpleRatingBar);
        int numberOfStars = simpleRatingBar.getNumStars();

        // Load data from previous activity
        latitude = getIntent().getStringExtra("LATITUDE");
        longitude = getIntent().getStringExtra("LONGITUDE");

        // default
        if (latitude == null || longitude == null) {
            latitude = "40.7128";
            longitude = "-74.0060";
        }

        // urlMap = "https://dev.virtualearth.net/REST/V1/Imagery/Metadata/Aerial/40.7128,-74.0060?zl=14&o=xml&ms=500,500&key=At7y4aOtMy4Uopf8cD8cu_um0-YGyp5nlzPLLDBxLmgDN4o6DUkvk0ZTs4QpYh1O";
        //urlMap = "https://dev.virtualearth.net/REST/V1/Imagery/Metadata/Aerial/"+ latitude +"," + longitude + "?zl=" + Integer.toString(zoom) + "&o=xml&ms=500,500&key=At7y4aOtMy4Uopf8cD8cu_um0-YGyp5nlzPLLDBxLmgDN4o6DUkvk0ZTs4QpYh1O";
        currentUrl = new ApiUrl(latitude, longitude, Integer.toString(zoom));
        urlMap = currentUrl.returnUrl();


        // Load data from sharedpreferences
        sharedPreferences = getSharedPreferences("ActivityTwo", Context.MODE_PRIVATE);
        String savedLatitude = sharedPreferences.getString("savedLatitude", DEFAULT);
        String savedLongitude = sharedPreferences.getString("savedLongitude", DEFAULT);
        String savedTitle = sharedPreferences.getString("savedTitle", DEFAULT);

        if (savedTitle.equals(DEFAULT)) {
            titleEditText.setText("");
        } else {
            titleEditText.setText(savedTitle);
        }

        // Load async task
        MapQuery init = new MapQuery();
        init.execute(urlMap);

        // Add click listener to favorite button
        favoriteButton.setOnClickListener(c -> {

            // Load shared preferences data into title
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("savedTitle", titleEditText.getText().toString());
            editor.commit();

            // send to activity3
            Intent intent = new Intent(getApplicationContext(), Activity3.class);
            intent.putExtra("LATITUDE", latitude);
            intent.putExtra("LONGITUDE", longitude);
            intent.putExtra("TITLE", titleEditText.getText().toString());
            intent.putExtra("DESCRIPTION", "");
            intent.putExtra("STARS", numberOfStars);
            startActivity(intent);
        });

        // moveLeft
        leftButton.setOnClickListener(c -> {
            Snackbar.make(leftButton, "You have moved left", Snackbar.LENGTH_LONG).show();
            currentUrl.moveLeft();
            moveMap();
        });

        // moveRight
        rightButton.setOnClickListener(c -> {
            Snackbar.make(rightButton, "You have moved right", Snackbar.LENGTH_LONG).show();
            currentUrl.moveRight();
            moveMap();
        });

        upButton.setOnClickListener(c -> {
            Snackbar.make(upButton, "You have moved up", Snackbar.LENGTH_LONG).show();
            currentUrl.moveUp();
            moveMap();
        });

        downButton.setOnClickListener(c -> {
            Snackbar.make(downButton, "You have moved down", Snackbar.LENGTH_LONG).show();
            currentUrl.moveDown();
            moveMap();
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


        // Fragment + View Coffee button
        dFragment = new DetailsFragment2();
        isTablet = findViewById(R.id.fragmentLocation) != null; //check if the FrameLayout is loaded
        coffeeshopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Show Toast
                Toast.makeText(getApplicationContext(), "Coffee shop list is only available in USA at this time. ", Toast.LENGTH_LONG).show();

                Bundle dataToPass = new Bundle();
                dataToPass.putString("LATITUDE", latitude);
                dataToPass.putString("LONGITUDE", longitude);

                if (isTablet) {
                    dFragment.setArguments(dataToPass);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.fragmentLocation, dFragment)
                            .commit();
                } else {
                    Intent extraIntent = new Intent(getApplicationContext(), Activity2_listview.class);
                    extraIntent.putExtra("LATITUDE", latitude);
                    extraIntent.putExtra("LONGITUDE", longitude);
                    startActivity(extraIntent);
                }
            }
        });
    }

    private class MapQuery extends AsyncTask < String, Integer, String > {
        protected void onPreExecute() {
            longitudeTextView.setVisibility(View.GONE);
            latitudeTextView.setVisibility(View.GONE);
        }

        @Override
        protected String doInBackground(String...args) {

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
                xpp.setInput(response, "UTF-8");

                // Start parsing
                String parameter = null;
                int eventType = xpp.getEventType(); //The parser is currently at START_DOCUMENT
                while (eventType != XmlPullParser.END_DOCUMENT) {
                    if (eventType == XmlPullParser.START_TAG) {
                        if (xpp.getName().equals("TraceId")) {
                            traceId = xpp.nextText().substring(0, 32);
                        } else if (xpp.getName().equals("ImageUrl")) {
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
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(TAG, "Error");
            }
            return null;
        }

        public void onProgressUpdate(Integer...args) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(args[0]);

            leftButton.setVisibility(View.INVISIBLE);
            rightButton.setVisibility(View.INVISIBLE);
            upButton.setVisibility(View.INVISIBLE);
            downButton.setVisibility(View.INVISIBLE);
        }

        public void onPostExecute(String fromDoInBackground) {
            progressBar.setVisibility(View.INVISIBLE);
            longitudeTextView.setVisibility(View.VISIBLE);
            latitudeTextView.setVisibility(View.VISIBLE);
            longitudeTextView.setText(currentUrl.longitude);
            latitudeTextView.setText(currentUrl.latitude);
            mapView.setImageBitmap(image);

            leftButton.setVisibility(View.VISIBLE);
            rightButton.setVisibility(View.VISIBLE);
            upButton.setVisibility(View.VISIBLE);
            downButton.setVisibility(View.VISIBLE);

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
        switch (item.getItemId()) {
            case R.id.itemZoomIn:
                zoom += 1;
                zoom();
                if (zoom > 10){
                    move_lat_long = 0.05;
                }
                Toast.makeText(getApplicationContext(), "You have zoomed in. Zoom level is now " + Integer.toString(zoom) , Toast.LENGTH_SHORT).show();
                break;
            case R.id.itemZoomOut:
                zoom -= 1;
                zoom();
                if (zoom <= 10){
                    move_lat_long = 0.5;
                }
                Toast.makeText(getApplicationContext(), "You have zoomed out. Zoom level is now " + Integer.toString(zoom) , Toast.LENGTH_SHORT).show();
                break;
            case R.id.helpItem:
                Dialog helpDialog = new Dialog(Activity2.this);
                helpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                helpDialog.setContentView(R.layout.activity_2_help_dialog);
                Button okButton = helpDialog.findViewById(R.id.okButton);
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        helpDialog.cancel();
                    }
                });
                helpDialog.show();
                break;
            case R.id.themeItem:

                break;
        }
        return true;
    }

    // Add actions to navigation drawer
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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

    public void moveMap() {
        MapQuery req = new MapQuery();
        req.execute(currentUrl.returnUrl());
        latitudeTextView.setText(currentUrl.latitude);
        longitudeTextView.setText(currentUrl.longitude);
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

        public void moveRight() {
            double longi = Double.parseDouble(this.longitude);
            this.longitude = Double.toString(longi + move_lat_long);
        }

        public void moveLeft() {
            double longi = Double.parseDouble(this.longitude);
            this.longitude = Double.toString(longi - move_lat_long);
        }

        public void moveUp() {
            double lat = Double.parseDouble(this.latitude);
            this.latitude = Double.toString(lat + move_lat_long);
        }

        public void moveDown() {
            double lat = Double.parseDouble(this.latitude);
            this.latitude = Double.toString(lat - move_lat_long);
        }

        public String returnUrl() {
            return start + latitude + "," + longitude + "?zl=" + zoom + end;
        }
    }

    // Close The Virtual keyboard
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