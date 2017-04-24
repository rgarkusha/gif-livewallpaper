package com.example.user.wallpaperapp;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.Surface;

/**
 * Created by User on 2017-04-19.
 */

public class WallpaperMediaPlayer {

    private static final String TAG = WallpaperMediaPlayer.class.toString();
    private MediaPlayer mMediaPlayer;
    private Context mContext;
    private boolean mMediaPlayerIsPrepared = false;

    public WallpaperMediaPlayer(Context context) {
        mContext = context;
        initMediaPlayer();
    }

    private void initMediaPlayer() {
        Log.d(TAG, "initMediaPlayer() called");
        mMediaPlayer = MediaPlayer.create(mContext, Utils.getVideoUri(mContext));
        mMediaPlayer.setVolume(0f, 0f);
        mMediaPlayer.setLooping(true);
        mMediaPlayerIsPrepared = true;
    }

    public void releaseMediaPlayer() {
        Log.d(TAG, "releaseMediaPlayer() called");
        if (mMediaPlayerIsPrepared) {
            mMediaPlayer.setSurface(null);
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayerIsPrepared = false;
        }
    }

    public void resetMediaPlayer(Surface surface) {
        Log.d(TAG, "resetMediaPlayer() called");
        if (!mMediaPlayerIsPrepared) {
            initMediaPlayer();
        }
        mMediaPlayer.setSurface(surface);
        mMediaPlayer.start();
    }
}
