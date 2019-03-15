package com.example.hmod_.bike;


import com.example.hmod_.bike.Activity.MainActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Rent {
    private String uid;
    private Date startTime;
    private Date endTime;
    private float cost;
    private String bikeNumber;
    private static final SimpleDateFormat formater = new SimpleDateFormat("yyyy/MM/dd hh:mma");
    public String id;

    public Rent(){
    }

    public Rent(String uid, Date startTime, Date endTime, float cost, String bikeNumber) {
        this.uid = uid;
        this.startTime = startTime;
        this.endTime = endTime;
        this.cost = cost;
        this.bikeNumber = bikeNumber;
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

    public String getBikeNumber() {
        return bikeNumber;
    }

    public void setBikeNumber(String bikeNumber) {
        this.bikeNumber = bikeNumber;
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

    public static void updateCurrentRent (String id) {
        if (id == null) {
            MainActivity.mainActivity.setCurrentRent(null);
            return;
        }
        MainActivity.db.collection("rents").document(id).get().addOnSuccessListener(documentSnapshot -> {
            MainActivity.mainActivity.setCurrentRent(documentSnapshot.toObject(Rent.class));
            MainActivity.currentRent.id = id;
        });
    }
}
