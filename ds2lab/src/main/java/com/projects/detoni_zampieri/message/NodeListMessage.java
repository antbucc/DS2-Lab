package com.projects.detoni_zampieri.message;

import akka.actor.ActorRef;
import com.projects.detoni_zampieri.message.Message;

import java.util.ArrayList;

public class NodeListMessage extends Message {

    public NodeListMessage(int id, ArrayList<ActorRef> nodes) {
        super(id);
        this.nodes = nodes;
    }

    public ArrayList<ActorRef> nodes;
}
