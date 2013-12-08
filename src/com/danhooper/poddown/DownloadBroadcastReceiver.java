package com.danhooper.poddown;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

public class DownloadBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "PodDownDownloadBroadcastReceiver";
    Context context = null;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        Log.v(TAG, "onReceive");
        String action = intent.getAction();
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
            Long downloadId = intent.getLongExtra(
                    DownloadManager.EXTRA_DOWNLOAD_ID, 0);
            PodcastHistoryList pHistList = new PodcastHistoryList(context);
            PodcastHistory pHist = pHistList.setDownloadedForDownloadId(
                    downloadId, true);
            Cursor downloadCursor = ((DownloadManager) context
                    .getSystemService(Context.DOWNLOAD_SERVICE))
                    .query(new DownloadManager.Query()
                            .setFilterById(downloadId));
            if (downloadCursor.moveToFirst()) {
                final String localFileName = downloadCursor
                        .getString(downloadCursor
                                .getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                MediaScannerConnection.scanFile(context,
                        new String[] { Environment.DIRECTORY_DOWNLOADS }, null,
                        new Playlist(context, localFileName, pHist)
                // new OnScanCompletedListener() {
                //
                // public void onScanCompleted(String path, Uri uri) {
                // Log.v(TAG, "file " + path
                // + " was scanned seccessfully: " + uri);
                // addToPlaylist(pHist, getFilenameFromURI(uri));
                //
                // }
                // }
                        );

            }

        } else {
            FeedList feedList = new FeedList(context);
            for (Feed f : feedList.feeds) {
                new FeedRetriever(context).execute(f);
            }
        }
    }

    public String getFilenameFromURI(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = { MediaStore.Files.FileColumns.DATA };
            cursor = context.getContentResolver().query(contentUri, proj, null,
                    null, null);
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
            cursor.moveToFirst();
            String fileName = cursor.getString(column_index);
            Log.v(TAG, "getFilenameFromURI " + fileName);
            return fileName;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void addToPlaylist(PodcastHistory pHist, String localFileName) {
        int audioId = getAudioId(localFileName);
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

    private int getAudioId(String localFileName) {
        String where = android.provider.MediaStore.Audio.Media.DISPLAY_NAME
                + "=?";
        String whereVal[] = { localFileName };
        // String whereVal[] = { "test-9.mp3" };
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, where,
                whereVal, null);
        if (cursor.moveToFirst()) {
            int audio_id = cursor
                    .getInt(cursor
                            .getColumnIndex(android.provider.MediaStore.Audio.Media._ID));
            Log.v(TAG, "Audio id is " + audio_id);
            return audio_id;
        } else {
            Log.e(TAG, "Could not find the audio by id for " + localFileName);
            return -1;
        }
    }

    public void onScanCompleted(String path, Uri uri) {
        Log.v(TAG, "Scan completed for " + path + " " + uri);
    }
}
