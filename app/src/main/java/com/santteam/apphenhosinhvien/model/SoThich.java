package com.santteam.apphenhosinhvien.model;

import java.util.ArrayList;

/**
 * Created by nqait97 on 20-Oct-17.
 */

public class SoThich {

    private static SoThich instance;
    ArrayList<Boolean> soThichs = new ArrayList<>();

    private SoThich() {
    }

    public static SoThich Intance(){
        if(instance == null){
            instance = new SoThich();
        }
        return instance;
    }

    public ArrayList<Boolean> getSoThichs() {
        return soThichs;
    }

    public void setSoThichs(ArrayList<Boolean> soThichs) {
        this.soThichs = soThichs;
    }

}
