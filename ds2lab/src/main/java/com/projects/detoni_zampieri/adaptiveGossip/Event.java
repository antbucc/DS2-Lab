package com.projects.detoni_zampieri.adaptiveGossip;

import java.util.UUID;

public class Event{
	
	public int age;
	public UUID id;

	public  Event() {
		this.age = 0;
		this.id = UUID.random(UUID);
	}

	public void incrementAge()
	{
		this.age++;
	}

	@Override
	public boolean equals(Object o)
	{
		if(o instanceof Event){
			return this.id.equals(((Event)o).id);
		}
		return false;
	}

	@Override
	public int compareTo(Object o){
		if(o instanceof Event){
			return this.id.compareTo(((Event)o).id);
		}
		// never used (hopefully)
		return -1;
	}
}
