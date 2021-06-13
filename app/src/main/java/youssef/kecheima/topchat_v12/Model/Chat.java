package youssef.kecheima.topchat_v12.Model;

public class Chat {
    private String sender,receiver,message,images_Url,file_Url,file_type,message_type,time;
    private boolean is_seen;

    public Chat() {
    }

    public Chat(String sender, String receiver, String message, String images_Url, String file_Url, String file_type, String message_type, String time, boolean is_seen) {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.images_Url = images_Url;
        this.file_Url = file_Url;
        this.file_type = file_type;
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

    public String getImages_Url() {
        return images_Url;
    }

    public void setImages_Url(String images_Url) {
        this.images_Url = images_Url;
    }

    public String getFile_Url() {
        return file_Url;
    }

    public void setFile_Url(String file_Url) {
        this.file_Url = file_Url;
    }

    public String getFile_type() {
        return file_type;
    }

    public void setFile_type(String file_type) {
        this.file_type = file_type;
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
