package com.projects.detoni_zampieri.lab2.actor;

import com.projects.detoni_zampieri.lab2.message.*;

public class PullActor extends Actor {
	
	@Override
	public void onReceive(Object message) {
		if(message instanceof PullRequestMessage) {
			onPullRequestMessage((PullRequestMessage)message);
		}
		else if (message instanceof PullReplyMessage) {
			onPullReplyMessage((PullReplyMessage)message);
		}
		else unhandled(message);
	}

	private void onPullReplyMessage(PullReplyMessage message) {
		// TODO Auto-generated method stub
		
	}

	private void onPullRequestMessage(PullRequestMessage message) {
		// TODO Auto-generated method stub
		
	}
	
}
