package com.example.hmod_.bike;

import com.google.firebase.auth.FirebaseUser;

public class User {
    private String name = "";
    private String email = "";
    private String phone = "";
    private String photo = "";
    private String providerId = "";
    private float credits = 0;

    public User (){

    }

    public User(FirebaseUser user){
        this.name = user.getDisplayName();
        this.email = user.getEmail();
        this.phone = user.getPhoneNumber();
        this.providerId = user.getProviderId();
        if (user.getPhotoUrl()!=null)
            this.photo = user.getPhotoUrl().toString();
    }

    public User(String name, String email, String phone, String photo, String providerId, float credits) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.photo = photo;
        this.providerId = providerId;
        this.credits = credits;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public float getCredits() {
        return credits;
    }

    public void setCredits(float credits) {
        this.credits = credits;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }
}
