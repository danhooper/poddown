package com.danhooper.poddown;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
    private static final String DATABASE_NAME = "podDown.db";
    private static final int DATABASE_VERSION = 5;
    private RuntimeExceptionDao<Feed, Integer> feedRuntimeDao = null;
    private RuntimeExceptionDao<PodcastHistory, Integer> pHistRuntimeDao = null;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onCreate");
            TableUtils.createTable(connectionSource, Feed.class);
            TableUtils.createTable(connectionSource, PodcastHistory.class);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource,
            int oldVersion, int newVersion) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onUpgrade");
            TableUtils.dropTable(connectionSource, Feed.class, true);
            TableUtils.dropTable(connectionSource, PodcastHistory.class, true);
            // after we drop the old databases, we create the new ones
            onCreate(db, connectionSource);
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
            throw new RuntimeException(e);
        }

    }

    public RuntimeExceptionDao<Feed, Integer> getFeedDao() {
        if (feedRuntimeDao == null) {
            feedRuntimeDao = getRuntimeExceptionDao(Feed.class);
        }
        return feedRuntimeDao;
    }

    public RuntimeExceptionDao<PodcastHistory, Integer> getPodcastHistoryDao() {
        if (pHistRuntimeDao == null) {
            pHistRuntimeDao = getRuntimeExceptionDao(PodcastHistory.class);
        }
        return pHistRuntimeDao;
    }

    /**
     * Close the database connections and clear any cached DAOs.
     */
    @Override
    public void close() {
        super.close();
        feedRuntimeDao = null;
    }
}
