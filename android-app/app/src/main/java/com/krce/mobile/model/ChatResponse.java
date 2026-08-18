package com.krce.mobile.model;

import java.util.List;

public class ChatResponse {
    public String answer;
    public String sourceUrl;
    public String sourceTitle;
    public List<Source> sources;

    public static class Source {
        public String title;
        public String url;
        public String imageUrl;
    }
}
