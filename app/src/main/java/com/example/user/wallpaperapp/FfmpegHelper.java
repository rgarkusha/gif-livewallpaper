package com.example.user.wallpaperapp;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.io.File;

/**
 * Created by User on 2017-04-17.
 */

public class FfmpegHelper {

    private static final String TAG = "ffmpeg";
    private static final String TOAST_MMPEG_FAILED_TO_LOAD = "Error: ffmpeg failed to load";
    private static final String FFMPEG_SUCCESSFULLY_RUNNING = "FFmpeg successfully running.";
    private final Context mContext;
    private boolean ffmpegIsRunning = false;

    FfmpegHelper(Context context_) {
        mContext = context_;
        initFFmpeg();
    }

    private void initFFmpeg() {
        if (!ffmpegIsRunning) {
            FFmpeg ffmpeg = FFmpeg.getInstance(mContext);
            try {
                ffmpeg.loadBinary(new LoadBinaryResponseHandler() {

                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onFailure() {
                        Utils.makeToast(mContext, TOAST_MMPEG_FAILED_TO_LOAD);
                    }

                    @Override
                    public void onSuccess() {
                        Log.d(TAG, FFMPEG_SUCCESSFULLY_RUNNING);
//                        makeToast(FFMPEG_SUCCESSFULLY_RUNNING);
                        ffmpegIsRunning = true;
                    }

                    @Override
                    public void onFinish() {
                    }
                });
            } catch (FFmpegNotSupportedException e) {
                Log.d(TAG, e.getMessage());
            }
        } else {
            Log.d(TAG, "FFmpeg is already running");
        }
    }

    public void convertGifToMp4(Uri gifUri, final FfmpegCallback callback) {
        String inputPath = Utils.getPath(mContext, gifUri);
        String outputPath = Utils.getVideoPath(mContext);
        String cmd = mContext.getString(R.string.ffmpeg_exec_gif_to_mp4, inputPath, outputPath);
        Log.d(TAG, "convertGifToMp4() running with cmd: " + cmd);
        execFFmpeg(splitCmd(cmd), callback);
    }

    private String[] splitCmd(String cmd) {
        String[] out = cmd.split(" ");
        return out;
    }

    public void execFFmpeg(String[] cmd, final FfmpegCallback callback) {
        if (ffmpegIsRunning) {
            FFmpeg ffmpeg = FFmpeg.getInstance(mContext);
            Log.d(TAG, "executing: " + cmd[0]);
            try {
                File f = new File(Utils.getVideoPath(mContext));
                if (f.exists()) {
                    f.delete();
                }
                // to execute "ffmpeg -version" command you just need to pass "-version"
                ffmpeg.execute(cmd, new ExecuteBinaryResponseHandler() {

                    @Override
                    public void onStart() {
                    }

                    @Override
                    public void onProgress(String message) {
                        Log.d(TAG, "onProgress: " + message);
                    }

                    @Override
                    public void onFailure(String message) {
                        Log.d(TAG, "onFailure: " + message);
                        Utils.makeToast(mContext, "Gif failed converting");
                        callback.ffmpegCallback(FfmpegCallback.FAILURE);
                    }

                    @Override
                    public void onSuccess(String message) {
                        Log.d(TAG, "onSuccess: " + message);
                        Utils.makeToast(mContext, "Gif finished converting");
                        callback.ffmpegCallback(FfmpegCallback.SUCCESS);
                    }

                    @Override
                    public void onFinish() {
                    }
                });
            } catch (FFmpegCommandAlreadyRunningException e) {
                Log.d(TAG, e.getMessage());
            }
        }
    }
}
