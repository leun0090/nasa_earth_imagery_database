package com.example.nasaearthimagerydatabase;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;


public class Activity2 extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {

    private static final String TAG = "Activity 2";

    String latitude = "43.6532";
    String longitude = "-79.3832";

    String urlMap = "https://dev.virtualearth.net/REST/V1/Imagery/Metadata/Aerial/"+ latitude +"," + longitude + "?zl=18&o=xml&ms=500,500&key=At7y4aOtMy4Uopf8cD8cu_um0-YGyp5nlzPLLDBxLmgDN4o6DUkvk0ZTs4QpYh1O";
    ProgressBar progressBar;
    ImageView mapView;
    Bitmap image = null;

    String imageUrl;

    TextView longitudeTextView;
    TextView latitudeTextView;
    TextView progressLabel;

    Button favoriteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);

        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        mapView = (ImageView) findViewById(R.id.mapView);
        latitudeTextView = (TextView) findViewById(R.id.latitudeTextView);
        longitudeTextView = (TextView) findViewById(R.id.longitudeTextView);
        progressLabel = (TextView) findViewById(R.id.progressLabel);

        MapQuery req = new MapQuery();
        req.execute(urlMap);


        favoriteButton = (Button) findViewById(R.id.favoriteButton);
        favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Snackbar.make(favoriteButton, "Your map location has been favorited", Snackbar.LENGTH_LONG)
                        .show();
            }

        });


        //This gets the toolbar from the layout:
        Toolbar tBar = findViewById(R.id.toolbar);

        //This loads the toolbar, which calls onCreateOptionsMenu below:
        setSupportActionBar(tBar);


        //For NavigationDrawer:
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                drawer, tBar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    private class MapQuery extends AsyncTask<String, Integer, String>
    {
        protected void onPreExecute() {
            longitudeTextView.setVisibility(View.GONE);
            latitudeTextView.setVisibility(View.GONE);


        }

        @Override
        protected String doInBackground(String... args) {

            try {

                URL url = new URL(urlMap);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                InputStream response = urlConnection.getInputStream();


                // TEMPERATURE
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();
                xpp.setInput( response  , "UTF-8");

                String parameter = null;
                int eventType = xpp.getEventType(); //The parser is currently at START_DOCUMENT


                while(eventType != XmlPullParser.END_DOCUMENT) {
                    if(eventType == XmlPullParser.START_TAG) {
                        if(xpp.getName().equals("ImageUrl"))
                        {
                            imageUrl = xpp.nextText();
                            Log.i(TAG, "imageUrl" + imageUrl);
                            publishProgress(10);

                        }
                    }
                    eventType = xpp.next(); //move to the next xml event and store it in a variable
                }

                publishProgress(20);
                URL iconUrl = new URL(imageUrl);
                publishProgress(30);
                HttpURLConnection iconConnection = (HttpURLConnection) iconUrl.openConnection();
                publishProgress(40);
                iconConnection.connect();
                publishProgress(50);
                int responseCode = iconConnection.getResponseCode();
                publishProgress(60);
                if (responseCode == 200) {
                    image = BitmapFactory.decodeStream(iconConnection.getInputStream());
                    publishProgress(80);
                }
                publishProgress(100);
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
            progressLabel.setVisibility(View.GONE);

            longitudeTextView.setVisibility(View.VISIBLE);
            latitudeTextView.setVisibility(View.VISIBLE);

            longitudeTextView.setText(longitude);
            latitudeTextView.setText(latitude);

            mapView.setImageBitmap(image);

        }
    }




// TOOLBAR
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        String message = null;
        switch(item.getItemId()) {
            case R.id.item1:
                message = "You clicked on Activity 1";
                break;
            case R.id.item2:
                message = "You clicked on Activity 2";
                break;
            case R.id.item3:
                message = "You clicked on Activity 3";

                Intent activityTwoIntent = new Intent(getApplicationContext(), Activity2.class);
                startActivity(activityTwoIntent);

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



    // NAVIGATION DRAWER

    // Needed for the OnNavigationItemSelected interface:
    @Override
    public boolean onNavigationItemSelected( MenuItem item) {

        String message = null;

        switch(item.getItemId())
        {
            case R.id.item1:
                message = "You clicked on Activity 1";
                break;
            case R.id.item2:
                message = "You clicked on Activity 2";
                break;
            case R.id.item3:
                message = "You clicked on Activity 3";
                break;
        }

        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);

        Toast.makeText(this, "NavigationDrawer: " + message, Toast.LENGTH_LONG).show();
        return false;
    }

}
