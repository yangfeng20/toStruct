package com.maple.plugs.log;

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