package com.projects.geloso.reliable_broadcast.messages;

import akka.actor.ActorRef;

import java.util.List;

public class StartMessage extends Message {
    public final List<ActorRef> group;

    public StartMessage(List<ActorRef> group) {
        this.group = group;
    }
}
