package com.hackatonglass2014.finderforglass;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;

import com.google.android.glass.content.Intents;

public class MainActivity extends BaseActivity {

    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int PREVIEW_REQUEST_CODE = 101;

    private String path;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.ac_main);

        if (bundle != null && bundle.containsKey(Constants.PHOTO_PATH_EXTRA)) {
            path = bundle.getString(Constants.PHOTO_PATH_EXTRA);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            path = data.getStringExtra(Intents.EXTRA_PICTURE_FILE_PATH);
            final Intent intent = new Intent(this, PreviewActivity.class);
            intent.putExtra(Constants.PHOTO_PATH_EXTRA, path);
            startActivityForResult(intent, PREVIEW_REQUEST_CODE);
        }
        if (requestCode == PREVIEW_REQUEST_CODE && resultCode == RESULT_OK) {
            final Intent intent = new Intent(this, ResultsActivity.class);
            intent.putExtra(Constants.PHOTO_PATH_EXTRA, path);
            startActivity(intent);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (path != null) {
            outState.putString(Constants.PHOTO_PATH_EXTRA, path);
        }
    }

    @Override
    protected void onTap() {
        super.onTap();
        path = null;
        startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), CAMERA_REQUEST_CODE);
    }
}
