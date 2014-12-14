package com.hackatonglass2014.finderforglass;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.hackatonglass2014.finderforglass.network.ApiClientProvider;
import com.hackatonglass2014.finderforglass.network.RecognizeResponse;
import com.hackatonglass2014.finderforglass.network.UploadResponse;

import java.io.File;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;


public class ResultsActivity extends BaseActivity {

    TextView status;
    View progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        status = (TextView) findViewById(R.id.results_status);
        progress = findViewById(R.id.results_progress);

        upload(getIntent().getStringExtra(Constants.PHOTO_PATH_EXTRA));
    }

    private void upload(String path) {
        final File file = new File(path);
        ApiClientProvider.provideApiClient().uploadPhoto(
                "en_US",
                new TypedFile("image/*", file),
                new Callback<UploadResponse>() {
                    @Override
                    public void success(final UploadResponse uploadResponse, Response response) {
                        status.setText(R.string.recognizing);
                        recognizeImage(uploadResponse.token, 10);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        handleError();
                    }
                });
    }

    private void recognizeImage(final String token, final int count) {
        if (count == 0) {
            return;
        }
        ApiClientProvider.provideApiClient().getPhoto(token, new Callback<RecognizeResponse>() {
            @Override
            public void success(RecognizeResponse recognizeResponse, Response response) {
                if (recognizeResponse.isCompleted()) {
                    status.setText(recognizeResponse.name);
                } else {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            recognizeImage(token, count - 1);
                        }
                    }, 10000);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                handleError();
            }
        });
    }

    private void handleError() {
        progress.setVisibility(View.GONE);
        status.setText("Error");
    }
}
