package com.tobot.disinfect.module.set.system;

/**
 * @author houdeming
 * @date 2020/5/29
 */
public class AppBean {
    private String appName;
    private int appCode;
    private String appVersion;

    public AppBean() {
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public int getAppCode() {
        return appCode;
    }

    public void setAppCode(int appCode) {
        this.appCode = appCode;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }
}
