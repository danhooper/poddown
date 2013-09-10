package com.danhooper.poddown;

import com.danhooper.poddown.R;

import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;

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
        feedView.setOnItemClickListener(feedlClickListener);
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
        feedList.updateFeeds();
        feedListViewAdapter.notifyDataSetChanged();
    }

    private OnItemClickListener feedlClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            Toast.makeText(parent.getContext(), "Item Clicked #" + position,
                    Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_feedview, menu);
    }
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        final AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
                .getMenuInfo();
        // Retrieve the item that was clicked on
        Object listItem = feedListViewAdapter.getItem(info.position);

        switch (item.getItemId()) {
        case R.id.deleteFeed:
            deleteFeed((Feed) listItem);
            return true;
        case R.id.editFeed:
            editFeed(((Feed) listItem));
            return true;
        case R.id.downloadFeed:
            downloadFeed(((Feed) listItem));
            return true;
        case R.id.podcastDownloads:
            showPodcastDownloads((Feed) listItem);
        default:
            return super.onContextItemSelected(item);
        }
    }
    public void downloadFeed(Feed feed) {
        new FeedRetriever(this, this).execute(feed);
    }
    public void editFeed(Feed feed) {
        Intent launchNewFeedActivity = new Intent(this, FeedFormActivity.class);
        launchNewFeedActivity.putExtra("feed", feed);
        startActivityForResult (launchNewFeedActivity, 0);
    }
    public void showPodcastDownloads(Feed feed) {
        Intent pHistActivity = new Intent(this, PodcastHistoryView.class);
        pHistActivity.putExtra("feed", feed);
        startActivityForResult (pHistActivity, 0);
    }
    public void deleteFeed(Feed feed) {
        Toast.makeText(this.getApplicationContext(),
                "Deleting " + feed.toString(), Toast.LENGTH_SHORT)
                .show();
        feedList.deleteFeed(feed);
        feedListViewAdapter.notifyDataSetChanged();
    }
}
