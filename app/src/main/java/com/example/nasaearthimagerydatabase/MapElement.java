package com.example.nasaearthimagerydatabase;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MapElement {
    private String title = "";
    private String latitude = "";
    private String longitude = "";
    private String description = "";
    private Bitmap image = null;
    private String image_path = "";
    private boolean favorite = false;


    public MapElement(String title, String latitude, String longitude, String description, String image_path, boolean favorite) {
        //String xml = getMapPath(latitude, longitude);
        //image_path = "";
        this.latitude = latitude;
        this.longitude = longitude;
        this.title = title;
        this.description = description;
        this.favorite = favorite;
        //this.image = getBitmapFromURL(image_path);
    }

    public Bitmap getBitmapFromURL(String url) {
        Bitmap bmp = null;
        try {
            InputStream in = new URL(url).openStream();
            bmp = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
            e.printStackTrace();
        }
        return bmp;
    }

    public void bitMapToImageView(ImageView view, Bitmap bmp) {
        view.setImageBitmap(bmp);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static String getMapPath(String latitude, String longitude) {
        String url = "https://dev.virtualearth.net/REST/V1/Imagery/Metadata/Aerial/" + latitude + "," + longitude + "?zl=15&o=xml&key=ApzeMYSxJulF36ptSnMPfbN9Tb3ZDRj5820D3_YGcudYRWnStu_hn7ADXK2-Ddkz";
        String contents = "";

        try {
            URLConnection conn = new URL(url).openConnection();
            //String str = conn.getContentEncoding();
            InputStream in = conn.getInputStream();
            contents = convertStreamToString(in);
        } catch (MalformedURLException e) {
            Log.v("TAG", "MALFORMED URL EXCEPTION");
        } catch (IOException e) {
            Log.e(e.getMessage(), e.toString());
        }

        return contents;
    }

    private static String convertStreamToString(InputStream is) throws UnsupportedEncodingException {

        BufferedReader reader = new BufferedReader(new
                InputStreamReader(is, "UTF-8"));
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
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
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
