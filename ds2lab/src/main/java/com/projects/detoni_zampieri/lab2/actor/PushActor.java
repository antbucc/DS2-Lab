package com.projects.detoni_zampieri.lab2.actor;

import akka.actor.Props;
import com.projects.detoni_zampieri.lab2.message.PushMessage;
import com.projects.detoni_zampieri.lab2.message.TimeoutMessage;

public class PushActor extends Actor {

    public PushActor() {
    }

    @Override
    protected void onEpidemicTimeout() {
        //System.out.println("Actor "+this.actorId+" sending push");
        PushMessage msg = new PushMessage(this.value);
        this.sendMessage(msg);
    }

    @Override
    public void onReceive(Object message) throws Exception {
        if (message instanceof PushMessage) {
            onPushMessage((PushMessage) message);
        } else if (message instanceof TimeoutMessage) {
            onTimeoutMessage((TimeoutMessage) message);
        } else {
            super.onReceive(message);
        }
    }

    public void onTimeoutMessage(TimeoutMessage message)
    {
        PushMessage msg = new PushMessage(this.value);
        sendMessage(msg);
    }

    public void onPushMessage(PushMessage message)
    {
        if (this.value.getTimestamp().before(message.value.getTimestamp()))
        {
            this.value = message.value;
        }
    }

    public static Props props() {
        return Props.create(PushActor.class,()->new PushActor());
    }
}
