package com.tobot.disinfect.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.text.TextUtils;

import com.tobot.disinfect.entity.ExecuteBean;
import com.tobot.disinfect.entity.TaskBean;
import com.tobot.slam.data.LocationBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author houdeming
 * @date 2019/5/14
 */
public class MyDBSource {
    private static final String TAG = MyDBSource.class.getSimpleName();
    private static MyDBSource sDBSource;
    private MySQLiteHelper mHelper;

    private MyDBSource(Context context) {
        mHelper = new MySQLiteHelper(context);
    }

    public static MyDBSource getInstance(Context context) {
        if (sDBSource == null) {
            synchronized (MyDBSource.class) {
                if (sDBSource == null) {
                    sDBSource = new MyDBSource(context.getApplicationContext());
                }
            }
        }
        return sDBSource;
    }

    public synchronized void insertLocation(LocationBean bean) {
        if (bean != null) {
            SQLiteDatabase database = mHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(MySQLiteHelper.MAP_NAME, bean.getMapName());
            values.put(MySQLiteHelper.LOCATION_NUMBER, bean.getLocationNumber());
            values.put(MySQLiteHelper.LOCATION_NAME_CHINA, bean.getLocationNameChina());
            values.put(MySQLiteHelper.LOCATION_NAME_ENGLISH, bean.getLocationNameEnglish());
            values.put(MySQLiteHelper.CONTENT, bean.getContent());
            values.put(MySQLiteHelper.X, bean.getX());
            values.put(MySQLiteHelper.Y, bean.getY());
            values.put(MySQLiteHelper.YAW, bean.getYaw());
            values.put(MySQLiteHelper.TYPE, bean.getType());
            values.put(MySQLiteHelper.SENSOR_STATUS, bean.getSensorStatus());
            values.put(MySQLiteHelper.START_X, bean.getStartX());
            values.put(MySQLiteHelper.START_Y, bean.getStartY());
            values.put(MySQLiteHelper.END_X, bean.getEndX());
            values.put(MySQLiteHelper.END_Y, bean.getEndY());
            database.insert(MySQLiteHelper.TABLE_LOCATION, null, values);
            mHelper.close();
        }
    }

