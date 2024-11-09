package com.db_group_three.www.demo;

public class Salesperson extends Person {
    private String username;
    private String password;
    private double commissionRate;

    public Salesperson(int personID, String name, String email, int phoneNumber, String address, String city, String state, int zipCode, String username, String password, double commissionRate) {
        super(personID, name, email, phoneNumber, address, city, state, zipCode);
        this.username = username;
        this.password = password;
        this.commissionRate = commissionRate;
    }

    // Getters and setters for Salesperson-specific attributes
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public double getCommissionRate() { return commissionRate; }
    public void setCommissionRate(double commissionRate) { this.commissionRate = commissionRate; }
}

