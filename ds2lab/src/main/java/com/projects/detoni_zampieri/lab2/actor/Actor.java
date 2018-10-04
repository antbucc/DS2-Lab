package com.projects.detoni_zampieri.lab2.actor;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import com.projects.detoni_zampieri.lab2.message.ActorListMessage;
import com.projects.detoni_zampieri.lab2.message.EpidemicValue;
import com.projects.detoni_zampieri.lab2.message.Message;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Random;

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

    protected void onActorList(ActorListMessage msg){ this.peers = msg.nodes; }

    protected void sendMessage(Message msg){

        int index=-1;
        do {
            index = rnd.nextInt(peers.size());
        } while (this.peers.get(index).equals(this.getSelf()));

        this.peers.get(index).tell(msg, this.getSelf());
    }

    public static Props props() {
        return Props.create(Actor.class,()->new Actor());
    }

}
