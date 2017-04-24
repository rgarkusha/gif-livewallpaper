package com.example.user.wallpaperapp;

import android.app.Fragment;
import android.net.Uri;
import android.os.Bundle;

/**
 * Created by User on 2017-04-21.
 */

public class RetainedFragment extends Fragment {

    // data object we want to retain
    private FfmpegHelper ffmpegHelper;
    private SetWallpaperActivity.State state;
    private ConvertingDialogFragment convertingDialogFragment;
    private Uri gifUri;

    // this method is only called once for this fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }

    public FfmpegHelper getFfmpegHelper() {
        return ffmpegHelper;
    }

    public void setFfmpegHelper(FfmpegHelper ffmpegHelper) {
        this.ffmpegHelper = ffmpegHelper;
    }

    public SetWallpaperActivity.State getState() {
        return state;
    }

    public void setState(SetWallpaperActivity.State state) {
        this.state = state;
    }

    public Uri getGifUri() {
        return gifUri;
    }

    public void setGifUri(Uri gifUri) {
        this.gifUri = gifUri;
    }

    public ConvertingDialogFragment getConvertingDialogFragment() {
        return convertingDialogFragment;
    }

    public void setConvertingDialogFragment(ConvertingDialogFragment convertingDialogFragment) {
        this.convertingDialogFragment = convertingDialogFragment;
    }
}