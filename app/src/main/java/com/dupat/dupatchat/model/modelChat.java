package com.dupat.dupatchat.model;

public class modelChat {
    public long timestamp;
    public String sender,receiver,message,date;
    public boolean is_read;

    public modelChat() {
    }

    public modelChat(long timestamp, String sender, String receiver, String message, String date, boolean is_read) {
        this.timestamp = timestamp;
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.date = date;
        this.is_read = is_read;
    }
}
