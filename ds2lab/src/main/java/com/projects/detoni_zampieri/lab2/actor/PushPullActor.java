package com.projects.detoni_zampieri.lab2.actor;

import com.projects.detoni_zampieri.lab2.message.PullReplyMessage;
import com.projects.detoni_zampieri.lab2.message.PushPullMessage;
import com.projects.detoni_zampieri.lab2.message.TimeoutMessage;

import akka.actor.Props;

public class PushPullActor extends Actor {

	@Override
	public void onReceive(Object message) throws Exception {
		if(message instanceof PushPullMessage) {
			onPushPullMessage((PushPullMessage)message);
		}
		else if(message instanceof PullReplyMessage) {
			onPullReplyMessage((PullReplyMessage)message);
		}
		else if(message instanceof TimeoutMessage) {
			onTimeoutMessage((TimeoutMessage)message);
		}
		else super.onReceive(message);
	}
	
	public void onPushPullMessage(PushPullMessage message) {
		if(this.value.getTimestamp().before(message.value.getTimestamp())) {
			this.value = message.value;
		}
		else if(this.value.getTimestamp().after(message.value.getTimestamp())) {
			PullReplyMessage rep = new PullReplyMessage(this.value);
			getSender().tell(rep, getSelf());
		}
	}
	
	public void onPullReplyMessage(PullReplyMessage message) {
		if(this.value.getTimestamp().before(message.value.getTimestamp())) {
			this.value = message.value;
		}
	}
	
	public void onTimeoutMessage(TimeoutMessage message) {
		PushPullMessage msg = new PushPullMessage(this.value);
		this.sendMessage(msg);
	}

	@Override
	protected void onEpidemicTimeout() {
		PushPullMessage msg = new PushPullMessage(this.value);
		this.sendMessage(msg);
	}

	public static Props props() {
        return Props.create(PushPullActor.class,()->new PushPullActor());
    }
}
