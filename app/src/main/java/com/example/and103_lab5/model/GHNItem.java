package com.example.and103_lab5.model;

public class GHNItem {
    private String name,code;
    private int quantity,price,weight;

    public GHNItem(String name, String code, int quantity, int price, int weight) {
        this.name = name;
        this.code = code;
        this.quantity = quantity;
        this.price = price;
        this.weight = weight;
    }

    public GHNItem() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
