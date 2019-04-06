package com.example.tanika.toombikeapplication.Recycler;

import java.util.ArrayList;

public class HistoryItem {


    private String bikeSign;
    private String date;
    private String bikeState;
    private String bikeStatus;
    private String price;
    private String hisNum;

    public HistoryItem(String hisNum, String date, String bikeSign, String bikeState, String bikeStatus, String price){


        this.hisNum = hisNum;
        this.date = date;
        this.bikeSign = bikeSign;
        this.bikeState = bikeState;
        this.bikeStatus = bikeStatus;
        this.price = price;

    }

    public String getBikeSign() {
        return bikeSign;
    }

    public String getBikeState() {
        return bikeState;
    }

    public String getBikeStatus() {
        return bikeStatus;
    }

    public String getDate() {
        return date;
    }

    public String getPrice() {
        return price;
    }

    public String getHisNum() {
        return hisNum;
    }
}
