package com.arieleo.webtview;

import java.io.Serializable;

public class Episode implements Serializable {
    static final long serialVersionUID = 727561609932127311L;
    String title;
    String link;

    @Override
    public String toString() {
        return "Episodes{" +
                "title='" + title + '\'' +
                ", link='" + link + '\'' +
                '}';
    }
}