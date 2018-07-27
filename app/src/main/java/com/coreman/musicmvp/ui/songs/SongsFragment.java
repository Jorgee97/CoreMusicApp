package com.coreman.musicmvp.ui.songs;


import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.coreman.musicmvp.R;
import com.coreman.musicmvp.models.Song;
import com.coreman.musicmvp.services.MusicPlayService;
import com.coreman.musicmvp.utils.OnSongClick;
import com.coreman.musicmvp.utils.PermissionUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class SongsFragment extends Fragment implements SongsContract.View, OnSongClick {

    private static final String TAG = "SongsFragment";
    private final int EXTERNAL_STORAGE_PERMISSIONS = 12;
    private Context mContext;

    private MusicPlayService mMusicPlayService;
    private boolean musicBound = false;
    private Intent playIntent;

    private RecyclerView mRecyclerSongs;
    private SongsAdapter mSongsAdapter;
    private SongsPresenter mSongsPresenter;

    private List<Song> mSongList = new ArrayList<>();

    public SongsFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            MusicPlayService.MusicPlayBinder binder = (MusicPlayService.MusicPlayBinder) service;
            mMusicPlayService = binder.getService();
            mMusicPlayService.setSongList(mSongList);
            musicBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            musicBound = false;
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        if (playIntent == null) {
            playIntent = new Intent(mContext, MusicPlayService.class);
            mContext.bindService(playIntent, mServiceConnection, Context.BIND_AUTO_CREATE);
            mContext.startService(playIntent);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_songs, container, false);

        mContext = getContext();

        mRecyclerSongs = rootView.findViewById(R.id.recycler_songs_view);
        mRecyclerSongs.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerSongs.setHasFixedSize(true);

        mSongsPresenter = new SongsPresenter(this);

        if (PermissionUtils.isStoragePermissionsGranted(mContext))
            mSongsPresenter.loadSongs(mContext);
        else {
            askForPermissions();
            if (PermissionUtils.isStoragePermissionsGranted(mContext)) {
                mSongsPresenter.loadSongs(mContext);
            }
        }

        return rootView;
    }

    private void askForPermissions() {
        if (!PermissionUtils.isStoragePermissionsGranted(mContext)) {
            PermissionUtils.requestPermissions(this, EXTERNAL_STORAGE_PERMISSIONS,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    @Override
    public void showAllSongs(List<Song> songs) {
        mSongList = songs;
        mSongsAdapter = new SongsAdapter(mContext, songs, this);
        mRecyclerSongs.setAdapter(mSongsAdapter);
    }

    @Override
    public void showError(String e) {
        Toast.makeText(mContext, e, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSongClicked(int position) {
        mMusicPlayService.setSongPosition(position);
        mMusicPlayService.playSong();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mContext.stopService(playIntent);
        mMusicPlayService = null;
    }
}
