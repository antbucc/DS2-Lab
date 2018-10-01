package com.projects.detoni_zampieri;

import akka.actor.ActorRef;
import akka.actor.UntypedActor;

import java.io.Serializable;
import java.lang.annotation.Inherited;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

public class ReliableBroadcast{

}

class Message implements Serializable{
    int id;
    public Message(int id)
    {
        this.id = id;
    }

    public int hashCode()
    {
        return this.id;
    }

    public boolean equals(Object o)
    {
        if(o instanceof Message)
        {
            return ((Message)o).id == this.id;
        }
        else return false;
    }
}

class BroadcastMessage extends Message{
    public BroadcastMessage(int id) {
        super(id);
    }
}

class StartBroadcast extends Message{
    public StartBroadcast(int id) {
        super(id);
    }
}

class Node extends UntypedActor{

    private HashSet<Message> delivered;
    private ArrayList<ActorRef> peers;
    private int messageId;
    private Random rnd;

    public Node()
    {
        this.delivered = new HashSet<Message>();
        this.peers = new ArrayList<ActorRef>();
        Random rnd = new Random();
        this.messageId = rnd.nextInt();
    }

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

    private void sendMessage(Message msg){sendMessage(msg,null);}

    private void sendMessage(Message msg,ActorRef avoid){
        for(ActorRef a:this.peers)
        {
            if(a != avoid && a != getSelf())
            {
                a.tell(msg,getSelf());
            }
        }
    }

    private void onStartBroadcast(StartBroadcast msg)
    {
        this.messageId = rnd.nextInt();
        BroadcastMessage message = new BroadcastMessage(this.messageId);
        sendMessage(message);
        r_deliver(message);
        this.delivered.add(msg);

    }

    private void onBroadcastMessage(BroadcastMessage msg)
    {
        if(!delivered.contains(msg))
        {
            sendMessage(msg,getSender());
            r_deliver(msg);
            this.delivered.add(msg);
        }
    }

    private void r_deliver(Message msg)
    {
        System.out.println("Received message " + msg.id);
    }
}