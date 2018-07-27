package com.coreman.musicmvp.utils;

import android.content.ContentUris;
import android.net.Uri;

public class ActionUtils {

    public static Uri getAlbumArtUri(long albumId) {
        return ContentUris.withAppendedId(Uri.parse("content://media/external/audio/albumart"), albumId);
    }
}
