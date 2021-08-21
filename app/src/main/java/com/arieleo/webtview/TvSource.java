package com.arieleo.webtview;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.arieleo.webtview.room.AppDatabase;
import com.arieleo.webtview.room.TvConfig;
import com.arieleo.webtview.room.VodDao;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public final class TvSource {
    private static final String TAG = "TvSource-DDD";
    public static final String INTENT_DRAMA = "drama";
    public static final String INTENT_DRAMAS = "dramas";
    public static final String INTENT_EPISODE = "episode";
    public static final String INTENT_EPISODES = "episodes";
    public static final String INTENT_SEARCH = "search";
    public static final String INTENT_RECENT = "recent";
    private static final Pattern NOT_ALLOWED_JS_FUNC = Pattern
            .compile("\\beval\\s*\\(|\\bsetTimeout\\s*\\(|\\bsetInterval\\s*\\(|\\bcreateElement\\s*\\(");
    private static final TvDataService dataService;
    private static TvConfig config;
    public static final List<String> titles = new ArrayList<>();
    static {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://lzj8csjmyd.execute-api.eu-west-2.amazonaws.com")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        dataService = retrofit.create(TvDataService.class);
    }

    public static boolean setSharedPreferencesTitle(Context context, String title) {
        if(config != null && !config.title.equals(title)) {
            SharedPreferences sharedPref = context.getSharedPreferences("arieleo.key", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("HOME_TITLE", title);
            boolean res = editor.commit();
            Log.i(TAG, "initialize: setSharedPreferencesUrlHome title =" + title + "; result =" + res);
            return res;
        } else {
            return false;
        }
    }

    public static String getSharedPreferencesTitle(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences("arieleo.key",
                Context.MODE_PRIVATE);
        String title = sharedPref.getString("HOME_TITLE", "");
        Log.i(TAG, "initialize: SharedPreferences title = " + title);
        return title;
    }

    public static Single<List<TvConfig>> http(Context context) {
        VodDao dao = AppDatabase.getInstance(context.getApplicationContext()).vodDao();
        return dataService.getConfig()
                .flatMap(list -> {
                    ArrayList<TvConfig> cc = new ArrayList<>();
//                    String error = "检测到不可用字符，无效的配置信息";
                    for(TvConfig c : list) {
                        Matcher matcher = NOT_ALLOWED_JS_FUNC.matcher(c.toString());
                        if(matcher.matches()) {
//                            error += " " + c.title;
                            Log.w(TAG, "initialize: NOT_ALLOWED_JS_FUNC " + c.toString());
                        } else {
                            Log.w(TAG, "initialize: getConfig OK " + c.title);
                            cc.add(c);
                        }
                    }
//                    if(cc.size() != list.size()) {
//                        Toast.makeText(context, error, Toast.LENGTH_LONG).show();
//                    }
                    return dao.insertTvConfig(cc.toArray(new TvConfig[0]));
                })
                .flatMap(longs -> dao.getConfig());
    }

    public static Single<String> initialize(Context context) {
        VodDao dao = AppDatabase.getInstance(context.getApplicationContext()).vodDao();
        return dao.getConfigCount()
                .subscribeOn(Schedulers.io())
                .flatMap(n -> {
                    if(n == 0) {
                        Log.i(TAG, "initialize: getConfigCount = 0, send http request");
                        return http(context);
                    } else {
                        Log.i(TAG, "initialize: getConfigCount = " + n);
                        return dao.getConfig();
                    }
                })
                .flatMap(list -> {
                    if (list != null && list.size() > 0) {
                        String current = getSharedPreferencesTitle(context);
                        for (TvConfig c : list) {
                            Log.i(TAG, "initialize: config.put => " + c.title);
                            titles.add(c.title);
                            if (current.length() == 0) current = c.title;
                            if (current.equals(c.title)) config = c;
                        }
                        if(config == null) {
                            Log.w(TAG, "initialize: config == null, reset...");
                            config = list.get(0);
                            current = config.title;
                        }
                        Log.i(TAG, "initialize: current title => " + current);
                        Log.i(TAG, "initialize: current config => " + config);
                        return Single.just(current);
                    } else {
                        Log.i(TAG, "initialize: config not found");
                        return Single.just("");
                    }
                });
    }

    public static String title() {
        return config.title;
    }

    public static String urlHome() {
        return config.urlHome;
    }

    public static String urlSearch() {
        return config.urlSearch;
    }

    public static String jsInject() {
        return String.format(
                "(function() {\n" +
                        "    const tag = document.createElement(\"script\");\n" +
                        "    tag.setAttribute(\"type\", \"text/javascript\");\n" +
                        "    tag.id = \"js_inject\";\n" +
                        "    tag.setAttribute(\"src\", \"https://assets/tv.js\");\n" +
                        "    document.head.appendChild(tag);\n" +
                        "    %s\n%s\n%s\n%s\n%s\n" +
                        "    return \"ok\";\n" +
                        "})()",
                config.jsGetVideoIframe,
                config.jsClearTag,
                config.jsLoadMeta,
                config.jsLoadEpisodes,
                config.jsSearchResults);
    }

    public static String jsRunTemplate(JScript js) {
        return "(function () {\n" +
                "    if (typeof window." + js.name() + " === \"function\") {\n" +
                "        return window." + js.name() + "();\n" +
                "    } else {\n" +
                "        return \"" + js.name() + "-func-not-found\";\n" +
                "    }\n" +
                "})()";
    }

    public static String jsVideoCmdTemplate(String cmd, String param) {
        return "(function () {\n" +
                "    if (typeof window.jsVideoCMD === \"function\") {\n" +
                "        return window.jsVideoCMD(\"" + cmd + "\", \"" + param + "\");\n" +
                "    } else {\n" +
                "        return \"func-not-found\";\n" +
                "    }\n" +
                "})()";
    }

    public enum JScript {
        jsInject,
        jsLoadMeta,
        jsLoadEpisodes,
        jsSearchResults,
        jsStart,
        jsClearTag,
        jsVideoCMD,
    }
    //https://lzj8csjmyd.execute-api.eu-west-2.amazonaws.com/default/tv
    public interface TvDataService {
        @GET("default/tv")
        Single<List<TvConfig>> getConfig();
    }
}
