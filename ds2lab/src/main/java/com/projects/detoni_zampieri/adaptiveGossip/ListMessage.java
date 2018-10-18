package com.projects.detoni_zampieri.adaptiveGossip;

import akka.actor.ActorRef;

import java.io.Serializable;
import java.util.List;

public class ListMessage implements Serializable {

    public ListMessage(List<ActorRef> nodes) {
        this.m_nodes = nodes;
    }

    public List<ActorRef> m_nodes;
}
