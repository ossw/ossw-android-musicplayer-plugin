package com.althink.android.ossw.plugins.musicplayer;

/**
 * Created by krzysiek on 14/06/15.
 */
public enum MusicPlayerPluginFunction {

    PLAY_PAUSE(1, "playPause"),
    PLAY(2, "play"),
    PAUSE(3, "pause"),
    STOP(4, "stop"),
    NEXT_TRACK(5, "nextTrack"),
    PREV_TRACK(6, "prevTrack");

    private final int id;
    private final String name;

    private MusicPlayerPluginFunction(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static MusicPlayerPluginFunction resolveById(int functionId) {
        for (MusicPlayerPluginFunction function : MusicPlayerPluginFunction.values()) {
            if (function.getId() == functionId) {
                return function;
            }
        }
        return null;
    }
}
