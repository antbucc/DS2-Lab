package com.projects.CeschiErcolani.Epidemic;

import java.sql.Timestamp;
import java.util.List;
import java.util.Random;

import akka.actor.ActorRef;
import java.util.TimerTask;
import akka.actor.Props;
import akka.actor.UntypedActor;
import com.projects.CeschiErcolani.Epidemic.AntiEntropyMessage.UpdateMsg;
// TODO

public class Epidemic extends UntypedActor {

    List<ActorRef> lGroup = null;
    long lTimeout;
    TimeOut b;
    int iValue;
    Timestamp tLastModificationTime;

    AntiEntropyType aetType = AntiEntropyType.PULL;
    Random rRandom = new Random();

    private class TimeOut extends TimerTask {

        public void run() {
            int iRecepient = rRandom.nextInt(lGroup.size());
            ActorRef arActor = lGroup.get(iRecepient);
            arActor.tell(new AntiEntropyMessage.RequestMsg(tLastModificationTime, iValue, aetType), getSelf());

        }
    }

    public Epidemic() {}
    public Epidemic(AntiEntropyType aetType) {
        this.aetType = aetType;
    }

    public static Props props() {
        return Props.create(Epidemic.class);
    }

    @Override
    public void onReceive(Object msg) throws Exception {
        System.out.println("Receiving: " + msg.toString());
        if (msg instanceof AntiEntropyMessage.RequestMsg) {
            onAntiEntropyRequestMsg((AntiEntropyMessage.RequestMsg) msg);
        } else if (msg instanceof AntiEntropyMessage.ReplyMsg) {
            onAntiEntropyReplyMsg((AntiEntropyMessage.RequestMsg) msg);
        } else if (msg instanceof HelloMsg) {
            onHelloMsg((HelloMsg) msg);
         } else if (msg instanceof UpdateMsg) {
            onAntiEntropyUpdateMsg((UpdateMsg) msg);
        }
    }

    protected void onAntiEntropyRequestMsg(AntiEntropyMessage.RequestMsg mMsg) {
        switch (mMsg.aetType) {
            case PUSH_PULL:
            case PUSH: {
                if (tLastModificationTime.compareTo(mMsg.tTimestamp) > 0) {
                    iValue = mMsg.iValue;
                    tLastModificationTime = mMsg.tTimestamp;
                }
                if (mMsg.aetType == AntiEntropyType.PUSH) {
                    break;
                }
            }
            case PULL: {
                if (tLastModificationTime.compareTo(mMsg.tTimestamp) < 0) {
                    getSender().tell(new AntiEntropyMessage.ReplyMsg(tLastModificationTime, iValue), getSelf());
                }
                break;
            }
        }
    }

    protected void onAntiEntropyReplyMsg(AntiEntropyMessage.RequestMsg mMsg) {
        if (tLastModificationTime.compareTo(mMsg.tTimestamp) > 0) {
            iValue = mMsg.iValue;
            tLastModificationTime = mMsg.tTimestamp;
        }
    }

    protected void onAntiEntropyUpdateMsg(AntiEntropyMessage.UpdateMsg mMsg) {
        if (tLastModificationTime.compareTo(mMsg.tTimestamp) > 0) {
            iValue = mMsg.iValue;
            tLastModificationTime = mMsg.tTimestamp;
        }
    }

    /* Informs an actor about the group structure. */
    public static class HelloMsg {

        final List<ActorRef> lGroup;
        AntiEntropyType aetType;

        HelloMsg(List<ActorRef> lGroup, AntiEntropyType aetType) {
            this.lGroup = lGroup;
            this.aetType = aetType;
        }

        List<ActorRef> getGroup() {
            return this.lGroup;
        }
    }

    protected void onHelloMsg(HelloMsg mMsg) {
        lGroup = mMsg.getGroup();
    }

}
