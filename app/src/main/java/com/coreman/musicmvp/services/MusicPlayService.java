package com.coreman.musicmvp.services;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;

import com.coreman.musicmvp.models.Song;

import java.util.List;

public class MusicPlayService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
    MediaPlayer.OnCompletionListener {

    private static final String TAG = "MusicPlayService";
    public static final String CURRENT_SONG_INFO = "currentSongInfo";
    public static final String CURRENT_STATE = "currentState";

    public static final String PAUSE_SONG = "pauseSong";
    public static final String START_SONG = "startSong";
    public static final String DURATION_SONG = "durationSong";
    public static final String NEXT_SONG = "nextSong";
    public static final String PREV_SONG = "prevSong";

    private MediaPlayer mMediaPlayer;
    private List<Song> mSongList;
    private int mSongPosition;
    private boolean mIsPlaying = false;

    private final IBinder musicBind = new MusicPlayBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    BroadcastReceiver eventBroadcast = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            handleCommandIntent(intent);
        }
    };

    @Override
    public boolean onUnbind(Intent intent) {
        mMediaPlayer.stop();
        mMediaPlayer.release();
        return false;
    }

    public void onCreate() {
        super.onCreate();

        mSongPosition = 0;
        mMediaPlayer = new MediaPlayer();

        initMusicPlayer();
        IntentFilter filter = new IntentFilter();
        filter.addAction(PAUSE_SONG);
        filter.addAction(START_SONG);
        filter.addAction(DURATION_SONG);
        filter.addAction(NEXT_SONG);
        filter.addAction(PREV_SONG);

        registerReceiver(eventBroadcast, filter);
    }

    public void initMusicPlayer() {
        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build();
            mMediaPlayer.setAudioAttributes(audioAttributes);
        } else {
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }

        mMediaPlayer.setOnErrorListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnPreparedListener(this);
    }

    public void playSong() {
        mMediaPlayer.reset();
        Song song = mSongList.get(mSongPosition);
        long currentSong = song.getId();
        Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currentSong);
        try {
            mMediaPlayer.setDataSource(getApplicationContext(), trackUri);
        } catch (Exception e) {
            Log.e(TAG, "playSong: ", e);
        }
        mMediaPlayer.prepareAsync();
        notifySongChanged(song);
    }

    public void pauseSong() {
        mMediaPlayer.pause();
        setIsPlaying(false);
    }

    public void start() {
        mMediaPlayer.start();
        setIsPlaying(true);
    }

    public void playNext() {
        mMediaPlayer.reset();
        int nextPosition = mSongPosition + 1;
        if (nextPosition >= mSongList.size()) {
            nextPosition = 0;
            setSongPosition(nextPosition);
        } else {
            setSongPosition(nextPosition);
        }

        Song song = mSongList.get(mSongPosition);
        Log.i(TAG, "playNext: " + nextPosition + " - " + song.getTitle() + " - " + mSongList.size());
        long currentSong = song.getId();
        Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currentSong);
        try {
            mMediaPlayer.setDataSource(getApplicationContext(), trackUri);
        } catch (Exception e) {
            Log.e(TAG, "playSong: ", e);
        }
        mMediaPlayer.prepareAsync();
        notifySongChanged(song);
        setIsPlaying(true);
    }

    public void playPrev() {
        mMediaPlayer.reset();
        int prevPosition = mSongPosition - 1;
        if (prevPosition < 0) {
            setSongPosition(mSongList.size() - 1);
        } else {
            setSongPosition(prevPosition);
        }

        Song song = mSongList.get(mSongPosition);
        long currentSong = song.getId();
        Uri trackUri = ContentUris.withAppendedId(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, currentSong);
        try {
            mMediaPlayer.setDataSource(getApplicationContext(), trackUri);
        } catch (Exception e) {
            Log.e(TAG, "playSong: ", e);
        }
        mMediaPlayer.prepareAsync();
        notifySongChanged(song);
        setIsPlaying(true);
    }

    public void setIsPlaying(boolean value) {
        mIsPlaying = value;
    }

    public boolean isPlaying() {
        return mIsPlaying;
    }

    public void notifyCurrentPosition() {
        Intent intent = new Intent();
        intent.setAction(CURRENT_STATE);
        intent.putExtra(CURRENT_STATE, String.valueOf(mMediaPlayer.getCurrentPosition()));
        sendBroadcast(intent);
    }

    public void notifySongChanged(Song song) {
        Intent intent = new Intent();
        intent.setAction(CURRENT_SONG_INFO);
        intent.putExtra(CURRENT_SONG_INFO, song);
        sendBroadcast(intent);
    }

    public void setSongList(List<Song> songList) {
        mSongList = songList;
    }

    public void setSongPosition(int songPosition) {
        mSongPosition = songPosition;
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        playNext();
    }

    @Override
    public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        mediaPlayer.start();
    }

    private void handleCommandIntent(Intent intent) {
        String action = intent.getAction();

        if (PAUSE_SONG.equals(action)) {
            pauseSong();
        } else if (START_SONG.equals(action)) {
            start();
        } else if (DURATION_SONG.equals(action)) {
            notifyCurrentPosition();
        } else if (NEXT_SONG.equals(action)) {
            playNext();
        } else if (PREV_SONG.equals(action)) {
            playPrev();
        }
    }

    public class MusicPlayBinder extends Binder {
        public MusicPlayService getService() {
            return MusicPlayService.this;
        }
    }
}
