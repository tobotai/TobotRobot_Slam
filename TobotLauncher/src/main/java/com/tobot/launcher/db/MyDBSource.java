package com.tobot.launcher.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.tobot.launcher.entity.LoginInfo;
import com.tobot.launcher.entity.OperatorBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author houdeming
 * @date 2020/5/21
 */
public class MyDBSource {
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

    public synchronized int insertOperator(OperatorBean bean) {
        long insertId = -1;
        if (bean != null) {
            SQLiteDatabase database = mHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(MySQLiteHelper.NAME, bean.getName());
            values.put(MySQLiteHelper.PWD, bean.getPwd());
            values.put(MySQLiteHelper.PHONE, bean.getPhone());
            values.put(MySQLiteHelper.CREATE_TIME, bean.getCreateTime());
            values.put(MySQLiteHelper.UPDATE_TIME, bean.getUpdateTime());
            values.put(MySQLiteHelper.TYPE, bean.getType());
            values.put(MySQLiteHelper.CONTENT, bean.getContent());
            // 插入成功就返回记录的id，否则返回-1
            insertId = database.insert(MySQLiteHelper.TABLE_OPERATOR, null, values);
            mHelper.close();
        }
        return (int) insertId;
    }

    public synchronized int updateOperator(String name, String pwd, OperatorBean bean) {
        int updateId = -1;
        if (bean != null) {
            SQLiteDatabase database = mHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(MySQLiteHelper.NAME, bean.getName());
            values.put(MySQLiteHelper.PWD, bean.getPwd());
            values.put(MySQLiteHelper.PHONE, bean.getPhone());
            values.put(MySQLiteHelper.CREATE_TIME, bean.getCreateTime());
            values.put(MySQLiteHelper.UPDATE_TIME, bean.getUpdateTime());
            values.put(MySQLiteHelper.TYPE, bean.getType());
            values.put(MySQLiteHelper.CONTENT, bean.getContent());
            String whereClause = MySQLiteHelper.NAME + "=? and " + MySQLiteHelper.PWD + "=?";
            String[] whereArgs = {name, pwd};
            updateId = database.update(MySQLiteHelper.TABLE_OPERATOR, values, whereClause, whereArgs);
            mHelper.close();
        }
        return updateId;
    }

    public synchronized int deleteOperator(String name, String pwd) {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        String whereClause = MySQLiteHelper.NAME + "=? and " + MySQLiteHelper.PWD + "=?";
        String[] whereArgs = {name, pwd};
        int deleteId = database.delete(MySQLiteHelper.TABLE_OPERATOR, whereClause, whereArgs);
        mHelper.close();
        return deleteId;
    }

    public synchronized int deleteAllOperator() {
        return deleteAll(MySQLiteHelper.TABLE_OPERATOR);
    }

    public synchronized OperatorBean queryOperator(String name, String pwd) {
        OperatorBean bean = null;
        SQLiteDatabase database = mHelper.getWritableDatabase();
        String selection = MySQLiteHelper.NAME + "=? and " + MySQLiteHelper.PWD + "=?";
        String[] selectionArgs = {name, pwd};
        Cursor cursor = database.query(MySQLiteHelper.TABLE_OPERATOR, null, selection,
                selectionArgs, null, null, null);
        if (cursor.getCount() == 1) {
            cursor.moveToFirst();
            bean = new OperatorBean();
            bean.setName(cursor.getString(1));
            bean.setPwd(cursor.getString(2));
            bean.setPhone(cursor.getString(3));
            bean.setCreateTime(cursor.getString(4));
            bean.setUpdateTime(cursor.getString(5));
            bean.setType(cursor.getInt(6));
            bean.setContent(cursor.getString(7));
        }
        cursor.close();
        mHelper.close();
        return bean;
    }

    public synchronized List<OperatorBean> queryOperatorList() {
        String orderBy = MySQLiteHelper.ID + " desc";
        return queryOperatorList(null, null, orderBy);
    }

    public synchronized List<OperatorBean> queryOperatorList(int type) {
        String selection = MySQLiteHelper.TYPE + "=?";
        String[] selectionArgs = {String.valueOf(type)};
        String orderBy = MySQLiteHelper.ID + " desc";
        return queryOperatorList(selection, selectionArgs, orderBy);
    }

