package com.projects.CeschiErcolani.ReliableMulticast;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;

public class ReliableBroadcast extends UntypedActor {

    Set<BroadcastMsg> sDelivered = null;
    List<ActorRef> lGroup = null;

    public ReliableBroadcast() {
        sDelivered = new HashSet<BroadcastMsg>();
    }

    public static Props props() {
        return Props.create(ReliableBroadcast.class);
    }

    void Deliver(BroadcastMsg mMsg) {
        System.out.println("Delivered RB msg.");
        if (!sDelivered.contains(mMsg)) {
            for (ActorRef arActor : lGroup) {
                if (arActor != getSelf() && arActor != getSender()) {
                    arActor.tell(mMsg, getSelf());
                }
            }
            Deliver(mMsg);
            sDelivered.add(mMsg);
        }
    }

    void SendRBroadcast(BroadcastMsg mMsg) {
        for (ActorRef arActor : lGroup) {
            if (arActor != getSelf()) {
                arActor.tell(mMsg, getSelf());
            }
        }
        Deliver(mMsg);
        sDelivered.add(mMsg);
        try {
            Thread.sleep(1000L);
            SendRBroadcast(mMsg);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void onReceive(Object msg) throws Exception {
        if (msg instanceof BroadcastMsg) {
            onBroadcastMsg((BroadcastMsg) msg);
        } else if (msg instanceof HelloMsg) {
            onHelloMsg((HelloMsg) msg);
        }
    }

    protected void onBroadcastMsg(BroadcastMsg mMsg) {

    }

    protected void onHelloMsg(HelloMsg mMsg) {
        lGroup = mMsg.getGroup();
    }

    public static class BroadcastMsg {

        String sText;

        BroadcastMsg(String sText) {
            this.sText = sText;
        }

        String getText() {
            return this.sText;
        }
    }

    public static class HelloMsg {

        final List<ActorRef> lGroup;

        public HelloMsg(List<ActorRef> lGroup) {
            this.lGroup = lGroup;
        }

        List<ActorRef> getGroup() {
            return this.lGroup;
        }
    }

}
