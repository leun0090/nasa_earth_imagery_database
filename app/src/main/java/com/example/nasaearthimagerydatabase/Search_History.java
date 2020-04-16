package com.example.nasaearthimagerydatabase;

/**
 * <h1>Activity 1 - Search_History Object</h1>
 * This is an Object class for Search_History items stored in ListView
 *
 * @author  Denesh Canjimavadivel
 * @version 1.0
 */

public class Search_History  {

    public String latitude;
    public String longitude;

    public Search_History(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

}
