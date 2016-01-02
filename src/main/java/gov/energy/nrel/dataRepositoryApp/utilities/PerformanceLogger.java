package gov.energy.nbc.car.utilities;

import org.apache.log4j.Logger;

import java.util.Date;

public class PerformanceLogger {

    protected Logger log;

    public String label;
    public long startTime;
    public long endTime;

    protected static boolean ENABLED = false;

    public static void disable() {
        ENABLED = false;
    }

    public static void enable() {
        ENABLED = true;
    }

    public static boolean isEnabled() {
        return ENABLED == false;
    }

    public PerformanceLogger(Logger log, String label) {

        if (ENABLED == false) return;

        this.log = log;

        this.label = label;

//        System.out.println("[BEGIN] " + label);
        startTime = new Date().getTime();

    }

    public void done() {

        if (ENABLED == false) return;

        endTime = new Date().getTime();
        log.info("[END]   " + label + "," +
                " duration: " + (endTime - startTime) + " ms (" + ((endTime - startTime) / 1000) + " sec) ");
    }


    public void done(String comment) {

        if (ENABLED == false) return;

        endTime = new Date().getTime();
        log.info("[END]   " + label + "," +
                " duration: " + (endTime - startTime) + " ms (" + ((endTime - startTime) / 1000) + " sec) " +
                comment);
    }
}
