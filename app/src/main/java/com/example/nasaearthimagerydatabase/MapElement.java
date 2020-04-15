package com.example.nasaearthimagerydatabase;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MapElement {
    private long id=0;
    private String title = "";
    private String latitude = "";
    private String longitude = "";
    private String description = "";
    private Bitmap image = null;
    private String image_path = "";
    private int favorite = 0;
    private int zoom = 12;


    public MapElement(long id, String title, String latitude, String longitude, String description, int favorite, int zoom) {
        this.id=id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.description = description;
        if (favorite>5) this.favorite = 5;
        else if (favorite<1) this.favorite=0;
        else this.favorite=favorite;
        this.id=id;
        if (zoom>22) this.zoom=22;
        else if (zoom<5) this.zoom=5;
        else this.zoom=zoom;
        String path= "https://dev.virtualearth.net/REST/V1/Imagery/Metadata/Aerial/"+latitude + "," + longitude + "?zl=" + this.zoom + "&o=xml&ms=500,500&key=At7y4aOtMy4Uopf8cD8cu_um0-YGyp5nlzPLLDBxLmgDN4o6DUkvk0ZTs4QpYh1O";
        //this.image_path="http://dev.virtualearth.net/REST/V1/Imagery/Map/Birdseye/"+latitude+","+longitude+
        //        "/19?dir=180&ms=500,500&key=ApzeMYSxJulF36ptSnMPfbN9Tb3ZDRj5820D3_YGcudYRWnStu_hn7ADXK2-Ddkz";
        this.image_path = getSubPath(path);
        if (this.image_path!=null)
        this.image = getBitmapFromURL(this.image_path);
    }

    public MapElement(long id, String title, String latitude, String longitude, String description, Bitmap image, int favorite, int zoom) {
        this.id=id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.description = description;
        if (favorite>5) this.favorite = 5;
        else if (favorite<1) this.favorite=0;
        else this.favorite=favorite;
        this.id=id;
        this.image = image;
        if (zoom>22) this.zoom=22;
        else if (zoom<5) this.zoom=5;
        else this.zoom=zoom;
    }

    public Bitmap getBitmapFromURL(String url) {
        Bitmap bmp=null;
        URL _url = null;
        try {
            _url = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) _url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        conn.setDoInput(true);
        conn.setDoOutput(true);
        try {

            InputStream in = conn.getInputStream();
            bmp = BitmapFactory.decodeStream(in);
        } catch(IOException e) {
            System.out.println(e);
        }
        return bmp;
    }

    protected String getSubPath(String path) {

        try {
            URL url = new URL(path);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            InputStream response = urlConnection.getInputStream();

            // Parse xml
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(false);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(response, "UTF-8");

            // Start parsing
            String p = null;
            int eventType = xpp.getEventType(); //The parser is currently at START_DOCUMENT
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (xpp.getName().equals("TraceId")) {
                        p = xpp.nextText().substring(0, 32);
                    } else if (xpp.getName().equals("ImageUrl")) {
                        return xpp.nextText();
                    }
                }
                eventType = xpp.next(); //move to the next xml event and store it in a variable
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public void bitMapToImageView(ImageView view, Bitmap bmp) {
        view.setImageBitmap(bmp);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public int getZoom() {
        return zoom;
    }

    public void setZoom(int zoom) {
        if (zoom>22) this.zoom=22;
        else if (zoom<5) this.zoom=5;
        else this.zoom=zoom;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public boolean isFavorite() {
        if (favorite==0) return false;
        else return true;
    }

    public int getFavorite() {
        return favorite;
    }

    public void setFavorite(int favorite) {
        if (favorite>5) this.favorite = 5;
        else if (favorite<1) this.favorite=0;
        else this.favorite=favorite;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getId() {return id;}

    public void setId(long id) {this.id = id;}
}
