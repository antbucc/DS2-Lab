package com.projects.geloso.epidemics.actors;

import akka.actor.Props;

public class EpidemicPullActor extends EpidemicActor {

    public static Props props() {
        return Props.create(EpidemicPullActor.class);
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

    public static class EpidemicPullMessage extends EpidemicMessage {

    }
}