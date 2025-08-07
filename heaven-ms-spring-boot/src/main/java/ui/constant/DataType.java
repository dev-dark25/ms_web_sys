package ui.constant;

import cn.nap.utils.date.NapDtUtils;

public enum DataType {
    INTEGER("Integer", "整数"),
    STRING("String", "字符串"),
    BOOLEAN("Boolean", "是否"),
    LONG("Long", "长整数"),
    DOUBLE("Double", "小数"),
    FLOAT("Float", "单精度小数"),
    DATE("Date", "日期"),
    BYTE("Byte", "字节"),
    SHORT("Short", "短整数"),
    CHARACTER("Character", "单字符"),
    UNKNOWN("unknown", "未知");

    private final String type;
    private final String desc;

    DataType(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public static DataType ofType(String type) {
        for (DataType value : values()) {
            if (value.type.equals(type)) {
                return value;
            }
        }
        return UNKNOWN;
    }

    public static Object convertToDataTypeValue(String dataType, String value) {
        switch (dataType) {
            case "Integer":
                return Integer.valueOf(value);
            case "Boolean":
                return Boolean.valueOf(value);
            case "Long":
                return Long.valueOf(value);
            case "Double":
                return Double.valueOf(value);
            case "Float":
                return Float.valueOf(value);
            case "Date":
                return NapDtUtils.toDate(value);
            case "Byte":
                return Byte.valueOf(value);
            case "Short":
                return Short.valueOf(value);
            case "Character":
                return value.charAt(0);
            default:
                return value;
        }
    }

    public String getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

}
