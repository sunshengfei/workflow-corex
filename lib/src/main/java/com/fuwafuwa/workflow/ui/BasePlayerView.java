package com.fuwafuwa.workflow.ui;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fuwafuwa.utils.Loger;
import com.fuwafuwa.utils.ModalComposer;
import com.fuwafuwa.workflow.BuildConfig;
import com.fuwafuwa.workflow.R;
import com.fuwafuwa.workflow.agent.IPlayDelegate;
import com.fuwafuwa.workflow.agent.IPlayDispatcher;
import com.fuwafuwa.workflow.databinding.VideoPlayerBinding;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.rtmp.RtmpDataSourceFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

public class BasePlayerView extends FrameLayout implements IPlayDispatcher {
    private SimpleExoPlayer player;
    private Context mContext;
    private int mode;
    private IPlayDelegate delegate;
    private VideoPlayerBinding mBinding;

    public BasePlayerView(@NonNull Context context) {
        super(context);
        init(context);
    }

    public BasePlayerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BasePlayerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        mBinding = VideoPlayerBinding.inflate(LayoutInflater.from(mContext), this, true);
        if (isInEditMode()) {
            return;
        }
        initEvent();
    }

    private void initEvent() {
        mBinding.negative.setOnClickListener(v -> {
            if (delegate != null) {
                delegate.onPlayerClose();
            }
        });
    }

    @Override
    public void play(Uri mp4VideoUri) {
        if (mp4VideoUri != null) {
            if (player == null)
                player = new SimpleExoPlayer.Builder(mContext).build();
            mBinding.exPlayerView.setPlayer(player);
//                player.setPlaybackParameters(new PlaybackParameters());
            View ratio = mBinding.exPlayerView.findViewById(R.id.exo_ratio);
            mode = AspectRatioFrameLayout.RESIZE_MODE_FIT;
            ratio.setOnClickListener(v -> {
                mode = (mode + 1) % (AspectRatioFrameLayout.RESIZE_MODE_ZOOM + 1);
                mBinding.exPlayerView.setResizeMode(mode);
//                    int flagsFullScreen = WindowManager.LayoutParams.FLAG_FULLSCREEN;
//                    if (getDialog() == null || getDialog().getWindow() == null) return;
//                    if (isFullScreen) {
//                        getDialog().getWindow().addFlags(flagsFullScreen); // 设置全屏
//                        //如果上面的不起作用，可以换成下面的。
//                        isFullScreen = false;
//                    } else { //退出全屏
//                        WindowManager.LayoutParams attrs = getDialog().getWindow().getAttributes();
//                        attrs.flags &= (~flagsFullScreen);
//                        getDialog().getWindow().setAttributes(attrs);
//                        getDialog().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//                        isFullScreen = true;
//                    }
            });
            MediaSource videoSource;
            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter.Builder(null).build();
            DataSource.Factory dataSourceFactory = new
                    DefaultDataSourceFactory(mContext,
                    Util.getUserAgent(mContext, BuildConfig.LIBRARY_PACKAGE_NAME)); // This is the MediaSource representing the media to be played. MediaSource
//                videoSource = new DashMediaSource.Factory(dataSourceFactory).createMediaSource(mp4VideoUri);
//                videoSource =  new SsMediaSource.Factory(dataSourceFactory).createMediaSource(mp4VideoUri);
            int type = Util.inferContentType(mp4VideoUri);
            Loger.d("🌺 type", type + "");
            if (type == C.TYPE_HLS) {
                videoSource = new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(mp4VideoUri);
            } else if (type == C.TYPE_SS) {
                videoSource = new SsMediaSource.Factory(dataSourceFactory).createMediaSource(mp4VideoUri);
            } else if (type == C.TYPE_DASH) {
                videoSource = new DashMediaSource.Factory(dataSourceFactory).createMediaSource(mp4VideoUri);
            } else {
                if ("rtmp".equalsIgnoreCase(mp4VideoUri.getScheme())) {
                    dataSourceFactory = new RtmpDataSourceFactory();
                }
                videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(mp4VideoUri); // Prepare the player with the source.
            }

            Player.EventListener eventListener = new Player.EventListener() {
                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                }

                @Override
                public void onPlayerError(ExoPlaybackException error) {
                    error.printStackTrace();
                }
            };
            player.addListener(eventListener);
            player.setPlayWhenReady(true);
            player.prepare(videoSource);
        } else {
            ModalComposer.showToast("资源地址不可用");
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public void pause() {

    }

    @Override
    public void release() {
        if (player != null) {
            player.release();
        }
    }

    @Override
    public void toggleFullScreen() {

    }

    @Override
    public void toggleDimens() {

    }

    @Override
    public void setDelegate(IPlayDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == ORIENTATION_LANDSCAPE) {
            mBinding.exPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
        } else {
            mBinding.exPlayerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
        }
        if (mContext instanceof Activity) {
            Window window = ((Activity) mContext).getWindow();
            if (window != null) {
                int flags = window.getDecorView().getSystemUiVisibility();
                if (newConfig.orientation == ORIENTATION_LANDSCAPE) {
                    flags |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE;
                } else {
                    flags &= (~(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE));
                }
                window.getDecorView().setSystemUiVisibility(flags);
            }
        }
    }

    @Override
    public long getDuration() {
        if (player != null) return player.getDuration();
        return 0;
    }
}
