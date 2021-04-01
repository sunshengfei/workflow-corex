package com.fuwafuwa.mqtt.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.annimon.stream.Stream;
import com.fuwafuwa.mqtt.bean.MQTTConnectUserEntity;
import com.fuwafuwa.hitohttp.model.Pager;
import com.fuwafuwa.utils.GsonUtils;
import com.fuwafuwa.utils.StringUtils;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableEmitter;


/**
 * fred
 */
public class MQTTProfileDBManager implements RxDBFlowable<MQTTConnectUserEntity> {

    private MQTTProfileDBHelper helper;

    public MQTTProfileDBManager(Context context) {
        this.helper = new MQTTProfileDBHelper(context);
    }

    @Override
    public Flowable<Integer> count() {
        return Flowable.create(emitter -> {
            try (SQLiteDatabase db = helper.getReadableDatabase()) {
                String[] columns = {MQTTProfileDBHelper.ID};
                Cursor cursor = db.query(MQTTProfileDBHelper.TABLE_NAME, columns, null, null, null, null, null);
                int count = 0;
                if (cursor != null) {
                    count = cursor.getCount();
                    cursor.close();
                }
                emitter.onNext(count);
            } catch (Exception e) {
                emitter.onError(e);
            }
            emitter.onComplete();
        }, BackpressureStrategy.BUFFER);
    }

    @Override
    public Flowable<Boolean> insert(MQTTConnectUserEntity entity) {
        return Flowable.create(emitter -> {
            insertSync(entity, emitter);
            emitter.onComplete();
        }, BackpressureStrategy.BUFFER);
    }

    @Override
    public boolean insertSync(MQTTConnectUserEntity entity, @Nullable FlowableEmitter<Boolean> emitter) {
        boolean tr = false;
        try (SQLiteDatabase db = helper.getReadableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(MQTTProfileDBHelper.ID,
                    entity.get_id());
            values.put(MQTTProfileDBHelper.KEY,
                    entity.getProfileName());
            values.put(MQTTProfileDBHelper.VAL,
                    GsonUtils.toJson(entity));
            long result = db.insert(MQTTProfileDBHelper.TABLE_NAME, null, values);
            tr = result != -1;
            if (emitter != null)
                emitter.onNext(tr);
        } catch (Exception e) {
            if (emitter != null)
                emitter.onError(e);
        }
        return tr;
    }

    @Override
    public Flowable<Pager<MQTTConnectUserEntity>> query(int page, int pageCount) {
        return Flowable.create(emitter -> {
            List<MQTTConnectUserEntity> list = new ArrayList<>();
            try (SQLiteDatabase db = helper.getReadableDatabase()) {
                String[] columns = {MQTTProfileDBHelper.ID, MQTTProfileDBHelper.KEY, MQTTProfileDBHelper.VAL, MQTTProfileDBHelper.CREATEDAT};
                Cursor cursor = db.query(MQTTProfileDBHelper.TABLE_NAME,
                        columns, "", null,
                        null, null,
                        MQTTProfileDBHelper.CREATEDAT + " DESC limit " + page * pageCount + "," + pageCount);
                int count = 0;
                if (cursor != null) {
                    count = cursor.getCount();
                    while (cursor.moveToNext()) {
                        String id = cursor.getString(cursor.getColumnIndex(MQTTProfileDBHelper.ID));
                        String key = cursor.getString(cursor.getColumnIndex(MQTTProfileDBHelper.KEY));
                        String value = cursor.getString(cursor.getColumnIndex(MQTTProfileDBHelper.VAL));
                        String date = cursor.getString(cursor.getColumnIndex(MQTTProfileDBHelper.CREATEDAT));
                        MQTTConnectUserEntity record = GsonUtils.parseJson(value, MQTTConnectUserEntity.class);
                        record.set_id(id);
                        record.setProfileName(key);
                        record.setCreatedAt(date);
                        list.add(record);
                    }
                    cursor.close();
                }
                Pager<MQTTConnectUserEntity> pager = new Pager<>();
                pager.setData(list);
                pager.setPageCount(pageCount);
                pager.setTotalPage(count % pageCount != 0 ? (count / pageCount + 1) : count / pageCount);
                pager.setCurrentPage(page);
                pager.setHasNext(page * pageCount < count);
                emitter.onNext(pager);
            } catch (Exception e) {
                emitter.onError(e);
            }
            emitter.onComplete();
        }, BackpressureStrategy.BUFFER);
    }

