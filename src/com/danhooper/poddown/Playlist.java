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
    String filename;
    PodcastHistory pHist;

    Playlist(Context context, String filename, PodcastHistory pHist) {
        this.context = context;
        this.filename = filename;
        this.pHist = pHist;
    }

    public void onScanCompleted(String path, Uri uri) {
        Log.v(TAG, "file " + path + " was scanned seccessfully: " + uri);
        addToPlaylist();

    }

    private void addToPlaylist() {
        int audioId = getAudioId();
        Log.v(TAG, "Audio id is " + audioId);
        ContentResolver resolver = context.getContentResolver();
        String[] cols = new String[] { "count(*)" };
        Feed f = pHist.getFeed();
        Uri playlistURI = MediaStore.Audio.Playlists.Members.getContentUri(
                "external", f.playlistId);
        Log.v(TAG, "playlistUri " + playlistURI.toString() + " id "
                + f.playlistId);
        Cursor cur = resolver.query(playlistURI, cols, null, null, null);
        cur.moveToFirst();
        final int base = cur.getInt(0);
        Log.v(this.getClass().getCanonicalName(), "base is " + base);
        cur.close();
        ContentValues values = new ContentValues();
        values.put(MediaStore.Audio.Playlists.Members.PLAY_ORDER, base + 1);
        values.put(MediaStore.Audio.Playlists.Members.AUDIO_ID, audioId);
        resolver.insert(playlistURI, values);

    }

    private int getAudioId() {
        String where = MediaStore.Audio.Media.DISPLAY_NAME + "=?";
        String whereVal[] = { filename };
        // String whereVal[] = { "test-9.mp3" };
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Audio.Media._ID }, where, whereVal,
                null);
        if (cursor.moveToFirst()) {
            int audio_id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.Audio.Media._ID));
            Log.v(TAG, "Audio id is " + audio_id);
            return audio_id;
        } else {
            Log.e(TAG, "Could not find the audio by id for " + filename);
            return -1;
        }
    }

    public void onMediaScannerConnected() {
        // TODO Auto-generated method stub

    }
}
