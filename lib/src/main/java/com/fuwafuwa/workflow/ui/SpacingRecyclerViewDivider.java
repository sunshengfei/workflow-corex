package com.fuwafuwa.workflow.ui;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class SpacingRecyclerViewDivider extends RecyclerView.ItemDecoration {

    private int dividerWidth;
    private int dividerWidthTop;
    private int dividerWidthBot;
    private int spanCount;

    public SpacingRecyclerViewDivider(int span, int space) {
        this.spanCount = span;
        this.dividerWidth = space;
        this.dividerWidthTop = dividerWidth / 2;
        this.dividerWidthBot = dividerWidth - dividerWidthTop;
    }

    @Override
    public void getItemOffsets(Rect outRect, View child, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, child, parent, state);
        int pos = parent.getChildAdapterPosition(child);
        int column = (pos) % spanCount;// 计算这个child 处于第几列
        if (column == 0) {
            outRect.left = dividerWidth;
        } else {
            outRect.left = (column * dividerWidth / spanCount);
        }
        if (column + 1 == spanCount) {
            outRect.right = dividerWidth;
        } else {
            outRect.right = dividerWidth - (column + 1) * dividerWidth / spanCount;
        }
        if (pos < spanCount) {
            outRect.top = dividerWidth;
        } else {
            outRect.top = dividerWidthTop;
        }
        outRect.bottom = dividerWidthBot;
    }
}