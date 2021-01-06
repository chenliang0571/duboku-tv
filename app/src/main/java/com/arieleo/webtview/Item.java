package com.arieleo.webtview;

import java.io.Serializable;

public class Item implements Serializable {
    static final long serialVersionUID = 727561609867527564L;
    String title;
    String href;
    String image;
    String tag;
    String pic_text;

    @Override
    public String toString() {
        return "Item{" +
                "title='" + title + '\'' +
                ", href='" + href + '\'' +
                ", image='" + image + '\'' +
                ", tag='" + tag + '\'' +
                ", pic_text='" + pic_text + '\'' +
                '}';
    }
}