    @Override
    public Flowable<MQTTConnectUserEntity> select(MQTTConnectUserEntity record) {
        return Flowable.create(emitter -> {
            querySync(record, emitter);
            emitter.onComplete();
        }, BackpressureStrategy.BUFFER);
    }

    @Override
    public MQTTConnectUserEntity querySync(MQTTConnectUserEntity record, @Nullable FlowableEmitter<MQTTConnectUserEntity> emitter) {
        MQTTConnectUserEntity mRecord = null;
        try (SQLiteDatabase db = helper.getReadableDatabase()) {
            // 删除条件
            String whereClause = MQTTProfileDBHelper.ID + "=?";
            // 删除条件参数
            String[] whereArgs = {String.valueOf(record.get_id())};
            String[] columns = {MQTTProfileDBHelper.ID, MQTTProfileDBHelper.KEY, MQTTProfileDBHelper.VAL, MQTTProfileDBHelper.CREATEDAT};
            Cursor cursor = db.query(MQTTProfileDBHelper.TABLE_NAME,
                    columns, whereClause, whereArgs,
                    null, null,
                    null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    String id = cursor.getString(cursor.getColumnIndex(MQTTProfileDBHelper.ID));
                    String key = cursor.getString(cursor.getColumnIndex(MQTTProfileDBHelper.KEY));
                    String value = cursor.getString(cursor.getColumnIndex(MQTTProfileDBHelper.VAL));
                    String date = cursor.getString(cursor.getColumnIndex(MQTTProfileDBHelper.CREATEDAT));
                    mRecord = GsonUtils.parseJson(value, MQTTConnectUserEntity.class);
                    mRecord.set_id(id);
                    mRecord.setProfileName(key);
                    mRecord.setCreatedAt(date);
                    cursor.close();
                }
            }
            if (mRecord == null) {
                mRecord = new MQTTConnectUserEntity();
                mRecord.set_id(null);
            }
            if (emitter != null)
                emitter.onNext(mRecord);
        } catch (Exception e) {
            if (emitter != null)
                emitter.onError(e);
        }
        return mRecord;
    }


    @Override
    public Flowable<Boolean> delete(MQTTConnectUserEntity speedRecord) {
        return Flowable.create(emitter -> {
            try (SQLiteDatabase db = helper.getReadableDatabase()) {
                // 删除条件
                String whereClause = MQTTProfileDBHelper.ID + "=?";
                // 删除条件参数
                String[] whereArgs = {String.valueOf(speedRecord.get_id())};
                // 执行删除
                int res = db.delete(MQTTProfileDBHelper.TABLE_NAME, whereClause, whereArgs);
                if (res != -1) {
                    emitter.onNext(true);
                } else {
                    emitter.onNext(false);
                }
            } catch (Exception e) {
                emitter.onError(e);
            }
            emitter.onComplete();
        }, BackpressureStrategy.BUFFER);
    }

    @Override
    public Flowable<Boolean> update(MQTTConnectUserEntity speedRecord) {
        return null;
    }

    @Override
    public Flowable<Boolean> deleteByPrimaryKeys(@NonNull List<String> keys) {
        return Flowable.create(emitter -> {
            try (SQLiteDatabase db = helper.getReadableDatabase()) {
                // 删除条件参数
                // 删除条件
                List<String> whereList = Stream.of(keys).map(item -> "?").toList();
                String whereClause = MQTTProfileDBHelper.ID + " in (" + StringUtils.join(whereList, ",") + ")";
                // 删除条件参数
                String[] whereArgs = keys.toArray(new String[]{});
                // 执行删除
                int res = db.delete(MQTTProfileDBHelper.TABLE_NAME, whereClause, whereArgs);
                if (res != -1) {
                    emitter.onNext(true);
                } else {
                    emitter.onNext(false);
                }
            } catch (Exception e) {
                emitter.onError(e);
            }
            emitter.onComplete();
        }, BackpressureStrategy.BUFFER);


    }

    @Override
    public Flowable<Boolean> deleteAll() {
        return Flowable.create(emitter -> {
            try (SQLiteDatabase db = helper.getReadableDatabase()) {
                // 删除条件参数
                // 执行删除
                int res = db.delete(MQTTProfileDBHelper.TABLE_NAME, null, null);
                if (res != -1) {
                    emitter.onNext(true);
                } else {
                    emitter.onNext(false);
                }
            } catch (Exception e) {
                emitter.onError(e);
            }
            emitter.onComplete();
        }, BackpressureStrategy.BUFFER);
    }

}
