package com.projects.CeschiErcolani.Epidemic;

import java.io.Serializable;
import java.sql.Timestamp;

public class AntiEntropyMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    Timestamp tTimestamp;
    int iValue;

    @Override
    public String toString() {
        return "[" + tTimestamp.toString() + ":" + Integer.toString(iValue) + "]";
    }

    public AntiEntropyMessage(Timestamp t, int val) {
        tTimestamp = t;
        iValue = val;
    }

    public static class RequestMsg extends AntiEntropyMessage {

        AntiEntropyType aetType;

        RequestMsg(Timestamp t, int val, AntiEntropyType aetType) {
            super(t, val);
            this.aetType = aetType;
        }

        AntiEntropyType getType() {
            return this.aetType;
        }
    }

    public static class ReplyMsg extends AntiEntropyMessage {

        public ReplyMsg(Timestamp t, int val) {
            super(t, val);
        }
    }

    public static class UpdateMsg extends AntiEntropyMessage {

        public UpdateMsg(Timestamp t, int val) {
            super(t, val);
        }
    }
}
