package com.projects.detoni_zampieri.lab2.actor;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import com.projects.detoni_zampieri.lab1.message.BroadcastMessage;
import com.projects.detoni_zampieri.lab1.message.Message;
import com.projects.detoni_zampieri.lab1.message.NodeListMessage;
import com.projects.detoni_zampieri.lab1.message.StartBroadcastMessage;
import com.projects.detoni_zampieri.lab2.message.EpidemicValue;
import scala.concurrent.duration.FiniteDuration;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Actor extends UntypedActor {

    private ArrayList<ActorRef> peers;
    private Random rnd;
    protected EpidemicValue value;

    public Actor()
    {
        this.rnd = new Random();
        this.value = new EpidemicValue(new Timestamp(System.currentTimeMillis()), 1);
    }

    public void onReceive(Object message) throws Exception {};

    private void onNodeList(NodeListMessage msg){ this.peers = msg.nodes; }

    private void sendMessage(Message msg){

        int index=-1;
        do {
            index = rnd.nextInt(peers.size());
        } while (this.peers.get(index).equals(this.getSelf()));

        this.peers.get(index).tell(msg, this.getSelf());
    }

    private void onStartBroadcast(StartBroadcastMessage msg)
    {
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
        return Props.create(Actor.class,()->new Actor());
    }

}
