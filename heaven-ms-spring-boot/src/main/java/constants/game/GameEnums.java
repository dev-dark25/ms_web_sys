package constants.game;

import config.CommonConfig;

public class GameEnums {
    public enum DebugLevel {
        SIMPLE("simple", "简单的debug信息"),
        MORE("more", "详细的debug信息"),
        FULL("full", "完整的debug信息");

        private final String level;
        private final String desc;

        DebugLevel(String level, String desc) {
            this.level = level;
            this.desc = desc;
        }

        public String getLevel() {
            return level;
        }

        public String getDesc() {
            return desc;
        }

        public static DebugLevel of(String level) {
            for (DebugLevel value : values()) {
                if (value.level.equals(level)) {
                    return value;
                }
            }
            // 默认simple
            return SIMPLE;
        }

        public static boolean isSimple() {
            return CommonConfig.config.server.useDebug;
        }

        public static boolean isMore() {
            return CommonConfig.config.server.useDebug && (MORE.level.equals(CommonConfig.config.server.debugLevel)
                    || FULL.level.equals(CommonConfig.config.server.debugLevel));
        }

        public static boolean isFull() {
            return CommonConfig.config.server.useDebug && FULL.level.equals(CommonConfig.config.server.debugLevel);
        }
    }
}
