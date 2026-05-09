package com.restaurantdeliverysystem.model;

/**
 * Class representing a customer in the food delivery system.
 */
public class Customer {

    private final int customerId; // Primary key
    private String firstName;
    private String lastName;
    private String address;
    private String phoneNumber;
    private String paymentMethod;

    public Customer(int customerId, String firstName, String lastName,
                    String address, String phoneNumber, String paymentMethod) {
        this.customerId    = customerId;
        this.firstName     = firstName;
        this.lastName      = lastName;
        this.address       = address;
        this.phoneNumber   = phoneNumber;
        this.paymentMethod = paymentMethod;
    }

    public int    getCustomerId()    { return customerId; }
    public String getFirstName()     { return firstName; }
    public String getLastName()      { return lastName; }
    public String getAddress()       { return address; }
    public String getPhoneNumber()   { return phoneNumber; }
    public String getPaymentMethod() { return paymentMethod; }

    @Override
    public String toString() { return firstName + " " + lastName; }
}
