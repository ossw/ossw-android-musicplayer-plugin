package com.althink.android.ossw.plugins.musicplayer;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String artist = intent.getStringExtra(MediaStore.Audio.AudioColumns.ARTIST);
            String album = intent.getStringExtra(MediaStore.Audio.AudioColumns.ALBUM);
            String track = intent.getStringExtra(MediaStore.Audio.AudioColumns.TRACK);
            playing = intent.getBooleanExtra("playing", false);

            ContentValues values = new ContentValues();
            values.put(MusicPlayerPluginProperty.ALBUM.getName(), album);
            values.put(MusicPlayerPluginProperty.ARTIST.getName(), artist);
            values.put(MusicPlayerPluginProperty.TRACK.getName(), track);
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

        registerReceiver(mReceiver, iF);
        return mMessenger.getBinder();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        //Log.d(TAG, "onUnbind");
        unregisterReceiver(mReceiver);
        return super.onUnbind(intent);
    }

    private class OperationHandler extends Handler {

        public OperationHandler() {
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            int action;
            int code;

            switch (MusicPlayerPluginFunction.resolveById(msg.what)) {
                case PLAY_PAUSE:
                    code = KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE;
                    //Log.i(TAG, "Play/pause: " + playing);
                    break;
                case NEXT_TRACK:
                    code = KeyEvent.KEYCODE_MEDIA_NEXT;
                    //Log.i(TAG, "Next track");
                    break;
                case PREV_TRACK:
                    code = KeyEvent.KEYCODE_MEDIA_PREVIOUS;
                    //Log.i(TAG, "Previous track");
                    break;
                case PLAY:
                    if (playing) {
                        return;
                    }
                    code = KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE;
                    //Log.i(TAG, "Play");
                    break;
                case PAUSE:
                    if (!playing) {
                        return;
                    }
                    code = KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE;
                    //Log.i(TAG, "Pause");
                    break;
                case STOP:
                    if (!playing) {
                        return;
                    }
                    code = KeyEvent.KEYCODE_MEDIA_STOP;
                    //Log.i(TAG, "Stop");
                    break;
                default:
                    // do nothing
                    return;
            }

            long eventTime = SystemClock.uptimeMillis();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                AudioManager am = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
                am.dispatchMediaKeyEvent(new KeyEvent(eventTime, eventTime, KeyEvent.ACTION_DOWN, code, 0));
                am.dispatchMediaKeyEvent(new KeyEvent(eventTime, eventTime, KeyEvent.ACTION_UP, code, 0));
            } else {
                Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON, null);
                intent.putExtra(Intent.EXTRA_KEY_EVENT, KeyEvent.ACTION_DOWN);
                sendOrderedBroadcast(intent, null);
                intent.putExtra(Intent.EXTRA_KEY_EVENT, KeyEvent.ACTION_UP);
                sendOrderedBroadcast(intent, null);
            }
        }
    }
}