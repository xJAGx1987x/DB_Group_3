package com.db_group_three.www.demo;

// Subclass representing a customer
public class Customer extends Person {
    public Customer(int personID, String name, String email, int phoneNum, String address, String city, String state, int zipcode) {
        super(personID, name, email, phoneNum, address, city, state, zipcode);
    }
}
