package com.danhooper.poddown;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class PodcastHistoryView extends Activity {
    ArrayAdapter<PodcastHistory> pHistListViewAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.podcast_history);
        pHistListViewAdapter = new ArrayAdapter<PodcastHistory>(this,
                android.R.layout.simple_list_item_1, 
                PodcastHistory.getAllPodcastHistory(this));
        final ListView pHistView = (ListView) findViewById(R.id.pHistListView);
//        feedView.setOnItemClickListener(feedlClickListener);
        pHistView.setAdapter(pHistListViewAdapter);
        this.registerForContextMenu(pHistView);
        pHistListViewAdapter.notifyDataSetChanged();
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }
    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(getApplicationContext(), URLView.class);
        startActivityForResult(myIntent, 0);
        return true;
    }
}
