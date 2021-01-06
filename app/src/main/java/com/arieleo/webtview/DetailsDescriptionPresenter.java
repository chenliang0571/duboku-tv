package com.arieleo.webtview;

import androidx.leanback.widget.AbstractDetailsDescriptionPresenter;

public class DetailsDescriptionPresenter extends AbstractDetailsDescriptionPresenter {

    @Override
    protected void onBindDescription(ViewHolder viewHolder, Object item) {
        Item movie = (Item) item;

        if (movie != null) {
            viewHolder.getTitle().setText(movie.title);
            viewHolder.getSubtitle().setText(movie.pic_text);
            viewHolder.getBody().setText(movie.tag);
        }
    }
}