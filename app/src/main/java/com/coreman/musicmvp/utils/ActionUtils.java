package com.coreman.musicmvp.utils;

import android.content.ContentUris;
import android.content.Context;
import android.net.Uri;

import com.coreman.musicmvp.R;

import java.util.concurrent.TimeUnit;

public class ActionUtils {

    public static Uri getAlbumArtUri(long albumId) {
        return ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumId);
    }

    public static String makeShortTimeString(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        seconds = seconds % 60;

        if (seconds < 10) {
            return String.valueOf(minutes) + ":0" + String.valueOf(seconds);
        } else {
            return String.valueOf(minutes) + ":" + String.valueOf(seconds);
        }
    }
}
