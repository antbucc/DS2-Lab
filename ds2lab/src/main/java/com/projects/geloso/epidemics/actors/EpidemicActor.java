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

    protected final ActorRef me = getSelf();
    private final long delta = 100;
    private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
    private List<ActorRef> processes = new ArrayList<>();
    private int round = 0;
    /*
     * Method to generate random delays
     */
    private Random rand = new Random(System.currentTimeMillis());
    private EpidemicValue value = new EpidemicValue(0, null);

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