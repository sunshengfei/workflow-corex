package com.fuwafuwa.workflow.agent;

import android.net.Uri;

import com.fuwafuwa.workflow.ui.MediaDialog;

public interface IPlayDispatcher {

    public void play(Uri mp4VideoUri);

    public void stop();

    public void pause();

    public void release();

    public void toggleFullScreen();

    public void toggleDimens();

    void setDelegate(IPlayDelegate delegate);

    boolean isPlaying();

    long getDuration();
}
