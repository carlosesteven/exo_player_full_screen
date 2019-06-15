package csc.app.anicast.reproductor;

import android.content.Context;
import android.net.Uri;
import android.view.SurfaceView;

import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.util.HashMap;
import java.util.Map;

class ExoPlayerViewManager {

    //private static final String TAG = "ExoPlayerViewManager";

    static final String EXTRA_VIDEO_URI = "video_uri";

    private static Map<String, ExoPlayerViewManager> instances = new HashMap<>();
    private Uri videoUri;

    static ExoPlayerViewManager getInstance(String videoUri) {
        ExoPlayerViewManager instance = instances.get(videoUri);
        if (instance == null) {
            instance = new ExoPlayerViewManager(videoUri);
            instances.put(videoUri, instance);
        }
        return instance;
    }

    private SimpleExoPlayer player;
    private boolean isPlayerPlaying;

    private ExoPlayerViewManager(String videoUri) {
        this.videoUri = Uri.parse(videoUri);
    }

    void prepareExoPlayer(Context context, PlayerView exoPlayerView) {
        if (context == null || exoPlayerView == null) {
            return;
        }
        if (player == null) {
            TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory();
            TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
            player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
            ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
            DataSource.Factory dataSourceFactory =
                    new DefaultDataSourceFactory(
                            context,
                            Util.getUserAgent(
                                    context,
                                    context.getString(R.string.app_name)
                            )
                    );
            MediaSource videoSource = new ExtractorMediaSource
                    .Factory(dataSourceFactory)
                    .setExtractorsFactory(extractorsFactory)
                    .createMediaSource(videoUri);
            player.prepare(videoSource);
        }
        player.clearVideoSurface();
        player.setVideoSurfaceView((SurfaceView) exoPlayerView.getVideoSurfaceView());
        player.seekTo(player.getCurrentPosition() + 1);
        exoPlayerView.setPlayer(player);
    }

    void releaseVideoPlayer() {
        if (player != null) {
            player.release();
        }
        player = null;
    }

    void goToBackground() {
        if (player != null) {
            isPlayerPlaying = player.getPlayWhenReady();
            player.setPlayWhenReady(false);
        }
    }

    void goToForeground() {
        if (player != null) {
            player.setPlayWhenReady(isPlayerPlaying);
        }
    }

}
