package com.danhooper.poddown;

import android.app.AlarmManager;
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
    private int NOTIFICATION = R.string.download_service;
    private BroadcastReceiver broadcastReceiver;
    private static final String TAG = "PodDownDownloadService";
    LocalBroadcastManager broadcastMgr;
    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }
    @Override
    public void onCreate() {
        Context context = (Context)this;
        broadcastMgr = LocalBroadcastManager.getInstance(context);
        notifMgr = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        // Display a notification about us starting.  We put an icon in the status bar.
        showNotification();
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                DatabaseHelper databaseHelper = new DatabaseHelper(context);
                FeedList feedList = new FeedList(databaseHelper);
                for( Feed f: feedList.feeds) {
                    new FeedRetriever(context).execute(f);
                }
                Log.v(TAG, "onReceive");
            }
        };
        scheduleTimer();
    }

    /**
     * Show a notification while this service is running.
     */
    private void showNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = getText(R.string.download_service_started);

        // Set the icon, scrolling text and timestamp
        Notification notification = new Notification(R.drawable.ic_launcher, text,
                System.currentTimeMillis());

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, URLView.class), 0);

        // Set the info for the views that show in the notification panel.
        notification.setLatestEventInfo(this, getText(R.string.download_service_started),
                       text, contentIntent);

        // Send the notification.
        notifMgr.notify(NOTIFICATION, notification);
    }
    private void scheduleTimer() {
            Context context = (Context)this;
            AlarmManager am = (AlarmManager)(context.getSystemService( ALARM_SERVICE));
            context.registerReceiver(broadcastReceiver, 
                    new IntentFilter("com.dan.hooper.poddown.download_feeds"));
            Intent intent = new Intent("com.dan.hooper.poddown.download_feeds");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0,
                    intent, 0);
            am.setInexactRepeating (AlarmManager.RTC_WAKEUP, 
                    System.currentTimeMillis(), AlarmManager.INTERVAL_HOUR, 
                    pendingIntent);
            Log.v(TAG, "scheduleTimer");
    }
}
