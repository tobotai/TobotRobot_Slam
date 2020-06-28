package com.tobot.disinfect.event;

import com.tobot.slam.data.LocationBean;

/**
 * @author houdeming
 * @date 2019/3/18
 */
public class NavigateEvent {
    private LocationBean locationBean;

    public NavigateEvent(LocationBean locationBean) {
        this.locationBean = locationBean;
    }

    public LocationBean getLocationBean() {
        return locationBean;
    }
}
