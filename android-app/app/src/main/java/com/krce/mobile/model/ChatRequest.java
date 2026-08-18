package com.krce.mobile.model;

public class ChatRequest {
    public String question;
    public boolean live;

    public ChatRequest(String question, boolean live) {
        this.question = question;
        this.live = live;
    }
}
