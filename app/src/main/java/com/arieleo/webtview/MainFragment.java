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
import com.arieleo.webtview.web.WebDetailActivity;
import com.arieleo.webtview.web.WebMainActivity;
import com.arieleo.webtview.web.WebSearchActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.android.schedulers.AndroidSchedulers;
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
    private Disposable insertVodDisposable;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG, "onCreate");
        super.onActivityCreated(savedInstanceState);

        //////////////prepareBackgroundManager/////////
        mBackgroundManager = BackgroundManager.getInstance(getActivity());
        mBackgroundManager.attach(getActivity().getWindow());
        mDefaultBackground = ContextCompat.getDrawable(getActivity(), R.drawable.default_background);
        mMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(mMetrics);

        //////////////setupUIElements///////////
        // Badge, when set, takes precedent
        setTitle(TvSource.title());
        // over title
        setHeadersState(HEADERS_ENABLED);
        setHeadersTransitionOnBackEnabled(true);
        // set fastLane (or headers) background color
        setBrandColor(ContextCompat.getColor(getActivity(), R.color.fastlane_background));
        // set search icon color
        setSearchAffordanceColor(ContextCompat.getColor(getActivity(), R.color.search_opaque));

        ///////////////setupEventListeners/////////////
        setOnSearchClickedListener(view -> search());
        setOnItemViewClickedListener(new ItemViewClickedListener());
//        setOnItemViewSelectedListener(new ItemViewSelectedListener());

        dramas = (Drama[]) getActivity().getIntent().getSerializableExtra(TvSource.INTENT_DRAMAS);
        if (dramas != null && dramas.length > 0) {
            Serializable recent = getActivity().getIntent().getSerializableExtra(TvSource.INTENT_RECENT);
            if(recent != null) {
                updateRows((Drama[]) recent);
            }

            loadRows();
            saveToDrama(dramas);
            Log.d(TAG, "onActivityCreated: " + dramas.length);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (null != mBackgroundTimer) {
            Log.d(TAG, "onDestroy: " + mBackgroundTimer.toString());
            mBackgroundTimer.cancel();
        }
        if(insertVodDisposable != null && !insertVodDisposable.isDisposed()) {
            insertVodDisposable.dispose();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        Log.d(TAG, "onActivityResult: " + requestCode);
        try {
            if (requestCode == 123) {
                Drama[] result = (Drama[]) intent.getSerializableExtra(TvSource.INTENT_SEARCH);
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

    private void loadRows() {
        LinkedHashMap<String, List<Drama>> categories = new LinkedHashMap<>();
        for (Drama drama : dramas) {
            if (categories.containsKey(drama.category)) {
                categories.get(drama.category).add(drama);
            } else {
                List<Drama> items = new ArrayList<>();
                items.add(drama);
                categories.put(drama.category, items);
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

        HeaderItem gridHeader = new HeaderItem(headerId, "切换视频源");
        GridItemPresenter mGridPresenter = new GridItemPresenter();
        ArrayObjectAdapter gridRowAdapter = new ArrayObjectAdapter(mGridPresenter);
        for(String title : getResources().getStringArray(R.array.tv_config_titles)) {
            if(!title.contains(TvSource.title())) {
                gridRowAdapter.add(title);
            }
        }
        rowsAdapter.add(new ListRow(gridHeader, gridRowAdapter));

        try {
            setAdapter(rowsAdapter);
        } catch (Exception err) {
            Toast.makeText(getActivity(), err.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    private void updateRows(Drama[] data) {
        Log.d(TAG, "updateRows - dram length: " + dramas.length);
        Log.d(TAG, "updateRows - new length: " + data.length);
        if (data.length == 0) { return; }

        HashSet<String> pk = new HashSet<>();
        LinkedList<Drama> all = new LinkedList<>();
        for (Drama datum : data) {
            if (pk.add(datum.url + datum.category))
                all.add(datum);
        }
        for (Drama drama : dramas) {
            if (pk.add(drama.url + drama.category))
                all.add(drama);
        }
        dramas = all.toArray(new Drama[0]);
        Log.d(TAG, "updateRows - dram length: " + dramas.length);
    }
    protected void search() {
        Intent intent = new Intent(getActivity(), WebSearchActivity.class);
        Drama[] data = (Drama[]) getActivity().getIntent().getSerializableExtra(TvSource.INTENT_DRAMAS);
        intent.putExtra(TvSource.INTENT_DRAMAS, data);
        startActivityForResult(intent, 123);
    }
    private void saveToDrama(Drama[] dramas) {
        for (Drama drama : dramas) {
            drama.urlHome = TvSource.urlHome();
        }
        insertVodDisposable = DataAccess.getInstance(getActivity().getApplicationContext())
                .vodDao().insertVod(dramas)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(() -> Log.d(TAG, "vodDao insertVod"), Throwable::printStackTrace);
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {
        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item,
                                  RowPresenter.ViewHolder rowViewHolder, Row row) {

            if (item instanceof Drama) {
                Drama movie = (Drama) item;
                Log.d(TAG, "Item: " + item.toString());
                Intent intent = new Intent(getActivity(), WebDetailActivity.class);
                intent.putExtra(TvSource.INTENT_DRAMA, movie);

                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(),
                        ((ImageCardView) itemViewHolder.view).getMainImageView(),
                        DetailsActivity.SHARED_ELEMENT_NAME)
                        .toBundle();
                getActivity().startActivity(intent, bundle);
            } else if (item instanceof String) {
                Log.d(TAG, TvSource.title() + " switch to " + item);
                boolean res = TvSource.setSharedPreferencesUrlHome(getActivity(), (String) item);
                if(res) {
                    startActivity(new Intent(getActivity(), WebMainActivity.class));
                    getActivity().finish();
                } else {
                    Toast.makeText(getActivity(), getString(R.string.error_not_implemented), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private final class GridItemPresenter extends Presenter {
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
}