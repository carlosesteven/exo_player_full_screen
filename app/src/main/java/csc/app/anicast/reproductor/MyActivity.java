package csc.app.anicast.reproductor;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.exoplayer2.ui.PlayerView;

import java.util.ArrayList;
import java.util.List;

public class MyActivity extends AppCompatActivity {

    private List<String> mVideoUrls = new ArrayList<>();
    private PlayerView videoView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.myactivity);

        videoView = findViewById(R.id.exoplayer);

        // Your activity setup code...
        mVideoUrls.add("http://csclab.xyz/video/video.mp4");

        for (String videoUrl : mVideoUrls) {
            setupPlayerView(videoView, videoUrl);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        for (String videoUrl : mVideoUrls) {
            ExoPlayerViewManager.getInstance( videoUrl )
                    .prepareExoPlayer(this, videoView);
            ExoPlayerViewManager.getInstance(videoUrl).goToForeground();
        }
        Log.d("csc_debug", "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        for (String videoUrl : mVideoUrls) {
            ExoPlayerViewManager.getInstance(videoUrl).goToBackground();
        }
        Log.d("csc_debug", "onPause");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (String videoUrl : mVideoUrls) {
            ExoPlayerViewManager.getInstance(videoUrl).releaseVideoPlayer();
        }
        Log.d("csc_debug", "onDestroy");
    }

    private void setupPlayerView(final PlayerView videoView, final String videoUrl) {
        ExoPlayerViewManager.getInstance(videoUrl).prepareExoPlayer(this, videoView);
        ExoPlayerViewManager.getInstance(videoUrl).goToForeground();

        View controlView = videoView.findViewById(R.id.exo_controller);
        controlView.findViewById(R.id.exo_fullscreen_button)
                .setOnClickListener(v -> {
                    Intent intent = new Intent(this, FullscreenVideoActivity.class);
                    intent.putExtra(ExoPlayerViewManager.EXTRA_VIDEO_URI, videoUrl);
                    startActivity(intent);
                });
    }

}
