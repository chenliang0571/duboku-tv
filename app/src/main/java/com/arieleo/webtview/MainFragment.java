package com.arieleo.webtview;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityOptionsCompat;
import androidx.core.content.ContextCompat;
import androidx.leanback.app.BackgroundManager;
import androidx.leanback.app.BrowseFragment;
import androidx.leanback.widget.ArrayObjectAdapter;
import androidx.leanback.widget.HeaderItem;
import androidx.leanback.widget.ImageCardView;
import androidx.leanback.widget.ListRow;
import androidx.leanback.widget.ListRowPresenter;
import androidx.leanback.widget.OnItemViewClickedListener;
import androidx.leanback.widget.OnItemViewSelectedListener;
import androidx.leanback.widget.Presenter;
import androidx.leanback.widget.Row;
import androidx.leanback.widget.RowPresenter;

import com.arieleo.webtview.room.DataAccess;
import com.arieleo.webtview.room.Drama;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainFragment extends BrowseFragment {
    private static final String TAG = "MainFragment-DDDD";

    private static final int BACKGROUND_UPDATE_DELAY = 300;
    private static final int GRID_ITEM_WIDTH = 200;
    private static final int GRID_ITEM_HEIGHT = 200;

    private final Handler mHandler = new Handler();
    private Drawable mDefaultBackground;
    private DisplayMetrics mMetrics;
    private Timer mBackgroundTimer;
    private String mBackgroundUri;
    private BackgroundManager mBackgroundManager;

    private Drama[] dramas;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onActivityCreated(savedInstanceState);

        prepareBackgroundManager();
        setupUIElements();
        setupEventListeners();

        dramas = (Drama[]) getActivity().getIntent().getSerializableExtra(TVduboku.IntentDramas);
        if (dramas != null && dramas.length > 0) {
            Serializable recent = getActivity().getIntent().getSerializableExtra(TVduboku.IntentRecent);
            if(recent != null) {
                updateRows((Drama[]) recent);
            }

            loadRows();
            saveToDrama(dramas);
            Log.d(TAG, "onActivityCreated: " + dramas.length);
        }
    }

    private void saveToDrama(Drama[] dramas) {
        DataAccess.getInstance(getActivity().getApplicationContext())
                .vodDao().insertVod(dramas)
                .subscribeOn(Schedulers.io())
                .subscribe(() -> Log.d(TAG, "vodDao insertVod"), err -> err.printStackTrace());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mBackgroundTimer) {
            Log.d(TAG, "onDestroy: " + mBackgroundTimer.toString());
            mBackgroundTimer.cancel();
        }
    }

    private void loadRows() {
        LinkedHashMap<String, List<Drama>> categories = new LinkedHashMap<>();
        for (int i = 0; i < dramas.length; i++) {
            if (categories.containsKey(dramas[i].category)) {
                categories.get(dramas[i].category).add(dramas[i]);
            } else {
                List<Drama> items = new ArrayList<>();
                items.add(dramas[i]);
                categories.put(dramas[i].category, items);
            }
        }
        //sort recent
        if(categories.get("recent") != null && categories.get("recent").size() > 0)
            Collections.sort(categories.get("recent"), (o1, o2) -> o2.upd.compareTo(o1.upd));

        ArrayObjectAdapter rowsAdapter = new ArrayObjectAdapter(new ListRowPresenter());
        CardPresenter cardPresenter = new CardPresenter();

        int headerId = 0;
        for (Map.Entry<String, List<Drama>> entry : categories.entrySet()) {
            ArrayObjectAdapter listRowAdapter = new ArrayObjectAdapter(cardPresenter);
            for (int j = 0; j < entry.getValue().size(); j++) {
                listRowAdapter.add(entry.getValue().get(j));
            }
            HeaderItem header = new HeaderItem(headerId++, entry.getKey());
            rowsAdapter.add(new ListRow(header, listRowAdapter));
        }

        HeaderItem gridHeader = new HeaderItem(headerId, "PREFERENCES");
        GridItemPresenter mGridPresenter = new GridItemPresenter();
        ArrayObjectAdapter gridRowAdapter = new ArrayObjectAdapter(mGridPresenter);
        gridRowAdapter.add(getResources().getString(R.string.grid_view));
        gridRowAdapter.add(getString(R.string.error_fragment));
        gridRowAdapter.add(getResources().getString(R.string.personal_settings));
        rowsAdapter.add(new ListRow(gridHeader, gridRowAdapter));

        try {
            setAdapter(rowsAdapter);
        } catch (Exception err) {
            Toast.makeText(getActivity(), err.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void prepareBackgroundManager() {

        mBackgroundManager = BackgroundManager.getInstance(getActivity());
        mBackgroundManager.attach(getActivity().getWindow());

        mDefaultBackground = ContextCompat.getDrawable(getActivity(), R.drawable.default_background);
        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);
    }

    private void setupUIElements() {
        // setBadgeDrawable(getActivity().getResources().getDrawable(
        // R.drawable.videos_by_google_banner));
        setTitle("www.duboku.tv"); // Badge, when set, takes precedent
        // over title
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);

        // set fastLane (or headers) background color
        setBrandColor(ContextCompat.getColor(getActivity(), R.color.fastlane_background));
        // set search icon color
        setSearchAffordanceColor(ContextCompat.getColor(getActivity(), R.color.search_opaque));
    }

    private void setupEventListeners() {
        setOnSearchClickedListener(view -> search());
        setOnItemViewClickedListener(new ItemViewClickedListener());
        setOnItemViewSelectedListener(new ItemViewSelectedListener());
    }

    protected void search() {
        Intent intent = new Intent(getActivity(), WebSearchActivity.class);
        Drama[] data = (Drama[]) getActivity().getIntent().getSerializableExtra(TVduboku.IntentDramas);
        intent.putExtra(TVduboku.IntentDramas, data);
        startActivityForResult(intent, 123);
    }

    private void updateBackground(String uri) {
        int width = mMetrics.widthPixels;
        int height = mMetrics.heightPixels;
        Glide.with(getActivity())
                .load(uri)
                .centerCrop()
                .error(mDefaultBackground)
                .into(new SimpleTarget<GlideDrawable>(width, height) {
                    @Override
                    public void onResourceReady(GlideDrawable resource,
                                                GlideAnimation<? super GlideDrawable>
                                                        glideAnimation) {
                        mBackgroundManager.setDrawable(resource);
                    }
                });
        mBackgroundTimer.cancel();
    }

    private void startBackgroundTimer() {
        if (null != mBackgroundTimer) {
            mBackgroundTimer.cancel();
        }
        mBackgroundTimer = new Timer();
        mBackgroundTimer.schedule(new UpdateBackgroundTask(), BACKGROUND_UPDATE_DELAY);
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {

            if (item instanceof Drama) {
                Drama movie = (Drama) item;
                Log.d(TAG, "Item: " + item.toString());
                Intent intent = new Intent(getActivity(), WebDetailActivity.class);
                intent.putExtra(TVduboku.IntentDrama, movie);

                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(),
                        ((ImageCardView) itemViewHolder.view).getMainImageView(),
                        DetailsActivity.SHARED_ELEMENT_NAME)
                        .toBundle();
                getActivity().startActivity(intent, bundle);
            } else if (item instanceof String) {
                if (((String) item).contains(getString(R.string.error_fragment))) {
                    Intent intent = new Intent(getActivity(), BrowseErrorActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(getActivity(), ((String) item), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private final class ItemViewSelectedListener implements OnItemViewSelectedListener {
        @Override
        public void onItemSelected(
                Presenter.ViewHolder itemViewHolder,
                Object item,
                RowPresenter.ViewHolder rowViewHolder,
                Row row) {
            if (item instanceof Drama) {
                mBackgroundUri = ((Drama) item).image;
                startBackgroundTimer();
            }
        }
    }

    private class UpdateBackgroundTask extends TimerTask {

        @Override
        public void run() {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    updateBackground(mBackgroundUri);
                }
            });
        }
    }

    private class GridItemPresenter extends Presenter {
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
            ((TextView) viewHolder.view).setText((String) item);
        }

        @Override
        public void onUnbindViewHolder(ViewHolder viewHolder) {
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.d(TAG, "onActivityResult: " + requestCode);
        try {
            if (requestCode == 123) {
                Drama[] result = (Drama[]) intent.getSerializableExtra(TVduboku.IntentSearch);
                Log.d(TAG, "onActivityResult: " + result.length);
                if(result.length > 0) {
                    saveToDrama(result);
                    updateRows(result);
                    loadRows();
                }
            }
        } catch (Exception error) {
            error.printStackTrace();
            Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateRows(Drama[] data) {
        Log.d(TAG, "updateRows - data length: " + data.length);
        Log.d(TAG, "updateRows - dram length: " + dramas.length);
        if (data.length == 0) { return; }

        HashSet<String> pk = new HashSet<>();
        LinkedList<Drama> all = new LinkedList<>();
        for (int i = 0; i < data.length; i++) {
            if (pk.add(data[i].title + data[i].category))
                all.add(data[i]);
        }
        for (int i = 0; i < dramas.length; i++) {
            if (pk.add(dramas[i].title + dramas[i].category))
                all.add(dramas[i]);
        }
        dramas = all.toArray(new Drama[0]);
    }
}