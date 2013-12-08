package com.danhooper.poddown;

import android.util.Log;

import com.j256.ormlite.field.DatabaseField;

public class PodcastHistory {
    public static final String TAG = "PodDown";
    public static final String PODCAST_FILE_NAME = "podcast_file_name";
    public static final String PODCAST_DOWNLOADED = "downloaded";
    public static final String PODCAST_DOWNLOAD_ID = "downloadId";
    @DatabaseField(generatedId = true)
    int id;
    @DatabaseField(foreign = true, foreignAutoRefresh = true)
    private Feed feed;
    @DatabaseField(columnName = PODCAST_FILE_NAME)
    String fileName;
    @DatabaseField
    String url;
    @DatabaseField(columnName = PODCAST_DOWNLOAD_ID)
    Long downloadId;
    @DatabaseField(columnName = PODCAST_DOWNLOADED)
    boolean downloaded;

    PodcastHistory() {
        // needed by ormlite
    }

    PodcastHistory(Feed feed, String fileName, String url, Long downloadId) {
        this.feed = feed;
        this.fileName = fileName;
        this.url = url;
        this.downloadId = downloadId;
        this.downloaded = false;
    }

    PodcastHistory(Feed feed, String fileName, String url, Long downloadId,
            boolean downloaded) {
        this.feed = feed;
        this.fileName = fileName;
        this.url = url;
        this.downloadId = downloadId;
        this.downloaded = downloaded;
    }

    @Override
    public String toString() {
        return this.fileName + " " + downloaded;
    }

    public void setDownloaded(boolean downloaded) {
        this.downloaded = downloaded;
    }

    public Feed getFeed() {
        Log.v(TAG, "feeds playlist id is " + feed.playlistId);
        return feed;
    }
}
