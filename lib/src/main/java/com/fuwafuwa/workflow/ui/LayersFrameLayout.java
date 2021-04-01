package com.fuwafuwa.workflow.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.DrawableRes;

import com.fuwafuwa.workflow.R;

/**
 * Created by fred on 2016/11/5.
 */

public class LayersFrameLayout extends FrameLayout implements View.OnClickListener {

    public static final int LAYER_LOADING = 1;
    public static final int LAYER_DATA_EMPTY = 1 << 1;
    public static final int LAYER_NET_ERROR = 1 << 2;
    public static final int LAYER_DATA_ERROR = 1 << 3;
    public static final int LAYER_CUSTOM = 1 << 4;

    public View customerView;
    MaskLayout emptyLayoutMask;
    MaskLayout emptyLayoutNetError;
    MaskLayout emptyLayoutException;
    MaskLoadingView layoutLoading;

    protected ViewGroup rootView;

    private int currentLayerType;

    public LayersFrameLayout(Context context) {
        this(context, null);
    }

    public LayersFrameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LayersFrameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initLayout(context);
    }

    private void initLayout(Context context) {
        LayoutInflater layoutInflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        layoutInflater.inflate(R.layout.wf_layers_layout, this);
        rootView = (ViewGroup) findViewById(R.id.rootView);
        emptyLayoutMask=findViewById(R.id.empty_layout_mask);
        emptyLayoutNetError=findViewById(R.id.empty_layout_neterror);
        emptyLayoutException= findViewById(R.id.empty_layout_exception);
        layoutLoading=findViewById(R.id.layout_loading);
        emptyLayoutMask.setOnClickListener(this);
        emptyLayoutNetError.setOnClickListener(this);
        emptyLayoutException.setOnClickListener(this);

        initChild();
    }

    protected void initChild() {
    }

    /**
     * @param resIds length:1~3,order:[LAYER_DATA_EMPTY,LAYER_DATA_ERROR,LAYER_NET_ERROR]
     * @param texts  same as context
     */
    public void initLayerIconAndText(@DrawableRes int[] resIds, String[] texts) {
        if (resIds == null || resIds.length != 3) {
            return;
        }
        if (texts == null || texts.length != 3) {
            return;
        }
        emptyLayoutMask.setText(texts[0]);
        emptyLayoutMask.setIcon(resIds[0]);
        emptyLayoutException.setText(texts[1]);
        emptyLayoutException.setIcon(resIds[1]);
        emptyLayoutNetError.setText(texts[2]);
        emptyLayoutNetError.setIcon(resIds[2]);
    }

    public void setLayerIconAndTextByLayerType(@DrawableRes int resId, String text, int layerType) {
        switch (layerType) {
            case LAYER_DATA_EMPTY:
                emptyLayoutMask.setText(text);
                emptyLayoutMask.setIcon(resId);
                break;
            case LAYER_DATA_ERROR:
                emptyLayoutException.setText(text);
                emptyLayoutException.setIcon(resId);
                break;
            case LAYER_NET_ERROR:
                emptyLayoutNetError.setText(text);
                emptyLayoutNetError.setIcon(resId);
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
    }

    // region : @fred  [2016/11/5]


    private OnMaskLayerClickEvent onMaskLayerClickEvent;

    public void setOnMaskLayerClickEvent(OnMaskLayerClickEvent onMaskLayerClickEvent) {
        this.onMaskLayerClickEvent = onMaskLayerClickEvent;
    }

    @Override
    public void onClick(View v) {
        int layerType;
        int i = v.getId();
        if (i == R.id.empty_layout_mask) {
            layerType = LAYER_DATA_EMPTY;

        } else if (i == R.id.empty_layout_neterror) {
            layerType = LAYER_NET_ERROR;

        } else if (i == R.id.empty_layout_exception) {
            layerType = LAYER_DATA_ERROR;

        } else {
            return;
        }
        if (onMaskLayerClickEvent != null) {
            onMaskLayerClickEvent.onClick(v, layerType);
        }
    }

    public interface OnMaskLayerClickEvent {
        void onClick(View v, int layerType);
    }


    public void setCustomerView(View customerView) {
        this.customerView = customerView;
        if (customerView != null) {
            if (customerView.getParent() != this) {
                if (customerView.getParent() != null) {
                    ((ViewGroup) customerView.getParent()).removeView(customerView);
                }
                rootView.addView(customerView, getChildCount() > 0 ? getChildCount() - 1 : 0);
            }
        }
    }

    public void showLayer(int layerType) {
        View currentView = null;
        currentLayerType = layerType;
        switch (layerType) {
            case LAYER_DATA_EMPTY:
                currentView = emptyLayoutMask;
                break;
            case LAYER_DATA_ERROR:
                currentView = emptyLayoutException;
                break;
            case LAYER_NET_ERROR:
                currentView = emptyLayoutNetError;
                break;
            case LAYER_CUSTOM:
                if (customerView != null) {
                    currentView = customerView;
                }
                break;
            case LAYER_LOADING:
                currentView = layoutLoading;
                break;
            default:
                return;
        }
        if (currentView == null) return;
        rootView.bringChildToFront(currentView);
//        bringChildViewToFront(currentView);
    }


    /**
     * @param childView
     * @deprecated replaced to use bringChildViewToFront
     */
    private void bringChildViewToFront(View childView) {
        if (rootView == null) return;
        for (int i = 0; i < rootView.getChildCount(); i++) {
            if (rootView.getChildAt(i) != childView) {
                rootView.getChildAt(i).setVisibility(GONE);
            }
        }
        if (childView != null)
            childView.setVisibility(VISIBLE);
    }


    public int getCurrentLayerType() {
        return currentLayerType;
    }
    // endregion

}
