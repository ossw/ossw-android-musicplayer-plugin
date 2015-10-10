package com.althink.android.ossw.plugins.musicplayer;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;

/**
 * Created by krzysiek on 08/06/15.
 */
public class MusicPlayerPluginService extends Service {

    private final static String TAG = MusicPlayerPluginService.class.getSimpleName();

    private final Messenger mMessenger = new Messenger(new OperationHandler());

    private boolean playing = false;

    private int previousVolume;

    private SettingsContentObserver settingsContentObserver;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            //Log.i(TAG, "Intent: " + intent + ", " + intent.getExtras());
            String artist = intent.getStringExtra(MediaStore.Audio.AudioColumns.ARTIST);
            String album = intent.getStringExtra(MediaStore.Audio.AudioColumns.ALBUM);
            String track = intent.getStringExtra(MediaStore.Audio.AudioColumns.TRACK);
            playing = intent.getBooleanExtra("playing", false);

            //Log.i(TAG, "artist: " + artist + " , track: " + track + ", album: " + album + ", playing: " + playing);

            ContentValues values = new ContentValues();
            if (album != null || track != null || artist != null) {
                values.put(MusicPlayerPluginProperty.ALBUM.getName(), album);
                values.put(MusicPlayerPluginProperty.ARTIST.getName(), artist);
                values.put(MusicPlayerPluginProperty.TRACK.getName(), track);
            }
            values.put(MusicPlayerPluginProperty.STATE.getName(), playing ? 1 : 0);

