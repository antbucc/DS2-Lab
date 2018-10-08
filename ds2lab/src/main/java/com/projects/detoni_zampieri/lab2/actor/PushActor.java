package com.projects.detoni_zampieri.lab2.actor;

import com.projects.detoni_zampieri.lab2.message.PushMessage;
import com.projects.detoni_zampieri.lab2.message.TimeoutMessage;

public class PushActor extends Actor {

    public PushActor() {
    }

    @Override
    protected void onEpidemicTimeout() {
        super.onEpidemicTimeout();
        System.out.println("Actor "+this.actorId+" sending push");
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
        if (this.value.getTimestamp().after(message.value.getTimestamp()))
        {
            this.value = message.value;
        }
    }
}
