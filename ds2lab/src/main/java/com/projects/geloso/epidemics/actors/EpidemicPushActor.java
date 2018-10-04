package com.projects.geloso.epidemics.actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.projects.geloso.epidemics.messages.EpidemicMessage;
import com.projects.geloso.epidemics.messages.EpidemicPushMessage;

public class EpidemicPushActor extends EpidemicActor {

    private final LoggingAdapter logger = Logging.getLogger(this);

    public static Props props() {
        return Props.create(EpidemicPushActor.class);
    }

    @Override
    protected void onEpidemicTimeoutImpl() {
        logger.debug("Timeout! Sending PUSH message with local value...");
        final ActorRef process = getRandomProcess();
        process.tell(new EpidemicPushMessage(getValue()), getSelf());
    }

    @Override
    protected void onEpidemicReceiveImpl(EpidemicMessage message) {
        if (getValue().getTimestamp() < message.getValue().getTimestamp()) {
            logger.debug("Received message with timestamp {}, updating local value...", message.getValue().getTimestamp());
            setValue(message.getValue().clone());
        }
    }

    @Override
    protected void valueSyncedImpl() {
    }

}