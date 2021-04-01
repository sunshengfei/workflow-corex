package com.fuwafuwa.workflow.adapter;

import android.content.Context;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.fuwafuwa.utils.RegexHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by fred on 2016/11/2.
 */

public abstract class BaseRecyclerAdapter<T> extends RecyclerView.Adapter<BaseRecyclerViewHolder<T>> {

    public AdapterItemEvent<T> adapterItemEvent;
    public String matchWord;
    public boolean followModeOn = false;
    protected Context mContext;
    public int mFocusIndex = -1;

    public boolean readOnly = false;

    public void setDataSets(List<T> dataSets) {
        this.dataSets = dataSets;
    }

    private List<T> dataSets;

    public T getItem(int position) {
        if (dataSets == null) {
            return null;
        }
        if (position < 0) return null;
        return dataSets.get(position);
    }


    public BaseRecyclerAdapter(Context context) {
        init(context, new ArrayList<T>());
    }

    public BaseRecyclerAdapter(Context context, List<T> objects) {
        init(context, objects);
    }

    protected void init(Context context, List<T> objects) {
        mContext = context;
        dataSets = objects;
    }

    @Override
    public final BaseRecyclerViewHolder<T> onCreateViewHolder(ViewGroup parent, int viewType) {
        return OnCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder holder, int position) {
        holder.itemView.setOnClickListener(view -> {
            if (onBaseRecyclerAdapterEvent != null)
                onBaseRecyclerAdapterEvent.onItemClick(holder);
        });
        holder.itemView.setOnLongClickListener(view -> {
            if (onBaseRecyclerAdapterEvent != null) {
                return onBaseRecyclerAdapterEvent.onItemLongClick(holder);
            }
            return false;
        });
        OnBindViewHolder(holder, position);
    }

    abstract public BaseRecyclerViewHolder<T> OnCreateViewHolder(ViewGroup parent, int viewType);

    protected void OnBindViewHolder(BaseRecyclerViewHolder<T> holder, final int position) {
        T item = getItem(position);
        holder.update(item, position, this);
    }

    @Override
    public int getItemCount() {
        return dataSets == null ? 0 : dataSets.size();
    }


    public List<T> getDataSets() {
        if (dataSets == null) {
            dataSets = new ArrayList<>();
        }
        return dataSets;
    }

    public void notify(List<T> datas) {
        if (dataSets != null) {
            dataSets.clear();
        } else {
            dataSets = new ArrayList<>();
        }
        if (RegexHelper.isNotEmpty(datas)) {
            dataSets.addAll(datas);
        }
        notifyDataSetChanged();
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    public void setOnBaseRecyclerAdapterEvent(OnBaseRecyclerAdapterEvent onBaseRecyclerAdapterEvent) {
        this.onBaseRecyclerAdapterEvent = onBaseRecyclerAdapterEvent;
    }

    public OnBaseRecyclerAdapterEvent<T> getOnBaseRecyclerAdapterEvent() {
        return onBaseRecyclerAdapterEvent;
    }

    private OnBaseRecyclerAdapterEvent<T> onBaseRecyclerAdapterEvent;


    public void remove(BaseRecyclerViewHolder<?> holder) {
    }

    public interface OnBaseRecyclerAdapterEvent<T> {
        void onItemClick(BaseRecyclerViewHolder<T> holder);

        boolean onItemLongClick(BaseRecyclerViewHolder<T> position);
    }


    public AdapterItemEvent<T> getAdapterItemEvent() {
        return adapterItemEvent;
    }

    public @interface AdapterItemEventDefault {
        int TYPE_ITEM = 0x01;
        int TYPE_ITEM_EDIT = 0x02;
        int TYPE_ITEM_DEL = 0x03;
    }

    public interface AdapterItemEvent<T> {
        void eventEmit(int eventType, BaseRecyclerViewHolder<T> appEntryHolder);
    }
}
