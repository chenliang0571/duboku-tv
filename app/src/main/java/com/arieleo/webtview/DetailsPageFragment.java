package com.arieleo.webtview;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.leanback.widget.AbstractDetailsDescriptionPresenter;
import androidx.leanback.widget.Action;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.ClassPresenterSelector;
import androidx.leanback.widget.DetailsOverviewRow;
import androidx.leanback.widget.FullWidthDetailsOverviewRowPresenter;
import androidx.leanback.widget.FullWidthDetailsOverviewSharedElementHelper;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;

import com.arieleo.webtview.room.AppDatabase;
import com.arieleo.webtview.room.Drama;
import com.arieleo.webtview.room.Episode;
import com.arieleo.webtview.web.WebVideoActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/*
 * LeanbackDetailsFragment extends DetailsFragment, a Wrapper fragment for leanback details screens.
 * It shows a detailed view of video and its meta plus related videos.
 */
public class DetailsPageFragment extends androidx.leanback.app.DetailsFragment {
    private static final String TAG = "VideoDetailsFragmentDDD";

    private static final long ACTION_PLAY = 1;
    private Action mActionPlay;

    private static final int DETAIL_THUMB_WIDTH = 274;
    private static final int DETAIL_THUMB_HEIGHT = 274;

    private Drama drama;

    private ArrayObjectAdapter mAdapter;
    private ClassPresenterSelector mPresenterSelector;
    private Disposable loadHistoryDisposable;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate DetailsFragment");
        super.onCreate(savedInstanceState);

