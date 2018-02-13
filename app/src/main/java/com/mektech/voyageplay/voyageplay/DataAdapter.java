package com.mektech.voyageplay.voyageplay;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by mek on 1/21/18.
 */

public class DataAdapter extends RecyclerView.Adapter<DataAdapter.ViewHolder> {
    private ArrayList<SongInfo> songInfos;
    private Context context;


    public DataAdapter(Context context,ArrayList<SongInfo> songInfos){
        this.context = context;
        this.songInfos = songInfos;
    }

    @Override
    public DataAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DataAdapter.ViewHolder holder, int position) {
        holder.s_title.setText(songInfos.get(position).getSong_title());
        holder.s_album.setText(songInfos.get(position).getSong_album());
        Picasso.with(context).load(songInfos.get(position).getAlbum_art_url()).resize(60,60).into(holder.img_album);
    }

    @Override
    public int getItemCount() {
        return songInfos.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView s_title,s_album;
        ImageView img_album;
        public ViewHolder(View view) {
            super(view);
            s_title = (TextView)view.findViewById(R.id.txtSongTitle);
            s_album = (TextView)view.findViewById(R.id.txtAlbumName);
            img_album = (ImageView)view.findViewById(R.id.album_art);
        }

    }
}
