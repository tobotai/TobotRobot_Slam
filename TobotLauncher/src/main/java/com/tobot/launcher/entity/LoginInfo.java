package com.tobot.launcher.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author houdeming
 * @date 2020/5/22
 */
public class LoginInfo implements Parcelable {
    private String name;
    private String time;
    private int type;
    private String content;

    public LoginInfo() {
    }

    private LoginInfo(Parcel in) {
        name = in.readString();
        time = in.readString();
        type = in.readInt();
        content = in.readString();
    }

    public static final Creator<LoginInfo> CREATOR = new Creator<LoginInfo>() {
        @Override
        public LoginInfo createFromParcel(Parcel in) {
            return new LoginInfo(in);
        }

        @Override
        public LoginInfo[] newArray(int size) {
            return new LoginInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(time);
        dest.writeInt(type);
        dest.writeString(content);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
