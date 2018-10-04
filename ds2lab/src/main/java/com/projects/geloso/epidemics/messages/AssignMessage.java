package com.projects.geloso.epidemics.messages;

/**
 * The AssignMessage from the main function will tell a process to update the value
 */
public class AssignMessage {

    private final String text;

    public AssignMessage(String text) {
        super();
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
