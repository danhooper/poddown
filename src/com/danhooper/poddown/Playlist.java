package com.danhooper.poddown;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaScannerConnection.MediaScannerConnectionClient;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

public class Playlist implements MediaScannerConnectionClient {
    private static final String TAG = "PodDownMediaScannerReceiver";
    Context context;
    String filepath;
    PodcastHistory pHist;

    Playlist(Context context, String filepath, PodcastHistory pHist) {
        this.context = context;
        this.filepath = filepath;
        this.pHist = pHist;
    }

    public void onScanCompleted(String path, Uri uri) {
        try {
            Thread.sleep(6000);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        Log.v(TAG, "file " + path + " was scanned successfully: " + uri);
        addToPlaylist();

    }

    private void addToPlaylist() {
        int audioId = getAudioId();
        // Log.v(TAG, "Audio id is " + audioId);
        ContentResolver resolver = context.getContentResolver();
        String[] cols = new String[] { "count(*)" };
        Feed f = pHist.getFeed();
        Uri playlistURI = MediaStore.Audio.Playlists.Members.getContentUri(
                "external", f.playlistId);
        Cursor cur = resolver.query(playlistURI, cols, null, null, null);
        int base = 0;
        if (cur.moveToFirst()) {
            base = cur.getInt(0);
        }
        cur.close();
        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, base + 1);
        values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, audioId);
        resolver.insert(playlistURI, values);

    }

    private int getAudioId() {
        String filename = filepath.substring(filepath.lastIndexOf("/") + 1);
        Log.v(TAG, "Getting audio id for filename " + filename);
        String where = MediaStore.Audio.Media.DISPLAY_NAME + "=?";
        String whereVal[] = { filename };
        int audio_id = -1;

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, where,
                whereVal, null);
        if (cursor.moveToFirst()) {
            audio_id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.Audio.Media._ID));
            Log.v(TAG, "Audio id is " + audio_id);
        } else {
            Log.e(TAG, "Could not find the audio by id for " + filename);
        }
        Log.v(TAG, "The MediaStore.Audio.Media query has " + cursor.getCount()
                + " results for " + filename);
        cursor.close();
        return audio_id;
    }

    public void onMediaScannerConnected() {
        // TODO Auto-generated method stub

    }
}
