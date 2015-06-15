package com.althink.android.ossw.plugins.musicplayer;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.util.Log;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by krzysiek on 10/06/15.
 */
public class PluginContentProvider extends ContentProvider {

    private final static String TAG = PluginContentProvider.class.getSimpleName();

    static final String PROVIDER_NAME = "com.althink.android.ossw.plugins.musicplayer";
    static final String PROVIDER_API_FUNCTIONS = "api/functions";
    static final String PROVIDER_API_PROPERTIES = "api/properties";
    static final String PROVIDER_PROPERTIES = "properties";

    static final int API_PROPERTIES = 1;
    static final int API_FUNCTIONS = 2;
    static final int PROPERTIES = 3;

    private Map<String, Object> values = new HashMap<>();

    static final UriMatcher uriMatcher;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(PROVIDER_NAME, PROVIDER_API_PROPERTIES, API_PROPERTIES);
        uriMatcher.addURI(PROVIDER_NAME, PROVIDER_API_FUNCTIONS, API_FUNCTIONS);
        uriMatcher.addURI(PROVIDER_NAME, PROVIDER_PROPERTIES, PROPERTIES);
    }


    @Override
    public boolean onCreate() {
        Log.i(TAG, "Process: " + android.os.Process.myUid());
        return true;
    }

    static final String PROPERTY_COLUMN_ID = "_id";
    static final String PROPERTY_COLUMN_NAME = "name";
    static final String PROPERTY_COLUMN_DESCRIPTION = "description";

    private static final String[] PROPERTY_COLUMNS = new String[]{
            PROPERTY_COLUMN_ID,
            PROPERTY_COLUMN_NAME,
            PROPERTY_COLUMN_DESCRIPTION
    };

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int match = uriMatcher.match(uri);
        switch (match) {
            case PROPERTIES:
                MatrixCursor cursor = new MatrixCursor(PROPERTY_COLUMNS);
                addPropertyRow(cursor, PluginProperty.TRACK, getString(R.string.property_track));
                addPropertyRow(cursor, PluginProperty.ALBUM, getString(R.string.property_album));
                addPropertyRow(cursor, PluginProperty.ARTIST, getString(R.string.property_artist));
                addPropertyRow(cursor, PluginProperty.PLAYING, getString(R.string.property_playing));
                return cursor;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    private void addPropertyRow(MatrixCursor cursor, PluginProperty property, String description) {
        cursor.newRow().add(property.getId()).add(property.getName()).add(description);
    }

    private String getString(int stringId) {
        return getContext().getResources().getString(stringId);
    }

    @Override
    public String getType(Uri uri) {
        int match = uriMatcher.match(uri);
        switch (match) {
            case API_PROPERTIES:
                return "application/vnd.com.althink.android.ossw.plugin.api.properties";
            case API_FUNCTIONS:
                return "application/vnd.com.althink.android.ossw.plugin.api.functions";
            case PROPERTIES:
                return "application/vnd.com.althink.android.ossw.plugin.properties";
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int match = uriMatcher.match(uri);
        switch (match) {
            case PROPERTIES:
                for (String key : values.keySet()) {
                    this.values.put(key, values.get(key));
                }
                return 0;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }
}
