package com.projects.detoni_zampieri;

import akka.actor.UntypedActor;

import java.io.Serializable;
import java.lang.annotation.Inherited;
import java.util.ArrayList;
import java.util.HashSet;

public class ReliableBroadcast{

}

class Message implements Serializable{

}

class BroadcastMessage extends Message{}

class StartBroadcast extends Message{}

class Node extends UntypedActor{

    private HashSet<Message> delivered = new HashSet<Message>();

    public void onReceive(Object message) throws Exception {
        if(message instanceof StartBroadcast)
        {
            onStartBroadcast((StartBroadcast) message);
        }
        else if (message instanceof BroadcastMessage)
        {
            onBroadcastMessage((BroadcastMessage) message);
        }
        else unhandled(message);
    }

    private void onStartBroadcast(StartBroadcast msg)
    {

    }

    private void onBroadcastMessage(BroadcastMessage msg)
    {
        if(!delivered.contains(msg))
        {
            
        }
    }
}