package com.projects.geloso.epidemics.messages;

import com.projects.geloso.epidemics.actors.EpidemicActor;

public class EpidemicMessage {

    private EpidemicActor.EpidemicValue value = new EpidemicActor.EpidemicValue(0, null);

    public EpidemicActor.EpidemicValue getValue() {
        return value;
    }

    public void setValue(EpidemicActor.EpidemicValue v) {
        this.value.copy(v);
    }
}
