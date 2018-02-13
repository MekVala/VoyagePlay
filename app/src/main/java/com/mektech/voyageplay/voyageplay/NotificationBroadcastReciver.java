package com.mektech.voyageplay.voyageplay;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.widget.Toast;

/**
 * Created by mek on 2/3/18.
 */

public class NotificationBroadcastReciver extends BroadcastReceiver {

    VoyagePlayMediaService mService;
    Boolean mServiceBound = false;
    Intent iService;

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().toString().equals("PLAY_NOTIF")) {

        }
    }

}
