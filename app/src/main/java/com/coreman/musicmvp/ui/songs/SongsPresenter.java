package com.coreman.musicmvp.ui.songs;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.coreman.musicmvp.models.Song;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

import static android.support.constraint.Constraints.TAG;

public class SongsPresenter implements SongsContract.Presenter {
    private SongsContract.View songView;
    private Uri external = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

    public SongsPresenter(SongsContract.View songView) {
        this.songView = songView;
    }

    @Override
    public void loadSongs(Context context) {
        observerSongs(context).subscribeWith(getObserver());
    }

    private Observable<List<Song>> observerSongs(Context context) {
        return Observable.fromCallable(() -> {
            List<Song> songs = new ArrayList<>();
            String[] PROJECTION_BUCKET = {MediaStore.Audio.Media._ID, MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.DURATION,
                    MediaStore.Audio.Media.TRACK, MediaStore.Audio.Media.ARTIST_ID, MediaStore.Audio.Media.ALBUM_ID};
            String SELECTION = "is_music=1 AND title != ''";

            Cursor cursor = context.getContentResolver().query(external, PROJECTION_BUCKET, SELECTION, null, null);

            if (cursor.moveToFirst()) {
                do {
                    long id = cursor.getLong(0);
                    String title = cursor.getString(1);
                    String artist = cursor.getString(2);
                    String album = cursor.getString(3);
                    int duration = cursor.getInt(4);
                    int trackNumber = cursor.getInt(5);
                    long artistId = cursor.getInt(6);
                    long albumId = cursor.getLong(7);

                    songs.add(new Song(id, artistId, albumId, title, artist, album, duration, trackNumber));
                } while (cursor.moveToNext());
            }

            cursor.close();
            return songs;
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread());
    }

    private DisposableObserver<List<Song>> getObserver() {
        return new DisposableObserver<List<Song>>() {
            @Override
            public void onNext(List<Song> songs) {
                songView.showAllSongs(songs);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: ", e);
                e.printStackTrace();

                songView.showError(e.toString());
            }
            @Override
            public void onComplete() {
            }
        };
    }

}
