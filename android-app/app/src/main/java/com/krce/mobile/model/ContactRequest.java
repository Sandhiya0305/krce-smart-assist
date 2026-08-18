package com.krce.mobile.model;

public class ContactRequest {
    public String name;
    public String phone;
    public String message;

    public ContactRequest(String name, String phone, String message) {
        this.name = name;
        this.phone = phone;
        this.message = message;
    }
}
