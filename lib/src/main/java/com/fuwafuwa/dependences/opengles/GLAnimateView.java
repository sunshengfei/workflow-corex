package com.fuwafuwa.dependences.opengles;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class GLAnimateView extends GLSurfaceView {

    public GLAnimateView(Context context) {
        super(context);
        setUp(context);
    }

    public GLAnimateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setUp(context);
    }

    private void setUp(Context context) {
        setEGLContextClientVersion(3);
        Renderer render = new AnimateImageRender();
        setRenderer(render);
//        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}
