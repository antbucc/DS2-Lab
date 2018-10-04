package com.projects.detoni_zampieri.lab1.message;

import akka.actor.ActorRef;
import java.util.ArrayList;

public class NodeListMessage extends Message {

    public NodeListMessage(int id, ArrayList<ActorRef> nodes) {
        super(id);
        this.nodes = nodes;
    }

    public ArrayList<ActorRef> nodes;
}
