package com.althink.android.ossw.plugins.musicplayer;

import com.althink.android.ossw.plugins.api.PluginPropertyType;

/**
 * Created by krzysiek on 14/06/15.
 */
public enum MusicPlayerPluginProperty {
    TRACK(1, "track", PluginPropertyType.STRING),
    ALBUM(2, "album", PluginPropertyType.STRING),
    ARTIST(3, "artist", PluginPropertyType.STRING),
    STATE(4, "state", PluginPropertyType.ENUM);

    private int id;
    private String name;
    private PluginPropertyType type;

    private MusicPlayerPluginProperty(int id, String name, PluginPropertyType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    public static MusicPlayerPluginProperty resolveByName(String propertyName) {
        for (MusicPlayerPluginProperty property : MusicPlayerPluginProperty.values()) {
            if (property.getName().equals(propertyName)) {
                return property;
            }
        }
        return null;
    }

    public PluginPropertyType getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }
}
