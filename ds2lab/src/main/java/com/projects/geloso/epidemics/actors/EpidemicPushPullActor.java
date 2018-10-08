package com.projects.geloso.epidemics.actors;

import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.projects.geloso.epidemics.messages.EpidemicMessage;
import com.projects.geloso.epidemics.messages.EpidemicPullMessage;
import com.projects.geloso.epidemics.messages.EpidemicPushMessage;

public class EpidemicPushPullActor extends EpidemicActor {

    private final LoggingAdapter logger = Logging.getLogger(this);

    public static Props props() {
        return Props.create(EpidemicPushPullActor.class);
    }

    @Override
    public Receive createReceive() {
        return null;
    }

    @Override
    protected void onEpidemicTimeoutImpl() {
        logger.debug("Timeout! Sending PUSH_PULL message with local value...");
        getRandomProcess().tell(new EpidemicPushMessage(getValue()), getSelf());
    }

    @Override
    protected void onEpidemicReceiveImpl(EpidemicMessage message) {
        EpidemicPullMessage epm = (EpidemicPullMessage) message;
        switch (epm.getType()) {
            case PUSH_PULL:
                boolean myValueIsNewer = getValue().getTimestamp() > message.getValue().getTimestamp();
                boolean itsValueIsNewer = getValue().getTimestamp() < message.getValue().getTimestamp();
                if (myValueIsNewer) {
                    logger.debug("Received PUSH_PULL message with timestamp {}, sending local value...",
                            message.getValue().getTimestamp());
                    EpidemicPullMessage reply = new EpidemicPullMessage(getValue());
                    reply.setType(EpidemicPullMessage.PullType.REPLY);
                    getSender().tell(reply, getSelf());
                } else if (itsValueIsNewer) {
                    logger.debug("Received PUSH_PULL message with timestamp {}, updating local value...",
                            message.getValue().getTimestamp());
                    setValue(message.getValue().clone());
                } // If values are equals do nothing
                break;
            case REPLY:
                boolean replyValueIsNewer = getValue().getTimestamp() < message.getValue().getTimestamp();
                if (replyValueIsNewer) {
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

