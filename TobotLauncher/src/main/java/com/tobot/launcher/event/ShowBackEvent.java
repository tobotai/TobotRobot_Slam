package com.tobot.launcher.event;

/**
 * @author houdeming
 * @date 2020/6/17
 */
public class ShowBackEvent {
    private boolean show;

    public ShowBackEvent(boolean show) {
        this.show = show;
    }

    public boolean isShow() {
        return show;
    }
}
