package com.danhooper.poddown;

import android.app.AlarmManager;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class DownloadService extends Service {
    private NotificationManager notifMgr;
    private final int NOTIFICATION = R.string.download_service;
    private BroadcastReceiver broadcastReceiver;
    private static final String TAG = "PodDownDownloadService";
    private static final String DOWNLOAD_FEEDS_INTENT = "com.danhooper.poddown.download_feeds";
    LocalBroadcastManager broadcastMgr;
    Context context;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onCreate() {
        context = this;
        broadcastMgr = LocalBroadcastManager.getInstance(context);
        notifMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        // Display a notification about us starting. We put an icon in the
        // status bar.
        showNotification();
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.v(TAG, "onReceive");
                String action = intent.getAction();
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                    Long downloadId = intent.getLongExtra(
                            DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                    PodcastHistoryList pHistList = new PodcastHistoryList(
                            context);
                    pHistList.setDownloadedForDownloadId(downloadId, true);

                } else {
                    FeedList feedList = new FeedList(context);
                    for (Feed f : feedList.feeds) {
                        new FeedRetriever(context).execute(f);
                    }
                }
            }
        };
        registerReceiver(broadcastReceiver, new IntentFilter(
                DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        scheduleTimer();
    }

    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        CharSequence text = getText(R.string.download_service_started);

        // The PendingIntent to launch our activity if the user selects this
        // notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, PodDown.class), 0);

        Notification notification = new Notification.Builder(context)
                .setContentTitle(text).setSmallIcon(R.drawable.ic_launcher)
                .setContentIntent(contentIntent).getNotification();

        notifMgr.notify(NOTIFICATION, notification);
    }

    private void scheduleTimer() {
        Log.v(TAG, "scheduleTimer");
        AlarmManager am = (AlarmManager) (context
                .getSystemService(ALARM_SERVICE));
        context.registerReceiver(broadcastReceiver, new IntentFilter(
                DOWNLOAD_FEEDS_INTENT));
        Intent intent = new Intent(DOWNLOAD_FEEDS_INTENT);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                intent, 0);
        am.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis(), AlarmManager.INTERVAL_HOUR,
                pendingIntent);
    }
}
