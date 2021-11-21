package com.uocp8.jigsawv2.model;

public class MyCalendar {
    String calId;
    String calName;


    public MyCalendar( String calName,String calId) {
        this.calId = calId;
        this.calName = calName;
    }

    public String getCalName() {
        return calName;
    }

    public void setCalName(String calName) {
        this.calName = calName;
    }

    public String getCalId() {
        return calId;
    }

    public void setCalId(String calId) {
        this.calId = calId;
    }
}
