package com.restaurantdeliverysystem.model;

import java.sql.Timestamp;

/**
 * Class representing an order in the food delivery system.
 */
public class Order {

    private final int orderId; // Primary key
    private int customerId;
    private int vendorId;
    private int driverId;
    private String restaurantStatus;
    private String deliveryStatus;
    private Timestamp orderTime;
    private double totalAmount;

    public Order(int orderId, int customerId, int vendorId, int driverId,
                 String restaurantStatus, String deliveryStatus, Timestamp orderTime, double totalAmount) {
        this.orderId          = orderId;
        this.customerId       = customerId;
        this.vendorId         = vendorId;
        this.driverId         = driverId;
        this.restaurantStatus = restaurantStatus;
        this.deliveryStatus   = deliveryStatus;
        this.orderTime        = orderTime;
        this.totalAmount      = totalAmount;
    }

    public int       getOrderId()          { return orderId; }
    public int       getCustomerId()       { return customerId; }
    public int       getVendorId()         { return vendorId; }
    public int       getDriverId()         { return driverId; }
    public String    getRestaurantStatus() { return restaurantStatus; }
    public String    getDeliveryStatus()   { return deliveryStatus; }
    public Timestamp getOrderTime()        { return orderTime; }
    public double    getTotalAmount()      { return totalAmount; }
}
