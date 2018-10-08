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
    protected int round;
    protected long timeout;
    protected long delta;

    public Actor()
    {
        this.rnd = new Random();
        this.value = new EpidemicValue(new Timestamp(System.currentTimeMillis()), 1);
        this.round = 0;
        this.delta = 100;
    }

    public void onReceive(Object message) throws Exception {
        if (message instanceof ActorListMessage)
        {
            onActorList((ActorListMessage) message);
        } else {
            unhandled(message);
        }
    }

    protected void onActorList(ActorListMessage msg) { this.peers = msg.nodes; }

    protected void sendMessage(Message msg){

        int index=-1;
        do {
            index = rnd.nextInt(peers.size());
        } while (this.peers.get(index).equals(this.getSelf()));

        this.peers.get(index).tell(msg, this.getSelf());
    }

    protected void runSchedule() {
        Thread t = new Thread(new Runnable() {

            public void run() {
                // TODO Auto-generated method stub
                while (true) {
                    if (System.currentTimeMillis() >= timeout) {
                        onEpidemicTimeout();
                        round++;
                        setEpidemicTimeOut();
                    }
                }
            }
        });
        t.start();
    }

    protected void setEpidemicTimeOut()
    {
        timeout = System.currentTimeMillis() + delta;
    }

    protected void onEpidemicTimeout()
    {}

    public static Props props() {
        return Props.create(Actor.class,()->new Actor());
    }

}
