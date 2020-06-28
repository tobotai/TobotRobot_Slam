package com.tobot.launcher.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author houdeming
 * @date 2020/5/21
 */
public class OperatorBean implements Parcelable {
    private String name;
    private String pwd;
    private String phone;
    private String createTime;
    private String updateTime;
    private int type;
    private String content;

    public OperatorBean() {
    }

    private OperatorBean(Parcel in) {
        name = in.readString();
        pwd = in.readString();
        phone = in.readString();
        createTime = in.readString();
        updateTime = in.readString();
        type = in.readInt();
        content = in.readString();
    }

    public static final Creator<OperatorBean> CREATOR = new Creator<OperatorBean>() {
        @Override
        public OperatorBean createFromParcel(Parcel in) {
            return new OperatorBean(in);
        }

        @Override
        public OperatorBean[] newArray(int size) {
            return new OperatorBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(pwd);
        dest.writeString(phone);
        dest.writeString(createTime);
        dest.writeString(updateTime);
        dest.writeInt(type);
        dest.writeString(content);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
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
