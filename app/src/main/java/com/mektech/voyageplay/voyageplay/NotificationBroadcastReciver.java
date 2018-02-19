package com.mektech.voyageplay.voyageplay;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by mek on 2/16/18.
 */

public class NotificationBroadcastReciver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

        if(intent.getAction().toString().equals("PLAY_NOTIF")) {
            Intent iPlay = new Intent(context,VoyagePlayMediaService.class);
            iPlay.putExtra("action","PLAY_NOTIF");
            context.startService(iPlay);
        }
        else if(intent.getAction().toString().equals("PREV_NOTIF")) {
            Intent iPlay = new Intent(context,VoyagePlayMediaService.class);
            iPlay.putExtra("action","PREV_NOTIF");
            context.startService(iPlay);
        }else if(intent.getAction().toString().equals("NEXT_NOTIF")) {
            Intent iPlay = new Intent(context,VoyagePlayMediaService.class);
            iPlay.putExtra("action","NEXT_NOTIF");
            context.startService(iPlay);
        }
    }
}
