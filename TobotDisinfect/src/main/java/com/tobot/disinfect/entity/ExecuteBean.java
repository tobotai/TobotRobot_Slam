package com.tobot.disinfect.entity;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author houdeming
 * @date 2020/6/22
 */
public class ExecuteBean implements Parcelable {
    private String mapName;
    private String taskName;
    private int runMode;
    private int obstacleMode;
    private int loopCount;
    private String locationNum;
    private String content;

    public ExecuteBean() {
    }

    protected ExecuteBean(Parcel in) {
        mapName = in.readString();
        taskName = in.readString();
        runMode = in.readInt();
        obstacleMode = in.readInt();
        loopCount = in.readInt();
        locationNum = in.readString();
        content = in.readString();
    }

    public static final Creator<ExecuteBean> CREATOR = new Creator<ExecuteBean>() {
        @Override
        public ExecuteBean createFromParcel(Parcel in) {
            return new ExecuteBean(in);
        }

        @Override
        public ExecuteBean[] newArray(int size) {
            return new ExecuteBean[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mapName);
        dest.writeString(taskName);
        dest.writeInt(runMode);
        dest.writeInt(obstacleMode);
        dest.writeInt(loopCount);
        dest.writeString(locationNum);
        dest.writeString(content);
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

    public int getRunMode() {
        return runMode;
    }

    public void setRunMode(int runMode) {
        this.runMode = runMode;
    }

    public int getObstacleMode() {
        return obstacleMode;
    }

    public void setObstacleMode(int obstacleMode) {
        this.obstacleMode = obstacleMode;
    }

    public int getLoopCount() {
        return loopCount;
    }

    public void setLoopCount(int loopCount) {
        this.loopCount = loopCount;
    }

    public String getLocationNum() {
        return locationNum;
    }

    public void setLocationNum(String locationNum) {
        this.locationNum = locationNum;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
