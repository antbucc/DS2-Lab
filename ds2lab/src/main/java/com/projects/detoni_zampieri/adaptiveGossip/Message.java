package com.projects.detoni_zampieri.adaptiveGossip;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Message implements Serializable {

    public Message(List<Event> events) {
        this.age = 0;
        this.events = new ArrayList<>(events);
    }

    public int age;
    public List<Event> events;
}
