package com.santteam.apphenhosinhvien;

/**
 * Created by nqait97 on 12-Nov-17.
 */

public class RequestsFriend {
    private String ID;
    private String request_type;
    public RequestsFriend() {
    }

    public RequestsFriend(String request_type) {
        this.request_type = request_type;
    }

    public String getRequest_type() {
        return request_type;
    }

    public void setRequest_type(String request_type) {
        this.request_type = request_type;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
