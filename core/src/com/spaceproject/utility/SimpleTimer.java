package com.spaceproject.utility;

public class SimpleTimer {

    private long interval;
    private long lastEvent;

    //TODO: will break on game pause, time will still be considered passed.
    public SimpleTimer(long time) {
        this(time, false);
    }

    public SimpleTimer(long time, boolean setLastEventTime) {
        interval = time;
        if (setLastEventTime)
            lastEvent = System.currentTimeMillis();
    }

    public boolean canDoEvent() {
        return timeSinceLastEvent() >= interval;
    }

    public void reset() {
        lastEvent = System.currentTimeMillis();
    }

    public long getInterval() {
        return  interval;
    }

    public void setLastEvent(long time) {
        lastEvent = time;
    }

    public long timeSinceLastEvent() {
        return System.currentTimeMillis() - lastEvent;
    }

    public float ratio() {
        return Math.min((float)timeSinceLastEvent()/(float)interval, 1.0f);
    }

    public static void unpause(){
        //lastEvent--;
        //lastEvent + timePaused; ...
    }

    @Override
    public String toString() {
        return timeSinceLastEvent() + " (" + ratio() + ")";
    }
}
