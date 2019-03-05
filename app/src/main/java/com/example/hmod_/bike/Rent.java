package com.example.hmod_.bike;


import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Rent {
    private String uid;
    private Date startTime;
    private Date endTime;
    private Timestamp test;
    private float cost;
    private static SimpleDateFormat formater = new SimpleDateFormat("yyyy/MM/dd hh:mma");

    public Rent(){

    }

    public Rent(String uid, Date startTime, Date endTime, float cost) {
        this.uid = uid;
        this.startTime = startTime;
        this.endTime = endTime;
        
        this.cost = cost;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public float getCost() {
        return cost;
    }

    public void setCost(float cost) {
        this.cost = cost;
    }

    public String getDuration (){
        if (endTime != null) {

            long diff = (endTime.getTime() - startTime.getTime()) / 1000;
            int hours = (int) diff / (60*60);
            int minutes = (int) (diff / 60) % 60;
            // int seconds = (int) (diff % 60);
            return String.format("%s%d Minutes", (hours==0) ? "" : String.format( "%d Hour and ", hours), minutes);

        }
        return "On rent";
    }
    public String getStartTimeAndCost (){
        return formater.format(startTime) +(this.cost != 0 ? String.format(" -- %.2f NIS", this.cost) :"");
    }
}
