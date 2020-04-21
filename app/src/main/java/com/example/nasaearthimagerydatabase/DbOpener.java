package com.example.nasaearthimagerydatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DbOpener extends SQLiteOpenHelper {

    protected final static String DATABASE_NAME = "TestDB";
    protected final static int VERSION_NUM = 1;
    public final static String TABLE_NAME = "LOCATIONS";
    public final static String COL_TITLE = "TITLE";
    public final static String COL_LATITUDE = "LATITUDE";
    public final static String COL_LONGITUDE = "LONGITUDE";
    public final static String COL_DESCRIPTION = "DESCRIPTION";
    public final static String COL_EMAIL = "EMAIL";
    public final static String COL_STARS = "STARS";
    public final static String COL_ZOOM = "ZOOM";
    public final static String COL_ID = "_id";

    public DbOpener(Context ctx)
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
}