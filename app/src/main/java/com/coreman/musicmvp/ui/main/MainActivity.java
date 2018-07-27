package com.coreman.musicmvp.ui.main;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.coreman.musicmvp.R;
import com.coreman.musicmvp.models.Song;
import com.coreman.musicmvp.services.MusicPlayService;
import com.coreman.musicmvp.ui.player.PlayerActivity;
import com.coreman.musicmvp.utils.ActionUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements  View.OnClickListener{
    private static final String TAG = "MainActivity";
    public static final String CURRENT_SONG_INFO = "currentSongInfo";
    public static final String CURRENT_STATE = "currentState";

    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;
    @BindView(R.id.title_song_playing)
    TextView mTitle;
    @BindView(R.id.author_song_playing)
    TextView mArtist;
    @BindView(R.id.album_song_playing)
    ImageView mAlbumArt;
    @BindView(R.id.play_pause_button)
    ImageButton mPlayPause;
    @BindView(R.id.now_playing_mini)
    CardView mCardView;
    @BindView(R.id.progress_bar_song_playing)
    ProgressBar mProgressBar;

    private Song currentSong;
    private boolean isPaused = false;

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.hasExtra(MusicPlayService.CURRENT_SONG_INFO)) {
                Song currentPlayingSong = intent.getParcelableExtra(MusicPlayService.CURRENT_SONG_INFO);
                currentSong = currentPlayingSong;

                mCardView.setVisibility(View.VISIBLE);

                Log.i(TAG, "onReceive: " + currentPlayingSong.getDuration());
                mProgressBar.setMax((int) currentPlayingSong.getDuration());
                mTitle.setText(currentPlayingSong.getTitle());
                mArtist.setText(currentPlayingSong.getArtist());
                Glide.with(getApplicationContext())
                        .load(ActionUtils.getAlbumArtUri(currentPlayingSong.getAlbumId()).toString())
                        .apply(new RequestOptions().placeholder(R.drawable.default_album_image_x64))
                        .into(mAlbumArt);

                // TODO: Find a better approach for showing up the process bar, this can cause some sort of
                // unexpected behavior

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent sendCommand = new Intent(MusicPlayService.DURATION_SONG);
                        sendBroadcast(sendCommand);
                        handler.postDelayed(this, 1000);
                    }
                }, 1000);
            }
            else if (intent.hasExtra(MusicPlayService.CURRENT_STATE)) {
                mProgressBar.setProgress(Integer.parseInt(intent.getStringExtra(MusicPlayService.CURRENT_STATE)));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this,
                getSupportFragmentManager());
        mViewPager.setAdapter(viewPagerAdapter);

        mTabLayout.setupWithViewPager(mViewPager);

        IntentFilter filter = new IntentFilter();
        filter.addAction(MusicPlayService.CURRENT_SONG_INFO);
        filter.addAction(MusicPlayService.CURRENT_STATE);

        registerReceiver(mReceiver, filter);

        mPlayPause.setOnClickListener(this);
        mCardView.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.play_pause_button:
                if (!isPaused) {
                    mPlayPause.setBackground(ContextCompat.getDrawable(this ,R.drawable.ic_baseline_play_circle_filled_24px));
                    Intent sendCommand = new Intent(MusicPlayService.PAUSE_SONG);
                    sendBroadcast(sendCommand);
                    isPaused = true;
                } else {
                    mPlayPause.setBackground(ContextCompat.getDrawable(this ,R.drawable.ic_baseline_pause_circle_filled_24px));
                    Intent sendCommand = new Intent(MusicPlayService.START_SONG);
                    sendBroadcast(sendCommand);
                    isPaused = false;
                }
                break;
            case R.id.now_playing_mini:
                Intent intent = new Intent(MainActivity.this, PlayerActivity.class);
                intent.putExtra(MusicPlayService.CURRENT_SONG_INFO, currentSong);
                intent.putExtra("ISPAUSED", isPaused);
                startActivity(intent);
                break;
        }
    }
}
