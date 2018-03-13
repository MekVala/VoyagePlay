package com.mektech.voyageplay.voyageplay;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.concurrent.TimeUnit;

public class PlayerActivity extends AppCompatActivity {

    Button btnPlayPause,btnPrevious,btnNext;
    ImageView albumArt,albumArtTop;
    TextView curTime,totTime,hTitle,hAlbum;
    SeekBar seekMusic;
    VoyagePlayMediaService mService;
    Boolean mServiceBound = false;
    ProgressDialog progressDialog;
    Intent iService;
    SongInfo song = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        getSupportActionBar().hide();

        btnPlayPause = findViewById(R.id.btnPlayPause);
        btnPrevious = findViewById(R.id.btnPrevious);
        btnNext = findViewById(R.id.btnNext);
        curTime = findViewById(R.id.txtCurDuration);
        totTime = findViewById(R.id.textTotalDuration);
        seekMusic = findViewById(R.id.seekMusicProgress);
        albumArt = findViewById(R.id.albumArt);
        hTitle = findViewById(R.id.head_title_name);
        hAlbum = findViewById(R.id.head_album_name);
        albumArtTop = findViewById(R.id.albumArtTop);

        seekMusic.getProgressDrawable().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        seekMusic.getThumb().setColorFilter(Color.RED,PorterDuff.Mode.SRC_IN);

        iService = new Intent(getApplicationContext(),VoyagePlayMediaService.class);
        bindService(iService,serviceConnection, Context.BIND_AUTO_CREATE);

        btnPlayPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mServiceBound){
                    if(!mService.isPlaying()){
                        mService.play();
                        btnPlayPause.setBackgroundResource(R.drawable.ic_pause_circle_filled_white_48px);
                    }else if(mService.isPlaying()){
                        mService.pause();
                        btnPlayPause.setBackgroundResource(R.drawable.ic_play_circle_filled_white_48px);
                    }
                }
            }
        });

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mServiceBound){
                    progressDialog.show();
                    mService.next();
                    setSongControls();
                }
            }
        });

        btnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mServiceBound){
                    progressDialog.show();
                    mService.previous();
                    setSongControls();
                }
            }
        });

        seekMusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mServiceBound){
                    if(fromUser) {
                        mService.seekTo(progress);
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            VoyagePlayMediaService.MyBinder binder = (VoyagePlayMediaService.MyBinder)service;
            mService = binder.getService();
            mServiceBound = true;
            if(mService != null){
                if(mService.isPlaying()){
                    seekMusic.setMax(mService.getDuration());
                    setTotalDurationText(mService.getDuration());
                    song = mService.getCurrentSong();
                    setSongControls();
                }else if(!mService.isPlaying()){
                    initializeProgressDialog();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBound = false;
        }
    };

    protected BroadcastReceiver progressBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int progress = intent.getIntExtra("Progress",0);
            setCurrDurationText(progress);
            seekMusic.setProgress(progress);
        }
    };

    protected BroadcastReceiver bufferBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int progress = intent.getIntExtra("Buffer",0);
            seekMusic.setSecondaryProgress(progress);
        }
    };

    protected BroadcastReceiver stopLoadingBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            progressDialog.dismiss();
            mService.createNotification();
            int totDuration = intent.getIntExtra("Duration",50000);
            seekMusic.setMax(totDuration);
            setTotalDurationText(totDuration);
            song = mService.getCurrentSong();
            setSongControls();
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        bindService(iService,serviceConnection, Context.BIND_AUTO_CREATE);
        registerBroadcast();
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(progressBroadcastReceiver);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(bufferBroadcastReceiver);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(stopLoadingBroadcastReceiver);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mServiceBound){
            unbindService(serviceConnection);
            mServiceBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(iService);
    }

    protected void registerBroadcast(){
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(progressBroadcastReceiver, new IntentFilter("ProgressBroadcast"));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(bufferBroadcastReceiver, new IntentFilter("BufferBroadcast"));
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(stopLoadingBroadcastReceiver, new IntentFilter("LoadingEndBroadcast"));
    }

    public void makeToast(String msg){
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
    }

    public void setTotalDurationText(int durationText){
        totTime.setText(String.format("%d:%d",
                TimeUnit.MILLISECONDS.toMinutes((long) durationText),
                TimeUnit.MILLISECONDS.toSeconds((long) durationText) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                toMinutes((long) durationText))));
    }

    public void setCurrDurationText(int durationText){
        curTime.setText(String.format("%d:%d",
                TimeUnit.MILLISECONDS.toMinutes((long) durationText),
                TimeUnit.MILLISECONDS.toSeconds((long) durationText) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                toMinutes((long) durationText))));
    }

    public void initializeProgressDialog(){
        progressDialog = new ProgressDialog(this,R.style.MyTheme);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setCancelable(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }

    public void setSongControls(){
        if(song!=null){
            hTitle.setText(song.getSong_title());
            hAlbum.setText(song.getSong_album());
            Picasso.with(getApplicationContext()).load(song.getAlbum_art_url()).into(albumArtTop);
            Picasso.with(getApplicationContext()).load(song.getAlbum_art_url()).into(albumArt);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent goMain = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(goMain);
    }
}
