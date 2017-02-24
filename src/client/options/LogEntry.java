package client.options;

import client.options.LogType;

import java.sql.Time;

public class LogEntry{
    public Class c;
    public LogType type;
    public String message;
    public Time timeCreated;

    public LogEntry(Class c, LogType type, String s){
        this.c = c;
        this.type = type;
        this.message = s;
        timeCreated = new Time(System.currentTimeMillis());
    }


}
