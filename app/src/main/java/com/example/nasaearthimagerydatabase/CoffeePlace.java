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
    protected  String name;
    protected  String address;
    protected  String telephone;
    protected  String website;

    public CoffeePlace(String name, String address, String telephone, String website) {
        this.name = name;
        this.address = address;
        this.telephone = telephone;
        this.website = website;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getWebsite() {
        return website;
    }

}