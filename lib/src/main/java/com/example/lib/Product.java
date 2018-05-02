package com.example.lib;

/**
 * Class representing each item.
 */
public class Product {
    private String name;
    private double calories;
    private double amount;
    private byte[] picutre;

    public Product(final String setName, final double setCal) {
        this.name = setName;
        this.calories = setCal;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCalories(double calories) {
        this.calories = calories;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }

    public double getCalories() {
        return calories;
    }

    public String getName() {
        return name;
    }

    public void setPicutre(byte[] picutre) {
        this.picutre = picutre;
    }

    public byte[] getPicutre() {
        return picutre;
    }

    @Override
    public String toString() {
        return this.name;
    }
}
