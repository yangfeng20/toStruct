package com.maple.plugs.log;

/**
 *
 *
 * @author maple
 * @date 2023/04/20
 */
public enum LogLeve {
        /**
         * 日志级别
         */
        DE_BUG("DEBUG", 1),
        INFO("INFO", 2),
        WARN("WARN", 3),
        ERROR("ERROR", 4),
        ;

        private final Integer code;
        private final String desc;

    public static LogLeve getByShortCode(String logLeve){
        for (LogLeve item : values()) {
            if (item.desc.equalsIgnoreCase(logLeve)) {
                return item;
            }
        }

        return WARN;
    }


        LogLeve(String desc, Integer code) {
            this.desc = desc;
            this.code = code;
        }

        public Integer getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }