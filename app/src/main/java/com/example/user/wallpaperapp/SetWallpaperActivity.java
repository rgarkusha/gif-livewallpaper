package com.example.user.wallpaperapp;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.WallpaperInfo;
import android.app.WallpaperManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by User on 2017-04-13.
 */

public class SetWallpaperActivity extends android.support.v4.app.FragmentActivity implements FfmpegCallback {


    private static final String GIF_MIME_TYPE = "image/gif";
    private static final String TAG_CONVERTING_DIALOG = "TAG_CONVERTING_DIALOG";
    private static final String TAG_RETAINED_FRAGMENT = "TAG_RETAINED_FRAGMENT";
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 2;
    public static String TAG = Activity.class.toString();
    public static int REQUEST_CODE_GIF = 1;

    private ImageView mImageView;
    private LinearLayout mConfirmLayout;
    private FfmpegHelper mFFmpegHelper;
    private ConvertingDialogFragment mConvertingDialogFragment;
    private RetainedFragment mRetainedFragment;

    public enum State {EMPTY, SHOW_IMAGE, GIF_CONVERTED, GIF_CONVERTING};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.main);

        // find the retained fragment on activity restarts
        FragmentManager fm = getFragmentManager();
        mRetainedFragment = (RetainedFragment) fm.findFragmentByTag(TAG_RETAINED_FRAGMENT);

        // create the fragment and data the first time
        if (mRetainedFragment == null) {
            // add the fragment
            mRetainedFragment = new RetainedFragment();
            fm.beginTransaction().add(mRetainedFragment, TAG_RETAINED_FRAGMENT).commit();
            // load data from a data source or perform any calculation
            mRetainedFragment.setFfmpegHelper(new FfmpegHelper(this));
            mRetainedFragment.setState(State.EMPTY);
            mRetainedFragment.setConvertingDialogFragment(new ConvertingDialogFragment());
        }

        mImageView = (ImageView) findViewById(R.id.preview);
        mConfirmLayout = (LinearLayout) findViewById(R.id.confirm_wallpaper);

        restoreSavedInstance();

        Log.d(TAG, "onCreate() called");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
        if (isWallPaperSet() && getAppState() == State.GIF_CONVERTED) {
            Log.d(TAG, "wallpaper is set, exiting activity");
            finish();
        }
    }

    private boolean isWallPaperSet() {
        boolean isSet;
        WallpaperManager wpm = WallpaperManager.getInstance(this);
        WallpaperInfo info = wpm.getWallpaperInfo();

        if (info != null && info.getPackageName().equals(this.getPackageName())) {
            isSet = true;
        } else {
            isSet = false;
        }
        return isSet;
    }

    @Override
    public void ffmpegCallback(int success) {
        setAppState(State.GIF_CONVERTED);
        if (success == FfmpegCallback.SUCCESS) {
            setWallpaper();
        }
    }

    private void showConvertingDialog() {
//        Log.d(TAG, "showConvertingDialog() called");
        if (!mConvertingDialogFragment.isAdded()) {
            mConvertingDialogFragment.show(getSupportFragmentManager(), TAG_CONVERTING_DIALOG);
            mConvertingDialogFragment.setCancelable(false);
        }
    }

    private void hideConvertingDialog() {
        try {
            mConvertingDialogFragment.dismiss();
        } catch(NullPointerException e) {

        }
    }

    private void setWallpaper() {
        Intent intent = new Intent(WallpaperManager.ACTION_CHANGE_LIVE_WALLPAPER);
        intent.putExtra(WallpaperManager.EXTRA_LIVE_WALLPAPER_COMPONENT,
                new ComponentName(this, WallpaperAppService.class));
        Log.d(this.getClass().toString(), "starting wallpaper activity");
        startActivity(intent);
    }

    private Uri getGifUri() {
        return mRetainedFragment.getGifUri();
    }

    private void setGifUri(Uri uri) {
        mRetainedFragment.setGifUri(uri);
    }

    private void restoreSavedInstance() {
        mFFmpegHelper = mRetainedFragment.getFfmpegHelper();
        mConvertingDialogFragment = mRetainedFragment.getConvertingDialogFragment();
        setAppState(mRetainedFragment.getState());
    }

    private void setAppState(State state) {
        mRetainedFragment.setState(state);
        Log.d(TAG, "mAppState is " + state);
        updateState();
    }

    private State getAppState() {
        return mRetainedFragment.getState();
    }

    private void updateState() {

        switch (getAppState()) {
            case EMPTY:
                mConfirmLayout.setVisibility(View.GONE);
                mImageView.setVisibility(View.VISIBLE);
                break;
            case SHOW_IMAGE:
                showImage();
                break;
            case GIF_CONVERTING:
                showConvertingDialog();
                showImage();
                break;
            case GIF_CONVERTED:
                hideConvertingDialog();
                showImage();
                break;
            default:
                break;
        }
    }

    private void showImage() {
        mConfirmLayout.setVisibility(View.VISIBLE);
        mImageView.setVisibility(View.VISIBLE);
        setImageView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_GIF && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                //Display an error
                Log.d(TAG, "Got Data: " + Utils.getPath(this, data.getData()));
                setGifUri(data.getData());
                setAppState(State.SHOW_IMAGE);
            }
        }
    }

    private void setImageView(Uri uri) {
        mImageView.setImageURI(uri);
    }

    private void setImageView() {
        if (getGifUri() != null) {
            setImageView(getGifUri());
        }
    }

    public void onClickSetWallpaper(View view) {
        setAppState(State.GIF_CONVERTING);
        mFFmpegHelper.convertGifToMp4(getGifUri(), this);
    }

    public void onClickGif(View view) {
        Log.d(TAG, "onClickGif() called");
        launchGetGifActivity();
    }

    private void requestReadExternalPermission() {
        Log.d(TAG, "requestReadExternalPermission() called");
        if (!hasReadExternalPermission()) {

            // Should we show an explanation?
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
//
//                // Show an explanation to the user *asynchronously* -- don't block
//                // this thread waiting for the user's response! After the user
//                // sees the explanation, try again to request the permission.
//
//            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
//            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {

        Log.d(TAG, "onRequestPermissionResult() called");
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
                // If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivityForResult(makeGetGifIntent(), REQUEST_CODE_GIF);
            }
            return;
        }
    }

    private boolean hasReadExternalPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void launchGetGifActivity() {
        Intent intent = makeGetGifIntent();
        if (hasReadExternalPermission()) {
            startActivityForResult(intent, REQUEST_CODE_GIF);
        } else {
            requestReadExternalPermission();
        }
    }

    @NonNull
    private Intent makeGetGifIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(GIF_MIME_TYPE);
        return intent;
    }

}
