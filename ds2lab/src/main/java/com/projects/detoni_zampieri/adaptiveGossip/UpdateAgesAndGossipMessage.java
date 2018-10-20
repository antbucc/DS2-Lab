package com.projects.detoni_zampieri.adaptiveGossip;

import java.io.Serializable;

public class UpdateAgesAndGossipMessage implements Serializable {
    public UpdateAgesAndGossipMessage() {
        this.newEvent = new Event(); //is this used anywhere?
    }

    public Event newEvent;
}
