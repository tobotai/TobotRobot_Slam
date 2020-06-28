package com.tobot.launcher.module.mode;

/**
 * @author houdeming
 * @date 2018/4/18
 */
public class ModeParam {
    private String action;
    private int type;
    private String content;
    private boolean flag;

    private ModeParam(Builder builder) {
        this.action = builder.action;
        this.type = builder.type;
        this.content = builder.content;
        this.flag = builder.flag;
    }

    public String getAction() {
        return action;
    }

    public int getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public boolean isFlag() {
        return flag;
    }

    public static class Builder {
        private String action;
        private int type;
        private String content;
        private boolean flag;

        public Builder action(String action) {
            this.action = action;
            return this;
        }

        public Builder type(int type) {
            this.type = type;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder flag(boolean flag) {
            this.flag = flag;
            return this;
        }

        public ModeParam build() {
            return new ModeParam(this);
        }
    }
}
