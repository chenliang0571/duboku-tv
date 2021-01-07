package com.arieleo.webtview;

import java.io.Serializable;
import java.util.List;

public class Meta implements Serializable {
    static final long serialVersionUID = 727561609867586888L;
    String category;
    String link;
    List<Item> items;

    @Override
    public String toString() {
        return "Meta{" +
                "category='" + category + '\'' +
                ", link='" + link + '\'' +
                ", items=" + items +
                '}';
    }
}