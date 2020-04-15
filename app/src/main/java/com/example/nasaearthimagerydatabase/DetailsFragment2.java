package com.example.nasaearthimagerydatabase;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
 * <h1>Details Fragment for Activity 2</h1>
 * This fragment stores the coffeeshop listview
 * The listview is populated by an asynctask which calls
 * the bing location search api
 *
 * @author  Pak Leung
 * @version 1.0
 */
public class DetailsFragment2 extends Fragment {

    private static final String TAG = "DetailsFragment2";
    private boolean isTablet;
    private Bundle dataFromActivity;
    private AppCompatActivity parentActivity;
    String coffeeUrl;
    private ArrayList < CoffeePlace > coffeePlaces;
    private ListView coffeeListView;
    private PlacesAdapter myAdapter;
    View result;
    TextView resultTextView;

    public void setTablet(boolean tablet) {
        isTablet = tablet;
    }

    public DetailsFragment2() {
        coffeePlaces = new ArrayList < CoffeePlace > ();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        dataFromActivity = getArguments();
        String latitude = this.getArguments().getString("LATITUDE");
        String longitude = this.getArguments().getString("LONGITUDE");

        // ListView
        coffeeUrl = "https://dev.virtualearth.net/REST/v1/LocalSearch/?query=coffee&userLocation=" + latitude + "," + longitude + "&key=ApzeMYSxJulF36ptSnMPfbN9Tb3ZDRj5820D3_YGcudYRWnStu_hn7ADXK2-Ddkz";
        //coffeeUrl = "https://dev.virtualearth.net/REST/v1/LocalSearch/?query=coffee&userLocation=47.602038,-122.333964&key=ApzeMYSxJulF36ptSnMPfbN9Tb3ZDRj5820D3_YGcudYRWnStu_hn7ADXK2-Ddkz";
        CoffeeQuery req = new CoffeeQuery();
        req.execute(coffeeUrl);

        result = inflater.inflate(R.layout.fragment_details2, container, false);

        resultTextView = (TextView)  result.findViewById(R.id.resultTextView);

        // get the delete button, and add a click listener:
        Button hideButton = (Button) result.findViewById(R.id.hideButton);
        hideButton.setOnClickListener(clk -> {
            coffeePlaces.clear();
            parentActivity.getSupportFragmentManager().beginTransaction().remove(this).commit();
        });

        return result;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        parentActivity = (AppCompatActivity) context;
    }

    // ASYNC TASK TO PULL LOCATION OF COFFEE SHOPS
    private class CoffeeQuery extends AsyncTask < String, Integer, String > {
        protected void onPreExecute() {}
        protected String doInBackground(String...args) {

            try {

                URL url = new URL(args[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream response = urlConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(response, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                String result = sb.toString();

                // convert string to JSON:
                JSONObject coffeeReport = new JSONObject(result);
                String data = coffeeReport.getString("resourceSets");
                JSONArray jsonArr = new JSONArray(data);
                JSONObject jsonObj = jsonArr.getJSONObject(0);
                String resources = jsonObj.getString("resources");
                JSONArray resourcesArr = new JSONArray(resources);

                // Retrieve Coffeeshop details like address
                for (int i = 0; i < resourcesArr.length(); i++) {
                    JSONObject jsonObject = resourcesArr.getJSONObject(i);
                    String name = jsonObject.getString("name");
                    String address = jsonObject.getString("Address");
                    JSONObject addObj = new JSONObject(address);
                    String formattedAddress = addObj.getString("formattedAddress");
                    String phoneNumber = jsonObject.getString("PhoneNumber");
                    String website = jsonObject.getString("Website");
                    coffeePlaces.add(new CoffeePlace(name, formattedAddress, phoneNumber, website));
                }
            } catch (Exception e) {}

            return null;
        }

        //Type 2
        public void onProgressUpdate(Integer...args) {}
        //Type3
        public void onPostExecute(String fromDoInBackground) {

            // ListView
            coffeeListView = (ListView) result.findViewById(R.id.theListView);
            coffeeListView.setAdapter(myAdapter = new PlacesAdapter());

            resultTextView.setText("Total: " + coffeePlaces.size());

            coffeeListView.setOnItemClickListener((parent, view, position, id) -> {
                CoffeePlace selectedCoffee = coffeePlaces.get(position);
                Dialog helpDialog = new Dialog(getContext());
                helpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                helpDialog.setContentView(R.layout.activity_2_help_dialog);

                TextView helpDescription = (TextView) helpDialog.findViewById(R.id.helpDescription);
                helpDescription.setText(selectedCoffee.name);
                TextView helpDescription2 = (TextView) helpDialog.findViewById(R.id.helpDescription2);
                helpDescription2.setText(selectedCoffee.address);
                TextView helpDescription3 = (TextView) helpDialog.findViewById(R.id.helpDescription3);
                helpDescription3.setText(selectedCoffee.telephone);
                TextView helpDescription4 = (TextView) helpDialog.findViewById(R.id.helpDescription4);
                helpDescription4.setText(selectedCoffee.website);

                Button okButton = helpDialog.findViewById(R.id.okButton);
                okButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        helpDialog.cancel();
                    }
                });
                helpDialog.show();
            });
        }
    }

    // Listview adapter
    private class PlacesAdapter extends BaseAdapter {
        public int getCount() {
            return coffeePlaces.size();
        }
        public Object getItem(int position) {
            return "This is row " + position;
        }
        public long getItemId(int position) {
            return (long) position;
        }
        public View getView(int position, View convertView, ViewGroup parent) {
            CoffeePlace aPlace = coffeePlaces.get(position);
            View newView = getLayoutInflater().inflate(R.layout.activity_2_listview_row_layout, parent, false);
            TextView placeName = (TextView) newView.findViewById(R.id.placeName);
            placeName.setText(aPlace.name);
            TextView placeAddress = (TextView) newView.findViewById(R.id.placeAddress);
            placeAddress.setText(aPlace.address);
            return newView;
        }
    }
}