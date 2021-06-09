package youssef.kecheima.topchat_v12.Model;

public class Chat {
    private String sender,receiver,message,image_Url,message_type,time;
    private boolean is_seen;

    public Chat() {
    }

    public Chat(String sender, String receiver, String message, String image_Url, String message_type, String time, boolean is_seen) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.image_Url = image_Url;
        this.message_type = message_type;
        this.time = time;
        this.is_seen = is_seen;
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

    public String getImage_Url() {
        return image_Url;
    }

    public void setImage_Url(String image_Url) {
        this.image_Url = image_Url;
    }

    public String getMessage_type() {
        return message_type;
    }

    public void setMessage_type(String message_type) {
        this.message_type = message_type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isIs_seen() {
        return is_seen;
    }

    public void setIs_seen(boolean is_seen) {
        this.is_seen = is_seen;
    }
}
