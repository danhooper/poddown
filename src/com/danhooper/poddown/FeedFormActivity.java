package com.danhooper.poddown;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MergeCursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;

import com.j256.ormlite.dao.RuntimeExceptionDao;

public class FeedFormActivity extends Activity {
    private Feed f;
    private final String TAG = "PodDown";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feed_form);
        f = (Feed) getIntent().getSerializableExtra("feed");
        if (f != null) {
            ((EditText) findViewById(R.id.EditTextName)).setText(f.name);
            ((EditText) findViewById(R.id.EditTextURL)).setText(f.url);
        }
        populatePlaylistDropDown();
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public void saveFeed(View button) {
        String name = ((EditText) findViewById(R.id.EditTextName)).getText()
                .toString();
        String url = ((EditText) findViewById(R.id.EditTextURL)).getText()
                .toString();
        boolean wifiOnly = ((CheckBox) findViewById(R.id.CheckBoxWifiOnly))
                .isChecked();
        String downloadFrequency = ((Spinner) findViewById(R.id.SpinnerFeedCheckFrequency))
                .getSelectedItem().toString();
        Cursor cursor = ((Cursor) ((Spinner) findViewById(R.id.SpinnerPlaylist))
                .getSelectedItem());
        int playlistId = cursor.getInt(cursor
                .getColumnIndex(MediaStore.Audio.Playlists._ID));
        if (f != null) {
            f.name = name;
            f.url = url;
            f.wifiOnly = wifiOnly;
            f.downloadFrequency = Feed
                    .convertDownloadFrequency(downloadFrequency);
            f.playlistId = playlistId;
            Log.v(TAG, "Setting playlistId " + playlistId);
        } else {
            f = new Feed(name, url, downloadFrequency, wifiOnly, null,
                    playlistId);
        }
        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        RuntimeExceptionDao<Feed, Integer> feedDao = databaseHelper
                .getFeedDao();
        feedDao.createOrUpdate(f);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent myIntent = new Intent(getApplicationContext(), PodDown.class);
        startActivityForResult(myIntent, 0);
        return true;
    }

    private void populatePlaylistDropDown() {
        Cursor cursor = getContentResolver().query(
                MediaStore.Audio.Playlists.EXTERNAL_CONTENT_URI, null, null,
                null, null);
        // add an extra row to explain this field
        MatrixCursor extras = new MatrixCursor(
                new String[] { MediaStore.Audio.Playlists._ID,
                        MediaStore.Audio.Playlists.NAME });
        extras.addRow(new String[] { "-1", "Add to playlist" });
        Cursor[] cursors = { extras, cursor };
        Cursor extendedCursor = new MergeCursor(cursors);

        Spinner playlistSpinner = (Spinner) findViewById(R.id.SpinnerPlaylist);
        int[] to = new int[] { android.R.id.text1 };
        SimpleCursorAdapter playlistAdapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_spinner_dropdown_item, extendedCursor,
                new String[] { MediaStore.Audio.Playlists.NAME }, to, 0);
        playlistAdapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        playlistSpinner.setAdapter(playlistAdapter);

    }
}
