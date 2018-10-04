package com.projects.geloso.epidemics.actors;

import akka.actor.Props;

public class EpidemicPushPullActor extends EpidemicActor {

    public static Props props() {
        return Props.create(EpidemicPushPullActor.class);
    }

    @Override
    public Receive createReceive() {
        return null;
    }

    @Override
    protected void onEpidemicTimeoutImpl() {
    }

    @Override
    protected void onEpidemicReceiveImpl(EpidemicMessage message) {
    }

    @Override
    protected void valueSyncedImpl() {
    }

    public static class EpidemicPushPullMessage extends EpidemicMessage {
    }
}

