package com.fuwafuwa.dependences

import android.content.Context

interface VideoPlayer {
    fun initPlayer()
    fun setPlayer(context: Context, mediaPath: String)
    fun pausePlayer()
    fun startPlayer()
    fun seekToPosition(position: Long)
    fun getPlayerCurrentPosition(): Int
    fun getDuration(): Int
    fun setPlaySpeed(speed: Float)
    fun enableFramePreviewMode()
    fun releasePlayer()
    fun isPlaying(): Boolean
}