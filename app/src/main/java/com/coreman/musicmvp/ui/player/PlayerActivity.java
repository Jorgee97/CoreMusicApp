package com.coreman.musicmvp.ui.player;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.coreman.musicmvp.R;
import com.coreman.musicmvp.models.Song;
import com.coreman.musicmvp.services.MusicPlayService;
import com.coreman.musicmvp.ui.main.MainActivity;
import com.coreman.musicmvp.utils.ActionUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlayerActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "PlayerActivity";

    @BindView(R.id.playing_now_album_art)
    ImageView mAlbumArt;
    @BindView(R.id.playing_now_title)
    TextView mTitle;
    @BindView(R.id.playing_now_artist)
    TextView mArtist;
    @BindView(R.id.playing_now_play_pause)
    ImageButton mPlayPause;
    @BindView(R.id.playing_now_next)
    ImageButton mNext;
    @BindView(R.id.playing_now_prev)
    ImageButton mPrev;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;
    @BindView(R.id.playing_now_song_duration)
    TextView mSongDuration;
    @BindView(R.id.actualState)
    TextView mActualStateSong;

    public boolean isPaused;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra(MusicPlayService.CURRENT_SONG_INFO)) {
                Song currentPlayingSong = intent.getParcelableExtra(MusicPlayService.CURRENT_SONG_INFO);

                mProgressBar.setMax((int) currentPlayingSong.getDuration());
                mTitle.setText(currentPlayingSong.getTitle());
                mArtist.setText(currentPlayingSong.getArtist());
                Glide.with(getApplicationContext())
                        .load(ActionUtils.getAlbumArtUri(currentPlayingSong.getAlbumId()).toString())
                        .apply(new RequestOptions().placeholder(R.drawable.default_album_image_x64))
                        .into(mAlbumArt);

            } else if (intent.hasExtra(MusicPlayService.CURRENT_STATE)) {
                mProgressBar.setProgress(Integer.parseInt(intent.getStringExtra(MusicPlayService.CURRENT_STATE)));
                mActualStateSong.setText(
                        ActionUtils.makeShortTimeString(Long.parseLong(intent.getStringExtra(MusicPlayService.CURRENT_STATE))));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        ButterKnife.bind(this);

        Song currentPlaying = getIntent().getParcelableExtra(MusicPlayService.CURRENT_SONG_INFO);
        isPaused = getIntent().getBooleanExtra("ISPAUSED", false);
        initViews(currentPlaying);

        IntentFilter filter = new IntentFilter();
        filter.addAction(MusicPlayService.CURRENT_SONG_INFO);
        filter.addAction(MusicPlayService.CURRENT_STATE);

        registerReceiver(mReceiver, filter);
    }

    private void initViews(Song currentPlaying) {
        mTitle.setText(currentPlaying.getTitle());
        mTitle.startAnimation((Animation) AnimationUtils.loadAnimation(this, R.anim.translate));

        mArtist.setText(currentPlaying.getArtist());
        mProgressBar.setMax((int) currentPlaying.getDuration());
        mSongDuration.setText(ActionUtils.makeShortTimeString(currentPlaying.getDuration()));
        Glide.with(getApplicationContext())
                .load(ActionUtils.getAlbumArtUri(currentPlaying.getAlbumId()).toString())
                .apply(new RequestOptions().placeholder(R.drawable.default_album_image_x64))
                .into(mAlbumArt);

        if (isPaused) {
            mPlayPause.setBackground(ContextCompat.getDrawable(this ,R.drawable.ic_baseline_play_circle_outline_24px));
        } else {
            mPlayPause.setBackground(ContextCompat.getDrawable(this ,R.drawable.ic_baseline_pause_circle_outline_24px));
        }

        mPlayPause.setOnClickListener(this);
        mNext.setOnClickListener(this);
        mPrev.setOnClickListener(this);

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent sendCommand = new Intent(MusicPlayService.DURATION_SONG);
                sendBroadcast(sendCommand);
                handler.postDelayed(this, 1000);
            }
        }, 1000);

        findViewById(R.id.appBarLayout).bringToFront();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.playing_now_play_pause:
                if (!isPaused) {
                    mPlayPause.setBackground(ContextCompat.getDrawable(this ,R.drawable.ic_baseline_play_circle_outline_24px));
                    Intent sendCommand = new Intent(MusicPlayService.PAUSE_SONG);
                    sendBroadcast(sendCommand);
                    isPaused = true;
                } else {
                    mPlayPause.setBackground(ContextCompat.getDrawable(this ,R.drawable.ic_baseline_pause_circle_outline_24px));
                    Intent sendCommand = new Intent(MusicPlayService.START_SONG);
                    sendBroadcast(sendCommand);
                    isPaused = false;
                }
                break;
            case R.id.playing_now_next:
                Intent sendCommand = new Intent(MusicPlayService.NEXT_SONG);
                sendBroadcast(sendCommand);
                break;
            case R.id.playing_now_prev:
                Intent sendCommand2 = new Intent(MusicPlayService.PREV_SONG);
                sendBroadcast(sendCommand2);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}
