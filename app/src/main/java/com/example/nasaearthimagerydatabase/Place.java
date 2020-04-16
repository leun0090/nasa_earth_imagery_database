package com.example.nasaearthimagerydatabase;

public class Place {

    protected String title;
    protected String latitude;
    protected String longitude;
    protected String description;
    protected String email;
    protected String stars;
    protected String zoom;
    protected long id;

    /**Constructor:*/
    public Place(String atitle, String alatitude, String alongitude, String adescription, String aemail, String astars, String azoom, long i)
    {
        title = atitle;
        latitude = alatitude;
        longitude = alongitude;
        description = adescription;
        email = aemail;
        stars = astars;
        zoom = azoom;
        id = i;
    }

//    public void update(String title)
//    {
//        title = title;
//    }

    /**Chaining constructor: */
//   public Place(String title) { this(title, 0);}

    public String getTitle() {
        return title;
    }
    public String getLatitude() {
        return latitude;
    }
    public String getLongitude() {
        return longitude;
    }
    public String getDescription() {
        return description;
    }
    public String getEmail() {
        return email;
    }
    public String getStars() {
        return stars;
    }
    public String getZoom() {
        return zoom;
    }

    public long getId() {
        return id;
    }

}