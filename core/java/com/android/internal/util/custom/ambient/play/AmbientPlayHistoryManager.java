package com.android.internal.util.custom.ambient.play;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.UserHandle;

import java.util.ArrayList;
import java.util.List;

public class AmbientPlayHistoryManager {

    private static final String AUTHORITY = "org.pixelexperience.ambient.play.history.provider";
    private static final Uri CONTENT_URI =
            Uri.parse("content://" + AUTHORITY + "/songs");
    private static final String KEY_ID = "_id";
    private static final String KEY_TIMESTAMP = "ts";
    private static final String KEY_ARTIST = "artist";
    private static final String KEY_SONG = "song";
    private static final String[] PROJECTION = {KEY_ID, KEY_TIMESTAMP, KEY_SONG, KEY_ARTIST};
    private static String ACTION_SONG_MATCH = "com.android.internal.util.custom.ambient.play.AMBIENT_PLAY_SONG_MATCH";
    public static Intent INTENT_SONG_MATCH = new Intent(ACTION_SONG_MATCH);

    public static void addSong(String song, String artist, Context context) {
        ContentValues values = new ContentValues();
        values.put(KEY_TIMESTAMP, System.currentTimeMillis());
        values.put(KEY_SONG, song);
        values.put(KEY_ARTIST, artist);
        context.getContentResolver().insert(CONTENT_URI, values);
    }

    public static List<AmbientPlayHistoryEntry> getSongs(Context context) {
        List<AmbientPlayHistoryEntry> result = new ArrayList<>();
        try (Cursor cursor = context.getContentResolver().query(CONTENT_URI, PROJECTION, null, null,
                null)) {
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    result.add(new AmbientPlayHistoryEntry(cursor.getInt(0), cursor.getLong(1), cursor.getString(2), cursor.getString(3)));
                }
            }
        }
        return result;
    }

    public static void deleteSong(int id, Context context) {
        context.getContentResolver().delete(Uri.parse(CONTENT_URI + "/" + id), null, null);
    }

    public static void deleteAll(Context context) {
        context.getContentResolver().delete(CONTENT_URI, null, null);
    }

    public static void sendMatchBroadcast(Context context) {
        context.sendBroadcastAsUser(INTENT_SONG_MATCH, UserHandle.CURRENT);
    }
}
