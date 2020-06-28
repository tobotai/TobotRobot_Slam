package com.tobot.disinfect.event;

/**
 * @author houdeming
 * @date 2019/4/1
 */
public class SwitchMapEvent {
    private String mapName;

    public SwitchMapEvent(String mapName) {
        this.mapName = mapName;
    }

    public String getMapName() {
        return mapName;
    }
}
