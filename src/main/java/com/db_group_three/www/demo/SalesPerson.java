package com.db_group_three.www.demo;

// Subclass representing a salesperson
public class SalesPerson extends Person {
    private String username;
    private String password;
    private double commissionRate;

    public SalesPerson(int personID, String name, String email, int phoneNum, String address, String city, String state, int zipcode, String username, String password, double commissionRate) {
        super(personID, name, email, phoneNum, address, city, state, zipcode);
        this.username = username;
        this.password = password;
        this.commissionRate = commissionRate;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getCommissionRate() {
        return commissionRate;
    }

    public void setCommissionRate(double commissionRate) {
        this.commissionRate = commissionRate;
    }
}


