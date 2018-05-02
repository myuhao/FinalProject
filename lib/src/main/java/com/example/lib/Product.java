package com.example.lib;

/**
 * Class representing each item.
 */
public class Product {
    private String name;
    private int calories;
    private int amount;
    private byte[] picutre;

    public Product(final String setName, final int setCal) {
        this.name = setName;
        this.calories = setCal;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getAmount() {
        return amount;
    }

    public int getCalories() {
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
