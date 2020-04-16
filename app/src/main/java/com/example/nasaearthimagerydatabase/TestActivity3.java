package com.example.nasaearthimagerydatabase;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Arrays;

public class TestActivity3 extends AppCompatActivity  implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "Test Activity 3";

    private MenuItem itemZoomIn;
    private MenuItem itemZoomOut;

    Button receiveButton;
    EditText chatEdit;

    ArrayList<Place> placesList = new ArrayList<Place>();
    int positionClicked = 0;
    MyOwnAdapter myAdapter;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test3);

        receiveButton = findViewById(R.id.receiveButton);
        chatEdit = findViewById(R.id.chatEdit);

        String title = getIntent().getStringExtra("TITLE");
        String latitude = getIntent().getStringExtra("LATITUDE");
        String longitude = getIntent().getStringExtra("LONGITUDE");
        String description = getIntent().getStringExtra("DESCRIPTION");


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


        // DATABASE
        loadDataFromDatabase();
        ListView theList = (ListView)findViewById(R.id.the_list);
        myAdapter = new MyOwnAdapter();
        theList.setAdapter(myAdapter);

        // ADD A PLACE
        receiveButton.setOnClickListener( click -> {
            String name = chatEdit.getText().toString();
            if (TextUtils.isEmpty(name)) {
                return;
            }
            ContentValues newRowValues = new ContentValues();
            newRowValues.put(DbOpener.COL_MESSAGE, name);
            long newId = db.insert(DbOpener.TABLE_NAME, null, newRowValues);
            Place newPlace = new Place(name, newId);
            placesList.add(newPlace);
            myAdapter.notifyDataSetChanged();
            chatEdit.setText("");
            closeKeyboard();
        });



        // DELETE PLACE
        theList.setOnItemLongClickListener(( parent,  view,  position,  id) -> {
            Place selectedPlace = placesList.get(position);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle( "Delete")
                    .setMessage("Delete: " + position)
                    .setPositiveButton("Yes", (click, arg) -> {
                        deletePlace(selectedPlace); //remove the contact from database
                        placesList.remove(position); //remove the contact from contact list
                        myAdapter.notifyDataSetChanged();
                    })
                    .setNegativeButton("No", (click, arg) -> { })
                    .setView(getLayoutInflater().inflate(R.layout.activity_test3_row_layout, null))
                    .create().show();
            return true;
        });



    } //End onCreate

    /**
     * Initialize top toolbar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.top_menu2, menu);

        itemZoomIn = menu.findItem(R.id.itemZoomIn);
        itemZoomOut = menu.findItem(R.id.itemZoomOut);
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
        switch (item.getItemId()) {
            case R.id.helpItem:
                Dialog helpDialog = new Dialog(TestActivity3.this);
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

            TextView rowTitle = (TextView)newView.findViewById(R.id.chatTitle);
            rowTitle.setText(thisRow.getMessage());
            return newView;
        }
        public long getItemId(int position)
        {
            return getItem(position).getId();
        }
    }

    protected void deletePlace(Place c) {
        db.delete(DbOpener.TABLE_NAME, DbOpener.COL_ID + "= ?", new String[] {Long.toString(c.getId())});
    }

    private void loadDataFromDatabase() {
        DbOpener dbOpener = new DbOpener(this);
        db = dbOpener.getWritableDatabase();

        String [] columns = {DbOpener.COL_ID, DbOpener.COL_MESSAGE};
        Cursor results = db.query(false, DbOpener.TABLE_NAME, columns, null, null, null, null, null, null);

        int titleColumnIndex = results.getColumnIndex(DbOpener.COL_MESSAGE);

        int idColIndex = results.getColumnIndex(DbOpener.COL_ID);

        while(results.moveToNext()) {
            String title = results.getString(titleColumnIndex);
            long id = results.getLong(idColIndex);
            placesList.add(new Place(title, id));
        }

        printCursor(results, db.getVersion());
    }

    private void printCursor(Cursor c, int version) {
        Log.v(TAG, "The database number = " + version);
        Log.v(TAG, "The number of columns in the cursor = " + c.getColumnCount());

        String[] columnNames = c.getColumnNames();
        Log.v(TAG, "The names of columns in the cursor = " + Arrays.toString(columnNames));

        Cursor  cursor = db.rawQuery("select * from " +  DbOpener.TABLE_NAME,null);
        int titleColumnIndex = cursor.getColumnIndex(DbOpener.COL_MESSAGE);
        int idColIndex = cursor.getColumnIndex(DbOpener.COL_ID);

        while(cursor.moveToNext()) {
            String title = cursor.getString(titleColumnIndex);
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