            getContentResolver().update(MusicPlayerPluginContentProvider.PROPERTY_VALUES_URI, values, null, null);
        }
    };

    @Override
    public IBinder onBind(Intent intent) {

        //Log.d(TAG, "onBind");
        IntentFilter iF = new IntentFilter();

        //Google Android player
        iF.addAction("com.android.music.metachanged");
        iF.addAction("com.android.music.playstatechanged");
        iF.addAction("com.android.music.playbackcomplete");
        //iF.addAction("com.android.music.queuechanged");
        //HTC Music
        iF.addAction("com.htc.music.playstatechanged");
        iF.addAction("com.htc.music.playbackcomplete");
        iF.addAction("com.htc.music.metachanged");
        //MIUI Player
        iF.addAction("com.miui.player.playstatechanged");
        iF.addAction("com.miui.player.playbackcomplete");
        iF.addAction("com.miui.player.metachanged");
        //Real
        iF.addAction("com.real.IMP.playstatechanged");
        iF.addAction("com.real.IMP.playbackcomplete");
        iF.addAction("com.real.IMP.metachanged");
        //SEMC Music Player
        iF.addAction("com.sonyericsson.music.playbackcontrol.ACTION_TRACK_STARTED");
        iF.addAction("com.sonyericsson.music.playbackcontrol.ACTION_PAUSED");
        iF.addAction("com.sonyericsson.music.TRACK_COMPLETED");
        iF.addAction("com.sonyericsson.music.metachanged");
        //rdio
        iF.addAction("com.rdio.android.metachanged");
        iF.addAction("com.rdio.android.playstatechanged");
        //Samsung Music Player
        iF.addAction("com.samsung.sec.android.MusicPlayer.playstatechanged");
        iF.addAction("com.samsung.sec.android.MusicPlayer.playbackcomplete");
        iF.addAction("com.samsung.sec.android.MusicPlayer.metachanged");
        iF.addAction("com.sec.android.app.music.playstatechanged");
        iF.addAction("com.sec.android.app.music.playbackcomplete");
        iF.addAction("com.sec.android.app.music.metachanged");
        //Winamp
        iF.addAction("com.nullsoft.winamp.playstatechanged");
        iF.addAction("com.nullsoft.winamp.metachanged");
        //Amazon
        iF.addAction("com.amazon.mp3.playstatechanged");
        iF.addAction("com.amazon.mp3.metachanged");
        //Rhapsody
        iF.addAction("com.rhapsody.playstatechanged");
        iF.addAction("com.rhapsody.metachanged");
        //PowerAmp
        iF.addAction("com.maxmpz.audioplayer.playstatechanged");
        iF.addAction("com.maxmpz.audioplayer.metachanged");
        // MyTouch4G
        iF.addAction("com.real.IMP.metachanged");
        //appollo
        iF.addAction("com.andrew.apollo.metachanged");
        iF.addAction("com.andrew.apollo.playstatechanged");
        //scrobblers detect for players (poweramp for example)
        //Last.fm
        iF.addAction("fm.last.android.metachanged");
        iF.addAction("fm.last.android.playbackpaused");
        iF.addAction("fm.last.android.playbackcomplete");
        //A simple last.fm scrobbler
        iF.addAction("com.adam.aslfms.notify.playstatechanged");
        //Scrobble Droid
        iF.addAction("net.jjc1138.android.scrobbler.action.MUSIC_STATUS");
        //Spotify
        iF.addAction("com.spotify.music.metadatachanged");
        iF.addAction("com.spotify.music.playbackstatechanged");

        previousVolume = -1;

        settingsContentObserver = new SettingsContentObserver(new Handler());
        getApplicationContext().getContentResolver().registerContentObserver(android.provider.Settings.System.CONTENT_URI, true, settingsContentObserver);

        handleVolumeChange();

        registerReceiver(mReceiver, iF);
        return mMessenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        //Log.d(TAG, "onUnbind");
        getApplicationContext().getContentResolver().unregisterContentObserver(settingsContentObserver);

        unregisterReceiver(mReceiver);
        return super.onUnbind(intent);
    }

    private class OperationHandler extends Handler {

        public OperationHandler() {
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            int keyCode = 0;

            switch (MusicPlayerPluginFunction.resolveById(msg.what)) {
                case PLAY_PAUSE:
                    keyCode = KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE;
                    break;
                case NEXT_TRACK:
                    keyCode = KeyEvent.KEYCODE_MEDIA_NEXT;
                    break;
                case PREV_TRACK:
                    keyCode = KeyEvent.KEYCODE_MEDIA_PREVIOUS;
                    break;
                case PLAY:
                    if (playing) {
                        return;
                    }
                    keyCode = KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE;
                    break;
                case PAUSE:
                    if (!playing) {
                        return;
                    }
                    keyCode = KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE;
                    break;
                case STOP:
                    if (!playing) {
                        return;
                    }
                    keyCode = KeyEvent.KEYCODE_MEDIA_STOP;
                    break;
                case VOLUME_UP: {
                    AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                            AudioManager.ADJUST_RAISE, 0);
                    handleVolumeChange();
                }
                break;
                case VOLUME_DOWN: {
                    AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC,
                            AudioManager.ADJUST_LOWER, 0);
                    handleVolumeChange();
                    break;
                }
                case VOLUME_MAX: {
                    AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
                    handleVolumeChange();
                }
                break;
                case VOLUME_MIN: {
                    AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                    handleVolumeChange();
                    break;
                }
                default:
                    // do nothing
                    return;
            }


            if (keyCode != 0) {
                long eventTime = SystemClock.uptimeMillis();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    AudioManager am = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
                    am.dispatchMediaKeyEvent(new KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN, keyCode, 0));
                    am.dispatchMediaKeyEvent(new KeyEvent(eventTime, eventTime, KeyEvent.ACTION_UP, keyCode, 0));
                } else {
                    Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
                    intent.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN, keyCode,
                            0));
                    sendOrderedBroadcast(intent, null);
                    intent.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(eventTime, eventTime, KeyEvent.ACTION_UP, keyCode,
                            0));
                    sendOrderedBroadcast(intent, null);
                }
            }
        }
    }

    private void handleVolumeChange() {

        AudioManager audio = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC);

        int delta = previousVolume - currentVolume;

        if (delta != 0) {
            ContentValues values = new ContentValues();
            values.put(MusicPlayerPluginProperty.VOLUME.getName(), 100 * currentVolume / audio.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            getContentResolver().update(MusicPlayerPluginContentProvider.PROPERTY_VALUES_URI, values, null, null);
            previousVolume = currentVolume;
        }
    }

    private class SettingsContentObserver extends ContentObserver {

        public SettingsContentObserver(Handler handler) {
            super(handler);
        }

        @Override
        public boolean deliverSelfNotifications() {
            return super.deliverSelfNotifications();
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);

            handleVolumeChange();
        }
    }
}

