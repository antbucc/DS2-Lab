package com.projects.geloso.epidemics.messages;

import com.projects.geloso.epidemics.EpidemicValue;

public class EpidemicPullMessage extends EpidemicMessage {

    private final PullType type;

    public EpidemicPullMessage(EpidemicValue value) {
        super(value);
        this.type = PullType.NONE;
    }

    public EpidemicPullMessage(EpidemicValue value, PullType pullType) {
        super(value);
        this.type = pullType;
    }

    public PullType getType() {
        return type;
    }

    public enum PullType {NONE, PULL, REPLY, PUSH_PULL}
}
