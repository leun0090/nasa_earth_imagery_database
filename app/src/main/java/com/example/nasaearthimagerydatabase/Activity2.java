package com.example.nasaearthimagerydatabase;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Activity2 extends AppCompatActivity {

    private static final String TAG = "WeatherForecast";

    private static final String urlMaptest = "http://dev.virtualearth.net/REST/V1/Imagery/Map/Birdseye/45.4215,-75.6972/18?dir=0&ms=500,500&key=At7y4aOtMy4Uopf8cD8cu_um0-YGyp5nlzPLLDBxLmgDN4o6DUkvk0ZTs4QpYh1O";

    private static final String urlMap = "https://dev.virtualearth.net/REST/V1/Imagery/Metadata/Aerial/45.4215,-75.6972?zl=18&o=xml&ms=500,500&key=At7y4aOtMy4Uopf8cD8cu_um0-YGyp5nlzPLLDBxLmgDN4o6DUkvk0ZTs4QpYh1O";
    ProgressBar progressBar;
    ImageView mapView;
    Bitmap image = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_2);

        progressBar = (ProgressBar)findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);

        mapView = (ImageView) findViewById(R.id.mapView);


        MapQuery req = new MapQuery();
        req.execute(urlMap);

    }

    private class MapQuery extends AsyncTask<String, Integer, String>
    {
        protected void onPreExecute() {

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

                            publishProgress(0);

                            String imageUrl = xpp.nextText();

                            URL iconUrl = new URL(imageUrl);

                            publishProgress(33);

                            HttpURLConnection iconConnection = (HttpURLConnection) iconUrl.openConnection();

                            iconConnection.connect();

                            publishProgress(66);

                            int responseCode = iconConnection.getResponseCode();
                            if (responseCode == 200) {
                                image = BitmapFactory.decodeStream(iconConnection.getInputStream());
                            }

                            publishProgress(100);
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
            mapView.setImageBitmap(image);

        }

    }
}
