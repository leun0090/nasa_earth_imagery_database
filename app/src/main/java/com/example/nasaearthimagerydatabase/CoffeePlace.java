package com.example.nasaearthimagerydatabase;

/**
 * <h1>Coffee Place</h1>
 * The class stores the coffeeshop object which
 * is used in the Activity2_listview
 *
 * @author  Pak Leung
 * @version 1.0
 */
public class CoffeePlace {
    public String name;
    public String address;
    public String telephone;
    public String website;
    public CoffeePlace(String name, String address, String telephone, String website) {
        this.name = name;
        this.address = address;
        this.telephone = telephone;
        this.website = website;
    }
}