package com.projects.geloso.epidemics.actors;

import akka.actor.Props;
import com.projects.geloso.epidemics.messages.EpidemicMessage;

public class EpidemicPushActor extends EpidemicActor {

    public static Props props() {
        return Props.create(EpidemicPushActor.class);
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

    public static class EpidemicPushMessage extends EpidemicMessage {

    }
}