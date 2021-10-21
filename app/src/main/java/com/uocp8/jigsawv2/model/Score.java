package com.uocp8.jigsawv2.model;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

public class Score {

    private int Iduser;
    private String name;
    private LocalDate date;
    private LocalTime time;

    public Score(int iduser, String name, LocalDate date, LocalTime time) {
        Iduser = iduser;
        this.name = name;
        this.date = date;
        this.time = time;
    }

    //Sobrecarga de constructores para insert

    public Score(String name, LocalDate date, LocalTime time) {
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

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    //Objeto a String


    @Override
    public String toString() {
        return "Score{" +
                "Iduser=" + Iduser +
                ", name='" + name + '\'' +
                ", date=" + date +
                ", time=" + time +
                '}';
    }
}
