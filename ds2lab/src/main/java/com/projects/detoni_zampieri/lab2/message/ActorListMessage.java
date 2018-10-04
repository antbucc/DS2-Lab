package com.projects.detoni_zampieri.lab2.message;

import akka.actor.ActorRef;

import java.util.ArrayList;

public class ActorListMessage extends Message {
    public ActorListMessage(ArrayList<ActorRef> nodes) {
        this.nodes = nodes;
    }

    public ArrayList<ActorRef> nodes;
}
