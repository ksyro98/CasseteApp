package com.syroniko.casseteapp.ChatAndMessages;

public class Chat {

    private String sender;
    private String receiver;
    private String timestamp;
    private String message;
    private boolean isSeen;
    private String chatId;

    public  Chat(){

    }

    public Chat(String sender, String receiver, String message, boolean isSeen, String timestamp){
        this.message=message;
        this.receiver=receiver;
        this.sender=sender;
        this.timestamp=timestamp;
        this.isSeen = isSeen;
    }

    public String getSender() {
        return sender;
    }
    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }
    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

    public String getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isSeen() {
        return isSeen;
    }
    public void setSeen(boolean seen) {
        isSeen = seen;
    }

    public String getChatId() {
        return chatId;
    }
    public void setChatId(String chatId) {
        this.chatId = chatId;
    }
}
