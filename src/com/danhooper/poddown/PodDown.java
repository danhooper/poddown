package com.danhooper.poddown;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

public class PodDown extends Activity {
    FeedAdapter feedAdapter;
    FeedList feedList;
    @SuppressWarnings("unused")
    private static final String TAG = "PodDown";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_urlview);
        feedList = new FeedList(this);
        feedAdapter = new FeedAdapter(this);
        final ListView feedView = (ListView) findViewById(R.id.feedListView);
        feedView.setOnItemClickListener(feedlClickListener);
        feedView.setAdapter(feedAdapter);
        this.registerForContextMenu(feedView);
        feedAdapter.notifyDataSetChanged();
        startService(new Intent(this, DownloadService.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_urlview, menu);
        return true;
    }

    public void newFeed(View button) {
        Intent launchNewFeedActivity = new Intent(this, FeedFormActivity.class);
        startActivityForResult(launchNewFeedActivity, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        feedList.updateFeeds();
        feedAdapter.notifyDataSetChanged();
    }

    private final OnItemClickListener feedlClickListener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
            openContextMenu(view);
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
        Object listItem = feedAdapter.getItem(info.position);

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
        new FeedRetriever(this).execute(feed);
    }

    public void editFeed(Feed feed) {
        Intent launchNewFeedActivity = new Intent(this, FeedFormActivity.class);
        launchNewFeedActivity.putExtra("feed", feed);
        startActivityForResult(launchNewFeedActivity, 0);
    }

    public void showPodcastDownloads(Feed feed) {
        Intent pHistActivity = new Intent(this, PodcastHistoryView.class);
        pHistActivity.putExtra("feed", feed);
        startActivityForResult(pHistActivity, 0);
    }

    public void deleteFeed(Feed feed) {
        Toast.makeText(this.getApplicationContext(),
                "Deleting " + feed.toString(), Toast.LENGTH_SHORT).show();
        feedList.deleteFeed(feed);
        feedAdapter.notifyDataSetChanged();
    }
}
