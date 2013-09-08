package com.danhooper.poddown;

import java.util.ArrayList;

import android.util.Log;

import com.j256.ormlite.dao.RuntimeExceptionDao;


public class FeedList {
    ArrayList<Feed> feeds;
    RuntimeExceptionDao<Feed, Integer> feedDao;
    private static final String TAG = "PodDownFeedList";
    DatabaseHelper databaseHelper;
    
    public FeedList(DatabaseHelper databaseHelper) {
        feedDao = databaseHelper.getFeedDao();
        feeds = new ArrayList<Feed>(feedDao.queryForAll());
    }
    public void updateFeeds() {
        feeds.clear();
        feeds.addAll(feedDao.queryForAll());
    }
    public void deleteFeed(Feed feed) {
        feedDao.delete(feed);
        updateFeeds();
    }
}
