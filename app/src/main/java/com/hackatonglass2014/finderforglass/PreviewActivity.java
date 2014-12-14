package com.hackatonglass2014.finderforglass;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.FileObserver;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.glass.widget.CardBuilder;

import java.io.File;

/**
 * Created by andrey_arzhannikov on 14.12.14.
 */
public class PreviewActivity extends BaseActivity {

    private ImageView photo;
    private TextView textHoler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final View view = new CardBuilder(this, CardBuilder.Layout.EMBED_INSIDE)
                .setEmbeddedLayout(R.layout.image_preview)
                .setFootnote("Tap to confirm or swipe down to cancel")
                .getView();

        photo = (ImageView) view.findViewById(R.id.photo);
        textHoler = (TextView) view.findViewById(R.id.placeholder);
        setContentView(view);

        processPictureWhenReady(getPicturePath());
    }

    private String getPicturePath() {
        return getIntent().getStringExtra(Constants.PHOTO_PATH_EXTRA);
    }

    private void loadImage(File file) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        options.inSampleSize = 4;
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        textHoler.setVisibility(View.GONE);
        photo.setImageBitmap(bitmap);
    }

    private void processPictureWhenReady(final String picturePath) {
        final File pictureFile = new File(picturePath);
        if (pictureFile.exists()) {
            loadImage(pictureFile);
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

    @Override
    protected void onTap() {
        super.onTap();
        setResult(RESULT_OK, null);
        finish();
    }
}
