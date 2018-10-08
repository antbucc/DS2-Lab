package com.projects.geloso.epidemics.messages;

import com.projects.geloso.epidemics.EpidemicValue;

public class EpidemicPullMessage extends EpidemicMessage {

    private PullType type = PullType.NONE;

    public EpidemicPullMessage(EpidemicValue value) {
        super(value);
    }

    public PullType getType() {
        return type;
    }

    public void setType(PullType type) {
        this.type = type;
    }

    public enum PullType {NONE, PULL, REPLY, PUSH_PULL}
}
