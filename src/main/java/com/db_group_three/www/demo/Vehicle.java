package com.db_group_three.www.demo;

public class Vehicle {
    private int stockNumber;
    private String make;
    private String model;
    private int year;
    private String color;
    private byte[] image; // Storing the image as a byte array
    private String carCondition;
    private int netSalePrice;
    private String status;
    private int locationID;

    // Constructor
    public Vehicle(int stockNumber, String make, String model, int year, String color, byte[] image, String carCondition, int netSalePrice, String status, int locationID) {
        this.stockNumber = stockNumber;
        this.make = make;
        this.model = model;
        this.year = year;
        this.color = color;
        this.image = image;
        this.carCondition = carCondition;
        this.netSalePrice = netSalePrice;
        this.status = status;
        this.locationID = locationID;
    }

    // Getters and Setters
    public int getStockNumber() {
        return stockNumber;
    }

    public void setStockNumber(int stockNumber) {
        this.stockNumber = stockNumber;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getCarCondition() {
        return carCondition;
    }

    public void setCarCondition(String carCondition) {
        this.carCondition = carCondition;
    }

    public int getNetSalePrice() {
        return netSalePrice;
    }

    public void setNetSalePrice(int netSalePrice) {
        this.netSalePrice = netSalePrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getLocationID() {
        return locationID;
    }

    public void setLocationID(int locationID) {
        this.locationID = locationID;
    }

    // toString method for displaying vehicle information
    @Override
    public String toString() {
        return "Vehicle{" +
                "stockNumber=" + stockNumber +
                ", make='" + make + '\'' +
                ", model='" + model + '\'' +
                ", year=" + year +
                ", color='" + color + '\'' +
                ", carCondition='" + carCondition + '\'' +
                ", netSalePrice=" + netSalePrice +
                ", status='" + status + '\'' +
                ", locationID=" + locationID +
                '}';
    }
}