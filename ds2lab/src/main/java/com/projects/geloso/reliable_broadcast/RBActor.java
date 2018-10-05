package com.projects.geloso.reliable_broadcast;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import com.projects.geloso.reliable_broadcast.messages.AppMessage;
import com.projects.geloso.reliable_broadcast.messages.Message;
import com.projects.geloso.reliable_broadcast.messages.StartMessage;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class RBActor extends UntypedActor {

    private final HashSet<Message> delivered;
    private Set<ActorRef> group;

    public RBActor() {
        group = new HashSet<>();
        delivered = new HashSet<>();
    }

    public static Props props() {
        return Props.create(RBActor.class);
    }

    public void onReceive(Object message) throws Exception {
        if (!(message instanceof Message)) {
            throw new IOException();
        }

        if (message instanceof AppMessage) {
            receiveAppMessage((AppMessage) message);
        } else if (message instanceof StartMessage) {
            receiveStartMessage((StartMessage) message);
        }
    }

    private void receiveStartMessage(StartMessage message) {
        group.addAll(message.group);
    }

    private void receiveAppMessage(AppMessage message) {
        if (!delivered.contains(message)) {
            group.stream()
                    .filter(a -> !a.equals(getSelf()))
                    .filter(a -> !a.equals(getSender()))
                    .forEach(a -> a.tell(message, getSelf()));

            myDeliver(message);
            delivered.add(message);
        }
    }

    private void myDeliver(AppMessage message) {
        // Do stuff
    }
}
