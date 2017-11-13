package com.example.lederui.developmenttest.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

/**
 * Created by Administrator on 2017/10/26 0026.
 */

public class GetVideoUri {

    public static Uri getMediaUriFromPath(Context context, String path){
        Uri mediaUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = context.getContentResolver().query(mediaUri,
                null,
                MediaStore.Video.Media.DISPLAY_NAME + "= ?",
                new String[]{path.substring(path.lastIndexOf("/")+1)},
                null);
        Uri uri = null;
        if (cursor.moveToFirst()){
            uri = ContentUris.withAppendedId(mediaUri,
                    cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media._ID)));
        }
        cursor.close();
        return uri;
    }

}
