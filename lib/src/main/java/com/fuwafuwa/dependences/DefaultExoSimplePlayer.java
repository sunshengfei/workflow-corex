package com.fuwafuwa.dependences;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.exoplayer2.BuildConfig;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ext.rtmp.RtmpDataSourceFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class DefaultExoSimplePlayer {

    public static SimpleExoPlayer getPlayer(@NonNull Context mContext) {
        return new SimpleExoPlayer.Builder(mContext)
                .build();
    }

    public static void setDataSource(@NonNull Context mContext, @NonNull SimpleExoPlayer player, @NonNull Uri dataSource) {
        setDataSource(mContext, player, dataSource, true);
    }

    public static void setDataSource(@NonNull Context mContext, @NonNull SimpleExoPlayer player, @NonNull Uri dataSource, boolean autoPlay) {
//        Context mContext = ctx.getApplicationContext();
        MediaSource videoSource;
        DataSource.Factory dataSourceFactory = new
                DefaultDataSourceFactory(mContext,
                Util.getUserAgent(mContext, BuildConfig.LIBRARY_PACKAGE_NAME)); // This is the MediaSource representing the media to be played. MediaSource
        int type = Util.inferContentType(dataSource);
        if (type == C.TYPE_HLS) {
            videoSource = new HlsMediaSource.Factory(dataSourceFactory).createMediaSource(dataSource);
        } else if (type == C.TYPE_SS) {
            videoSource = new SsMediaSource.Factory(dataSourceFactory).createMediaSource(dataSource);
        } else if (type == C.TYPE_DASH) {
            videoSource = new DashMediaSource.Factory(dataSourceFactory).createMediaSource(dataSource);
        } else {
            if ("rtmp".equalsIgnoreCase(dataSource.getScheme())) {
                dataSourceFactory = new RtmpDataSourceFactory();
            }
            MediaItem mediaItem = MediaItem.fromUri(dataSource);
            videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(mediaItem); // Prepare the player with the source.
        }
        player.setMediaSource(videoSource);
        player.setPlayWhenReady(autoPlay);
        player.prepare();
    }
}
