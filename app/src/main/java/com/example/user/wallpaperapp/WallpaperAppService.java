package com.example.user.wallpaperapp;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.SurfaceHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by User on 2017-04-13.
 */

public class WallpaperAppService extends WallpaperService {
    @Override
    public Engine onCreateEngine() {
        Log.d(this.getClass().toString(), "onCreateEngine() called");
        return new VideoEngine();
    }

    class VideoEngine extends Engine
    {

        private final String        TAG     = getClass().getSimpleName();
        private WallpaperMediaPlayer mediaPlayer;
        private Surface mSurface;

        public VideoEngine()
        {
            super();
            Log.i( TAG, "( VideoEngine )");
//            mediaPlayer = MediaPlayer.create(getBaseContext(), R.raw.ezgif);
            mediaPlayer = new WallpaperMediaPlayer(getBaseContext());
            setOffsetNotificationsEnabled(false);
        }

//        private void initMediaPlayer() {
//            mediaPlayer = MediaPlayer.create(getBaseContext(), Utils.getVideoUri(getBaseContext()));
//            mediaPlayer.setVolume(0f, 0f);
//            mediaPlayer.setLooping(true);
//        }


        @Override
        public void onVisibilityChanged(boolean visible) {
            super.onVisibilityChanged(visible);
            Log.d(TAG, "onVisibilityChanged() called");
            if (visible) {
                if (mSurface != null && mediaPlayer != null) {
                    mediaPlayer.resetMediaPlayer(mSurface);
                }
            } else {
                mediaPlayer.releaseMediaPlayer();
            }
        }

        @Override
        public void onSurfaceCreated( SurfaceHolder holder )
        {
            Log.d( TAG, "onSurfaceCreated() called" );
            mSurface = holder.getSurface();
            mediaPlayer.resetMediaPlayer(mSurface);
        }

//        private void resetMediaPlayer() {
//            mediaPlayer.setSurface(mSurface);
//            mediaPlayer.start();
//        }


        @Override
        public void onSurfaceDestroyed( SurfaceHolder holder )
        {
            Log.i( TAG, "( INativeWallpaperEngine ): onSurfaceDestroyed" );
//            playheadTime = mediaPlayer.getCurrentPosition();
            mSurface = null;
            mediaPlayer.releaseMediaPlayer();
        }

//        private void releaseMediaPlayer() {
//            mediaPlayer.setSurface(null);
//            mediaPlayer.reset();
//            mediaPlayer.release();
//        }
    }

}


