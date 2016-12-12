package com.five.conquest.Chat;

/**
 * Created by Terry Antony on 12/5/2016.
 */

public class Message {

    private String messageText;
    private boolean user = true;
    private long messageTime;

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
    public long getMessageTime() {
        return messageTime;
    }
    public void setUserType(boolean user) {
        this.user = user;
    }
    public boolean getUserType() {
        return user;
    }
    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }
    public String getMessageText() {return messageText;}

}