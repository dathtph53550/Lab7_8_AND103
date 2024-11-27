package com.example.and103_lab5.model;

public class GHNOrderRespone {
    private String order_code;

    public GHNOrderRespone(String order_code) {
        this.order_code = order_code;
    }

    public GHNOrderRespone() {
    }

    public String getOrder_code() {
        return order_code;
    }

    public void setOrder_code(String order_code) {
        this.order_code = order_code;
    }
}
