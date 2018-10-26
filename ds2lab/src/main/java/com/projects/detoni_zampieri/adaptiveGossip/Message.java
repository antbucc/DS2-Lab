package com.projects.detoni_zampieri.adaptiveGossip;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Message implements Serializable {

    public Message(List<Event> events, int age, int minBuffer) {
        this.age = age;
        this.minBuffer = minBuffer;
        this.events = new ArrayList<>(events);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {

        Message tmp = new Message(new ArrayList<>(), 0, 0);
        tmp.age = this.age;
        tmp.minBuffer = this.minBuffer;

        tmp.events = new ArrayList<Event>();
        for (Event e : this.events)
        {
            tmp.events.add((Event)e.clone());
        }

        return tmp;
    }

    public int age;
    public List<Event> events;
    public int minBuffer;
}
