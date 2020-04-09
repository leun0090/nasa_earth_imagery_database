package com.example.nasaearthimagerydatabase;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailsFragment2 extends Fragment {

    private static final String TAG = "DetailsFragment2";

    private boolean isTablet;
    private Bundle dataFromActivity;
    private AppCompatActivity parentActivity;

    String coffeeUrl;
    private ArrayList<CoffeePlace> coffeePlaces;
    private ListView coffeeListView;
    private PlacesAdapter myAdapter;

    View result;

    public void setTablet(boolean tablet) { isTablet = tablet; }


    public DetailsFragment2() {
        // Required empty public constructor

        coffeePlaces = new ArrayList<CoffeePlace>();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        dataFromActivity = getArguments();
        String latitude = this.getArguments().getString("LATITUDE");
        String longitude = this.getArguments().getString("LONGITUDE");

        // ListView

        coffeeUrl = "https://dev.virtualearth.net/REST/v1/LocalSearch/?query=coffee&userLocation="+ latitude+ "," + longitude + "&key=ApzeMYSxJulF36ptSnMPfbN9Tb3ZDRj5820D3_YGcudYRWnStu_hn7ADXK2-Ddkz";
        //coffeeUrl = "https://dev.virtualearth.net/REST/v1/LocalSearch/?query=coffee&userLocation=47.602038,-122.333964&key=ApzeMYSxJulF36ptSnMPfbN9Tb3ZDRj5820D3_YGcudYRWnStu_hn7ADXK2-Ddkz";
        CoffeeQuery req = new CoffeeQuery();
        req.execute(coffeeUrl);


        result =  inflater.inflate(R.layout.fragment_details2, container, false);

        // get the delete button, and add a click listener:
        Button hideButton = (Button)result.findViewById(R.id.hideButton);
        hideButton.setOnClickListener( clk -> {
            coffeePlaces.clear();
            parentActivity.getSupportFragmentManager().beginTransaction().remove(this).commit();
        });

        return result;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        parentActivity = (AppCompatActivity)context;
    }


    // ASYNC TASK TO PULL LOCATION OF COFFEE SHOPS
    private class CoffeeQuery extends AsyncTask<String, Integer, String>
    {
        protected void onPreExecute() {
        }
        protected String doInBackground(String ... args) {

            try {

                // UV
                URL url = new URL(args[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream response = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(response, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null)
                {
                    sb.append(line + "\n");
                }
                String result = sb.toString(); //result is the whole string


                // convert string to JSON:
                JSONObject coffeeReport = new JSONObject(result);

                String data = coffeeReport.getString("resourceSets");
                JSONArray jsonArr = new JSONArray(data);
                JSONObject jsonObj = jsonArr.getJSONObject(0);
                String resources  = jsonObj.getString("resources");
                JSONArray resourcesArr = new JSONArray(resources);

                for (int i = 0; i < resourcesArr.length(); i++) {
                    JSONObject jsonObject = resourcesArr.getJSONObject(i);
                    String name  = jsonObject.getString("name");
                    String address  = jsonObject.getString("Address");
                    JSONObject addObj = new JSONObject(address);
                    String formattedAddress = addObj.getString("formattedAddress");
                    coffeePlaces.add(new CoffeePlace(name, formattedAddress));

                }


            }
            catch (Exception e) {
            }

            return null;
        }

        //Type 2
        public void onProgressUpdate(Integer ... args) {
        }
        //Type3
        public void onPostExecute(String fromDoInBackground) {

            // ListView

            coffeeListView = (ListView) result.findViewById(R.id.theListView);
            coffeeListView.setAdapter( myAdapter = new PlacesAdapter());

        }

    }


    // Listview adapter
    private class PlacesAdapter extends BaseAdapter {
        public int getCount() { return coffeePlaces.size();}
        public Object getItem(int position) { return "This is row " + position; }
        public long getItemId(int position) { return (long) position; }
        public View getView(int position, View convertView, ViewGroup parent) {
            CoffeePlace aPlace = coffeePlaces.get(position);
            View newView = getLayoutInflater().inflate(R.layout.activity_2_extra_row_layout, parent, false);
            TextView placeName = (TextView)newView.findViewById(R.id.placeName);
            placeName.setText(aPlace.name);
            TextView placeAddress = (TextView)newView.findViewById(R.id.placeAddress);
            placeAddress.setText(aPlace.address);

            return newView;
        }
    }

}