package com.projects.detoni_zampieri.adaptiveGossip;

import java.util.concurrent.atomic.AtomicLong;

public class GlobalClock {

    private GlobalClock() {
        this.clock = new AtomicLong();
    }

    public long getAndIncrement()
    {
        return this.clock.getAndIncrement();
    }

    private AtomicLong clock;
    private static GlobalClock instance;

    public static GlobalClock getClock()
    {
        if (instance == null)
        {
            instance = new GlobalClock();
        }
        return instance;
    }


}
