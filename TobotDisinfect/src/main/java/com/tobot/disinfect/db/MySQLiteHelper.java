package com.tobot.disinfect.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * @author houdeming
 * @date 2019/5/14
 */
public class MySQLiteHelper extends SQLiteOpenHelper {
    public static final String ID = "id";
    public static final String MAP_NAME = "mapName";
    public static final String LOCATION_NUMBER = "locationNumber";
    public static final String LOCATION_NAME_CHINA = "locationNameChina";
    public static final String LOCATION_NAME_ENGLISH = "locationNameEnglish";
    public static final String CONTENT = "content";
    public static final String X = "x";
    public static final String Y = "y";
    public static final String YAW = "yaw";
    public static final String TYPE = "type";
    public static final String SENSOR_STATUS = "sensorStatus";
    public static final String START_X = "startX";
    public static final String START_Y = "startY";
    public static final String END_X = "endX";
    public static final String END_Y = "endY";
    public static final String NAME = "name";
    public static final String MODE = "mode";
    public static final String TIME = "time";
    public static final String TASK_NAME = "taskName";
    public static final String LOOP_COUNT = "loopCount";
    public static final String RUN_MODE = "runMode";
    public static final String OBSTACLE_MODE = "obstacleMode";

    private static final String DATABASE_NAME = "disinfect.db";
    private static final int DATABASE_VERSION = 2;
    public static final String TABLE_LOCATION = "location";
    public static final String TABLE_TASK = "task";
    public static final String TABLE_TASK_DETAIL = "taskDetail";
    public static final String TABLE_WAIT_POINT = "waitPoint";
    public static final String TABLE_POINT_CONFIG = "pointConfig";
    public static final String TABLE_EXECUTE = "execute";

    private static final String LOCATION_CREATE = "create table " + TABLE_LOCATION +
            "("
            + ID + " integer primary key autoincrement,"
            + MAP_NAME + " text,"
            + LOCATION_NUMBER + " text,"
            + LOCATION_NAME_CHINA + " text,"
            + LOCATION_NAME_ENGLISH + " text,"
            + CONTENT + " text,"
            + X + " real,"
            + Y + " real,"
            + YAW + " real,"
            + TYPE + " integer,"
            + SENSOR_STATUS + " integer,"
            + START_X + " real,"
            + START_Y + " real,"
            + END_X + " real,"
            + END_Y + " real"
            + " );";

    private static final String TASK_SQL = "create table " + TABLE_TASK +
            "("
            + ID + " integer primary key autoincrement,"
            + TYPE + " integer,"
            + MAP_NAME + " text,"
            + NAME + " text not null,"
            + MODE + " integer,"
            + CONTENT + " text"
            + " );";

    private static final String TASK_DETAIL_SQL = "create table " + TABLE_TASK_DETAIL +
            "("
            + ID + " integer primary key autoincrement,"
            + NAME + " text not null,"
            + MAP_NAME + " text,"
            + LOCATION_NUMBER + " text,"
            + LOCATION_NAME_CHINA + " text,"
            + LOCATION_NAME_ENGLISH + " text,"
            + CONTENT + " text,"
            + X + " real,"
            + Y + " real,"
            + YAW + " real,"
            + TYPE + " integer,"
            + SENSOR_STATUS + " integer,"
            + START_X + " real,"
            + START_Y + " real,"
            + END_X + " real,"
            + END_Y + " real"
            + " );";

    private static final String WAIT_POINT_SQL = "create table " + TABLE_WAIT_POINT +
            "("
            + ID + " integer primary key autoincrement,"
            + MAP_NAME + " text,"
            + LOCATION_NUMBER + " text,"
            + LOCATION_NAME_CHINA + " text,"
            + LOCATION_NAME_ENGLISH + " text,"
            + CONTENT + " text,"
            + X + " real,"
            + Y + " real,"
            + YAW + " real,"
            + TYPE + " integer,"
            + SENSOR_STATUS + " integer,"
            + START_X + " real,"
            + START_Y + " real,"
            + END_X + " real,"
            + END_Y + " real"
            + " );";

    private static final String POINT_CONFIG_SQL = "create table " + TABLE_POINT_CONFIG +
            "("
            + ID + " integer primary key autoincrement,"
            + MAP_NAME + " text,"
            + LOCATION_NUMBER + " text not null,"
            + TIME + " integer,"
            + CONTENT + " text"
            + " );";

    private static final String EXECUTE_SQL = "create table " + TABLE_EXECUTE +
            "("
            + ID + " integer primary key autoincrement,"
            + MAP_NAME + " text,"
            + TASK_NAME + " text,"
            + RUN_MODE + " integer,"
            + OBSTACLE_MODE + " integer,"
            + LOOP_COUNT + " integer,"
            + LOCATION_NUMBER + " text,"
            + CONTENT + " text"
            + " );";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(LOCATION_CREATE);
        db.execSQL(TASK_SQL);
        db.execSQL(TASK_DETAIL_SQL);
        db.execSQL(WAIT_POINT_SQL);
        db.execSQL(POINT_CONFIG_SQL);

        int initVersion = 1;
        onUpgrade(db, initVersion, DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (int i = oldVersion; i < newVersion; i++) {
            if (i == 1) {
                db.execSQL(EXECUTE_SQL);
            }
        }
    }
}
