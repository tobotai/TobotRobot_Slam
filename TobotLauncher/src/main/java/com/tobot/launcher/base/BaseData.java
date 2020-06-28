package com.tobot.launcher.base;

import android.content.Context;
import android.text.TextUtils;

import com.tobot.common.util.SharedPreferencesUtils;

/**
 * @author houdeming
 * @date 2018/7/30
 */
public class BaseData {
    /**
     * 默认管理员密码
     */
    private static final String DEFAULT_ADMIN_PWD = "88888888";
    private static final String PWD_KEY = "pwd_key";
    private static final String IO_KEY = "io_key";
    private String mDeviceId;
    private int mUserIdentityId;
    private String mUserName;
    private String mAdminPwd;
    private int mSelectIoId;

    private BaseData() {
    }

    private static class BaseDataHolder {
        private static final BaseData INSTANCE = new BaseData();
    }

    public static BaseData getInstance() {
        return BaseDataHolder.INSTANCE;
    }

    public void setDeviceId(String deviceId) {
        mDeviceId = deviceId;
    }

    public String getDeviceId() {
        return mDeviceId;
    }

    public int getUserIdentityId() {
        return mUserIdentityId;
    }

    public void setUserIdentityId(int userIdentityId) {
        this.mUserIdentityId = userIdentityId;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        this.mUserName = userName;
    }

    public void setAdminPwd(Context context, String adminPwd) {
        mAdminPwd = adminPwd;
        SharedPreferencesUtils.getInstance(context).putString(PWD_KEY, adminPwd);
    }

    public String getAdminPwd(Context context) {
        if (TextUtils.isEmpty(mAdminPwd)) {
            mAdminPwd = SharedPreferencesUtils.getInstance(context).getString(PWD_KEY, DEFAULT_ADMIN_PWD);
        }
        return mAdminPwd;
    }

    public void setSelectIoId(Context context, int ioId) {
        mSelectIoId = ioId;
        SharedPreferencesUtils.getInstance(context).putInt(IO_KEY, ioId);
    }

    public int getSelectIoId(Context context) {
        if (mSelectIoId == 0) {
            mSelectIoId = SharedPreferencesUtils.getInstance(context).getInt(IO_KEY, 0);
        }
        return mSelectIoId;
    }
}
