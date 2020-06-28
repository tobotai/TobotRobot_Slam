package com.tobot.launcher.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author houdeming
 * @date 2020/5/21
 */
public class MySQLiteHelper extends SQLiteOpenHelper {
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String PWD = "pwd";
    public static final String PHONE = "phone";
    public static final String CREATE_TIME = "create_time";
    public static final String UPDATE_TIME = "update_time";
    public static final String TYPE = "type";
    public static final String CONTENT = "content";
    public static final String TIME = "time";

    private static final String DATABASE_NAME = "launcher_app.db";
    private static final int DATABASE_VERSION = 1;
    public static final String TABLE_OPERATOR = "operator";
    public static final String TABLE_LOGIN = "login";

    private static final String OPERATOR_SQL = "create table " + TABLE_OPERATOR +
            "("
            + ID + " integer primary key autoincrement,"
            + NAME + " text not null,"
            + PWD + " text not null,"
            + PHONE + " text,"
            + CREATE_TIME + " text,"
            + UPDATE_TIME + " text,"
            + TYPE + " integer,"
            + CONTENT + " text"
            + " );";

    private static final String LOGIN_SQL = "create table " + TABLE_LOGIN +
            "("
            + ID + " integer primary key autoincrement,"
            + NAME + " text not null,"
            + TIME + " text,"
            + TYPE + " integer,"
            + CONTENT + " text"
            + " );";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 初始版本
        int initVersion = 1;
        db.execSQL(OPERATOR_SQL);
        db.execSQL(LOGIN_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
