package com.restaurantdeliverysystem.model;

import java.sql.Timestamp;

public class Order {

    private int orderId;
    private int customerId;
    private int vendorId;
    private int driverId;
    private String status;
    private Timestamp orderTime;
    private double totalAmount;

    public Order(int orderId, int customerId, int vendorId, int driverId,
                 String status, Timestamp orderTime, double totalAmount) {
        this.orderId     = orderId;
        this.customerId  = customerId;
        this.vendorId    = vendorId;
        this.driverId    = driverId;
        this.status      = status;
        this.orderTime   = orderTime;
        this.totalAmount = totalAmount;
    }

    public int       getOrderId()     { return orderId; }
    public int       getCustomerId()  { return customerId; }
    public int       getVendorId()    { return vendorId; }
    public int       getDriverId()    { return driverId; }
    public String    getStatus()      { return status; }
    public Timestamp getOrderTime()   { return orderTime; }
    public double    getTotalAmount() { return totalAmount; }
}
