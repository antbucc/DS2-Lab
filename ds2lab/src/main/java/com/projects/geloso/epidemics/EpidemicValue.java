package com.projects.geloso.epidemics;

public class EpidemicValue implements Cloneable {
    private final long timestamp;
    private final String value;

    public EpidemicValue(long timestamp, String value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getValue() {
        return value;
    }

    @Override
    @SuppressWarnings({"MethodDoesntCallSuperMethod", "CloneDoesntDeclareCloneNotSupportedException"})
    public EpidemicValue clone() {
        return new EpidemicValue(this.timestamp, this.value);
    }

    @Override
    public String toString() {
        return "EpidemicValue{" +
                "timestamp=" + timestamp +
                ", value='" + value + '\'' +
                '}';
    }
}
