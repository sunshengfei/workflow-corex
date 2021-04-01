package com.fuwafuwa.mqtt.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by fred on 2014/12/19.
 */
public class MQTTProfileDBHelper extends SQLiteOpenHelper {
    public static final String DBNAME = "mqtt_profile.db";//数据库名称
    public static final String TABLE_NAME = "mqtt_profiles";//数据库表名
    public static final String ID = "_id";

    public static final String KEY = "name";

    public static final String VAL = "val";

    public static final String CREATEDAT = "created_at";
    /**
     * 创建表的sql语句
     */
    private static final String SQL = "create table if not exists "
            + TABLE_NAME
            + "("
            + ID + " text primary key, "
            + KEY + " text , "
            + VAL + " text , "
            + CREATEDAT + "  datetime DEFAULT (datetime(CURRENT_TIMESTAMP,'localtime'))"
            + ");";

    public MQTTProfileDBHelper(Context context) {
        super(context, DBNAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + DBNAME);
        onCreate(db);
    }


}
