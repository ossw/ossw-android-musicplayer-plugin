package com.althink.android.ossw.plugins.musicplayer;

/**
 * Created by krzysiek on 14/06/15.
 */
public enum PluginProperty {
    TRACK(1, "track"), ALBUM(2, "album"), ARTIST(3, "artist"), PLAYING(4, "playing");

    private int id;
    private String name;

    private PluginProperty(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}
