package com.db_group_three.www.demo;

// Subclass representing a manager
public class Manager extends SalesPerson {
    public Manager(int personID, String name, String email, int phoneNum, String address, String city, String state, int zipcode, String username, String password, double commissionRate) {
        super(personID, name, email, phoneNum, address, city, state, zipcode, username, password, commissionRate);
    }
}
