package com.coreman.musicmvp.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Song implements Parcelable {

    private long mId;
    private long mArtistId;
    private long mAlbumId;
    private String mTitle;
    private String mArtist;
    private String album;
    private int duration;
    private long track;

    public Song(long id, long artistId, long albumId, String title, String artist, String album, int duration, long track) {
        mId = id;
        mArtistId = artistId;
        mAlbumId = albumId;
        mTitle = title;
        mArtist = artist;
        this.album = album;
        this.duration = duration;
        this.track = track;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public long getArtistId() {
        return mArtistId;
    }

    public void setArtistId(long artistId) {
        mArtistId = artistId;
    }

    public long getAlbumId() {
        return mAlbumId;
    }

    public void setAlbumId(long albumId) {
        mAlbumId = albumId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getArtist() {
        return mArtist;
    }

    public void setArtist(String artist) {
        mArtist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public long getTrack() {
        return track;
    }

    public void setTrack(long track) {
        this.track = track;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mId);
        dest.writeLong(this.mArtistId);
        dest.writeLong(this.mAlbumId);
        dest.writeString(this.mTitle);
        dest.writeString(this.mArtist);
        dest.writeString(this.album);
        dest.writeInt(this.duration);
        dest.writeLong(this.track);
    }

    protected Song(Parcel in) {
        this.mId = in.readLong();
        this.mArtistId = in.readLong();
        this.mAlbumId = in.readLong();
        this.mTitle = in.readString();
        this.mArtist = in.readString();
        this.album = in.readString();
        this.duration = in.readInt();
        this.track = in.readLong();
    }

    public static final Parcelable.Creator<Song> CREATOR = new Parcelable.Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel source) {
            return new Song(source);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };
}
