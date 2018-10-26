package com.projects.detoni_zampieri.adaptiveGossip;

import java.util.UUID;

public class Event{
	
	public int age;
	public UUID id;

	public  Event() {
		this.age = 0;
		this.id = UUID.randomUUID();
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
	protected Object clone() throws CloneNotSupportedException {
		Event tmp = new Event();
		tmp.age = this.age;
		tmp.id = this.id;
		return tmp;
	}
}
