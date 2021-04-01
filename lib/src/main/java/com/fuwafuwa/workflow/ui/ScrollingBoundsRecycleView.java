package com.fuwafuwa.workflow.ui;

import android.content.Context;
import android.util.AttributeSet;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;


/**
 * Created by fred on 2016/11/5.
 */

public class ScrollingBoundsRecycleView extends RecyclerView {
    private boolean isLoadMore;

    public ScrollingBoundsRecycleView(Context context) {
        this(context, null);
    }

    public ScrollingBoundsRecycleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public ScrollingBoundsRecycleView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        addRecycleViewLoadMore();
    }

    public void setCanLoadMore(boolean isLoadMore) {
        this.isLoadMore = isLoadMore;
    }

    private void addRecycleViewLoadMore() {
        addOnScrollListener(new OnScrollListener() {

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                //这里加载更多绝对不能使用lastVisibleItemPosition == itemCount - 1来判断是否滑动到底部,这样会进行多次加载
                //!ViewCompat.canScrollVertically(recyclerView, 1)也可以判断是否滑动到最底部,但是我的测试demo中慢速滑动可能会触发两次,在我们app中添加了isLoadMore不会出现多次触发
                //下边这种比!ViewCompat.canScrollVertically(recyclerView, 1)更优秀,非常完美
                int offset = recyclerView.computeVerticalScrollOffset();
                int extent = recyclerView.computeVerticalScrollExtent();
                int range = recyclerView.computeVerticalScrollRange();
                if (isLoadMore) {
                    if ((offset + extent >= range) && dy > 0) {
                        if (loadMoreRecycleViewEvent != null) {
                            loadMoreRecycleViewEvent.onScrollToBottom();
                        }
                    }

                    if ((offset == 0) && dy < 0) {
                        if (loadMoreRecycleViewEvent != null) {
                            loadMoreRecycleViewEvent.onScrollToTop();
                        }
                    }

                }
            }
        });
    }

    public void setLoadMoreRecycleViewEvent(LoadMoreRecycleViewEvent loadMoreRecycleViewEvent) {
        this.loadMoreRecycleViewEvent = loadMoreRecycleViewEvent;
    }

    LoadMoreRecycleViewEvent loadMoreRecycleViewEvent;

    public interface LoadMoreRecycleViewEvent {

        void onScrollToBottom();

        void onScrollToTop();
    }
}
