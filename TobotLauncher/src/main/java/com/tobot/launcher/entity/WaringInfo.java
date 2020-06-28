package com.tobot.launcher.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author houdeming
 * @date 2020/5/21
 */
public class WaringInfo implements Parcelable {
    private String time;
    private String mapName;
    private String taskName;
    private String pointName;
    private String content;

    public WaringInfo() {
    }

    protected WaringInfo(Parcel in) {
        time = in.readString();
        mapName = in.readString();
        taskName = in.readString();
        pointName = in.readString();
        content = in.readString();
    }

    public static final Creator<WaringInfo> CREATOR = new Creator<WaringInfo>() {
        @Override
        public WaringInfo createFromParcel(Parcel in) {
            return new WaringInfo(in);
        }

        @Override
        public WaringInfo[] newArray(int size) {
            return new WaringInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(time);
        dest.writeString(mapName);
        dest.writeString(taskName);
        dest.writeString(pointName);
        dest.writeString(content);
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getMapName() {
        return mapName;
    }

    public void setMapName(String mapName) {
        this.mapName = mapName;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getPointName() {
        return pointName;
    }

    public void setPointName(String pointName) {
        this.pointName = pointName;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
