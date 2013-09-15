package com.danhooper.poddown;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AutoStart extends BroadcastReceiver {   
    private static final String TAG = "PodDown";
    
    @Override
    public void onReceive(Context context, Intent intent)
    {   
        Log.v(TAG, "onReceive");
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            Log.v(TAG, "Received android.intent.action.BOOT_COMPLETED");
           context.startService(new Intent(context, DownloadService.class));
        }
    }
}
