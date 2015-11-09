package gov.energy.nbc.car.utilities;

import org.apache.log4j.Logger;

import java.util.Date;

public class PerformanceLogger {

    protected Logger log;

    public String label;
    public long startTime;
    public long endTime;

    public boolean DISABLED = true;

    public PerformanceLogger(Logger log, String label) {

        if (DISABLED) return;

        this.log = log;

        this.label = label;

//        System.out.println("[BEGIN] " + label);
        startTime = new Date().getTime();

    }

    public PerformanceLogger(Logger log, String label, boolean force) {

        this.log = log;

        if (force) DISABLED = false;
        if (DISABLED) return;

        this.label = label;

//        log.info("[BEGIN] " + label);
        startTime = new Date().getTime();

    }

    public void done() {

        if (DISABLED) return;

        endTime = new Date().getTime();
        log.info("[END]   " + label + "," +
                " duration: " + (endTime - startTime) + " miliseconds (" + ((endTime - startTime) / 1000) + " seconds) ");
    }


    public void done(String comment) {

        if (DISABLED) return;

        endTime = new Date().getTime();
        log.info("[END]   " + label + "," +
                " duration: " + (endTime - startTime) + " miliseconds (" + ((endTime - startTime) / 1000) + " seconds) " +
                comment);
    }
}
