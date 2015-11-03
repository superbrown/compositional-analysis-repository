package gov.energy.nbc.car.utilities;

import java.util.Date;

public class PerformanceLogger {

    public String label;
    public long startTime;
    public long endTime;

    public boolean DISABLED = true;

    public PerformanceLogger(String label) {

        if (DISABLED) return;

        this.label = label;

        System.out.println("\n[========] BEGIN " + label);
        startTime = new Date().getTime();

    }

    public void done() {

        if (DISABLED) return;

        endTime = new Date().getTime();
        System.out.println(("[========] END   " + label + "," +
                " duration: " + (endTime - startTime) / 1000) + " seconds");
    }
}
