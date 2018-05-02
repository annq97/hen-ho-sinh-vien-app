package com.santteam.apphenhosinhvien;

/**
 * Created by nqait97 on 16-Nov-17.
 */

public class Chat {
    private Boolean seen;
    private Long time;
    private String lastmessage;
    private String ID;
    public Chat() {
    }

    public Chat(String lastmessage) {
        this.lastmessage = lastmessage;
    }

    public Boolean getSeen() {
        return seen;
    }

    public void setSeen(Boolean seen) {
        this.seen = seen;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getLastmessage() {
        return lastmessage;
    }

    public void setLastmessage(String lastmessage) {
        this.lastmessage = lastmessage;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
