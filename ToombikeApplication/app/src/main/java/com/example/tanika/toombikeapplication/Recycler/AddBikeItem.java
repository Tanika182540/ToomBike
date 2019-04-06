package com.example.tanika.toombikeapplication.Recycler;

public class AddBikeItem {


    private String bikeBrand,bikeModel,bikeColor,bikeSign,userName,userPhone;
    public AddBikeItem(String brand, String model, String color, String sign,String userName,String userPhone){

       this.bikeBrand = brand;
       this.bikeModel = model;
       this.bikeColor = color;
       this.bikeSign = sign;
       this.userName = userName;
       this.userPhone = userPhone;
    }

    public String getBikeBrand() {
        return bikeBrand;
    }

    public String getBikeModel() {
        return bikeModel;
    }

    public String getBikeColor() {
        return bikeColor;
    }

    public String getBikeSign() {
        return bikeSign;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserPhone() {
        return userPhone;
    }
}
