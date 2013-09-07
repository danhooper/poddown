package com.danhooper.poddown;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

public class FeedFormActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.feed_form);
    }
    public void saveFeed(View button) {
        String name = ((EditText) findViewById(R.id.EditTextName)).getText().toString();
        String url = ((EditText) findViewById(R.id.EditTextURL)).getText().toString();
        boolean wifiOnly = ((CheckBox) findViewById(R.id.CheckBoxWifiOnly)).isChecked();
        String downloadFrequency = ((Spinner) findViewById(R.id.SpinnerFeedCheckFrequency)).getSelectedItem().toString();
        Feed feed = new Feed(name, url, 0, wifiOnly);

        DatabaseHelper databaseHelper = new DatabaseHelper((Context) this);
        RuntimeExceptionDao<Feed, Integer> feedDao = databaseHelper.getFeedDao();
        feedDao.create(feed);
        finish();
    }
    
}
