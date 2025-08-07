package constants.ui;

import cn.nap.utils.common.NapComUtils;

import java.util.ArrayList;
import java.util.List;

public enum DataTypeEnum {
    INTEGER("Integer", "整数"),
    LONG("Long", "长整数"),
    DOUBLE("Double", "小数"),
    BOOLEAN("Boolean", "是否"),
    STRING("String", "字符");

    private final String type;
    private final String desc;

    DataTypeEnum(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

    public static DataTypeEnum of(String type) {
        for (DataTypeEnum value : values()) {
            if (value.getType().equals(type)) {
                return value;
            }
        }
        return STRING;
    }

    public static List<String> getTypeList() {
        DataTypeEnum[] values = values();
        List<String> typeList = new ArrayList<>(values.length);
        for (DataTypeEnum value : values) {
            typeList.add(value.type);
        }
        return typeList;
    }

    public static List<String> getDescList() {
        DataTypeEnum[] values = values();
        List<String> descList = new ArrayList<>(values.length);
        for (DataTypeEnum value : values) {
            descList.add(value.desc);
        }
        return descList;
    }

    public static Object getValue(Object data, String dataType) {
        if (NapComUtils.isEmpty(data)) {
            return null;
        }
        DataTypeEnum typeEnum = of(dataType);
        // 让隐式类型变成显式类型
        switch (typeEnum) {
            case INTEGER:
                return Integer.parseInt(data.toString());
            case DOUBLE:
                return Double.parseDouble(data.toString());
            case BOOLEAN:
                return Boolean.parseBoolean(data.toString());
            case STRING:
            default:
                return String.valueOf(data);
        }
    }
}
