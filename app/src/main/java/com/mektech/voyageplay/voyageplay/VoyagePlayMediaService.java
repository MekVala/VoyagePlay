package com.mektech.voyageplay.voyageplay;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.RemoteViews;
import java.util.ArrayList;

public class VoyagePlayMediaService extends Service {

    private IBinder mBinder = new MyBinder();
    private static MediaPlayer mediaPlayer;
    private int length = 0;
    private ArrayList songList;
    private SongInfo song;
    private int currentSong = 0;

    public VoyagePlayMediaService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initializePlayer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String action = intent.getStringExtra("action");
        if(action!= null && action.equals("PLAY_NOTIF")){
            if(mediaPlayer.isPlaying()){
                pause();
            }else if(!mediaPlayer.isPlaying()){
                play();
            }
        }else if(action!=null && action.equals("PREV_NOTIF")){
            previous();
        }else if(action !=null && action.equals("NEXT_NOTIF")){
            next();
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {return mBinder;}

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        mediaPlayer = null;

        NotificationManager nm = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        nm.cancel(0);
    }

    public void play(){
        if(!(mediaPlayer.isPlaying())&&(mediaPlayer !=null)){
            mediaPlayer.seekTo(length);
            mediaPlayer.start();
        }
    }

    public void pause(){
        if(mediaPlayer.isPlaying() && mediaPlayer!=null){
            length = mediaPlayer.getCurrentPosition();
            mediaPlayer.pause();
        }
    }

    public void seekTo(int seekAt){
        if(mediaPlayer!=null){
            mediaPlayer.seekTo(seekAt);
            length = seekAt;
        }
    }

    public void next(){
        if(mediaPlayer!=null){
            mediaPlayer.reset();
            initializePlayer();
            currentSong = currentSong + 1;
            if(currentSong == songList.size()){currentSong = 0;}
            setCurrentSong(currentSong);
        }
    }

    public void previous(){
        if(mediaPlayer!=null){
            mediaPlayer.reset();
            initializePlayer();
            currentSong=currentSong-1;
            if(currentSong==-1){currentSong = songList.size()-1;}
            setCurrentSong(currentSong);
        }
    }

    public boolean isPlaying(){return mediaPlayer.isPlaying();}

    public void initializePlayer(){
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mediaPlayer.start();
                sendLoadingComplete(getApplicationContext());
            }
        });

        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            @Override
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                sendBufferInfo(getApplicationContext(),percent);
            }
        });

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                next();
            }
        });
    }

    public void setCurrentSong(int position){
        currentSong = position;
        song = (SongInfo) songList.get(position);
        setSourceMusic(song.getUrl());
    }

    public SongInfo getCurrentSong(){return  song;}

    public void setSourceMusic(String url){

        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.reset();
        }

        try {
            mediaPlayer.setDataSource(url);
        }catch (Exception e){
            Log.d("VoyagePlay:",e.getMessage());
        }
        //new Player().execute(url);
        try {
            mediaPlayer.prepareAsync();
        }catch (Exception ee){Log.d("VoyagePlay:","error on prepare");}

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                sendProgressInfo(getApplicationContext(),mediaPlayer.getCurrentPosition());
                handler.postDelayed(this,100);
            }
        }, 100);
    }

    public int getDuration(){return mediaPlayer.getDuration();}

    public void setSongQueue(ArrayList songList){this.songList = songList;}

    public void createNotification(){
        Intent intent = new Intent(this, PlayerActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setTicker("VoyagePlayTicker")
                .setAutoCancel(false)
                .setContentIntent(pIntent);
        builder.setContent(getComplexNotificationView());

        Notification n = builder.build();
        //n.flags |= Notification.FLAG_NO_CLEAR | Notification.FLAG_ONGOING_EVENT;
        NotificationManager notificationManager;
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,n);
    }

    private RemoteViews getComplexNotificationView() {
        RemoteViews notificationView = new RemoteViews(
                getApplicationContext().getPackageName(),
                R.layout.player_notification
        );

        Intent iPlay = new Intent(getApplicationContext(),NotificationBroadcastReciver.class);
        iPlay.setAction("PLAY_NOTIF");
        PendingIntent playpendingIntent = PendingIntent.getBroadcast(getApplicationContext(),7,iPlay,0);
        notificationView.setOnClickPendingIntent(R.id.btnPlayNotif,playpendingIntent);

        Intent iPrev = new Intent(getApplicationContext(),NotificationBroadcastReciver.class);
        iPlay.setAction("PREV_NOTIF");
        PendingIntent prevpendingIntent = PendingIntent.getBroadcast(getApplicationContext(),7,iPrev,0);
        notificationView.setOnClickPendingIntent(R.id.btnPrevNotif,prevpendingIntent);

        Intent iNext = new Intent(getApplicationContext(),NotificationBroadcastReciver.class);
        iPlay.setAction("NEXT_NOTIF");
        PendingIntent nextpendingIntent = PendingIntent.getBroadcast(getApplicationContext(),7,iNext,0);
        notificationView.setOnClickPendingIntent(R.id.btnNextNotif,nextpendingIntent);




        return notificationView;
    }
    /*
    public class Player extends AsyncTask<String,Void,Boolean>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            Boolean prepared = false;
            try {
                mediaPlayer.setDataSource(strings[0]);
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        mediaPlayer.pause();
                        length = 0;
                    }
                });
                mediaPlayer.prepare();
                //Log.d("VoyagePlay:","Prepared");
                prepared = true;

            } catch (IOException e) {
                Log.d("VoyagePlay:",e.getMessage());
                prepared = false;
            }
            return prepared;
        }


        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            loaded = true;
            mediaPlayer.start();
            sendLoadingComplete(getApplicationContext());
            //Log.d("VoyagePlay:","Async Loading stop");
        }
    }

    */

    public class MyBinder extends Binder{
        VoyagePlayMediaService getService(){
            return VoyagePlayMediaService.this;
        }
    }

    public void sendProgressInfo(Context context,int progress){
        Intent progressInten = new Intent("ProgressBroadcast");
        progressInten.putExtra("Progress",progress);
        LocalBroadcastManager.getInstance(context).sendBroadcast(progressInten);
    }

    public void sendBufferInfo(Context context,int bufProg){
        Intent bufferProgIntent = new Intent("BufferBroadcast");
        bufferProgIntent.putExtra("Buffer",bufProg);
        LocalBroadcastManager.getInstance(context).sendBroadcast(bufferProgIntent);
    }

    public void sendLoadingComplete(Context context){
        Intent loadingIntent = new Intent("LoadingEndBroadcast");
        loadingIntent.putExtra("Duration",mediaPlayer.getDuration());
        LocalBroadcastManager.getInstance(context).sendBroadcast(loadingIntent);
    }

}
