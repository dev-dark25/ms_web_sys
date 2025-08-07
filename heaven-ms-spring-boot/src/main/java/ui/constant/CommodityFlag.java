package ui.constant;

public enum CommodityFlag {
    ITEM_ID(1, "物品ID"),
    COUNT(1 << 1, "数量"),
    PRICE(1 << 2, "价格"),
    UNKNOWN_3(1 << 3, "未知3"),
    PRIORITY(1 << 4, "优先级"),
    PERIOD(1 << 5, "有效时间"),
    UNKNOWN_6(1 << 6, "未知6"),
    MESO(1 << 7, "金币"),
    UNKNOWN_8(1 << 8, "未知8"),
    GENDER(1 << 9, "性别"),
    ON_SALE(1 << 10, "是否销售"),
    UNKNOWN_11(1 << 11, "未知11"),
    UNKNOWN_12(1 << 12, "未知12"),
    UNKNOWN_13(1 << 13, "未知13"),
    UNKNOWN_14(1 << 14, "未知14"),
    UNKNOWN_15(1 << 15, "未知15"),
    UNKNOWN_16(1 << 16, "未知16"),
    UNKNOWN_17(1 << 17, "未知17"),
    UNKNOWN_18(1 << 18, "未知18"),
    UNKNOWN_19(1 << 19, "未知19"),
    UNKNOWN_20(1 << 20, "未知20"),
    UNKNOWN_21(1 << 21, "未知21"),
    UNKNOWN_22(1 << 22, "未知22"),
    UNKNOWN_23(1 << 23, "未知23"),
    PACK_SN(1 << 24, "礼包SN"),
    UNKNOWN_25(1 << 25, "未知25"),
    UNKNOWN_26(1 << 26, "未知26"),
    UNKNOWN_27(1 << 27, "未知27"),
    UNKNOWN_28(1 << 28, "未知28"),
    UNKNOWN_29(1 << 29, "未知29"),
    UNKNOWN_30(1 << 30, "最大只能30，到31就从超过int的最大值了");

    private final int flag;
    private final String desc;

    CommodityFlag(int flag, String desc) {
        this.flag = flag;
        this.desc = desc;
    }

    public int getFlag() {
        return flag;
    }

    public String getDesc() {
        return desc;
    }

    public static int getAvailableFlag() {
        int flag = 0;
        for (CommodityFlag value : values()) {
            if (value.toString().contains("UNKNOWN")) {
                continue;
            }
            // 金币、礼包暂不支持
            if (MESO.equals(value) || PACK_SN.equals(value)) {
                continue;
            }
            flag |= value.getFlag();
        }
        return flag;
    }
}
