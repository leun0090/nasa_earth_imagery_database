package com.example.nasaearthimagerydatabase;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.widget.SearchView;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Arrays;

public class Activity3 extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "Test Activity 3";

    String loginEmail;

    ArrayList<Place> initialList = new ArrayList<>();
    ArrayList<Place> placesList = new ArrayList<>();
    int positionClicked = 0;
    MyOwnAdapter myAdapter;
    SQLiteDatabase db;

    SharedPreferences sharedPreferences = null;
    public static final String DEFAULT="N/A";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_3);

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

        //Email
        sharedPreferences = getSharedPreferences("Bing", Context.MODE_PRIVATE);
        loginEmail = sharedPreferences.getString("EMAIL",DEFAULT);
        TextView emailTextView = findViewById(R.id.emailTextView);
        emailTextView.setText("All favorite places by: " + loginEmail);

        // DATABASE
        loadDataFromDatabase();
        ListView theList = (ListView)findViewById(R.id.the_list);
        myAdapter = new MyOwnAdapter();
        theList.setAdapter(myAdapter);
        theList.setOnItemClickListener((list, item, position, id) -> {
            Toast.makeText(getApplicationContext(), "Data was saved successfully ", Toast.LENGTH_LONG).show();
        });

        // ADD A PLACE
        String title = getIntent().getStringExtra("TITLE");
        String latitude = getIntent().getStringExtra("LATITUDE");
        String longitude = getIntent().getStringExtra("LONGITUDE");
        String description = getIntent().getStringExtra("DESCRIPTION");

        String stars = getIntent().getStringExtra("STARS");
        String zoom = getIntent().getStringExtra("ZOOM");

            if (TextUtils.isEmpty(title)) {
                return;
            }
            if (TextUtils.isEmpty(latitude)) {
                return;
            }
            if (TextUtils.isEmpty(longitude)) {
                return;
            }
            if (TextUtils.isEmpty(description)) {
                return;
            }
            if (TextUtils.isEmpty(stars)) {
                return;
            }
            if (TextUtils.isEmpty(zoom)) {
                return;
            }

            ContentValues newRowValues = new ContentValues();
            newRowValues.put(DbOpener.getCOL_TITLE(), title);
            newRowValues.put(DbOpener.getCOL_LATITUDE(), latitude);
            newRowValues.put(DbOpener.getCOL_LONGITUDE(), longitude);
            newRowValues.put( DbOpener.getCOL_DESCRIPTION(), description);
            newRowValues.put(DbOpener.getCOL_EMAIL(), loginEmail);
            newRowValues.put(DbOpener.getCOL_STARS(), stars);
            newRowValues.put(DbOpener.getCOL_ZOOM(), zoom);

            long newId = db.insert(DbOpener.getTABLE_NAME(), null, newRowValues);
            Place newPlace = new Place(title, latitude, longitude,description,loginEmail,stars,zoom, newId);
            placesList.add(newPlace);
            myAdapter.notifyDataSetChanged();



    } //End onCreate

    /**
     * Initialize top toolbar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                    ArrayList<Place> tempList = new ArrayList<>();
                    for (int i = 0; i < placesList.size(); i++){
                        Place aPlace = placesList.get(i);
                        if (aPlace.getTitle().equals(query)) {
                            tempList.add(aPlace);
                        }
                    }
                    placesList = tempList;
                    myAdapter.notifyDataSetChanged();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                Toast.makeText(getApplicationContext(), "close ", Toast.LENGTH_LONG).show();
                placesList = initialList;
                myAdapter.notifyDataSetChanged();
                return false;
            }
        });

        return true;
    }

    /**
     * Top toolbar items
     *
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.helpItem:
                Dialog helpDialog = new Dialog(Activity3.this);
                helpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                helpDialog.setContentView(R.layout.activity_2_help_dialog);
                Button okButton = helpDialog.findViewById(R.id.okButton);
                TextView helpDescription = (TextView) helpDialog.findViewById(R.id.helpDescription);
                helpDescription.setText("Select an item from the listview to view more details");
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



    /*
    * DATABASES
    *
    *
     */
    protected class MyOwnAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return placesList.size();
        }

        public Place getItem(int position){
            return placesList.get(position);
        }

        public View getView(int position, View old, ViewGroup parent) {
            Place thisRow = getItem(position);

            View newView = getLayoutInflater().inflate(R.layout.activity_test3_row_layout, parent, false );

            TextView rowTitle = (TextView)newView.findViewById(R.id.placeTitle);
            rowTitle.setText(thisRow.getTitle());

            RatingBar mRatingBar = newView.findViewById(R.id.ratingBar);
            String s = thisRow.getStars();
            mRatingBar.setRating(Character.getNumericValue(s.charAt(0)));

            TextView data = (TextView)newView.findViewById(R.id.data);
            data.setText(thisRow.getLatitude() + ", " + thisRow.getLongitude()) ;

            // Go back to activity 2
            ImageButton detailsButton =  newView.findViewById(R.id.detailsButton);
            detailsButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Dialog helpDialog = new Dialog(Activity3.this);
                    helpDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    helpDialog.setContentView(R.layout.activity_2_help_dialog);
                    Button okButton = helpDialog.findViewById(R.id.okButton);
                    TextView helpDescription = (TextView) helpDialog.findViewById(R.id.helpDescription);
                    helpDescription.setText(thisRow.getTitle());
                    TextView helpDescription2 = (TextView) helpDialog.findViewById(R.id.helpDescription2);
                    helpDescription2.setText(thisRow.getDescription());
                    TextView helpDescription3 = (TextView) helpDialog.findViewById(R.id.helpDescription3);
                    helpDescription3.setText("Latitude: " + thisRow.getLatitude()  + " Longitude: " + thisRow.getLongitude());
                    TextView helpDescription4 = (TextView) helpDialog.findViewById(R.id.helpDescription4);
                    helpDescription4.setText("Zoom Level: " + thisRow.getZoom());
                    TextView helpDescription5 = (TextView) helpDialog.findViewById(R.id.helpDescription5);
                    helpDescription5.setText("Email: " + thisRow.getEmail());
                    okButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            helpDialog.cancel();
                        }
                    });
                    helpDialog.show();
                }
            });

            // View details
            ImageButton viewButton =  newView.findViewById(R.id.viewButton);
            viewButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), Activity2.class);
                    intent.putExtra("LATITUDE", thisRow.getLatitude());
                    intent.putExtra("LONGITUDE", thisRow.getLongitude());
                    intent.putExtra("ZOOM", thisRow.getZoom());
                    startActivity(intent);
                }
            });

            // Delete
            ImageButton deleteButton =  newView.findViewById(R.id.deleteButton);
            deleteButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    placesList.remove(position);
                    deletePlace(thisRow);
                    notifyDataSetChanged();
                }
            });

            return newView;
        }
        public long getItemId(int position)
        {
            return getItem(position).getId();
        }
    }

    protected void deletePlace(Place c) {
        db.delete(DbOpener.getTABLE_NAME(), DbOpener.COL_ID + "= ?", new String[] {Long.toString(c.getId())});
    }

    private void loadDataFromDatabase() {
        DbOpener dbOpener = new DbOpener(this);
        db = dbOpener.getWritableDatabase();

        String [] columns = {DbOpener.COL_ID, DbOpener.COL_TITLE, DbOpener.COL_LATITUDE, DbOpener.COL_LONGITUDE, DbOpener.COL_DESCRIPTION, DbOpener.COL_EMAIL, DbOpener.COL_STARS, DbOpener.COL_ZOOM};
        Cursor results = db.query(false, DbOpener.TABLE_NAME, columns, null, null, null, null, null, null);

        int titleColumnIndex = results.getColumnIndex(DbOpener.COL_TITLE);
        int latitudeColumnIndex = results.getColumnIndex(DbOpener.COL_LATITUDE);
        int longitudeColumnIndex = results.getColumnIndex(DbOpener.COL_LONGITUDE);
        int descriptionColumnIndex = results.getColumnIndex(DbOpener.COL_DESCRIPTION);
        int emailColumnIndex = results.getColumnIndex(DbOpener.COL_EMAIL);
        int starsColumnIndex = results.getColumnIndex(DbOpener.COL_STARS);
        int zoomColumnIndex = results.getColumnIndex(DbOpener.COL_ZOOM);
        int idColIndex = results.getColumnIndex(DbOpener.COL_ID);

        while(results.moveToNext()) {

            String title = results.getString(titleColumnIndex);
            String latitude = results.getString(latitudeColumnIndex);
            String longitude = results.getString(longitudeColumnIndex);
            String description = results.getString(descriptionColumnIndex);
            String email = results.getString(emailColumnIndex);
            String stars = results.getString(starsColumnIndex);
            String zoom = results.getString(zoomColumnIndex);
            long id = results.getLong(idColIndex);

            if (email.equals(loginEmail)) {
                placesList.add(new Place(title, latitude, longitude, description, email, stars, zoom, id));
            }

        }

        initialList = placesList;
        printCursor(results, db.getVersion());
    }

    private void printCursor(Cursor c, int version) {
        Log.v(TAG, "The database number = " + version);
        Log.v(TAG, "The number of columns in the cursor = " + c.getColumnCount());

        String[] columnNames = c.getColumnNames();
        Log.v(TAG, "The names of columns in the cursor = " + Arrays.toString(columnNames));

        Cursor  cursor = db.rawQuery("select * from " +  DbOpener.TABLE_NAME() + " WHERE email='" + loginEmail + "'",null);
        int titleColumnIndex = cursor.getColumnIndex(DbOpener.COL_TITLE());
        int latitudeColumnIndex = cursor.getColumnIndex(DbOpener.COL_LATITUDE());
        int longitudeColumnIndex = cursor.getColumnIndex(DbOpener.COL_LONGITUDE());
        int descriptionColumnIndex = cursor.getColumnIndex(DbOpener.COL_DESCRIPTION());
        int starsColumnIndex = cursor.getColumnIndex(DbOpener.COL_STARS());
        int zoomColumnIndex = cursor.getColumnIndex(DbOpener.COL_ZOOM());
        int idColIndex = cursor.getColumnIndex(DbOpener.COL_ID());

        while(cursor.moveToNext()) {
            String title = cursor.getString(titleColumnIndex);
            String latitude = cursor.getString(latitudeColumnIndex);
            String longitude = cursor.getString(longitudeColumnIndex);
            String description = cursor.getString(descriptionColumnIndex);
            String stars = cursor.getString(starsColumnIndex);
            String zoom = cursor.getString(zoomColumnIndex);
            long id = cursor.getLong(idColIndex);
            Log.v(TAG, "_id:" + id + " - title:" + title);
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
