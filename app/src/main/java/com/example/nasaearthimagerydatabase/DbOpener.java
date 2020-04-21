package com.example.nasaearthimagerydatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DbOpener extends SQLiteOpenHelper {

    private static String DATABASE_NAME = "Locations";
    private  static int VERSION_NUM = 1;
    private  static String TABLE_NAME = "LOCATIONS";
    private  static String COL_TITLE = "TITLE";
    private  static String COL_LATITUDE = "LATITUDE";
    private  static String COL_LONGITUDE = "LONGITUDE";
    private  static String COL_DESCRIPTION = "DESCRIPTION";
    private  static String COL_EMAIL = "EMAIL";
    private  static String COL_STARS = "STARS";
    private  static String COL_ZOOM = "ZOOM";

    private DbOpener(Context ctx)
    {
        super(ctx, DATABASE_NAME, null, VERSION_NUM);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("CREATE TABLE " + TABLE_NAME +
                " (_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_TITLE + " text,"
                + COL_LATITUDE + " text,"
                + COL_LONGITUDE + " text,"
                + COL_DESCRIPTION + " text,"
                + COL_EMAIL + " text,"
                + COL_STARS + " text,"
                + COL_ZOOM + " text,"
                + " text);");  // add or remove columns
    }

    //this function gets called if the database version on your device is lower than VERSION_NUM
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {   //Drop the old table:
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME);

        //Create the new table:
        onCreate(db);
    }

    //this function gets called if the database version on your device is higher than VERSION_NUM
    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {   //Drop the old table:
        db.execSQL( "DROP TABLE IF EXISTS " + TABLE_NAME);

        //Create the new table:
        onCreate(db);
    }

    public String getTABLE_NAME() {
        return TABLE_NAME;
    }
    public String getCOL_TITLE() {
        return COL_TITLE;
    }
    public String getCOL_LATITUDE() {
        return COL_LATITUDE;
    }
    public String getCOL_LONGITUDE() {
        return COL_LONGITUDE;
    }
    public String getCOL_DESCRIPTION() {
        return COL_DESCRIPTION;
    }
    public String getCOL_EMAIL() {
        return COL_EMAIL;
    }
    public String getCOL_STARS() {
        return COL_STARS;
    }
    public String getCOL_ZOOM() {
        return COL_ZOOM;
    }
}