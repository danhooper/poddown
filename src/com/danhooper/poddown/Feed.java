package com.danhooper.poddown;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;

public class Feed implements Serializable {
    @DatabaseField(generatedId = true)
    int id;
    @DatabaseField
    String name;
    @DatabaseField
    String url;
    @DatabaseField
    int downloadFrequency;
    @DatabaseField
    boolean wifiOnly;
    
    Feed() {
        // needed by ormlite
    }

    Feed(String name, String url, int downloadFrequency, boolean wifiOnly) {
        this.name = name;
        this.url = url;
        this.downloadFrequency = downloadFrequency;
        this.wifiOnly = wifiOnly;
    }
    Feed(String name, String url, String downloadFrequency, boolean wifiOnly) {
        this.name = name;
        this.url = url;
        this.downloadFrequency = convertDownloadFrequency(downloadFrequency);
        this.wifiOnly = wifiOnly;
    }
    
    @Override
    public String toString() {
        return this.name;
    }
    public static int convertDownloadFrequency(String downloadFrequency) {
        if (downloadFrequency.contains("6")) {
            return 6;
        } else if (downloadFrequency.contains("12")) {
            return 12;
        } else if (downloadFrequency.contains("24")) {
            return 24;
        }
        return 0;
    }
    public String getFeedFileName() {
        String fileName = name.replaceAll("\\W+", "_");
        fileName += ".txt";
        return fileName;
    }
}
