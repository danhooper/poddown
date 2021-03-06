package com.danhooper.poddown;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.j256.ormlite.dao.RuntimeExceptionDao;

public class FeedRetriever extends AsyncTask<Feed, Void, Boolean> {
    private static final String TAG = "PodDownFeedRetriever";
    Feed feed;
    Context context;

    public FeedRetriever(Context context) {
        this.context = context;
    }

    private Boolean openHttpConnection(Feed feed) throws IOException {
        feed.lastChecked = new Date();
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        RuntimeExceptionDao<Feed, Integer> feedDao = databaseHelper
                .getFeedDao();
        feedDao.createOrUpdate(feed);
        InputStream in = null;
        String line = "";
        int response = -1;
        URL url = new URL(feed.url);
        Log.v(TAG, "Trying to download from url " + feed.url);
        URLConnection conn = url.openConnection();
        if (!(conn instanceof HttpURLConnection))
            throw new IOException("Not an HTTP connection");
        try {
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            response = httpConn.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();
                InputStreamReader urlReader = new InputStreamReader(in);
                BufferedReader bufferedReader = new BufferedReader(urlReader);
                FileOutputStream fos = context.openFileOutput(
                        feed.getFeedFileName(), Context.MODE_PRIVATE);
                line = bufferedReader.readLine();
                while (line != null) {
                    fos.write(line.getBytes());
                    line = bufferedReader.readLine();
                }
                fos.close();
            }
        } catch (Exception ex) {
            throw new IOException("Error connecting");
        }
        return true;
    }

    private void downloadPodcasts(Feed feed) {
        try {
            FileInputStream urlFile = context.openFileInput(feed
                    .getFeedFileName());
            InputStreamReader urlFileStream = new InputStreamReader(urlFile);
            BufferedReader bufferedReader = new BufferedReader(urlFileStream);
            String line;
            Pattern p = Pattern.compile("url=\"(([\\w:/\\.-])+)\"");
            while ((line = bufferedReader.readLine()) != null) {
                Matcher m = p.matcher(line);
                while (m.find()) {
                    String podcastUrl = m.group(1);
                    downloadPodcast(podcastUrl, feed);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void downloadPodcast(String podcastUrl, Feed feed) {
        Uri podcastURI = Uri.parse(podcastUrl);
        String path = podcastURI.getPath();
        String destFile = path.substring(path.lastIndexOf('/') + 1);
        PodcastHistoryList pHistList = new PodcastHistoryList(context, feed);
        if (!pHistList.alreadyDownloaded(destFile)) {
            DownloadManager downloadMgr = (DownloadManager) context
                    .getSystemService(Context.DOWNLOAD_SERVICE);
            Request req = new Request(podcastURI);
            req.setDestinationInExternalPublicDir(
                    Environment.DIRECTORY_DOWNLOADS, destFile)
                    .setNotificationVisibility(
                            Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            req.allowScanningByMediaScanner();
            Long downloadId = downloadMgr.enqueue(req);
            pHistList.addPodcast(new PodcastHistory(feed, destFile, podcastUrl,
                    downloadId));
        }
    }

    @Override
    protected Boolean doInBackground(Feed... feeds) {
        for (Feed feed : feeds) {
            try {
                if (openHttpConnection(feed)) {
                    downloadPodcasts(feed);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    protected void onPostExecute(String result) {
        Toast.makeText(context, "Feed Downloaded", Toast.LENGTH_LONG).show();
    }
}
