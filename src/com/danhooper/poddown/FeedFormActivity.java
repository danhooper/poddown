package com.danhooper.poddown;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

public class FeedFormActivity extends Activity {
    private Feed f;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feed_form);
        Intent i = getIntent();
        f = (Feed) i.getSerializableExtra("feed");
        if (f != null) {
            ((EditText) findViewById(R.id.EditTextName)).setText(f.name);
            ((EditText) findViewById(R.id.EditTextURL)).setText(f.url);
        }
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
    public void saveFeed(View button) {
        String name = ((EditText) findViewById(R.id.EditTextName)).getText().toString();
        String url = ((EditText) findViewById(R.id.EditTextURL)).getText().toString();
        boolean wifiOnly = ((CheckBox) findViewById(R.id.CheckBoxWifiOnly)).isChecked();
        String downloadFrequency = ((Spinner) findViewById(R.id.SpinnerFeedCheckFrequency)).getSelectedItem().toString();
        if( f != null) {
            f.name = name;
            f.url = url;
            f.wifiOnly = wifiOnly;
            f.downloadFrequency = Feed.convertDownloadFrequency(downloadFrequency);
        }
        else {
            f = new Feed(name, url, downloadFrequency, wifiOnly);
        }
        DatabaseHelper databaseHelper = new DatabaseHelper((Context) this);
        RuntimeExceptionDao<Feed, Integer> feedDao = databaseHelper.getFeedDao();
        feedDao.createOrUpdate(f);
        finish();
    }
    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), PodDown.class);
        startActivityForResult(myIntent, 0);
        return true;
    }
}
