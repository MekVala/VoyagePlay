package com.mektech.voyageplay.voyageplay;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by mek on 2/3/18.
 */

public class NotificationBroadcastReciver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().toString().equals("PLAY_NOTIF")) {
            Toast.makeText(context, "PLAY", Toast.LENGTH_LONG).show();
        }
    }
}
