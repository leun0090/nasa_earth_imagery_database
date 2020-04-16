package com.example.nasaearthimagerydatabase;

public class Place {

    protected String message;
    protected long id;

    /**Constructor:*/
    public Place(String n, long i)
    {
        message =n;
        id = i;
    }

    public void update(String n)
    {
        message = n;
    }

    /**Chaining constructor: */
    public Place(String n) { this(n, 0);}


    public String getMessage() {
        return message;
    }

    public long getId() {
        return id;
    }

}