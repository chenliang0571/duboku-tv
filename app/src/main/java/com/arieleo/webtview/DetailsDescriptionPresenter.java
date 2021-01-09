package com.arieleo.webtview;

import androidx.leanback.widget.AbstractDetailsDescriptionPresenter;

import com.arieleo.webtview.room.Drama;

public class DetailsDescriptionPresenter extends AbstractDetailsDescriptionPresenter {

    @Override
    protected void onBindDescription(ViewHolder viewHolder, Object item) {
        Drama movie = (Drama) item;

        if (movie != null) {
            viewHolder.getTitle().setText(movie.title);
            viewHolder.getSubtitle().setText(movie.picText);
            viewHolder.getBody().setText(movie.tag);
        }
    }
}