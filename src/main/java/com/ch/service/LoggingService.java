package com.ch.service;

import com.ch.configuration.FluentLoggingConfiguration;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.fluentd.logger.FluentLogger;

/**
 * Created by elliott.jenkins on 19/05/2016.
 */
@SuppressWarnings("PMD")
public class LoggingService {

    public static String tag;
    private static boolean isFluentLoggingOn;
    private static FluentLogger fluentLogger;

    /**
     * Constructor for creating a logging service instance.
     *
     * @param configuration fluent logging configuration object.
     */
    public static void setFluentLogging(FluentLoggingConfiguration configuration) {
        isFluentLoggingOn = true;
        fluentLogger = FluentLogger.getLogger(null, configuration.getFluentHost(),
            configuration.getFluentPort());
        tag = configuration.getTag();
    }

    /**
     * Logs key/value pair as JSON into FluentD instance using the tag as a matcher, if FluentLogger is off the
     * object value is logged via log4j
     *
     * @param tag   string which is used as matcher category in FluentD
     * @param level used as a JSON key in FluentD
     * @param value the object to be logged.
     */
    public static void log(String tag, LoggingLevel level, Object value, Class clazz) {
        if (isFluentLoggingOn) {
            fluentLogger.log(tag, "message", level.toString() + " " + value.toString());
        } else {
            Logger log = LogManager.getLogger(clazz);
            logType(level, log, value);
        }
    }

    /**
     * Ensures correct log type is recorded into log4j.
     *
     * @param level the level at which to log
     * @param log   the log object
     * @param value the value to log
     */
    private static void logType(LoggingLevel level, Logger log, Object value) {

        switch (level) {
            case ERROR:
                log.error(value);
                break;
            case DEBUG:
                log.debug(value);
                break;
            default:
                log.info(value);
        }
    }

    public enum LoggingLevel {
        INFO, DEBUG, ERROR
    }
}
