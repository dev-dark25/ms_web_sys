package ui.constant;

public enum ConfigType {
    WORLD("world", "大区配置"),
    DATABASE("database", "数据库配置"),
    GAME("game", "游戏配置"),
    SERVER("server", "服务配置"),
    DEBUG("debug", "调试配置"),
    UNKNOWN("unknown", "未知配置");

    private final String type;
    private final String desc;

    ConfigType(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static ConfigType ofType(String type) {
        for (ConfigType value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return UNKNOWN;
    }

    public String getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }
}
