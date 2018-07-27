package com.coreman.musicmvp.ui.songs;

import android.content.Context;

import com.coreman.musicmvp.models.Song;

import java.util.List;

public interface SongsContract {
    interface View {
        void showAllSongs(List<Song> songs);
        void showError(String e);
    }

    interface Presenter {
        void loadSongs(Context context);
    }

}
