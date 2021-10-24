package com.uocp8.jigsawv2.model;

import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

public class Score {

    private int Iduser;
    private String name;
    private String date;
    private double time;

    public Score(int iduser, String name, String date, double time) {
        Iduser = iduser;
        this.name = name;
        this.date = date;
        this.time = time;
    }

    //Sobrecarga de constructores para insert

    public Score(String name, String date, double time) {
        this.name = name;
        this.date = date;
        this.time = time;
    }

    // Getter & setter


    public int getIduser() {
        return Iduser;
    }

    public void setIduser(int iduser) {
        Iduser = iduser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public double getTime() {
        return time;
    }

    public void setTime(double time) {
        this.time = time;
    }

    //Objeto a String


    @Override
    public String toString() {
        return "Score{" +
                "Iduser=" + Iduser +
                ", name='" + name + '\'' +
                ", date=" + date.toString() +
                ", time=" + time +
                '}';
    }
}
