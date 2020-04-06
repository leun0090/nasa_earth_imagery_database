package com.example.nasaearthimagerydatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;


public class DbNasaEarthImagery extends SQLiteOpenHelper {
    public static final String DB_NAME = "LocationsDB";
    public static final String TABLE_NAME = "EarthImgTable";
    public static final String TITLE = "Title";
    public static final String LATITUDE = "Latitude";
    public static final String LONGITUDE = "Longitude";
    public static final String DESCRIPTION = "Description";
    public static final String FAVORITE = "Favorite";
    public static final String IMAGE = "Image";
    public static final String LOCATION_ID = "_id";
    public static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+" ("+LOCATION_ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+
            TITLE+" TEXT, "+LATITUDE+" TEXT,"+LONGITUDE+" TEXT,"+DESCRIPTION+" TEXT,"+IMAGE+" BLOB,"+FAVORITE+" INTEGER);";

    public DbNasaEarthImagery(Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long insertLocation(String title, String latitude, String longitude, String description, Bitmap image, boolean favorite) throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TITLE, title);
        cv.put(LATITUDE, latitude);
        cv.put(LONGITUDE, longitude);
        cv.put(DESCRIPTION, description);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        //image.compress(Bitmap.CompressFormat.PNG, 100, bos);
        //byte[] bArray = bos.toByteArray();
        //cv.put(IMAGE, bArray);
        if (favorite) cv.put(FAVORITE, 1);
        else cv.put(FAVORITE, 0);
        return db.insert(TABLE_NAME, null, cv);
    }

    public Cursor readAllLocationsToCursor(){
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "Select * from "+TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }

    public void deleteLocation(long id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, LOCATION_ID + " = " + id, null);
        db.close();
    }


    public void getImageById(long id) {

    }
}
