package com.projects.detoni_zampieri.adaptiveGossip;

import java.io.Serializable;

public class UpdateAgesAndGossipMessage implements Serializable {
    public UpdateAgesAndGossipMessage() {
        this.newEvent = new Event();
    }

    public Event newEvent;
}
