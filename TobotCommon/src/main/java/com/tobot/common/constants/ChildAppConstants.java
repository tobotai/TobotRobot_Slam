package com.tobot.common.constants;

/**
 * @author houdeming
 * @date 2018/4/17
 */
public class ChildAppConstants {
    /**
     * 包名
     */
    public static class PackageName {
        public static final String DISINFECT = "com.tobot.disinfect";
        private static final String LAUNCHER = "com.tobot.launcher";
    }

    /**
     * 类名
     */
    public static class ClassName {
        public static final String DISINFECT = "com.tobot.disinfect.MainActivity";
    }

    public static class ServiceName {
        public static final String DISINFECT = "com.tobot.disinfect.DisinfectService";
    }

    public static String[] getAllPackage() {
        return new String[]{PackageName.DISINFECT, PackageName.LAUNCHER};
    }
}
