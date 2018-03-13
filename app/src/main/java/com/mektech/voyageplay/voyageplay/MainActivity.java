package com.mektech.voyageplay.voyageplay;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Parcel;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity{

    VoyagePlayMediaService mService;
    Boolean mServiceBound = false;
    Intent iService;
    ArrayList songs;

    private final String song_titles[] = {
            "Without You",
            "Let Me Love You",
            "Vhalam Aavo Ne",
            "Musafir"
    };

    private final String albums[] = {
            "Without You",
            "Let Me Love You",
            "Love Ni Bhavai",
            "Jagga Jasoos"
    };

    private final String imgs[] = {
        "https://stream.demodrop.com/media/a9c64b8771ec7f61589b6508e36d124b.jpg",
        "https://s3.amazonaws.com/images.sheetmusicdirect.com/Product/smd_h_1186929h0sn4HHdl5/large.jpg",
        "https://c-sf.smule.com/sf/s79/arr/1d/6a/b817c108-c8d1-4a63-b0eb-e31a67427e1a_256.jpg",
        "http://www.bollywoodlife.com/wp-content/uploads/2017/02/Jagga-Jasoos-poster-2.jpg"
       };

    private final String url[] = {
            "https://archive.org/download/WithoutYou_201801/WithoutYou.mp3",
            "https://archive.org/download/WithoutYou_201801/LetMeLoveYou.mp3",
            "https://archive.org/download/WithoutYou_201801/VhalamAavoNe.mp3",
            "https://archive.org/download/WithoutYou_201801/Musafir.mp3"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("Music List");
        iService = new Intent(getApplicationContext(),VoyagePlayMediaService.class);
        startService(iService);
        bindService(iService,serviceConnection, Context.BIND_AUTO_CREATE);

        initView();
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            VoyagePlayMediaService.MyBinder binder = (VoyagePlayMediaService.MyBinder)service;
            mService = binder.getService();
            mServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mServiceBound = false;
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        bindService(iService,serviceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        /*if(mServiceBound){
            unbindService(serviceConnection);
            mServiceBound = false;
        }*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(iService);
    }

    private void initView(){
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.musicList);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        songs = prepareData();
        DataAdapter adapter = new DataAdapter(getApplicationContext(),songs);
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(new RecyclerViewItemClickListener(getApplicationContext(), new RecyclerViewItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if(mServiceBound) {
                    mService.setSongQueue(songs);
                    mService.setCurrentSong(position);
                }
                Intent intent = new Intent(getApplicationContext(),PlayerActivity.class);
                startActivity(intent);
            }
        }));
    }

    private ArrayList prepareData(){
        ArrayList song = new ArrayList();
        for(int i=0;i < song_titles.length;i++){
            SongInfo songInfo = new SongInfo();
            songInfo.setSong_title(song_titles[i]);
            songInfo.setSong_album(albums[i]);
            songInfo.setAlbum_art_url(imgs[i]);
            songInfo.setUrl(url[i]);
            song.add(songInfo);
        }
        return song;
    }

    public void makeToast(String msg){
        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_SHORT).show();
    }
}
