package com.example.epidemic;



import akka.actor.ActorRef;
import akka.actor.Props;

public class EpidemicPushActor extends EpidemicActor {
	
	public static Props props() {
        return Props.create(EpidemicPushActor.class);
    }
	
	public static class EpidemicPushMessage extends EpidemicMessage {
		
	}

	@Override
	protected void onEpidemicTimeoutImpl() {
		if (getValue().getValue() != null) {
			ActorRef q = randomProcess();
			EpidemicPushMessage push = new EpidemicPushMessage();
			push.setValue(getValue());
			q.tell(push, me);
		}
	}

	@Override
	protected void onEpidemicReceiveImpl(EpidemicMessage message) {
		EpidemicPushMessage epm = (EpidemicPushMessage) message;
		EpidemicValue v = getValue();
		if (v.getTimestamp() < epm.getValue().getTimestamp()) {
			setValue(epm.getValue());
			valueSynced();
		}
	}
	
	@Override
	protected void valueSyncedImpl() {
	}
}
