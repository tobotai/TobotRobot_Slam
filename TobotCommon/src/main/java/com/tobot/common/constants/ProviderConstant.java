package com.tobot.common.constants;

/**
 * @author houdeming
 * @date 2018/7/17
 */
public class ProviderConstant {
    public static final String PROVIDER_AUTHOR = "com.tobot.provider";

    /**
     * 数据库表
     */
    public static final class DBTable {
        public static final String IO = "io";
        public static final String WARING = "waring";
        public static final String ERROR = "error";
    }

    /**
     * 数据库表属性
     */
    public static final class DBAttribute {
        public static final String ID = "id";
        public static final String TYPE = "type";
        public static final String IO_ID = "io_id";
        public static final String NAME = "name";
        public static final String CONTENT = "content";
        public static final String TIME = "time";
        public static final String MAP_NAME = "map_name";
        public static final String TASK_NAME = "task_name";
        public static final String POINT_NAME = "point_name";
        public static final String REMARKS = "remarks";
    }

    /**
     * 路径
     */
    public static final class ProviderPath {
        public static final String IO = "io";
        public static final String WARING = "waring";
        public static final String ERROR = "error";
    }

    /**
     * URI
     */
    public static final class ProviderUri {
        public static final String IO = "content://" + PROVIDER_AUTHOR + "/" + ProviderPath.IO;
        public static final String WARING = "content://" + PROVIDER_AUTHOR + "/" + ProviderPath.WARING;
        public static final String ERROR = "content://" + PROVIDER_AUTHOR + "/" + ProviderPath.ERROR;
    }
}
