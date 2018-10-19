package com.projects.detoni_zampieri.lab2.actor;

import com.projects.detoni_zampieri.lab2.message.*;

import akka.actor.Props;

public class PullActor extends Actor {

	@Override
	public void onReceive(Object message) throws Exception {
		if(message instanceof PullRequestMessage) {
			onPullRequestMessage((PullRequestMessage)message);
		}
		else if (message instanceof PullReplyMessage) {
			onPullReplyMessage((PullReplyMessage)message);
		}
		else if(message instanceof TimeoutMessage) {
			onTimeoutMessage((TimeoutMessage)message);
		}
		else super.onReceive(message);
	}

	private void onTimeoutMessage(TimeoutMessage message) {
		PullRequestMessage req = new PullRequestMessage(this.value.getTimestamp());
		this.sendMessage(req);
	}

	private void onPullReplyMessage(PullReplyMessage message) {
		if(this.value.getTimestamp().before(message.value.getTimestamp())) {
			this.value = message.value;
		}
	}

	private void onPullRequestMessage(PullRequestMessage message) {
		if(this.value.getTimestamp().after(message.timestamp)) {
			PullReplyMessage rep = new PullReplyMessage(this.value);
			getSender().tell(rep, getSender());
		}
	}

	@Override
	protected void onEpidemicTimeout() {
		PullRequestMessage req = new PullRequestMessage(this.value.getTimestamp());
		this.sendMessage(req);
	}

	public static Props props() {
        return Props.create(PullActor.class,()->new PullActor());
    }
}
