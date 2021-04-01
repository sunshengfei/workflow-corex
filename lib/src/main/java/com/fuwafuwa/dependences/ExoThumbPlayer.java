package com.fuwafuwa.dependences;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.TextureView;

import androidx.annotation.Nullable;

import com.fuwafuwa.utils.FileUtil;
import com.fuwafuwa.utils.Loger;
import com.fuwafuwa.utils.ModalComposer;
import com.fuwafuwa.workflow.BuildConfig;
import com.fuwafuwa.workflow.agent.IPlayDelegate;
import com.fuwafuwa.workflow.agent.IPlayDispatcher;
import com.fuwafuwa.workflow.plugins.cipher.lib.MD5;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.rtmp.RtmpDataSourceFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.File;
import java.util.LinkedList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ExoThumbPlayer extends PlayerView implements IPlayDispatcher {

    private static final int CHECK_INTERVAL_MS = 30;
    private SimpleExoPlayer player;
    private Context mContext;
    private Uri currentDataSource;
    private LinkedList<Long> thumbnailMillSecList;
    private Disposable mThumbObserver;
    private TextureView surfaceView;
    private File thumbDir;
    private File currentThumbDir;

    public interface ThumbCallback {
        void onThumb(String url, int index);
    }

    private ThumbCallback callback;

    public ExoThumbPlayer(Context context) {
        super(context);
        initContext(context);
    }

    public ExoThumbPlayer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initContext(context);
    }

    public ExoThumbPlayer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initContext(context);
    }

    private void initContext(Context context) {
        this.mContext = context;
        thumbDir = new File(context.getExternalCacheDir(), "thumb");
        if (!thumbDir.isDirectory()) {
            thumbDir.mkdirs();
        }
    }


    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        surfaceView = (TextureView) getVideoSurfaceView();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mThumbObserver != null) {
            if (!mThumbObserver.isDisposed())
                mThumbObserver.dispose();
        }
    }

    @Override
    public void play(Uri mp4VideoUri) {
        if (mp4VideoUri != null) {
            currentDataSource = mp4VideoUri;
            if (player == null)
                player = new SimpleExoPlayer.Builder(mContext).build();
            setPlayer(player);
            player.setRepeatMode(Player.REPEAT_MODE_OFF);
            setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            MediaSource videoSource;
//            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter.Builder(null).build();
            DataSource.Factory dataSourceFactory = new
                    DefaultDataSourceFactory(mContext,
                    Util.getUserAgent(mContext, BuildConfig.LIBRARY_PACKAGE_NAME)); // This is the MediaSource representing the media to be played. MediaSource
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
                MediaItem mediaItem = MediaItem.fromUri(mp4VideoUri);
                videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                        .createMediaSource(mediaItem); // Prepare the player with the source.
            }

            Player.EventListener eventListener = new Player.EventListener() {
                @Override
                public void onPlayerError(ExoPlaybackException error) {
                    error.printStackTrace();
                }
            };
            player.setMediaSource(videoSource);
            player.addListener(eventListener);
            player.setPlayWhenReady(true);
            player.prepare();
            player.setPlaybackParameters(new PlaybackParameters(20f));
        } else {
            ModalComposer.showToast("资源地址不可用");
        }
    }

    public void startAndPullThumb(String mp4VideoUri, final int thumbnailCount, final int millsecsPerFrame, ThumbCallback callback) {
        this.callback = callback;
        if (mp4VideoUri != null) {
            play(Uri.parse(mp4VideoUri));
            player.setVolume(0f);
            prepareThumbObserver(thumbnailCount, millsecsPerFrame);
        }
    }

    private void prepareThumbObserver(final int thumbnailCount, final int millsecsPerFrame) {
        thumbnailMillSecList = new LinkedList<>();
        String thumbPrefix = MD5.encode(currentDataSource.toString());
        currentThumbDir = new File(thumbDir, thumbPrefix);
        if (!currentThumbDir.isDirectory()) {
            currentThumbDir.mkdirs();
        }
        mThumbObserver = Flowable.<LinkedList<Long>>create(emitter -> {
            LinkedList<Long> msList = new LinkedList<>();
            long duration = MediaUtils.getDuration(mContext, currentDataSource);
            long millSec = 0L;

            for (int i = 0; i < thumbnailCount; i++) {
                if (millSec > duration) {
                    millSec = duration;
                }
                msList.add(millSec);
                millSec += millsecsPerFrame;
            }
            emitter.onNext(msList);
            emitter.onComplete();
        }, BackpressureStrategy.BUFFER)
                .subscribeOn(Schedulers.io())
                .doOnNext(longs -> {
                    if (longs.size() != 0) thumbnailMillSecList.addAll(longs);
                })
//                .flatMap((Function<LinkedList<Long>, Publisher<Long>>) longs -> {
//                    if (longs.size() == 0) return Flowable.empty();
//                    return Flowable.interval(CHECK_INTERVAL_MS, TimeUnit.MILLISECONDS)
//                            .subscribeOn(Schedulers.io());
//                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(interval -> {
                    safeOnNext(currentThumbDir, 0);
                }, e -> {
                    e.printStackTrace();
                }, () -> {

                });
    }


    private void safeOnNext(File currentThumbDir, int index) {
        if (thumbnailMillSecList.size() == 0) return;
        Long timeMs = thumbnailMillSecList.get(0);
        int nextIndex = index;
        if (player.getCurrentPosition() > timeMs) {
            player.setPlayWhenReady(false);
            Bitmap bitmap = surfaceView.getBitmap();
            File bitFile = new File(currentThumbDir, "thumb_" + index);
            FileUtil.writeFile(bitmap, bitFile, 50);
            //TODO 通知
            if (this.callback != null) {
                this.callback.onThumb(bitFile.getAbsolutePath(), index);
            }
            nextIndex = index + 1;
            thumbnailMillSecList.remove(0);
        }
        player.setPlayWhenReady(true);
        if (!player.isPlaying()) {
            player.play();
        }
        final int finalNextIndex = nextIndex;
        postDelayed(() -> {
            safeOnNext(currentThumbDir, finalNextIndex);
        }, CHECK_INTERVAL_MS);
    }

    @Override
    public void stop() {
        if (player != null)
            player.stop();
    }

    @Override
    public void pause() {
        if (player != null)
            player.pause();
    }

    @Override
    public void release() {
        if (player != null)
            player.release();
    }

    @Override
    public void toggleFullScreen() {

    }

    @Override
    public void toggleDimens() {

    }

    @Override
    public void setDelegate(IPlayDelegate delegate) {

    }

    @Override
    public boolean isPlaying() {
        if (player != null)
            return player.isPlaying();
        return false;
    }

    @Override
    public long getDuration() {
        if (player != null)
            return player.getDuration();
        return 0;
    }
}