    public synchronized void resetOperatorId() {
        resetId(MySQLiteHelper.TABLE_OPERATOR);
    }

    private List<OperatorBean> queryOperatorList(String selection, String[] selectionArgs, String orderBy) {
        List<OperatorBean> dataList = new ArrayList<>();
        SQLiteDatabase database = mHelper.getWritableDatabase();
        Cursor cursor = database.query(MySQLiteHelper.TABLE_OPERATOR, null, selection, selectionArgs, null, null, orderBy);
        cursor.moveToFirst();
        OperatorBean bean;
        while (!cursor.isAfterLast()) {
            bean = new OperatorBean();
            bean.setName(cursor.getString(1));
            bean.setPwd(cursor.getString(2));
            bean.setPhone(cursor.getString(3));
            bean.setCreateTime(cursor.getString(4));
            bean.setUpdateTime(cursor.getString(5));
            bean.setType(cursor.getInt(6));
            bean.setContent(cursor.getString(7));
            dataList.add(bean);
            cursor.moveToNext();
        }
        cursor.close();
        mHelper.close();
        return dataList;
    }

    public synchronized int insertLoginInfo(LoginInfo info) {
        long insertId = -1;
        if (info != null) {
            SQLiteDatabase database = mHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(MySQLiteHelper.NAME, info.getName());
            values.put(MySQLiteHelper.TIME, info.getTime());
            values.put(MySQLiteHelper.TYPE, info.getType());
            values.put(MySQLiteHelper.CONTENT, info.getContent());
            insertId = database.insert(MySQLiteHelper.TABLE_LOGIN, null, values);
            mHelper.close();
        }
        return (int) insertId;
    }

    public synchronized List<LoginInfo> queryLoginInfoList() {
        List<LoginInfo> dataList = new ArrayList<>();
        SQLiteDatabase database = mHelper.getWritableDatabase();
        String orderBy = MySQLiteHelper.ID + " desc";
        Cursor cursor = database.query(MySQLiteHelper.TABLE_LOGIN, null, null, null, null, null, orderBy);
        cursor.moveToFirst();
        LoginInfo info;
        while (!cursor.isAfterLast()) {
            info = new LoginInfo();
            info.setName(cursor.getString(1));
            info.setTime(cursor.getString(2));
            info.setType(cursor.getInt(3));
            info.setContent(cursor.getString(4));
            dataList.add(info);
            cursor.moveToNext();
        }
        cursor.close();
        mHelper.close();
        return dataList;
    }

    public synchronized int deleteLoginInfo(LoginInfo info) {
        if (info == null) {
            return -1;
        }
        SQLiteDatabase database = mHelper.getWritableDatabase();
        String whereClause = MySQLiteHelper.NAME + "=? and " + MySQLiteHelper.TIME + "=? and " + MySQLiteHelper.TYPE + "=?";
        String[] whereArgs = {info.getName(), info.getTime(), String.valueOf(info.getType())};
        int deleteId = database.delete(MySQLiteHelper.TABLE_LOGIN, whereClause, whereArgs);
        mHelper.close();
        return deleteId;
    }

    public synchronized int deleteAllLoginInfo() {
        return deleteAll(MySQLiteHelper.TABLE_LOGIN);
    }

    public synchronized void resetLoginInfoId() {
        resetId(MySQLiteHelper.TABLE_LOGIN);
    }

    private int deleteAll(String tableName) {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        int deleteId = database.delete(tableName, null, null);
        resetId(database, tableName);
        mHelper.close();
        return deleteId;
    }

    public synchronized void clearAllTabData() {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        // 将所有表的自增ID列都归零
        String sql = "DELETE FROM sqlite_sequence";
        database.execSQL(sql);
        mHelper.close();
    }

    private void resetId(String tableName) {
        SQLiteDatabase database = mHelper.getWritableDatabase();
        resetId(database, tableName);
        mHelper.close();
    }

    private void resetId(SQLiteDatabase database, String tableName) {
        // sqlite_sequence 系统自生成的序列表，用来记录id 增长
        String sql = "UPDATE sqlite_sequence SET seq = 0 WHERE name =" + "'" + tableName + "'";
        database.execSQL(sql);
    }
}
