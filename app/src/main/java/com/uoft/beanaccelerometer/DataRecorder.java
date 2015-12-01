package com.uoft.beanaccelerometer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jenna on 15-11-30.
 */
public class DataRecorder {


    private List<Float> xArray = new ArrayList<Float>();
    private List<Float> yArray = new ArrayList<Float>();
    private List<Float> zArray = new ArrayList<Float>();
    private List<Long> tArray = new ArrayList<Long>();

    private final Logger accelReadingLogger = new Logger("fileTest", ".csv",
            "TimeSinceStart(ns), x, y, z");

    private boolean isWriting;


    public void logAccelReadings(long t, float x, float y, float z) {
        // Add values to array (column)
        tArray.add(t);
        xArray.add(x);
        yArray.add(y);
        zArray.add(z);

        // Add values to arry (row)
        String[] array = new String[4];
        array[0] = String.valueOf(t);
        array[1] = String.valueOf(x);
        array[2] = String.valueOf(y);
        array[3] = String.valueOf(z);




        if (isWriting) {
            accelReadingLogger.printRow(array);
        }


    }

    public void enableLogger() {
        isWriting = true;
        accelReadingLogger.setEnabled(isWriting);
        accelReadingLogger.initPrintWriter();
    }

    public void disableLogger() {
        isWriting = false;
        accelReadingLogger.close();
    }

}
