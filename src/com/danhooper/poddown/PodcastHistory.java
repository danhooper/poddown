package com.danhooper.poddown;

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
}
