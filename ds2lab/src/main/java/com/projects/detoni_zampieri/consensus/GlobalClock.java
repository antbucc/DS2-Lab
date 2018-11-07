package com.projects.detoni_zampieri.consensus;

public class GlobalClock {

	public static GlobalClock instance = null;
	private long start_time;
	public long delta;
	
	
	private GlobalClock()
	{
		start_time = System.currentTimeMillis();
		delta = 200;
	}
	
	public static GlobalClock getClock() {
		if(GlobalClock.instance==null) {
			GlobalClock.instance = new GlobalClock();
		}
		return GlobalClock.instance;
	}
	
	public int currentTick() {
		return (int)(System.currentTimeMillis()-start_time/delta);
	}
	
}
