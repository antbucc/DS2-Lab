package com.projects.detoni_zampieri.lab1;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import com.projects.detoni_zampieri.lab1.message.BroadcastMessage;
import com.projects.detoni_zampieri.lab1.message.Message;
import com.projects.detoni_zampieri.lab1.message.NodeListMessage;
import com.projects.detoni_zampieri.lab1.message.StartBroadcastMessage;
import scala.concurrent.duration.FiniteDuration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.TimeUnit;

class Node extends UntypedActor {

    private HashSet<Message> delivered;
    private ArrayList<ActorRef> peers;
    private int messageId;
    private Random rnd;

    public Node()
    {
        this.delivered = new HashSet<Message>();
        this.rnd = new Random();
        this.messageId = rnd.nextInt();
    }

    public void onReceive(Object message) throws Exception {
        if(message instanceof StartBroadcastMessage)
        {
            onStartBroadcast((StartBroadcastMessage) message);
        }
        else if (message instanceof BroadcastMessage)
        {
            onBroadcastMessage((BroadcastMessage) message);
        }
        else if(message instanceof NodeListMessage)
        {
            onNodeList((NodeListMessage) message);
        }
        else unhandled(message);
    }

    private void onNodeList(NodeListMessage msg){ this.peers = msg.nodes; }

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

    private void onStartBroadcast(StartBroadcastMessage msg)
    {
        this.messageId = this.rnd.nextInt();
        BroadcastMessage message = new BroadcastMessage(this.messageId);
        sendMessage(message);
        System.out.println("Sending "+this.messageId);
        r_deliver(message);
        this.delivered.add(message);

        // schedule another send of a new message in the future
        getContext().system().scheduler().scheduleOnce(
                new FiniteDuration(2000, TimeUnit.MILLISECONDS),
                getSelf(),
                new StartBroadcastMessage(-1),
                getContext().system().dispatcher(),
                getSelf()
        );
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

    public static Props props() {
        return Props.create(Node.class,()->new Node());
    }

}
