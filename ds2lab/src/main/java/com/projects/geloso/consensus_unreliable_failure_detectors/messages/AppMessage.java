package com.projects.geloso.consensus_unreliable_failure_detectors.messages;

import java.util.Objects;

public class AppMessage extends Message {
    private final int id;

    public AppMessage(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppMessage that = (AppMessage) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
