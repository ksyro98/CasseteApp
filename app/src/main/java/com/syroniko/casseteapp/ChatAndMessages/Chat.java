package com.syroniko.casseteapp.ChatAndMessages;

public class Chat {
    public String getSender() {
        return sender;
    }
    public  Chat(){

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

    private String sender;
    private String receiver;

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    private String timestamp;
    private String message;

    public boolean isSeen() {
        return isseen;
    }

    public void setSeen(boolean seen) {
        isseen = seen;
    }

    private boolean isseen;

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    private String chatId;

    public Chat(String sender, String receiver, String message,boolean isseen,String timestamp){
        this.message=message;
        this.receiver=receiver;
        this.sender=sender;
        this.timestamp=timestamp;
        this.isseen=isseen;
    }

}
