package com.example.epidemic;


import akka.actor.ActorRef;
import akka.actor.Props;

public class EpidemicPushPullActor extends EpidemicActor {
	
	public static Props props() {
        return Props.create(EpidemicPushPullActor.class);
    }
	
	public static class EpidemicPushPullMessage extends EpidemicMessage {
		public static enum PullType {NONE, PULL, REPLY};
		private PullType type = PullType.NONE;
		private ActorRef sender = null;
		public PullType getType() {
			return type;
		}
		public void setType(PullType type) {
			this.type = type;
		}
		public ActorRef getSender() {
			return sender;
		}
		public void setSender(ActorRef sender) {
			this.sender = sender;
		}
	}

	@Override
	protected void onEpidemicTimeoutImpl() {
		ActorRef q = randomProcess();
		EpidemicPushPullMessage ppm = new EpidemicPushPullMessage();
		ppm.setValue(getValue());
		ppm.setSender(me);
		ppm.setType(EpidemicPushPullMessage.PullType.PULL);
		log.info("Send PULL to {}", q.path().name());
		q.tell(ppm, me);
	}

	@Override
	protected void onEpidemicReceiveImpl(EpidemicMessage message) {
		EpidemicPushPullMessage eppm = (EpidemicPushPullMessage) message;
		EpidemicValue v = getValue();
		switch (eppm.getType()) {
		case PULL:
			log.info("Receive PUSHPULL from {}", eppm.getSender().path().name());
			if (v.getTimestamp() < eppm.getValue().getTimestamp()) {
				setValue(eppm.getValue());
				valueSynced();
			} else if (getValue().getTimestamp() > eppm.getValue().getTimestamp()) {
				EpidemicPushPullMessage reply = new EpidemicPushPullMessage();
				reply.setValue(getValue());
				reply.setSender(me);
				reply.setType(EpidemicPushPullMessage.PullType.REPLY);
				log.info("Send REPLY to {}", eppm.getSender().path().name());
				eppm.getSender().tell(reply, me);
				
			}
			break;
		case REPLY:
			log.info("Receive REPLY from {}", eppm.getSender().path().name());
			if (v.getTimestamp() < eppm.getValue().getTimestamp()) {
				setValue(eppm.getValue());
				valueSynced();
			}
			break;
		case NONE:
		default:
		}
	}
	
	@Override
	protected void valueSyncedImpl() {
	}
}