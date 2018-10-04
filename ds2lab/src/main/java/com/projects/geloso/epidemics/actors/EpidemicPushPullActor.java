package com.projects.geloso.epidemics.actors;

import akka.actor.Props;
import com.projects.geloso.epidemics.messages.EpidemicMessage;

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

}

