package com.trodev.petdiary.model;

import com.google.firebase.Timestamp;

public class Note {

    private  String nID;

    String title;
    String content;
    private long timestamp;

    String age ;
    String food;
    String feedingTime;
    String walkingTime;
    String medicine;
    String description;
    String ownerName;
    String address;
    String phone;
    String notes;
    String photoUrl;

    public Note() {
    }

    public Note(String nID, String title, String content,long  timestamp, String age, String food, String feedingTime, String walkingTime, String medicine, String description, String ownerName, String address, String phone, String notes, String photoUrl) {
        this.nID = nID;
        this.title = title;
        this.content = content;
        this.timestamp = timestamp;
        this.age = age;
        this.food = food;
        this.feedingTime = feedingTime;
        this.walkingTime = walkingTime;
        this.medicine = medicine;
        this.description = description;
        this.ownerName = ownerName;
        this.address = address;
        this.phone = phone;
        this.notes = notes;
        this.photoUrl = photoUrl;
    }

    public String getnID() {
        return nID;
    }

    public void setnID(String nID) {
        this.nID = nID;
    }


    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getFood() {
        return food;
    }

    public void setFood(String food) {
        this.food = food;
    }

    public String getFeedingTime() {
        return feedingTime;
    }

    public void setFeedingTime(String feedingTime) {
        this.feedingTime = feedingTime;
    }

    public String getWalkingTime() {
        return walkingTime;
    }

    public void setWalkingTime(String walkingTime) {
        this.walkingTime = walkingTime;
    }

    public String getMedicine() {
        return medicine;
    }

    public void setMedicine(String medicine) {
        this.medicine = medicine;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

