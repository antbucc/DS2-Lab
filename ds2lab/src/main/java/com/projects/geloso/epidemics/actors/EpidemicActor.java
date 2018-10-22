package com.projects.geloso.epidemics.actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import com.projects.geloso.epidemics.EpidemicValue;
import com.projects.geloso.epidemics.messages.AssignMessage;
import com.projects.geloso.epidemics.messages.EpidemicMessage;
import com.projects.geloso.epidemics.messages.StartMessage;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class EpidemicActor extends AbstractActor {

    // Actor variables
    private static final double UPDATE_PROBABILITY = 0.05;
    // Adaptive variables
    int T = 1;          // Gossip period, every this ms sends all buffered events
    int bufferSize = 1; // Actual size of the buffer
    int S = 2 * T;      //
    int delta2 = 2;     //
    int H = 7;          //
    int L = 5;           //
    double alpha = 0.8; //
    double rH = 0.05;   //
    double rL = 0.05;    //
    double W = 0.5;     //
    private final long delta = 100;
    protected final ActorRef me = getSelf();
    private int round = 0;
    private List<ActorRef> processes = new ArrayList<>();
    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    private EpidemicValue value = new EpidemicValue(0, null);

    // Method to generate random delays
    private Random rand = new Random(System.currentTimeMillis());

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(StartMessage.class, this::startMessage)
                .match(AssignMessage.class, this::assignMessage)
                .match(EpidemicMessage.class, this::onEpidemicReceiveImpl)
                .build();
    }

    private void assignMessage(final AssignMessage assignMessage) {
        setValue(new EpidemicValue(System.currentTimeMillis(), assignMessage.getText()));
        valueSynced();
    }

    private void startMessage(final StartMessage startMessage) {
        processes = startMessage.getGroup();
        runSchedule();
    }

    protected long randomDelay() {
        return (long) (1000 + rand.nextInt(9000));
    }

    protected ActorRef getRandomProcess() {
        int index = rand.nextInt(processes.size());
        while (processes.indexOf(getSelf()) == index) {
            index = rand.nextInt(processes.size());
        }
        return processes.get(rand.nextInt(processes.size()));
    }

    protected EpidemicValue getValue() {
        return value;
    }

    protected void setValue(EpidemicValue v) {
        this.value = v;
    }

    private void runSchedule() {
        getContext().getSystem().getScheduler().schedule(
                Duration.ofMillis(100),
                Duration.ofMillis(100), () -> {
                    if (rand.nextDouble() < UPDATE_PROBABILITY) {

                        EpidemicValue current = getValue();
                        EpidemicValue newVal = new EpidemicValue(current.getTimestamp() + 1, String.valueOf(rand.nextInt(100)));
                        setValue(newVal);
                        log.debug("UPDATED Value: {}", newVal);
                    }
                    onEpidemicTimeoutImpl();
                    round++;
                }, getContext().getSystem().getDispatcher());
    }

    private void valueSynced() {
        log.info("Current value is \"{}\" at round {}", getValue().getValue(), round);
        valueSyncedImpl();
    }

    protected abstract void valueSyncedImpl();

    protected abstract void onEpidemicTimeoutImpl();

    protected abstract void onEpidemicReceiveImpl(EpidemicMessage message);

}