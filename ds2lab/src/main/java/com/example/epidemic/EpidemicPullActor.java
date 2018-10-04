package com.example.epidemic;

import akka.actor.ActorRef;
import akka.actor.Props;

public class EpidemicPullActor extends EpidemicActor {
	
	public static Props props() {
        return Props.create(EpidemicPullActor.class);
    }
	
	public static class EpidemicPullMessage extends EpidemicMessage {
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
		EpidemicPullMessage pull = new EpidemicPullMessage();
		pull.setValue(getValue());
		pull.setSender(me);
		pull.setType(EpidemicPullMessage.PullType.PULL);
		//log.info("Send PULL to {}", q.path().name());
		q.tell(pull, me);
	}

	@Override
	protected void onEpidemicReceiveImpl(EpidemicMessage message) {
		EpidemicPullMessage epm = (EpidemicPullMessage) message;
		switch (epm.getType()) {
		case PULL:
			//log.info("Receive PULL from {}", epm.getSender().path().name());
			if (getValue().getTimestamp() > epm.getValue().getTimestamp()) {
				EpidemicPullMessage reply = new EpidemicPullMessage();
				reply.setValue(getValue());
				reply.setSender(me);
				reply.setType(EpidemicPullMessage.PullType.REPLY);
				//log.info("Send REPLY to {}", epm.getSender().path().name());
				epm.getSender().tell(reply, me);
				
			}
			break;
		case REPLY:
			//log.info("Receive REPLY from {}", epm.getSender().path().name());
			EpidemicValue v = getValue();
			if (v.getTimestamp() < epm.getValue().getTimestamp()) {
				setValue(epm.getValue());
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
