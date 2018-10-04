package com.projects.geloso.epidemics.messages;

import com.projects.geloso.epidemics.EpidemicValue;

public abstract class EpidemicMessage {

    private final EpidemicValue value;

    public EpidemicMessage(EpidemicValue value) {
        this.value = value.clone();
    }

    public EpidemicValue getValue() {
        return value;
    }

}
