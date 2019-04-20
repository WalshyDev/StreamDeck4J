package com.walshydev.streamdeck4j.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * This class serves as a LoggerFactory for SD4J's internals.
 * <br>It will either return a Logger from a SLF4J implementation via {@link org.slf4j.LoggerFactory} if present,
 * or an instance of a custom {@link SimpleLogger} (From slf4j-simple).
 * <p>
 * It also has the utility method {@link #getLazyString(LazyEvaluation)} which is used to lazily construct Strings for
 * Logging.
 */
public class SD4JLogger {

    /**
     * Marks whether or not a SLF4J <code>StaticLoggerBinder</code> was found. If false, SD4J will use its fallback
     * logger.
     * <br>This variable is initialized during static class initialization.
     */
    public static final boolean SLF4J_ENABLED;

    static {
        boolean tmp = false;
        try {
            Class.forName("org.slf4j.impl.StaticLoggerBinder");
            tmp = true;
        } catch (ClassNotFoundException e) {
            //prints warning of missing implementation
            LoggerFactory.getLogger(SD4JLogger.class);
        }
        SLF4J_ENABLED = tmp;
    }

    private static final Map<String, Logger> LOGS = new HashMap<>();

    private SD4JLogger() {
    }

    /**
     * Will get the {@link org.slf4j.Logger} with the given log-name or create and cache a fallback logger if there is
     * no SLF4J implementation present.
     * <p>
     * The fallback logger will be an instance of a slightly modified version of SLF4Js SimpleLogger.
     *
     * @param name The name of the Logger
     * @return Logger with given log name
     */
    public static Logger getLog(String name) {
        synchronized (LOGS) {
            if (SLF4J_ENABLED)
                return LoggerFactory.getLogger(name);
            return LOGS.computeIfAbsent(name, SimpleLogger::new);
        }
    }

    /**
     * Will get the {@link org.slf4j.Logger} for the given Class or create and cache a fallback logger if there is no
     * SLF4J implementation present.
     * <p>
     * The fallback logger will be an instance of a slightly modified version of SLF4Js SimpleLogger.
     *
     * @param clazz The class used for the Logger name
     * @return Logger for given Class
     */
    public static Logger getLog(Class<?> clazz) {
        synchronized (LOGS) {
            if (SLF4J_ENABLED)
                return LoggerFactory.getLogger(clazz);
            return LOGS.computeIfAbsent(clazz.getName(), (n) -> new SimpleLogger(clazz.getSimpleName()));
        }
    }

    /**
     * Utility function to enable logging of complex statements more efficiently (lazy).
     *
     * @param lazyLambda The Supplier used when evaluating the expression
     * @return An Object that can be passed to SLF4J's logging methods as lazy parameter
     */
    public static Object getLazyString(LazyEvaluation lazyLambda) {
        return new Object() {
            @Override
            public String toString() {
                try {
                    return lazyLambda.getString();
                } catch (Exception ex) {
                    StringWriter sw = new StringWriter();
                    ex.printStackTrace(new PrintWriter(sw));
                    return "Error while evaluating lazy String... " + sw.toString();
                }
            }
        };
    }

    /**
     * Functional interface used for {@link #getLazyString(LazyEvaluation)} to lazily construct a String.
     */
    @FunctionalInterface
    public interface LazyEvaluation {

        /**
         * This method is used by {@link #getLazyString(LazyEvaluation)} when SLF4J requests String construction.
         * <br>The String returned by this is used to construct the log message.
         *
         * @return The String for log message
         * @throws Exception To allow lazy evaluation of methods that might throw exceptions
         */
        String getString() throws Exception;
    }
}
