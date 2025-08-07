package constants.ui;

import java.util.ArrayList;
import java.util.List;

public enum ConfigEnum {
    WORLD("world", "大区"),
    DATABASE("database", "数据库"),
    SERVER("server", "服务"),
    DEBUG("debug", "调试"),
    GAME("game", "游戏内容"),

    UNDEFINED("undefined", "未知");

    private final String type;
    private final String desc;

    ConfigEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

    public static ConfigEnum of(String type) {
        for (ConfigEnum value : values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }
        return UNDEFINED;
    }

    public static String ofType(String type) {
        for (ConfigEnum value : values()) {
            if (value.getType().equals(type)) {
                return value.desc;
            }
        }
        return UNDEFINED.desc;
    }

    public static List<String> getDescList() {
        ConfigEnum[] values = values();
        List<String> descList = new ArrayList<>(values.length);
        for (ConfigEnum value : values) {
            descList.add(value.desc);
        }
        return descList;
    }
}
