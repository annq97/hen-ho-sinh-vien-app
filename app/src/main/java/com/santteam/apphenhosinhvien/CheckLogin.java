package com.santteam.apphenhosinhvien;

/**
 * Created by nqait97 on 16-Nov-17.
 */

public class CheckLogin {

    private static CheckLogin instanse;
    private int check;
    private CheckLogin(){
        this.check = 1;
    }

    public static CheckLogin Instance(){
        if(instanse == null){
            instanse = new CheckLogin();
        }
        return instanse;
    }

    public int getCheck() {
        return check;
    }

    public void setCheck(int check) {
        this.check = check;
    }
}
