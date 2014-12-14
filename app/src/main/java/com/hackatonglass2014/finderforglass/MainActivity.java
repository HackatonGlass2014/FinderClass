package com.hackatonglass2014.finderforglass;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.FileObserver;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.glass.content.Intents;
import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.hackatonglass2014.finderforglass.network.ApiClientProvider;
import com.hackatonglass2014.finderforglass.network.RecognizeResponse;
import com.hackatonglass2014.finderforglass.network.UploadResponse;

import java.io.File;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class MainActivity extends Activity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private static final int CAMERA_REQUEST_CODE = 100;

    private TextView mView;

    private AudioManager am;
    private GestureDetector mGestureDetector;

    /** Listener that displays the options menu when the touchpad is tapped. */
    private final GestureDetector.BaseListener mBaseListener = new GestureDetector.BaseListener() {
        @Override
        public boolean onGesture(Gesture gesture) {
            if (gesture == Gesture.TAP) {
                am.playSoundEffect(Sounds.TAP);
                startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), CAMERA_REQUEST_CODE);
                return true;
            } else {
                return false;
            }
        }
    };

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        return mGestureDetector.onMotionEvent(event);
    }

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        setContentView(R.layout.ac_main);
        init();

    }

    private void init() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mGestureDetector = new GestureDetector(this).setBaseListener(mBaseListener);
        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mView = (TextView) findViewById(R.id.open_camera);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            String thumbnailPath = data.getStringExtra(Intents.EXTRA_THUMBNAIL_FILE_PATH);
            String picturePath = data.getStringExtra(Intents.EXTRA_PICTURE_FILE_PATH);

            processPictureWhenReady(picturePath);
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void processPictureWhenReady(final String picturePath) {
        final File pictureFile = new File(picturePath);
        if (pictureFile.exists()) {
            processPicture(pictureFile);
        } else {
            final File parentDirectory = pictureFile.getParentFile();
            FileObserver observer = new FileObserver(parentDirectory.getPath(),
                    FileObserver.CLOSE_WRITE | FileObserver.MOVED_TO) {
                private boolean isFileWritten;

                @Override
                public void onEvent(int event, String path) {
                    if (!isFileWritten) {
                        File affectedFile = new File(parentDirectory, path);
                        isFileWritten = affectedFile.equals(pictureFile);

                        if (isFileWritten) {
                            stopWatching();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    processPictureWhenReady(picturePath);
                                }
                            });
                        }
                    }
                }
            };
            observer.startWatching();
        }
    }

    private void processPicture(File pictureFile) {
        Log.v(TAG, "photo complete");
        ApiClientProvider.provideApiClient().uploadPhoto(
                "en_US",
                new TypedFile("image/*", pictureFile),
                new Callback<UploadResponse>() {
                    @Override
                    public void success(UploadResponse uploadResponse, Response response) {
                        ApiClientProvider.provideApiClient().getPhoto(uploadResponse.token, new Callback<RecognizeResponse>() {
                            @Override
                            public void success(RecognizeResponse recognizeResponse, Response response) {
                                Log.v(TAG, recognizeResponse.name);
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                Log.v(TAG, "error");
                            }
                        });
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.v(TAG, "error");
                    }
                });
    }

}
