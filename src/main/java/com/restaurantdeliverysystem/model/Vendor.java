package com.restaurantdeliverysystem.model;

/**
 * Class representing a vendor in the food delivery system.
 */
public class Vendor {

    private final int vendorId; // Primary key
    private String name;
    private String address;
    private String phoneNumber;
    private String cuisineType;

    public Vendor(int vendorId, String name, String address,
                  String phoneNumber, String cuisineType) {
        this.vendorId    = vendorId;
        this.name        = name;
        this.address     = address;
        this.phoneNumber = phoneNumber;
        this.cuisineType = cuisineType;
    }

    public int    getVendorId()    { return vendorId; }
    public String getName()        { return name; }
    public String getAddress()     { return address; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getCuisineType() { return cuisineType; }

    @Override
    public String toString() { return name; }
}
