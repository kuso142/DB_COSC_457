package com.restaurantdeliverysystem.model;

public class Driver {

    private int driverId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String status;

    public Driver(int driverId, String firstName, String lastName,
                  String phoneNumber, String status) {
        this.driverId    = driverId;
        this.firstName   = firstName;
        this.lastName    = lastName;
        this.phoneNumber = phoneNumber;
        this.status      = status;
    }

    public int    getDriverId()    { return driverId; }
    public String getFirstName()   { return firstName; }
    public String getLastName()    { return lastName; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getStatus()      { return status; }

    @Override
    public String toString() { return firstName + " " + lastName + " (" + status + ")"; }
}
