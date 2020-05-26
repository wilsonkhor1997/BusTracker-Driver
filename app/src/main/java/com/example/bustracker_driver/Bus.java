package com.example.bustracker_driver;

public class Bus {

    //private int userid;
    private String latitude1;
    private String longitude1;
    private String busStop;
    private String StudNum;

    public Bus(String latitude1,String longitude1){
        this.latitude1=latitude1;
        this.longitude1=longitude1;
    }

    public Bus(String latitude1,String longitude1,String busStop,String StudNum){
        this.latitude1=latitude1;
        this.longitude1=longitude1;
        this.busStop=busStop;
        this.StudNum=StudNum;
    }

    public String getLatitude1() {
        return latitude1;
    }

    public String getLongitude1(){
        return longitude1;
    }

    public String getBusStop() {
        return busStop;
    }

    public String getStudNum() {
        return StudNum;
    }
}