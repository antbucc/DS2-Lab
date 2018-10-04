package com.projects.geloso.epidemics.messages;

import akka.actor.ActorRef;

import java.util.Collections;
import java.util.List;

/**
 * The StartMessage from the main function will tell a process about the peers
 */
public class StartMessage {

    private final List<ActorRef> group;

    public StartMessage(List<ActorRef> group) {
        this.group = Collections.unmodifiableList(group);
    }

    public List<ActorRef> getGroup() {
        return group;
    }
}
