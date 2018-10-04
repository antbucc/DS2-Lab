package com.projects.detoni_zampieri.message;

import java.io.Serializable;

public class Message implements Serializable {

    public Message(int id)
    {
        this.id = id;
    }

    public int hashCode()
    {
        return this.id;
    }

    public boolean equals(Object o)
    {
        if(o instanceof Message)
        {
            return ((Message)o).id == this.id;
        }
        else return false;
    }

    public int id;
}
