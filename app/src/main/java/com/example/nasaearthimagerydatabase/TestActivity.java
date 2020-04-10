package com.example.nasaearthimagerydatabase;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class TestActivity extends AppCompatActivity {

//    https://www.youtube.com/watch?v=Tdb_WSEEZbQ

    private static final String TAG = "TestActivity";

    String latitude;
    String longitude;

    EditText latitudeEditText;
    EditText longitudeEditText;
    Button randomButton;
    Button searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        // Initialize components
        latitudeEditText = (EditText) findViewById(R.id.latitudeEditText);
        longitudeEditText = (EditText) findViewById(R.id.longitudeEditText);
        randomButton = (Button) findViewById(R.id.randomButton);
        searchButton = (Button) findViewById(R.id.searchButton);

        randomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Load async task
                RandomCoordinates req = new RandomCoordinates();
                req.execute("https://api.3geonames.org/?randomland=yes");

            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Activity2.class);
                intent.putExtra("LATITUDE", latitudeEditText.getText().toString());
                intent.putExtra("LONGITUDE", longitudeEditText.getText().toString());
                startActivity(intent);
            }
        });
    }


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
                String parameter = null;
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