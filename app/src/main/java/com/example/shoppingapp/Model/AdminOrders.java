package com.example.shoppingapp.Model;

public class AdminOrders {

private String Name,City,Address,Time,State,Phone,Date,TotalAmount;

public AdminOrders(){


}

    public AdminOrders(String name, String city, String address, String time, String state, String phone, String date, String totalAmount) {
        Name = name;
        City = city;
        Address = address;
        Time = time;
        State = state;
        Phone = phone;
        Date = date;
        TotalAmount = totalAmount;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getTime() {
        return Time;
    }

    public void setTime(String time) {
        Time = time;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getTotalAmount() {
        return TotalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        TotalAmount = totalAmount;
    }
}
