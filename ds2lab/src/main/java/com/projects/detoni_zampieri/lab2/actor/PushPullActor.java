package com.projects.detoni_zampieri.lab2.actor;

import com.projects.detoni_zampieri.lab2.message.PullReplyMessage;
import com.projects.detoni_zampieri.lab2.message.PushPullMessage;
import com.projects.detoni_zampieri.lab2.message.TimeoutMessage;

public class PushPullActor extends Actor {
	
	@Override
	public void onReceive(Object message) {
		if(message instanceof PushPullMessage) {
			onPushPullMessage((PushPullMessage)message);
		}
		else if(message instanceof PullReplyMessage) {
			onPullReplyMessage((PullReplyMessage)message);
		}
		else if(message instanceof TimeoutMessage) {
			onTimeoutMessage((TimeoutMessage)message);
		}
		else unhandled(message);
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
		
	}
	
	public void onTimeoutMessage(TimeoutMessage message) {
		PushPullMessage msg = new PushPullMessage(this.value);
		this.sendMessage(msg);
	}
}
