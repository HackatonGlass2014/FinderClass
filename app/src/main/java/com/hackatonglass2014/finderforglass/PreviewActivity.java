package com.hackatonglass2014.finderforglass;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.glass.widget.CardBuilder;

import java.io.File;

/**
 * Created by andrey_arzhannikov on 14.12.14.
 */
public class PreviewActivity extends Activity {

    private ImageView photo;
    private TextView textHoler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View view = new CardBuilder(this, CardBuilder.Layout.EMBED_INSIDE)
                .setEmbeddedLayout(R.layout.image_preview)
                .setFootnote("Foods you tracked")
                .setTimestamp("today")
                .getView();

        photo = (ImageView) view.findViewById(R.id.photo);
        textHoler = (TextView) view.findViewById(R.id.placeholder);
        setContentView(view);



    }

    private String getPicturePath() {

        String path = getIntent().getStringExtra("path");
        return path;
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadImage(getPicturePath());
    }

    private void loadImage(String path) {
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, options);
        textHoler.setVisibility(View.GONE);
        photo.setImageURI(Uri.fromFile(new File(path)));
    }
}
