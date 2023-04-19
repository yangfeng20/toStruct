package com.maple.plugs.log;

/**
 * @author yangfeng
 * @date : 2023/3/14 14:19
 * desc:
 */

public class StructLog {

    private LogLeve logLeve;

    private StructLog() {
        logLeve = LogLeve.INFO;
    }

    public void setLogLeve(LogLeve logLeve) {
        this.logLeve = logLeve;
    }

    public void printStackTrace(Exception e) {
        if (LogLeve.DE_BUG.getCode() >= logLeve.getCode()) {
            e.printStackTrace();
        }
    }

    public void warn(String str) {
        if (LogLeve.WARN.getCode() >= logLeve.getCode()) {
            System.err.println(str);
        }
    }
    public void debug(String str) {
        if (LogLeve.DE_BUG.getCode() >= logLeve.getCode()) {
            System.out.println(str);
        }
    }

    public void info(String str) {
        if (LogLeve.INFO.getCode() >= logLeve.getCode()) {
            System.out.println(str);
        }
    }


    private static volatile StructLog instance = null;

    public static StructLog getLogger() {
        return getLogger(LogLeve.INFO);
    }

    public static StructLog getLogger(LogLeve logLeve) {
        synchronized (StructLog.class) {
            if (instance == null) {
                synchronized (StructLog.class) {
                    instance = new StructLog();
                    instance.setLogLeve(logLeve);
                }
            }
        }
        return instance;
    }

}
