package com.netrush.netrushapp.models;

/**
 * Created by Garrett on 8/17/2016.
 */
public class Order {

    private String date;
    private String title;
    private String asin;
    private String quantity;
    private double unitprice;
    private String imageUrl;

    public Order() {}

    public Order(String date, String title, String asin, String quantity, double unitprice, String imageUrl) {
        this.date = date;
        this.title = title;
        this.asin = asin;
        this.quantity = quantity;
        this.unitprice = unitprice;
        this.imageUrl = imageUrl;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAsin() {
        return asin;
    }

    public void setAsin(String asin) {
        this.asin = asin;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public double getUnitprice() {
        return unitprice;
    }

    public void setUnitprice(double unitprice) {
        this.unitprice = unitprice;
    }
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

}
