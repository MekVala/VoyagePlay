package com.mektech.voyageplay.voyageplay;

/**
 * Created by mek on 1/21/18.
 */

public class SongInfo{
    private String song_title;
    private String song_album;
    //private String song_artist;
    private String url;
    private String album_art_url;

    public void setSong_title(String song_title) {
        this.song_title = song_title;
    }

    public void setSong_album(String song_album) {
        this.song_album = song_album;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    /*
    public void setSong_artist(String song_artist) {
        this.song_artist = song_artist;
    }*/

    public void setAlbum_art_url(String album_art_url) {
        this.album_art_url = album_art_url;
    }

    public String getSong_title() {
        return song_title;
    }

    public String getAlbum_art_url() {
        return album_art_url;
    }

    public String getSong_album() {
        return song_album;
    }
    /*
    public String getSong_artist() {
        return song_artist;
    }*/

    public String getUrl() {
        return url;
    }
}
