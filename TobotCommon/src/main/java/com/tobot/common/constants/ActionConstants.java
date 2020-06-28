package com.tobot.common.constants;

/**
 * @author houdeming
 * @date 2018/5/24
 */
public class ActionConstants {
    /**
     * 检查升级的key
     */
    public static final String CHECK_UPGRADE_KEY = "check_upgrade_key";
    /**
     * 检查升级的广播
     */
    public static final String ACTION_CHECK_UPGRADE = "action_check_upgrade";
    /**
     * 网络检测升级的key
     */
    public static final String ACTION_UPGRADE_FROM_NET_KEY = "action_upgrade_from_net_key";
    /**
     * 检查升级广播的结果
     */
    public static final String ACTION_UPGRADE_RESULT = "action_upgrade_result";
    /**
     * 关闭APP的广播
     */
    public static final String ACTION_CLOSE_APP = "action_close_app";
    /**
     * 用来拼接名字的分割符
     */
    public static final String NAME_SPLIT = ",";
    public static final String CONTENT_KEY = "content_key";

    /**
     * 文件夹
     */
    public static final String DIRECTORY = "tobot";
    public static final String DIRECTORY_MAP = "map";
    /**
     * 地图文件格式
     */
    public static final String FILE_NAME_SUFFIX = ".stcm";

    /**
     * 左转
     */
    public static final int ACTION_TURN_LEFT = 101;
    /**
     * 右转
     */
    public static final int ACTION_TURN_RIGHT = 102;
    /**
     * 前进
     */
    public static final int ACTION_GO_FORWARD = 103;
    /**
     * 后退
     */
    public static final int ACTION_GO_BACK = 104;
    /**
     * 停止
     */
    public static final int ACTION_MOVE_STOP = 105;
    /**
     * 工作
     */
    public static final int ACTION_WORK = 106;
    /**
     * 充电
     */
    public static final int ACTION_GO_HOME = 107;

    /**
     * 自由运行
     */
    public static final int MODE_RUNNING = 0;
    /**
     * 机器部署
     */
    public static final int MODE_DEPLOY = 1;
    /**
     * 遇障绕行
     */
    public static final int MEET_OBSTACLE_AVOID = 0;
    /**
     * 遇障暂停
     */
    public static final int MEET_OBSTACLE_SUSPEND = 1;
}
