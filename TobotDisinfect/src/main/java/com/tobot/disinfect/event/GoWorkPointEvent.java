package com.tobot.disinfect.event;

import com.tobot.slam.data.LocationBean;

/**
 * @author houdeming
 * @date 2020/6/8
 */
public class GoWorkPointEvent {
    private LocationBean locationBean;

    public GoWorkPointEvent(LocationBean locationBean) {
        this.locationBean = locationBean;
    }

    public LocationBean getLocationBean() {
        return locationBean;
    }
}
