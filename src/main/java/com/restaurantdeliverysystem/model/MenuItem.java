package com.restaurantdeliverysystem.model;

public class MenuItem {

    private int itemId;
    private int vendorId;
    private String itemName;
    private double price;
    private String description;

    public MenuItem(int itemId, int vendorId, String itemName,
                    double price, String description) {
        this.itemId      = itemId;
        this.vendorId    = vendorId;
        this.itemName    = itemName;
        this.price       = price;
        this.description = description;
    }

    public int    getItemId()      { return itemId; }
    public int    getVendorId()    { return vendorId; }
    public String getItemName()    { return itemName; }
    public double getPrice()       { return price; }
    public String getDescription() { return description; }

    @Override
    public String toString() { return itemName; }
}