    public synchronized void insertLocationList(List<LocationBean> dataList) {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        String sql = "insert into " + MySQLiteHelper.TABLE_LOCATION + "("
                + MySQLiteHelper.MAP_NAME + ","
                + MySQLiteHelper.LOCATION_NUMBER + ","
                + MySQLiteHelper.LOCATION_NAME_CHINA + ","
                + MySQLiteHelper.LOCATION_NAME_ENGLISH + ","
                + MySQLiteHelper.CONTENT + ","
                + MySQLiteHelper.X + ","
                + MySQLiteHelper.Y + ","
                + MySQLiteHelper.YAW + ","
                + MySQLiteHelper.TYPE + ","
                + MySQLiteHelper.SENSOR_STATUS + ","
                + MySQLiteHelper.START_X + ","
                + MySQLiteHelper.START_Y + ","
                + MySQLiteHelper.END_X + ","
                + MySQLiteHelper.END_Y + ") "
                + "values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        SQLiteStatement statement = database.compileStatement(sql);
        database.beginTransaction();
        try {
            for (LocationBean bean : dataList) {
                String mapName = bean.getMapName();
                // 为空的时候要做空处理，不然当空的时候读到的数据会是上一次的数据
                statement.bindString(1, !TextUtils.isEmpty(mapName) ? mapName : "");
                String number = bean.getLocationNumber();
                statement.bindString(2, !TextUtils.isEmpty(number) ? number : "");
                String nameChina = bean.getLocationNameChina();
                statement.bindString(3, !TextUtils.isEmpty(nameChina) ? nameChina : "");
                String nameEnglish = bean.getLocationNameEnglish();
                statement.bindString(4, !TextUtils.isEmpty(nameEnglish) ? nameEnglish : "");
                String content = bean.getContent();
                statement.bindString(5, !TextUtils.isEmpty(content) ? content : "");
                statement.bindDouble(6, bean.getX());
                statement.bindDouble(7, bean.getY());
                statement.bindDouble(8, bean.getYaw());
                statement.bindLong(9, bean.getType());
                statement.bindLong(10, bean.getSensorStatus());
                statement.bindDouble(11, bean.getStartX());
                statement.bindDouble(12, bean.getStartY());
                statement.bindDouble(13, bean.getEndX());
                statement.bindDouble(14, bean.getEndY());
                statement.executeInsert();
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            database.endTransaction();
            mHelper.close();
        }
    }

    public synchronized void updateLocation(String locationNumber, LocationBean bean) {
        if (bean != null) {
            SQLiteDatabase database = mHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(MySQLiteHelper.MAP_NAME, bean.getMapName());
            values.put(MySQLiteHelper.LOCATION_NUMBER, bean.getLocationNumber());
            values.put(MySQLiteHelper.LOCATION_NAME_CHINA, bean.getLocationNameChina());
            values.put(MySQLiteHelper.LOCATION_NAME_ENGLISH, bean.getLocationNameEnglish());
            values.put(MySQLiteHelper.CONTENT, bean.getContent());
            values.put(MySQLiteHelper.X, bean.getX());
            values.put(MySQLiteHelper.Y, bean.getY());
            values.put(MySQLiteHelper.YAW, bean.getYaw());
            values.put(MySQLiteHelper.TYPE, bean.getType());
            values.put(MySQLiteHelper.SENSOR_STATUS, bean.getSensorStatus());
            values.put(MySQLiteHelper.START_X, bean.getStartX());
            values.put(MySQLiteHelper.START_Y, bean.getStartY());
            values.put(MySQLiteHelper.END_X, bean.getEndX());
            values.put(MySQLiteHelper.END_Y, bean.getEndY());
            String whereClause = MySQLiteHelper.LOCATION_NUMBER + "=?";
            String[] whereArgs = {locationNumber};
            database.update(MySQLiteHelper.TABLE_LOCATION, values, whereClause, whereArgs);
            mHelper.close();
        }
    }

    public synchronized LocationBean queryLocation(String locationNumber) {
        return queryLocation(MySQLiteHelper.LOCATION_NUMBER, locationNumber);
    }

    public synchronized LocationBean queryLocationByChineseName(String locationName) {
        return queryLocation(MySQLiteHelper.LOCATION_NAME_CHINA, locationName);
    }

    public synchronized LocationBean queryLocationByEnglishName(String locationName) {
        return queryLocation(MySQLiteHelper.LOCATION_NAME_ENGLISH, locationName);
    }

    private LocationBean queryLocation(String selection, String selectionArg) {
        LocationBean bean = null;
        SQLiteDatabase database = mHelper.getWritableDatabase();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_LOCATION, null, selection + "=?",
                new String[]{selectionArg}, null, null, null);
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            bean = new LocationBean();
            bean.setMapName(cursor.getString(1));
            bean.setLocationNumber(cursor.getString(2));
            bean.setLocationNameChina(cursor.getString(3));
            bean.setLocationNameEnglish(cursor.getString(4));
            bean.setContent(cursor.getString(5));
            bean.setX(cursor.getFloat(6));
            bean.setY(cursor.getFloat(7));
            bean.setYaw(cursor.getFloat(8));
            bean.setType(cursor.getInt(9));
            bean.setSensorStatus(cursor.getInt(10));
            bean.setStartX(cursor.getFloat(11));
            bean.setStartY(cursor.getFloat(12));
            bean.setEndX(cursor.getFloat(13));
            bean.setEndY(cursor.getFloat(14));
        }
        cursor.close();
        mHelper.close();
        return bean;
    }

    public synchronized List<LocationBean> queryLocation() {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        List<LocationBean> dataList = new ArrayList<>();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_LOCATION, getColumns(), null, null, null, null, null);
        cursor.moveToFirst();
        LocationBean bean;
        while (!cursor.isAfterLast()) {
            bean = new LocationBean();
            bean.setMapName(cursor.getString(1));
            bean.setLocationNumber(cursor.getString(2));
            bean.setLocationNameChina(cursor.getString(3));
            bean.setLocationNameEnglish(cursor.getString(4));
            bean.setContent(cursor.getString(5));
            bean.setX(cursor.getFloat(6));
            bean.setY(cursor.getFloat(7));
            bean.setYaw(cursor.getFloat(8));
            bean.setType(cursor.getInt(9));
            bean.setSensorStatus(cursor.getInt(10));
            bean.setStartX(cursor.getFloat(11));
            bean.setStartY(cursor.getFloat(12));
            bean.setEndX(cursor.getFloat(13));
            bean.setEndY(cursor.getFloat(14));
            dataList.add(bean);
            cursor.moveToNext();
        }
        cursor.close();
        mHelper.close();
        return dataList;
    }

    private String[] getColumns() {
        return new String[]{
                MySQLiteHelper.ID,
                MySQLiteHelper.MAP_NAME,
                MySQLiteHelper.LOCATION_NUMBER,
                MySQLiteHelper.LOCATION_NAME_CHINA,
                MySQLiteHelper.LOCATION_NAME_ENGLISH,
                MySQLiteHelper.CONTENT,
                MySQLiteHelper.X,
                MySQLiteHelper.Y,
                MySQLiteHelper.YAW,
                MySQLiteHelper.TYPE,
                MySQLiteHelper.SENSOR_STATUS,
                MySQLiteHelper.START_X,
                MySQLiteHelper.START_Y,
                MySQLiteHelper.END_X,
                MySQLiteHelper.END_Y};
    }

    public synchronized void deleteLocation(String locationNumber) {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        // 如果数据库中有该数据返回1，否则返回0
        database.delete(MySQLiteHelper.TABLE_LOCATION, MySQLiteHelper.LOCATION_NUMBER + "=?", new String[]{locationNumber});
        mHelper.close();
    }

    public synchronized void deleteLocation() {
        delete(MySQLiteHelper.TABLE_LOCATION);
    }

    public synchronized void insertTask(TaskBean bean) {
        if (bean != null) {
            SQLiteDatabase database = mHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(MySQLiteHelper.TYPE, bean.getType());
            values.put(MySQLiteHelper.MAP_NAME, bean.getMapName());
            values.put(MySQLiteHelper.NAME, bean.getName());
            values.put(MySQLiteHelper.MODE, bean.getMode());
            values.put(MySQLiteHelper.CONTENT, bean.getContent());
            database.insert(MySQLiteHelper.TABLE_TASK, null, values);
            mHelper.close();
        }
    }

    public synchronized void deleteTask(String mapNum, String taskName) {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        String whereClause = MySQLiteHelper.MAP_NAME + "=? and " + MySQLiteHelper.NAME + "=?";
        database.delete(MySQLiteHelper.TABLE_TASK, whereClause, new String[]{mapNum, taskName});
        mHelper.close();
    }

    public synchronized void deleteTask(String mapNum) {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        String whereClause = MySQLiteHelper.MAP_NAME + "=?";
        database.delete(MySQLiteHelper.TABLE_TASK, whereClause, new String[]{mapNum});
        mHelper.close();
    }

    public synchronized void deleteTask() {
        delete(MySQLiteHelper.TABLE_TASK);
    }

    public synchronized TaskBean queryTask(String mapNum, String taskName) {
        TaskBean bean = null;
        SQLiteDatabase database = mHelper.getWritableDatabase();
        String selection = MySQLiteHelper.MAP_NAME + "=? and " + MySQLiteHelper.NAME + "=?";
        Cursor cursor = database.query(MySQLiteHelper.TABLE_TASK, null, selection,
                new String[]{mapNum, taskName}, null, null, null);
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            bean = new TaskBean();
            bean.setType(cursor.getInt(1));
            bean.setMapName(cursor.getString(2));
            bean.setName(cursor.getString(3));
            bean.setMode(cursor.getInt(4));
            bean.setContent(cursor.getString(5));
        }
        cursor.close();
        mHelper.close();
        return bean;
    }

    public synchronized List<TaskBean> queryTask(String mapNum) {
        List<TaskBean> dataList = new ArrayList<>();
        SQLiteDatabase database = mHelper.getWritableDatabase();
        String orderBy = MySQLiteHelper.ID + " desc";
        Cursor cursor = database.query(MySQLiteHelper.TABLE_TASK, null, MySQLiteHelper.MAP_NAME + "=?",
                new String[]{mapNum}, null, null, orderBy);
        cursor.moveToFirst();
        TaskBean bean;
        while (!cursor.isAfterLast()) {
            bean = new TaskBean();
            bean.setType(cursor.getInt(1));
            bean.setMapName(cursor.getString(2));
            bean.setName(cursor.getString(3));
            bean.setMode(cursor.getInt(4));
            bean.setContent(cursor.getString(5));
            dataList.add(bean);
            cursor.moveToNext();
        }
        cursor.close();
        mHelper.close();
        return dataList;
    }

    public synchronized List<LocationBean> queryTaskDetail(String mapNum, String taskName) {
        List<LocationBean> beanList = new ArrayList<>();
        SQLiteDatabase database = mHelper.getWritableDatabase();
        String selection = MySQLiteHelper.MAP_NAME + "=? and " + MySQLiteHelper.NAME + "=?";
        Cursor cursor = database.query(MySQLiteHelper.TABLE_TASK_DETAIL, null, selection,
                new String[]{mapNum, taskName}, null, null, null);
        cursor.moveToFirst();
        LocationBean bean;
        while (!cursor.isAfterLast()) {
            bean = new LocationBean();
            bean.setMapName(cursor.getString(2));
            bean.setLocationNumber(cursor.getString(3));
            bean.setLocationNameChina(cursor.getString(4));
            bean.setLocationNameEnglish(cursor.getString(5));
            bean.setContent(cursor.getString(6));
            bean.setX(cursor.getFloat(7));
            bean.setY(cursor.getFloat(8));
            bean.setYaw(cursor.getFloat(9));
            bean.setType(cursor.getInt(10));
            bean.setSensorStatus(cursor.getInt(11));
            bean.setStartX(cursor.getFloat(12));
            bean.setStartY(cursor.getFloat(13));
            bean.setEndX(cursor.getFloat(14));
            bean.setEndY(cursor.getFloat(15));
            beanList.add(bean);
            cursor.moveToNext();
        }
        cursor.close();
        mHelper.close();
        return beanList;
    }

    public synchronized void insertTaskDetail(String taskName, List<LocationBean> data) {
        if (data != null && !data.isEmpty()) {
            SQLiteDatabase database = mHelper.getWritableDatabase();
            String sql = "insert into " + MySQLiteHelper.TABLE_TASK_DETAIL + "("
                    + MySQLiteHelper.NAME + ","
                    + MySQLiteHelper.MAP_NAME + ","
                    + MySQLiteHelper.LOCATION_NUMBER + ","
                    + MySQLiteHelper.LOCATION_NAME_CHINA + ","
                    + MySQLiteHelper.LOCATION_NAME_ENGLISH + ","
                    + MySQLiteHelper.CONTENT + ","
                    + MySQLiteHelper.X + ","
                    + MySQLiteHelper.Y + ","
                    + MySQLiteHelper.YAW + ","
                    + MySQLiteHelper.TYPE + ","
                    + MySQLiteHelper.SENSOR_STATUS + ","
                    + MySQLiteHelper.START_X + ","
                    + MySQLiteHelper.START_Y + ","
                    + MySQLiteHelper.END_X + ","
                    + MySQLiteHelper.END_Y + ") "
                    + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            SQLiteStatement statement = database.compileStatement(sql);
            database.beginTransaction();
            try {
                for (LocationBean bean : data) {
                    statement.bindString(1, taskName);
                    String mapName = bean.getMapName();
                    // 为空的时候要做空处理，不然当空的时候读到的数据会是上一次的数据
                    statement.bindString(2, !TextUtils.isEmpty(mapName) ? mapName : "");
                    String number = bean.getLocationNumber();
                    statement.bindString(3, !TextUtils.isEmpty(number) ? number : "");
                    String nameChina = bean.getLocationNameChina();
                    statement.bindString(4, !TextUtils.isEmpty(nameChina) ? nameChina : "");
                    String nameEnglish = bean.getLocationNameEnglish();
                    statement.bindString(5, !TextUtils.isEmpty(nameEnglish) ? nameEnglish : "");
                    String content = bean.getContent();
                    statement.bindString(6, !TextUtils.isEmpty(content) ? content : "");
                    statement.bindDouble(7, bean.getX());
                    statement.bindDouble(8, bean.getY());
                    statement.bindDouble(9, bean.getYaw());
                    statement.bindLong(10, bean.getType());
                    statement.bindLong(11, bean.getSensorStatus());
                    statement.bindDouble(12, bean.getStartX());
                    statement.bindDouble(13, bean.getStartY());
                    statement.bindDouble(14, bean.getEndX());
                    statement.bindDouble(15, bean.getEndY());
                    statement.executeInsert();
                }
                database.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                database.endTransaction();
                mHelper.close();
            }
        }
    }

    public synchronized void deleteTaskDetail(String mapNum, String taskName) {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        String whereClause = MySQLiteHelper.MAP_NAME + "=? and " + MySQLiteHelper.NAME + "=?";
        database.delete(MySQLiteHelper.TABLE_TASK_DETAIL, whereClause, new String[]{mapNum, taskName});
        mHelper.close();
    }

    public synchronized void deleteTaskDetail(String mapNum) {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        String whereClause = MySQLiteHelper.MAP_NAME + "=?";
        database.delete(MySQLiteHelper.TABLE_TASK_DETAIL, whereClause, new String[]{mapNum});
        mHelper.close();
    }

    public synchronized void deleteTaskDetail() {
        delete(MySQLiteHelper.TABLE_TASK_DETAIL);
    }

    public synchronized List<LocationBean> queryWaitPoint(String mapNum) {
        List<LocationBean> beanList = new ArrayList<>();
        SQLiteDatabase database = mHelper.getWritableDatabase();
        String selection = MySQLiteHelper.MAP_NAME + "=?";
        Cursor cursor = database.query(MySQLiteHelper.TABLE_WAIT_POINT, null, selection,
                new String[]{mapNum}, null, null, null);
        cursor.moveToFirst();
        LocationBean bean;
        while (!cursor.isAfterLast()) {
            bean = new LocationBean();
            bean.setMapName(cursor.getString(1));
            bean.setLocationNumber(cursor.getString(2));
            bean.setLocationNameChina(cursor.getString(3));
            bean.setLocationNameEnglish(cursor.getString(4));
            bean.setContent(cursor.getString(5));
            bean.setX(cursor.getFloat(6));
            bean.setY(cursor.getFloat(7));
            bean.setYaw(cursor.getFloat(8));
            bean.setType(cursor.getInt(9));
            bean.setSensorStatus(cursor.getInt(10));
            bean.setStartX(cursor.getFloat(11));
            bean.setStartY(cursor.getFloat(12));
            bean.setEndX(cursor.getFloat(13));
            bean.setEndY(cursor.getFloat(14));
            beanList.add(bean);
            cursor.moveToNext();
        }
        cursor.close();
        mHelper.close();
        return beanList;
    }

    public synchronized void insertWaitPoint(List<LocationBean> data) {
        if (data != null && !data.isEmpty()) {
            SQLiteDatabase database = mHelper.getWritableDatabase();
            String sql = "insert into " + MySQLiteHelper.TABLE_WAIT_POINT + "("
                    + MySQLiteHelper.MAP_NAME + ","
                    + MySQLiteHelper.LOCATION_NUMBER + ","
                    + MySQLiteHelper.LOCATION_NAME_CHINA + ","
                    + MySQLiteHelper.LOCATION_NAME_ENGLISH + ","
                    + MySQLiteHelper.CONTENT + ","
                    + MySQLiteHelper.X + ","
                    + MySQLiteHelper.Y + ","
                    + MySQLiteHelper.YAW + ","
                    + MySQLiteHelper.TYPE + ","
                    + MySQLiteHelper.SENSOR_STATUS + ","
                    + MySQLiteHelper.START_X + ","
                    + MySQLiteHelper.START_Y + ","
                    + MySQLiteHelper.END_X + ","
                    + MySQLiteHelper.END_Y + ") "
                    + "values(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            SQLiteStatement statement = database.compileStatement(sql);
            database.beginTransaction();
            try {
                for (LocationBean bean : data) {
                    String mapName = bean.getMapName();
                    // 为空的时候要做空处理，不然当空的时候读到的数据会是上一次的数据
                    statement.bindString(1, !TextUtils.isEmpty(mapName) ? mapName : "");
                    String number = bean.getLocationNumber();
                    statement.bindString(2, !TextUtils.isEmpty(number) ? number : "");
                    String nameChina = bean.getLocationNameChina();
                    statement.bindString(3, !TextUtils.isEmpty(nameChina) ? nameChina : "");
                    String nameEnglish = bean.getLocationNameEnglish();
                    statement.bindString(4, !TextUtils.isEmpty(nameEnglish) ? nameEnglish : "");
                    String content = bean.getContent();
                    statement.bindString(5, !TextUtils.isEmpty(content) ? content : "");
                    statement.bindDouble(6, bean.getX());
                    statement.bindDouble(7, bean.getY());
                    statement.bindDouble(8, bean.getYaw());
                    statement.bindLong(9, bean.getType());
                    statement.bindLong(10, bean.getSensorStatus());
                    statement.bindDouble(11, bean.getStartX());
                    statement.bindDouble(12, bean.getStartY());
                    statement.bindDouble(13, bean.getEndX());
                    statement.bindDouble(14, bean.getEndY());
                    statement.executeInsert();
                }
                database.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                database.endTransaction();
                mHelper.close();
            }
        }
    }

    public synchronized void deleteWaitPoint(String mapNum) {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        String whereClause = MySQLiteHelper.MAP_NAME + "=?";
        database.delete(MySQLiteHelper.TABLE_WAIT_POINT, whereClause, new String[]{mapNum});
        mHelper.close();
    }

    public synchronized void deleteWaitPoint() {
        delete(MySQLiteHelper.TABLE_WAIT_POINT);
    }

    public synchronized LocationBean queryPointConfig(String mapNum, String locationNum) {
        LocationBean bean = null;
        SQLiteDatabase database = mHelper.getWritableDatabase();
        String selection = MySQLiteHelper.MAP_NAME + "=? and " + MySQLiteHelper.LOCATION_NUMBER + "=?";
        String[] selectionArgs = {mapNum, locationNum};
        Cursor cursor = database.query(MySQLiteHelper.TABLE_POINT_CONFIG, null, selection,
                selectionArgs, null, null, null);
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            bean = new LocationBean();
            bean.setMapName(cursor.getString(1));
            bean.setLocationNumber(cursor.getString(2));
            bean.setTime(cursor.getLong(3));
            bean.setContent(cursor.getString(4));
        }
        cursor.close();
        mHelper.close();
        return bean;
    }

    public synchronized void insertPointConfig(LocationBean bean) {
        if (bean != null) {
            SQLiteDatabase database = mHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(MySQLiteHelper.MAP_NAME, bean.getMapName());
            values.put(MySQLiteHelper.LOCATION_NUMBER, bean.getLocationNumber());
            values.put(MySQLiteHelper.TIME, bean.getTime());
            values.put(MySQLiteHelper.CONTENT, bean.getContent());
            database.insert(MySQLiteHelper.TABLE_POINT_CONFIG, null, values);
            mHelper.close();
        }
    }

    public synchronized void updatePointConfig(LocationBean bean) {
        if (bean != null) {
            SQLiteDatabase database = mHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(MySQLiteHelper.TIME, bean.getTime());
            values.put(MySQLiteHelper.CONTENT, bean.getContent());
            String whereClause = MySQLiteHelper.MAP_NAME + "=? and " + MySQLiteHelper.LOCATION_NUMBER + "=?";
            String[] whereArgs = {bean.getMapName(), bean.getLocationNumber()};
            database.update(MySQLiteHelper.TABLE_POINT_CONFIG, values, whereClause, whereArgs);
            mHelper.close();
        }
    }

    public synchronized void deletePointConfig(String mapNum, String locationNum) {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        String whereClause = MySQLiteHelper.MAP_NAME + "=? and " + MySQLiteHelper.LOCATION_NUMBER + "=?";
        String[] whereArgs = {mapNum, locationNum};
        database.delete(MySQLiteHelper.TABLE_POINT_CONFIG, whereClause, whereArgs);
        mHelper.close();
    }

    public synchronized void deletePointConfig(String mapNum) {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        String whereClause = MySQLiteHelper.MAP_NAME + "=?";
        String[] whereArgs = {mapNum};
        database.delete(MySQLiteHelper.TABLE_POINT_CONFIG, whereClause, whereArgs);
        mHelper.close();
    }

    public synchronized void deletePointConfig() {
        delete(MySQLiteHelper.TABLE_POINT_CONFIG);
    }

    public synchronized ExecuteBean queryExecute(String mapNum) {
        ExecuteBean bean = null;
        SQLiteDatabase database = mHelper.getWritableDatabase();
        String selection = MySQLiteHelper.MAP_NAME + "=?";
        String[] selectionArgs = {mapNum};
        Cursor cursor = database.query(MySQLiteHelper.TABLE_EXECUTE, null, selection,
                selectionArgs, null, null, null);
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            bean = new ExecuteBean();
            bean.setMapName(cursor.getString(1));
            bean.setTaskName(cursor.getString(2));
            bean.setRunMode(cursor.getInt(3));
            bean.setObstacleMode(cursor.getInt(4));
            bean.setLoopCount(cursor.getInt(5));
            bean.setLocationNum(cursor.getString(6));
            bean.setContent(cursor.getString(7));
        }
        cursor.close();
        mHelper.close();
        return bean;
    }

    public synchronized void deleteExecute(String mapNum) {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        String whereClause = MySQLiteHelper.MAP_NAME + "=?";
        String[] whereArgs = {mapNum};
        database.delete(MySQLiteHelper.TABLE_EXECUTE, whereClause, whereArgs);
        mHelper.close();
    }

    public synchronized void insertExecute(ExecuteBean bean) {
        if (bean != null) {
            SQLiteDatabase database = mHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(MySQLiteHelper.MAP_NAME, bean.getMapName());
            values.put(MySQLiteHelper.TASK_NAME, bean.getTaskName());
            values.put(MySQLiteHelper.RUN_MODE, bean.getRunMode());
            values.put(MySQLiteHelper.OBSTACLE_MODE, bean.getObstacleMode());
            values.put(MySQLiteHelper.LOOP_COUNT, bean.getLoopCount());
            values.put(MySQLiteHelper.LOCATION_NUMBER, bean.getLocationNum());
            values.put(MySQLiteHelper.CONTENT, bean.getContent());
            database.insert(MySQLiteHelper.TABLE_EXECUTE, null, values);
            mHelper.close();
        }
    }

    private void delete(String tab) {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        database.delete(tab, null, null);
        resetId(database, tab);
        mHelper.close();
    }

    private void resetId(String tableName) {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        resetId(database, tableName);
        mHelper.close();
    }

    private void resetId(SQLiteDatabase database, String tableName) {
        try {
            // sqlite_sequence 系统自生成的序列表，用来记录id 增长
            String sql = "UPDATE sqlite_sequence SET seq = 0 WHERE name =" + "'" + tableName + "'";
            database.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
