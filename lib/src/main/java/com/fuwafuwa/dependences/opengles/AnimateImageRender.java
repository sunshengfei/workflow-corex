package com.fuwafuwa.dependences.opengles;

import android.opengl.GLES30;
import android.opengl.GLSurfaceView;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

public class AnimateImageRender implements GLSurfaceView.Renderer {


    // 控件视图大小
    protected int mViewWidth;
    protected int mViewHeight;

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        //初始化
        GLES30.glDisable(GL10.GL_DITHER);
        //设置清屏（背景）颜色 ： 透明
        GLES30.glClearColor(0, 0, 0, 0);
        GLES30.glEnable(GL10.GL_CULL_FACE);
        GLES30.glEnable(GL10.GL_DEPTH_TEST);
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        mViewWidth = width;
        mViewHeight = height;
        //设置视图大小
        GLES30.glViewport(0, 0, width, height);

    }

    @Override
    public void onDrawFrame(GL10 gl) {
        //清屏
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT | GLES30.GL_DEPTH_BUFFER_BIT);


    }


    final static String V_P = "attribute vec4 vPosition;\n" +
            "attribute vec2 vCoordinate;\n" +
            "uniform mat4 vMatrix;\n" +
            "\n" +
            "varying vec2 aCoordinate;\n" +
            "\n" +
            "void main(){\n" +
            "    gl_Position=vMatrix*vPosition;\n" +
            "    aCoordinate=vCoordinate;\n" +
            "}";

    final static String F_P = "precision mediump float;\n" +
            "\n" +
            "uniform sampler2D vTexture;\n" +
            "varying vec2 aCoordinate;\n" +
            "\n" +
            "void main(){\n" +
            "    gl_FragColor=texture2D(vTexture,aCoordinate);\n" +
            "}";
}
