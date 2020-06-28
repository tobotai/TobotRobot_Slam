package com.tobot.launcher.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.tobot.common.constants.ProviderConstant;
import com.tobot.common.util.LogUtils;

/**
 * @author houdeming
 * @date 2018/7/17
 */
public class ProviderHelper extends ContentProvider {
    private static final String TAG = ProviderHelper.class.getSimpleName();
    private static final int CODE_IO = 1;
    private static final int CODE_WARING = 2;
    private static final int CODE_ERROR = 3;
    private static final UriMatcher uriMatcher;
    private ProviderSQLite helper;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(ProviderConstant.PROVIDER_AUTHOR, ProviderConstant.ProviderPath.IO, CODE_IO);
        uriMatcher.addURI(ProviderConstant.PROVIDER_AUTHOR, ProviderConstant.ProviderPath.WARING, CODE_WARING);
        uriMatcher.addURI(ProviderConstant.PROVIDER_AUTHOR, ProviderConstant.ProviderPath.ERROR, CODE_ERROR);
    }

    @Override
    public boolean onCreate() {
        helper = new ProviderSQLite(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        int code = uriMatcher.match(uri);
        LogUtils.i(TAG, "query() code=" + code);
        SQLiteDatabase database = helper.getWritableDatabase();
        Cursor query = null;
        switch (code) {
            case CODE_IO:
                query = database.query(ProviderConstant.DBTable.IO, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CODE_WARING:
                query = database.query(ProviderConstant.DBTable.WARING, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case CODE_ERROR:
                query = database.query(ProviderConstant.DBTable.ERROR, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                break;
        }
        return query;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        int code = uriMatcher.match(uri);
        LogUtils.i(TAG, "insert() code=" + code);
        SQLiteDatabase database = helper.getWritableDatabase();
        StringBuilder builder = new StringBuilder();
        long insert;
        Uri mUri = null;
        switch (code) {
            case CODE_IO:
                insert = database.insert(ProviderConstant.DBTable.IO, null, values);
                mUri = Uri.parse(builder.append(uri).append("/").append(insert).toString());
                break;
            case CODE_WARING:
                insert = database.insert(ProviderConstant.DBTable.WARING, null, values);
                mUri = Uri.parse(builder.append(uri).append("/").append(insert).toString());
                break;
            case CODE_ERROR:
                insert = database.insert(ProviderConstant.DBTable.ERROR, null, values);
                mUri = Uri.parse(builder.append(uri).append("/").append(insert).toString());
                break;
            default:
                break;
        }
        return mUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int code = uriMatcher.match(uri);
        LogUtils.i(TAG, "delete() code=" + code);
        SQLiteDatabase database = helper.getWritableDatabase();
        int delete = 0;
        switch (code) {
            case CODE_IO:
                delete = database.delete(ProviderConstant.DBTable.IO, selection, selectionArgs);
                break;
            case CODE_WARING:
                delete = database.delete(ProviderConstant.DBTable.WARING, selection, selectionArgs);
                break;
            case CODE_ERROR:
                delete = database.delete(ProviderConstant.DBTable.ERROR, selection, selectionArgs);
                break;
            default:
                break;
        }
        return delete;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int code = uriMatcher.match(uri);
        LogUtils.i(TAG, "update() code=" + code);
        SQLiteDatabase database = helper.getWritableDatabase();
        int update = 0;
        switch (code) {
            case CODE_IO:
                update = database.update(ProviderConstant.DBTable.IO, values, selection, selectionArgs);
                break;
            case CODE_WARING:
                update = database.update(ProviderConstant.DBTable.WARING, values, selection, selectionArgs);
                break;
            case CODE_ERROR:
                update = database.update(ProviderConstant.DBTable.ERROR, values, selection, selectionArgs);
                break;
            default:
                break;
        }
        return update;
    }
}
