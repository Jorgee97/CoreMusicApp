package com.coreman.musicmvp.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Album implements Parcelable {

    private long mId;
    private long mAlbumId;
    private long mArtistId;
    private String mAlbum;
    private String Artist;
    private int mNumSongs;

    public Album(long id, long albumId, long artistId, String album, String artist, int numSongs) {
        mId = id;
        mAlbumId = albumId;
        mArtistId = artistId;
        mAlbum = album;
        Artist = artist;
        mNumSongs = numSongs;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }

    public long getAlbumId() {
        return mAlbumId;
    }

    public void setAlbumId(long albumId) {
        mAlbumId = albumId;
    }

    public long getArtistId() {
        return mArtistId;
    }

    public void setArtistId(long artistId) {
        mArtistId = artistId;
    }

    public String getAlbum() {
        return mAlbum;
    }

    public void setAlbum(String album) {
        mAlbum = album;
    }

    public String getArtist() {
        return Artist;
    }

    public void setArtist(String artist) {
        Artist = artist;
    }

    public int getNumSongs() {
        return mNumSongs;
    }

    public void setNumSongs(int numSongs) {
        mNumSongs = numSongs;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.mId);
        dest.writeLong(this.mAlbumId);
        dest.writeLong(this.mArtistId);
        dest.writeString(this.mAlbum);
        dest.writeString(this.Artist);
        dest.writeInt(this.mNumSongs);
    }

    protected Album(Parcel in) {
        this.mId = in.readLong();
        this.mAlbumId = in.readLong();
        this.mArtistId = in.readLong();
        this.mAlbum = in.readString();
        this.Artist = in.readString();
        this.mNumSongs = in.readInt();
    }

    public static final Parcelable.Creator<Album> CREATOR = new Parcelable.Creator<Album>() {
        @Override
        public Album createFromParcel(Parcel source) {
            return new Album(source);
        }

        @Override
        public Album[] newArray(int size) {
            return new Album[size];
        }
    };
}
