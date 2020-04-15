package com.example.nasaearthimagerydatabase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import java.io.ByteArrayOutputStream;
import java.sql.SQLException;
import java.util.ArrayList;


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
    public static final String IMAGE_PATH = "ImagePath";
    public static final String ZOOM = "Zoom";
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" + LOCATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TITLE + " TEXT, " + LATITUDE + " TEXT," + LONGITUDE + " TEXT," + DESCRIPTION + " TEXT," + IMAGE + " BLOB," + IMAGE_PATH + " TEXT," + FAVORITE + " INTEGER," + ZOOM + " INTERER);";

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

    //public long insertLocation(String title, String latitude, String longitude, String description, Bitmap image, String imagePath, boolean favorite) throws SQLException {
    public long insertLocation(MapElement me) throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(TITLE, me.getTitle());
        cv.put(LATITUDE, me.getLatitude());
        cv.put(LONGITUDE, me.getLongitude());
        cv.put(DESCRIPTION, me.getDescription());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        if (me.getImage()!=null) me.getImage().compress(Bitmap.CompressFormat.PNG, 100, bos);
        byte[] bArray = bos.toByteArray();
        cv.put(IMAGE, bArray);
        cv.put(IMAGE_PATH, me.getImage_path());
        cv.put(FAVORITE, me.getFavorite());
        cv.put(ZOOM, me.getZoom());
        return db.insert(TABLE_NAME, null, cv);
    }

    public Cursor readAllLocationsToCursor() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "Select * from " + TABLE_NAME;
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }

    public ArrayList<MapElement> getListElements() {
        ArrayList<MapElement> list_map_elements = new ArrayList<>();;
        Cursor cursor = readAllLocationsToCursor();
        int i = cursor.getCount();
        MapElement[] loc = new MapElement[i];
        if (cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                Bitmap bmp=null;
                if (cursor.getBlob(5)!=null) {
                byte[] byteArray = cursor.getBlob(5);
                bmp = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);}
                loc[--i] = new MapElement(cursor.getLong(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), bmp, cursor.getInt(7),cursor.getInt(8));
                list_map_elements.add(loc[i]);
            }
        }
        return list_map_elements;
    }

    public void deleteLocation(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, LOCATION_ID + " = " + id, null);
        db.close();
    }


    public void deleteAllLocation() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
        db.close();
    }
    public void deleteTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL(CREATE_TABLE);;
        db.close();
    }


}
