package com.projects.geloso.epidemics.actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.projects.geloso.epidemics.messages.EpidemicMessage;
import com.projects.geloso.epidemics.messages.EpidemicPullMessage;

public class EpidemicPullActor extends EpidemicActor {

    private final LoggingAdapter logger = Logging.getLogger(this);

    public static Props props() {
        return Props.create(EpidemicPullActor.class);
    }

    @Override
    protected void onEpidemicTimeoutImpl() {
//        logger.debug("Timeout! Sending PULL message with local value...");
        final ActorRef process = getRandomProcess();
        EpidemicPullMessage epm = new EpidemicPullMessage(getValue(), EpidemicPullMessage.PullType.PULL);
        process.tell(epm, getSelf());
    }

    @Override
    protected void onEpidemicReceiveImpl(EpidemicMessage message) {
        EpidemicPullMessage epm = (EpidemicPullMessage) message;
        switch (epm.getType()) {
            case PULL:
                if (getValue().getTimestamp() > message.getValue().getTimestamp()) {
                    logger.debug("Received PULL message with timestamp {}, sending local value...",
                            message.getValue().getTimestamp());
                    EpidemicPullMessage reply = new EpidemicPullMessage(getValue(), EpidemicPullMessage.PullType.REPLY);
                    getSender().tell(reply, getSelf());
                }
                break;
            case REPLY:
                if (getValue().getTimestamp() < message.getValue().getTimestamp()) {
                    logger.debug("Received REPLY message with timestamp {}.",
                            message.getValue().getTimestamp());
                    setValue(message.getValue().clone());
                }
                break;
            case NONE:
            default:
//                logger.debug("Received NONE message with timestamp {}.",
//                        message.getValue().getTimestamp());
        }
    }

    @Override
    protected void valueSyncedImpl() {
    }

}