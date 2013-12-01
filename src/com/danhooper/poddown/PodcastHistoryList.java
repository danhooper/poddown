package com.danhooper.poddown;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;

public class PodcastHistoryList {
    private final Context context;
    private ArrayList<PodcastHistory> pHistList;
    private final Feed feed;

    public PodcastHistoryList(Context context) {
        this.context = context;
        this.feed = null;
        pHistList = buildPodcastHistoryList();
    }

    public PodcastHistoryList(Context context, Feed feed) {
        this.context = context;
        this.feed = feed;
        pHistList = buildPodcastHistoryList();
    }

    public ArrayList<PodcastHistory> getList() {
        return pHistList;
    }

    public void refreshList() {
        buildPodcastHistoryList();
    }

    private ArrayList<PodcastHistory> buildPodcastHistoryList() {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        RuntimeExceptionDao<PodcastHistory, Integer> pHistDao = databaseHelper
                .getPodcastHistoryDao();
        ArrayList<PodcastHistory> podcastHists = null;
        try {
            QueryBuilder<PodcastHistory, Integer> query = pHistDao
                    .queryBuilder().orderBy("id", true);
            if (feed != null) {
                podcastHists = new ArrayList<PodcastHistory>(query.where()
                        .eq("feed_id", feed).query());
            } else {
                podcastHists = new ArrayList<PodcastHistory>(query.query());

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return podcastHists;
    }

    public boolean alreadyDownloaded(String fileName) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        RuntimeExceptionDao<PodcastHistory, Integer> pHistDao = databaseHelper
                .getPodcastHistoryDao();
        try {
            List<PodcastHistory> podcasts = pHistDao.queryBuilder().where()
                    .eq(PodcastHistory.PODCAST_FILE_NAME, fileName).query();
            if (podcasts.size() > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void addPodcast(PodcastHistory pHist) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        RuntimeExceptionDao<PodcastHistory, Integer> pHistDao = databaseHelper
                .getPodcastHistoryDao();
        pHistDao.create(pHist);
        pHistList = buildPodcastHistoryList();
    }

    public PodcastHistory setDownloadedForDownloadId(Long downloadId,
            boolean downloaded) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        RuntimeExceptionDao<PodcastHistory, Integer> pHistDao = databaseHelper
                .getPodcastHistoryDao();
        try {
            List<PodcastHistory> podcasts = pHistDao.queryBuilder().where()
                    .eq(PodcastHistory.PODCAST_DOWNLOAD_ID, downloadId).query();
            if (podcasts.size() > 0) {
                PodcastHistory pHist = podcasts.get(0);
                if (pHist != null) {
                    Log.i(DatabaseHelper.class.getName(),
                            "set downloaded true for " + downloadId);
                    pHist.setDownloaded(downloaded);
                    pHistDao.createOrUpdate(pHist);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
