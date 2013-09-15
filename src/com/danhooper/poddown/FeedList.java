package com.danhooper.poddown;

import java.util.ArrayList;

import android.content.Context;

import com.j256.ormlite.dao.RuntimeExceptionDao;

public class FeedList {
    ArrayList<Feed> feeds;
    RuntimeExceptionDao<Feed, Integer> feedDao;
    @SuppressWarnings("unused")
    private static final String TAG = "PodDownFeedList";

    public FeedList(Context context) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        feedDao = databaseHelper.getFeedDao();
        feeds = queryForAllFeeds(feedDao);
    }

    public void updateFeeds() {
        feeds.clear();
        feeds.addAll(feedDao.queryForAll());
    }

    public void deleteFeed(Feed feed) {
        feedDao.delete(feed);
        updateFeeds();
    }

    private static ArrayList<Feed> queryForAllFeeds(
            RuntimeExceptionDao<Feed, Integer> feedDao) {
        ArrayList<Feed> feeds = new ArrayList<Feed>(feedDao.queryForAll());
        return feeds;
    }

    public static ArrayList<Feed> getAllFeeds(Context context) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        RuntimeExceptionDao<Feed, Integer> feedDao = databaseHelper
                .getFeedDao();
        return queryForAllFeeds(feedDao);
    }
}