        drama = (Drama) getActivity().getIntent().getSerializableExtra(TvSource.INTENT_DRAMA);
        if (drama != null) {
            mPresenterSelector = new ClassPresenterSelector();
            mAdapter = new ArrayObjectAdapter(mPresenterSelector);
            setupDetailsOverviewRow();
            setupDetailsOverviewRowPresenter();

            Episode[] episodes = (Episode[]) this.getActivity().getIntent().getSerializableExtra(TvSource.INTENT_EPISODES);
            setupRelatedMovieListRow(episodes);
            List<Episode> list = Arrays.asList(episodes);
            Collections.reverse(list);
            episodes = list.toArray(new Episode[0]);
            setupRelatedMovieListRow(episodes);

            setAdapter(mAdapter);
            setOnItemViewClickedListener(new ItemViewClickedListener());

            //load history
            loadHistoryDisposable = AppDatabase.getInstance(getActivity().getApplicationContext())
                    .vodDao()
                    .loadHistoryByDrama(drama.url)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(history -> {
                        Log.d(TAG, "onCreate: loadHistoryByDrama " + history);
                        drama.tag = "";
                        for(int i = 0; i < history.size(); i ++) {
                            drama.tag += ("\n" + history.get(i).title + " - " + history.get(i).upd
                                    + " | " + (history.get(i).currentTime != null ?
                                    history.get(i).currentTime + "s" : ""));
                        }
                        //setupDetailsOverviewRow();
                        mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size());
                    }, error -> Log.e(TAG, "onCreate: initialize", error));
        } else {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(loadHistoryDisposable != null && !loadHistoryDisposable.isDisposed()) {
            loadHistoryDisposable.dispose();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.d(TAG, "onActivityResult: " + requestCode);
        try {
            if (requestCode == 123) {
                Log.d(TAG, "onActivityResult: ");
            }
        } catch (Exception error) {
            error.printStackTrace();
            Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void setupDetailsOverviewRow() {
        final DetailsOverviewRow row = new DetailsOverviewRow(drama);
        row.setImageDrawable(
                ContextCompat.getDrawable(getActivity(), R.drawable.default_background));
        int width = convertDpToPixel(getActivity().getApplicationContext(), DETAIL_THUMB_WIDTH);
        int height = convertDpToPixel(getActivity().getApplicationContext(), DETAIL_THUMB_HEIGHT);
        Glide.with(getActivity())
                .load(drama.image)
                .centerCrop()
                .error(R.drawable.default_background)
                .into(new SimpleTarget<GlideDrawable>(width, height) {
                    @Override
                    public void onResourceReady(GlideDrawable resource,
                                                GlideAnimation<? super GlideDrawable>
                                                        glideAnimation) {
                        row.setImageDrawable(resource);
                        mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size());
                    }
                });

        ArrayObjectAdapter actionAdapter = new ArrayObjectAdapter();
        mActionPlay = new Action(ACTION_PLAY, getString(R.string.action_play));
        actionAdapter.add(mActionPlay);
        row.setActionsAdapter(actionAdapter);
        mAdapter.add(row);
    }

    private void setupDetailsOverviewRowPresenter() {
        // Set detail background.
        FullWidthDetailsOverviewRowPresenter detailsPresenter =
                new FullWidthDetailsOverviewRowPresenter(new DetailsDescriptionPresenter());
        detailsPresenter.setBackgroundColor(
                ContextCompat.getColor(getActivity(), R.color.selected_background));

        // Hook up transition element.
        FullWidthDetailsOverviewSharedElementHelper sharedElementHelper =
                new FullWidthDetailsOverviewSharedElementHelper();
        sharedElementHelper.setSharedElementEnterTransition(
                getActivity(), DetailsActivity.SHARED_ELEMENT_NAME);
        detailsPresenter.setListener(sharedElementHelper);
        detailsPresenter.setParticipatingEntranceTransition(true);

        detailsPresenter.setOnActionClickedListener(action -> Toast.makeText(getActivity(),
                action.toString() + " " + action.getId(), Toast.LENGTH_SHORT).show());
        mPresenterSelector.addClassPresenter(DetailsOverviewRow.class, detailsPresenter);
    }

    private void setupRelatedMovieListRow(Episode[] episodes) {
        ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(new EpisodesItemPresenter());
        for (Episode episode : episodes) {
            listRowAdapter.add(episode);
        }

        HeaderItem header = new HeaderItem(0, "episodes");
        mAdapter.add(new ListRow(header, listRowAdapter));
        ListRowPresenter listRowPresenter = new ListRowPresenter();
        listRowPresenter.setNumRows(2);
        mPresenterSelector.addClassPresenter(ListRow.class, listRowPresenter);
    }

    private int convertDpToPixel(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    private final class EpisodesItemPresenter extends Presenter {
        private static final int GRID_ITEM_WIDTH = 200;
        private static final int GRID_ITEM_HEIGHT = 200;
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent) {
            TextView view = new TextView(parent.getContext());
            view.setLayoutParams(new ViewGroup.LayoutParams(GRID_ITEM_WIDTH, GRID_ITEM_HEIGHT));
            view.setFocusable(true);
            view.setFocusableInTouchMode(true);
            view.setBackgroundColor(
                    ContextCompat.getColor(getActivity(), R.color.default_background));
            view.setTextColor(Color.WHITE);
            view.setGravity(Gravity.CENTER);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, Object item) {
            Episode episode = (Episode) item;
            ((TextView) viewHolder.view).setText(episode.title);
            viewHolder.view.setOnClickListener(v -> {
                Intent intent = new Intent(getActivity(), WebVideoActivity.class);
                intent.putExtra(TvSource.INTENT_EPISODE, episode);
//                getActivity().startActivity(intent);
                startActivityForResult(intent, 123);
            });
        }

        @Override
        public void onUnbindViewHolder(ViewHolder viewHolder) {
        }
    }
    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(
                Presenter.ViewHolder itemViewHolder,
                Object item,
                RowPresenter.ViewHolder rowViewHolder,
                Row row) {
            if(item instanceof Episode) {
                Intent intent = new Intent(getActivity(), WebVideoActivity.class);
                intent.putExtra(TvSource.INTENT_EPISODE, (Episode)item);
                getActivity().startActivity(intent);
            }
        }
    }
    private final class DetailsDescriptionPresenter extends AbstractDetailsDescriptionPresenter {

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
}