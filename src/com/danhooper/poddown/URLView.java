package com.danhooper.poddown;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class URLView extends Activity {
    private DatabaseHelper databaseHelper;
    ArrayAdapter<Feed> feedListViewAdapter;
    FeedList feedList;
    private static final String TAG = "PodDown";
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_urlview);
        databaseHelper = new DatabaseHelper((Context) this);
        feedList = new FeedList(databaseHelper);
        feedListViewAdapter = new ArrayAdapter<Feed>(this,
                android.R.layout.simple_list_item_1, feedList.feeds);
        final ListView feedView = (ListView) findViewById(R.id.feedListView);
        feedView.setAdapter(feedListViewAdapter);
        this.registerForContextMenu(feedView);
        feedListViewAdapter.notifyDataSetChanged();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_urlview, menu);
        return true;
    }
    public void newFeed(View button) {
        Intent launchNewFeedActivity = new Intent(this, FeedFormActivity.class);
        startActivityForResult (launchNewFeedActivity, 0);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        feedList.UpdateFeeds();
        feedListViewAdapter.notifyDataSetChanged();
    }
}
