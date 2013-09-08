package com.danhooper.poddown;

import java.sql.SQLException;
import java.util.List;

import android.content.Context;

import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.field.DatabaseField;

public class PodcastHistory {
    public static final String PODCAST_FILE_NAME = "podcast_file_name";
    @DatabaseField(generatedId = true)
    int id;
    @DatabaseField(columnName = PODCAST_FILE_NAME)
    String fileName;
    @DatabaseField
    String url;
    
    PodcastHistory() {
        // needed by ormlite
    }

    PodcastHistory(String fileName, String url) {
        this.fileName = fileName;
        this.url = url;
    }    
    @Override
    public String toString() {
        return this.fileName;
    }
    public static boolean alreadyDownloaded(Context context, String fileName) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        RuntimeExceptionDao<PodcastHistory, Integer> pHistDao = databaseHelper.getPodcastHistoryDao();
        try {
            List<PodcastHistory> podcasts =
                    pHistDao.queryBuilder().where()
                       .eq(PodcastHistory.PODCAST_FILE_NAME, fileName)
                       .query();
            if (podcasts.size() > 0) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public static void addPostcast(Context context, PodcastHistory pHist) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        RuntimeExceptionDao<PodcastHistory, Integer> pHistDao = databaseHelper.getPodcastHistoryDao();
        pHistDao.create(pHist);        
    }
}
