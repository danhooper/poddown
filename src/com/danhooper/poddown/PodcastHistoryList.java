package com.danhooper.poddown;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;
import java.sql.SQLException;

public class PodcastHistoryList {
    private Context context;
    private ArrayList<PodcastHistory> pHistList;

    public PodcastHistoryList(Context context) {
        this.context = context;
        pHistList = buildPodcastHistoryList();
    }
    public ArrayList<PodcastHistory> getList() {
        return pHistList;
    }

    private ArrayList<PodcastHistory> buildPodcastHistoryList() {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        RuntimeExceptionDao<PodcastHistory, Integer> pHistDao = databaseHelper
                .getPodcastHistoryDao();
        QueryBuilder<PodcastHistory, Integer> pHistQuery = pHistDao
                .queryBuilder();
        ArrayList<PodcastHistory> podcastHists = null;
        try {
            podcastHists = new ArrayList<PodcastHistory>(pHistQuery.orderBy(
                    "id", true).query());
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
}
