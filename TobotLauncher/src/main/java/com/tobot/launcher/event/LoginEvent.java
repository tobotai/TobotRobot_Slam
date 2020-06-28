package com.tobot.launcher.event;

/**
 * @author houdeming
 * @date 2020/4/3
 */
public class LoginEvent {
    private boolean login;
    private String userName;

    public LoginEvent(boolean login, String userName) {
        this.login = login;
        this.userName = userName;
    }

    public boolean isLogin() {
        return login;
    }

    public String getUserName() {
        return userName;
    }
}