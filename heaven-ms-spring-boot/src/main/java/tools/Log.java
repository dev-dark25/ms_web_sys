package tools;

import constants.net.ServerConstants;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

public class Log {
    private static final Set<ILog> logs = new HashSet<>();

    static {
        System.setProperty("log4j.configurationFile", ServerConstants.RESOURCE_DIR + "xml/log4j2.xml");
    }

    private static final Logger logger = LogManager.getLogger(Log.class);

    public static void addLogInterface(ILog logInterface) {
        logs.add(logInterface);
    }

    public static void removeLogInterface(ILog logInterface) {
        logs.remove(logInterface);
    }

    public static void debug(Object msg) {
        if (Level.DEBUG.compareTo(logger.getLevel()) > -1) {
            return;
        }
        logger.debug(msg);
//        for (ILog iLog : logs) {
//            iLog.debug(msg);
//        }
    }

    public static void info(Object msg) {
        if (Level.INFO.compareTo(logger.getLevel()) > -1) {
            return;
        }
        logger.info(msg);
//        for (ILog iLog : logs) {
//            iSystem.out.println(msg);
//        }
    }

    public static void warn(Object msg) {
        if (Level.WARN.compareTo(logger.getLevel()) > -1) {
            return;
        }
        logger.warn(msg);
//        for (ILog iLog : logs) {
//            iLog.warn(msg);
//        }
    }

    public static void error(Object msg) {
        if (Level.ERROR.compareTo(logger.getLevel()) > -1) {
            return;
        }
        logger.error(msg);
//        for (ILog iLog : logs) {
//            iSystem.out.println(msg);
//        }
    }

    public static void error(Throwable t) {
        if (Level.ERROR.compareTo(logger.getLevel()) > -1) {
            return;
        }
        String msg = "�������г����쳣:";
        logger.error(msg, t);
//        for (ILog iLog : logs) {
//            iSystem.out.println(t);
//        }
    }

    public static void error(Object msg, Throwable t) {
        if (Level.ERROR.compareTo(logger.getLevel()) > -1) {
            return;
        }
        logger.error(msg, t);
//        for (ILog iLog : logs) {
//            iSystem.out.println(msg, t);
//        }
    }
}
