package com.tobot.launcher.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.tobot.common.constants.ProviderConstant;

/**
 * @author houdeming
 * @date 2018/7/17
 */
public class ProviderSQLite extends SQLiteOpenHelper {
    private static final String DB_NAME = "tobotProvider.db";
    private static final int DB_VERSION = 1;

    private static final String IO_SQL = "CREATE TABLE IF NOT EXISTS " + ProviderConstant.DBTable.IO +
            "(" +
            ProviderConstant.DBAttribute.ID + " integer primary key autoincrement," +
            ProviderConstant.DBAttribute.TYPE + " integer," +
            ProviderConstant.DBAttribute.IO_ID + " integer," +
            ProviderConstant.DBAttribute.NAME + " text," +
            ProviderConstant.DBAttribute.CONTENT + " text" +
            ")";

    private static final String WARING_SQL = "CREATE TABLE IF NOT EXISTS " + ProviderConstant.DBTable.WARING +
            "(" +
            ProviderConstant.DBAttribute.ID + " integer primary key autoincrement," +
            ProviderConstant.DBAttribute.TYPE + " integer," +
            ProviderConstant.DBAttribute.TIME + " text," +
            ProviderConstant.DBAttribute.NAME + " text," +
            ProviderConstant.DBAttribute.MAP_NAME + " text," +
            ProviderConstant.DBAttribute.TASK_NAME + " text," +
            ProviderConstant.DBAttribute.POINT_NAME + " text," +
            ProviderConstant.DBAttribute.CONTENT + " text," +
            ProviderConstant.DBAttribute.REMARKS + " text" +
            ")";

    private static final String ERROR_SQL = "CREATE TABLE IF NOT EXISTS " + ProviderConstant.DBTable.ERROR +
            "(" +
            ProviderConstant.DBAttribute.ID + " integer primary key autoincrement," +
            ProviderConstant.DBAttribute.TYPE + " integer," +
            ProviderConstant.DBAttribute.TIME + " text," +
            ProviderConstant.DBAttribute.NAME + " text," +
            ProviderConstant.DBAttribute.MAP_NAME + " text," +
            ProviderConstant.DBAttribute.TASK_NAME + " text," +
            ProviderConstant.DBAttribute.POINT_NAME + " text," +
            ProviderConstant.DBAttribute.CONTENT + " text," +
            ProviderConstant.DBAttribute.REMARKS + " text" +
            ")";

    public ProviderSQLite(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 初始版本
        int initVersion = 1;
        db.execSQL(IO_SQL);
        db.execSQL(WARING_SQL);
        db.execSQL(ERROR_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
