package com.example.nasaearthimagerydatabase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

/**
 * <h1>Activity 2</h1>
 * This activity is uses an asynctask that call the bing api
 * which fetches an image
 *
 * @author  Pak Leung
 * @version 1.0
 */

public class Activity2 extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private static final String TAG = "Activity 2";
    String latitude;
    String longitude;
    String urlMap;
    String traceId;
    int zoom = 12;
    ProgressBar progressBar;
    ImageView mapView;
    ImageView photoView;
    Bitmap image = null;
    String imageUrl;
    TextView longitudeTextView;
    TextView latitudeTextView;
    ImageButton leftButton;
    ImageButton rightButton;
    ImageButton upButton;
    ImageButton downButton;
    Button favoriteButton;
    ApiUrl currentUrl;
    Boolean isTablet;
    DetailsFragment2 dFragment;
    Double move_lat_long = 0.05;
    String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);

        // Initialize layout items
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        mapView = (ImageView) findViewById(R.id.mapView);
        photoView = (ImageView) findViewById(R.id.photoView);
        latitudeTextView = (TextView) findViewById(R.id.latitudeTextView);
        longitudeTextView = (TextView) findViewById(R.id.longitudeTextView);
        favoriteButton = (Button) findViewById(R.id.favoriteButton);
        leftButton = (ImageButton) findViewById(R.id.leftButton);
        rightButton = (ImageButton) findViewById(R.id.rightButton);
        upButton = (ImageButton) findViewById(R.id.upButton);
        downButton = (ImageButton) findViewById(R.id.downButton);


        // Load data from previous activity
        latitude = getIntent().getStringExtra("LATITUDE");
        longitude = getIntent().getStringExtra("LONGITUDE");
        String zoomData = getIntent().getStringExtra("ZOOM");
        if (zoomData != null) {
            zoom = Integer.parseInt(zoomData);
        }

        // default
        if (latitude == null || longitude == null) {
            latitude = "40.7128";
            longitude = "-74.0060";
        }

        // urlMap = "https://dev.virtualearth.net/REST/V1/Imagery/Metadata/Aerial/40.7128,-74.0060?zl=14&o=xml&ms=500,500&key=At7y4aOtMy4Uopf8cD8cu_um0-YGyp5nlzPLLDBxLmgDN4o6DUkvk0ZTs4QpYh1O";
        //urlMap = "https://dev.virtualearth.net/REST/V1/Imagery/Metadata/Aerial/"+ latitude +"," + longitude + "?zl=" + Integer.toString(zoom) + "&o=xml&ms=500,500&key=At7y4aOtMy4Uopf8cD8cu_um0-YGyp5nlzPLLDBxLmgDN4o6DUkvk0ZTs4QpYh1O";
        currentUrl = new ApiUrl(latitude, longitude, Integer.toString(zoom));
        urlMap = currentUrl.returnUrl();
        MapQuery init = new MapQuery();
        init.execute(urlMap);


        // Add click listener to favorite button
        favoriteButton.setOnClickListener(c -> {
            Dialog favoriteDialog = new Dialog(Activity2.this);
            favoriteDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            favoriteDialog.setContentView(R.layout.activity_2_favorite_dialog);
            EditText titleEditText = (EditText) favoriteDialog.findViewById(R.id.titleEditText);
            RatingBar simpleRatingBar = (RatingBar) favoriteDialog.findViewById(R.id.simpleRatingBar);
            EditText descriptionEditText = (EditText) favoriteDialog.findViewById(R.id.descriptionEditText);

            favoriteDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

            Button okButton = favoriteDialog.findViewById(R.id.okButton);

            okButton.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if (titleEditText.getText().toString().equals("")){
                        Toast.makeText(getApplicationContext(), R.string.favorite_validation, Toast.LENGTH_LONG).show();
                    }
                    else {
                        Intent testIntent3 = new Intent(Activity2.this, Activity3.class);
                        testIntent3.putExtra("LATITUDE", latitude);
                        testIntent3.putExtra("LONGITUDE", longitude);
                        testIntent3.putExtra("TITLE", titleEditText.getText().toString());
                        testIntent3.putExtra("DESCRIPTION", descriptionEditText.getText().toString());
                        testIntent3.putExtra("STARS", String.valueOf(simpleRatingBar.getRating()));
                        testIntent3.putExtra("ZOOM",  String.valueOf(zoom));
                        startActivity(testIntent3);
                        closeKeyboard();
                        favoriteDialog.cancel();
                    }

                    return true;
                }
            });


            Button cancelButton = favoriteDialog.findViewById(R.id.cancelButton);
            cancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    favoriteDialog.cancel();
                }
            });
            favoriteDialog.show();
        });

        // moveLeft
        leftButton.setOnClickListener(c -> {
            Snackbar.make(leftButton, R.string.move_left, Snackbar.LENGTH_LONG).show();
            currentUrl.moveLeft();
            moveMap();
        });

        // moveRight
        rightButton.setOnClickListener(c -> {
            Snackbar.make(rightButton, R.string.move_right, Snackbar.LENGTH_LONG).show();
            currentUrl.moveRight();
            moveMap();
        });

        upButton.setOnClickListener(c -> {
            Snackbar.make(upButton, R.string.move_top, Snackbar.LENGTH_LONG).show();
            currentUrl.moveUp();
            moveMap();
        });

        downButton.setOnClickListener(c -> {
            Snackbar.make(downButton, R.string.move_bottom, Snackbar.LENGTH_LONG).show();
            currentUrl.moveDown();
            moveMap();
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

        // Bottom menu
        dFragment = new DetailsFragment2();
        isTablet = findViewById(R.id.fragmentLocation) != null;

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.itemHeart:
                        Intent intent = new Intent(getApplicationContext(), Activity3.class);
                        startActivity(intent);
                        break;


                    case R.id.itemCoffee:
                        Toast.makeText(getApplicationContext(), R.string.coffee_warning, Toast.LENGTH_LONG).show();

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
                            Intent activityTwoListviewIntent = new Intent(getApplicationContext(), Activity2_listview.class);
                            activityTwoListviewIntent.putExtra("LATITUDE", latitude);
                            activityTwoListviewIntent.putExtra("LONGITUDE", longitude);
                            startActivity(activityTwoListviewIntent);
                        }
                        break;
                }
                return true;
            }
        });

    }

    /**
     * This method uses an synctask that calls the bing api
     * which returns an image.
     *
     */
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
            progressBar.setVisibility(View.GONE);
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

    /**
     * Initialize top toolbar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_menu, menu);
        return true;
    }

    /**
     * Top toolbar items
     *
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.itemZoomIn:
                zoom += 1;
                zoom();
                if (zoom > 10){
                    move_lat_long = 0.05;
                }
                Toast.makeText(getApplicationContext(), "Zoom: " + zoom , Toast.LENGTH_SHORT).show();
                break;
            case R.id.itemZoomOut:
                zoom -= 1;
                zoom();
                if (zoom <= 10){
                    move_lat_long = 0.5;
                }
                Toast.makeText(getApplicationContext(), "Zoom: " + zoom , Toast.LENGTH_SHORT).show();
                break;
            case R.id.helpItem:
                Dialog helpDialog = new Dialog(Activity2.this);
                helpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                helpDialog.setContentView(R.layout.activity_2_help_dialog);
                Button okButton = helpDialog.findViewById(R.id.okButton);
                okButton.setOnClickListener(click -> helpDialog.cancel());
                helpDialog.show();
                break;
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

    /**
     * This function is called when the zoom keys on top toolbar
     * are pressed. This calls the api again with new parameters
     *
     */
    public void zoom() {
        MapQuery req = new MapQuery();
        currentUrl.changeZoom(Integer.toString(zoom));
        req.execute(currentUrl.returnUrl());

    }

    /**
     * This function is called when the arrow keys
     * are pressed. This calls the api again with new parameters
     *
     */
    public void moveMap() {
        MapQuery req = new MapQuery();
        req.execute(currentUrl.returnUrl());
        latitudeTextView.setText(currentUrl.latitude);
        longitudeTextView.setText(currentUrl.longitude);
    }


    /**
     * This class is used to store the api
     */
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


    /**
     *  CAMERA
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            photoView.setImageBitmap(imageBitmap);
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

}