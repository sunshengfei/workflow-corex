package com.fuwafuwa.mqtt.db;

import androidx.annotation.Nullable;

import com.fuwafuwa.hitohttp.model.Pager;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableEmitter;

public interface RxDBFlowable<T> {

    Flowable<Integer> count();

    Flowable<Boolean> insert(T record);

    boolean insertSync(T entity, @Nullable FlowableEmitter<Boolean> emitter);

    Flowable<Pager<T>> query(int page, int pageCount);

    Flowable<T> select(T record);

    T querySync(T record, @Nullable FlowableEmitter<T> emitter);

    Flowable<Boolean> delete(T record);

    Flowable<Boolean> update(T record);

    Flowable<Boolean> deleteByPrimaryKeys(List<String> keys);

    Flowable<Boolean> deleteAll();
}
