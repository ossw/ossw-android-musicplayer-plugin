package com.althink.android.ossw.plugins.musicplayer;

/**
 * Created by krzysiek on 14/06/15.
 */
public enum MusicPlayerPluginProperty {
    TRACK(1, "track"), ALBUM(2, "album"), ARTIST(3, "artist"), PLAYING(4, "playing");

    private int id;
    private String name;

    private MusicPlayerPluginProperty(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public static MusicPlayerPluginProperty resolveByName(String propertyName) {
        for (MusicPlayerPluginProperty property : MusicPlayerPluginProperty.values()) {
            if (property.getName().equals(propertyName)) {
                return property;
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}
