package com.danhooper.poddown;

import com.j256.ormlite.field.DatabaseField;

public class Feed {
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
    
    @Override
    public String toString() {
        return this.name;
    }
}
